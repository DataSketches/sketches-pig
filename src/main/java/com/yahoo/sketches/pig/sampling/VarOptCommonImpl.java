package com.yahoo.sketches.pig.sampling;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

import com.yahoo.memory.Memory;
import com.yahoo.sketches.sampling.VarOptItemsSamples;
import com.yahoo.sketches.sampling.VarOptItemsSketch;
import com.yahoo.sketches.sampling.VarOptItemsUnion;

/**
 * A collection of methods and constants used across VarOpt UDFs.
 *
 * @author Jon Malkin
 */
class VarOptCommonImpl {
  static final int DEFAULT_TARGET_K = 1024;
  static final int DEFAULT_WEIGHT_IDX = 0;

  static final String WEIGHT_ALIAS = "vo_weight";
  static final String RECORD_ALIAS = "record";

  private static final BagFactory BAG_FACTORY = BagFactory.getInstance();
  private static final TupleFactory TUPLE_FACTORY = TupleFactory.getInstance();
  private static final ArrayOfTuplesSerDe SERDE = new ArrayOfTuplesSerDe();

  // Produces a sketch from a bag of input Tuples
  static VarOptItemsSketch<Tuple> rawTuplesToSketch(final Tuple inputTuple, final int k)
          throws IOException {
    assert inputTuple != null;
    assert inputTuple.size() >= 1;
    assert !inputTuple.isNull(0);

    final DataBag samples = (DataBag) inputTuple.get(0);
    final VarOptItemsSketch<Tuple> sketch = VarOptItemsSketch.newInstance(k);

    for (Tuple t : samples) {
      // first element is weight
      final double weight = (double) t.get(0);
      sketch.update(t, weight);
    }

    return sketch;
  }

  // Produces a union from a bag of serialized sketches
  static VarOptItemsUnion<Tuple> unionSketches(final Tuple inputTuple, final int k)
          throws IOException {
    assert inputTuple != null;
    assert inputTuple.size() >= 1;
    assert !inputTuple.isNull(0);

    final VarOptItemsUnion<Tuple> union = VarOptItemsUnion.newInstance(k);

    final DataBag sketchBag = (DataBag) inputTuple.get(0);
    for (Tuple t : sketchBag) {
      final DataByteArray dba = (DataByteArray) t.get(0);
      final Memory mem = Memory.wrap(dba.get());
      union.update(mem, SERDE);
    }

    return union;
  }

  // Serializes a sketch to a DataByteArray and wraps it in a Tuple
  static Tuple wrapSketchInTuple(final VarOptItemsSketch<Tuple> sketch) throws IOException {
    final DataByteArray dba = new DataByteArray(sketch.toByteArray(SERDE));
    final Tuple outputTuple = TUPLE_FACTORY.newTuple(1);
    outputTuple.set(0, dba);
    return outputTuple;
  }

  // Produces a DataBag containing the samples from the input sketch
  static DataBag createDataBagFromSketch(final VarOptItemsSketch<Tuple> sketch) {
    final DataBag output = BAG_FACTORY.newDefaultBag();

    final VarOptItemsSamples<Tuple> samples = sketch.getSketchSamples();

    try {
      // create (weight, item) tuples to add to output bag
      for (VarOptItemsSamples.WeightedSample ws : samples) {
        final Tuple weightedSample = TUPLE_FACTORY.newTuple(2);
        weightedSample.set(0, ws.getWeight());
        weightedSample.set(1, ws.getItem());
        output.add(weightedSample);
      }
    } catch (final ExecException e) {
      throw new RuntimeException("Pig error: " + e.getMessage(), e);
    }

    return output;
  }

  /**
   * Adds raw Tuples to a sketch, returns a Tuple with the serialized sketch.
   */
  public static class RawTuplesToSketchTuple extends EvalFunc<Tuple> {
    private final int targetK_;
    private final int weightIdx_;

    public RawTuplesToSketchTuple() {
      targetK_ = DEFAULT_TARGET_K;
      weightIdx_ = DEFAULT_WEIGHT_IDX;
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     */
    public RawTuplesToSketchTuple(final String kStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = DEFAULT_WEIGHT_IDX;

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOpt requires target reservoir size >= 1: "
                + targetK_);
      }
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     * @param weightIdxStr String indicating column index (0-based) of weight values
     */
    public RawTuplesToSketchTuple(final String kStr, final String weightIdxStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = Integer.parseInt(weightIdxStr);

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOptSampling requires target sample size >= 1: "
                + targetK_);
      }
      if (weightIdx_ < 0) {
        throw new IllegalArgumentException("VarOptSampling requires weight index >= 0: "
                + weightIdx_);
      }
    }

    @Override
    public Tuple exec(final Tuple inputTuple) throws IOException {
      if (inputTuple == null || inputTuple.size() < 1 || inputTuple.isNull(0)) {
        return null;
      }

      final VarOptItemsSketch<Tuple> sketch = rawTuplesToSketch(inputTuple, targetK_);
      return wrapSketchInTuple(sketch);
    }
  }

  /**
   * Unions serialized sketches, returns a Tuple with a sketch obtained from the union result.
   */
  public static class UnionSketchesAsTuple extends EvalFunc<Tuple> {
    private final int targetK_;
    private final int weightIdx_;

    public UnionSketchesAsTuple() {
      targetK_ = DEFAULT_TARGET_K;
      weightIdx_ = DEFAULT_WEIGHT_IDX;
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     */
    public UnionSketchesAsTuple(final String kStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = DEFAULT_WEIGHT_IDX;

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOpt requires target sample size >= 1: "
                + targetK_);
      }
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     * @param weightIdxStr String indicating column index (0-based) of weight values
     */
    public UnionSketchesAsTuple(final String kStr, final String weightIdxStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = Integer.parseInt(weightIdxStr);

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOptSampling requires target sample size >= 1: "
                + targetK_);
      }
      if (weightIdx_ < 0) {
        throw new IllegalArgumentException("VarOptSampling requires weight index >= 0: "
                + weightIdx_);
      }
    }

    @Override
    public Tuple exec(final Tuple inputTuple) throws IOException {
      if (inputTuple == null || inputTuple.size() < 1 || inputTuple.isNull(0)) {
        return null;
      }

      final VarOptItemsUnion<Tuple> union = unionSketches(inputTuple, targetK_);
      return wrapSketchInTuple(union.getResult());
    }
  }

  /**
   * Unions serialized sketches, returns a DataByteArray of the sketch obtained from the union.
   */
  public static class UnionSketchesAsByteArray extends EvalFunc<DataByteArray> {
    private final int targetK_;
    private final int weightIdx_;

    public UnionSketchesAsByteArray() {
      targetK_ = DEFAULT_TARGET_K;
      weightIdx_ = DEFAULT_WEIGHT_IDX;
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     */
    public UnionSketchesAsByteArray(final String kStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = DEFAULT_WEIGHT_IDX;

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOpt requires target sample size >= 1: "
                + targetK_);
      }
    }

    /**
     * VarOpt sampling constructor.
     * @param kStr String indicating the maximum number of desired samples to return.
     * @param weightIdxStr String indicating column index (0-based) of weight values
     */
    public UnionSketchesAsByteArray(final String kStr, final String weightIdxStr) {
      targetK_ = Integer.parseInt(kStr);
      weightIdx_ = Integer.parseInt(weightIdxStr);

      if (targetK_ < 1) {
        throw new IllegalArgumentException("VarOptSampling requires target sample size >= 1: "
                + targetK_);
      }
      if (weightIdx_ < 0) {
        throw new IllegalArgumentException("VarOptSampling requires weight index >= 0: "
                + weightIdx_);
      }
    }

    @Override
    public DataByteArray exec(final Tuple inputTuple) throws IOException {
      if (inputTuple == null || inputTuple.size() < 1 || inputTuple.isNull(0)) {
        return null;
      }

      final VarOptItemsUnion<Tuple> union = unionSketches(inputTuple, targetK_);
      return new DataByteArray(union.getResult().toByteArray(SERDE));
    }
  }

}