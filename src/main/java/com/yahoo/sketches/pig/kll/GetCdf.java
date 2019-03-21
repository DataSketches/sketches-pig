/*
 * Copyright 2019, Verizon Media.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.pig.kll;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;

import com.yahoo.memory.Memory;
import com.yahoo.sketches.kll.KllFloatsSketch;

/**
 * This UDF is to get an approximation to the Cumulative Distribution Function (CDF) of the input stream
 * given a sketch and a set of split points - an array of <i>m</i> unique, monotonically increasing
 * float values that divide the real number line into <i>m+1</i> consecutive disjoint intervals.
 * The function returns an array of <i>m+1</i> double values, the first <i>m</i> of which are approximations
 * to the ranks of the corresponding split points (fraction of input stream values that are less than
 * a split point). The last value is always 1. CDF can also be viewed as a cumulative version of PMF.
 */
public class GetCdf extends EvalFunc<Tuple> {

  @Override
  public Tuple exec(final Tuple input) throws IOException {
    if (input.size() < 2) {
      throw new IllegalArgumentException(
          "expected two or more inputs: sketch and list of split points");
    }

    if (!(input.get(0) instanceof DataByteArray)) {
      throw new IllegalArgumentException("expected a DataByteArray as a sketch, got "
          + input.get(0).getClass().getSimpleName());
    }
    final DataByteArray dba = (DataByteArray) input.get(0);
    final KllFloatsSketch sketch = KllFloatsSketch.heapify(Memory.wrap(dba.get()));

    final float[] splitPoints = new float[input.size() - 1];
    for (int i = 1; i < input.size(); i++) {
      if (!(input.get(i) instanceof Float)) {
        throw new IllegalArgumentException("expected a float value as a split point, got "
            + input.get(i).getClass().getSimpleName());
      }
      splitPoints[i - 1] = (float) input.get(i);
    }
    final double[] cdf = sketch.getCDF(splitPoints);
    if (cdf == null) { return null; }
    return GetPmf.doubleArrayToTuple(cdf);
  }

}