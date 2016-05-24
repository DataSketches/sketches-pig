/*
 * Copyright 2016, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */
package com.yahoo.sketches.pig.quantiles;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;

import com.yahoo.sketches.memory.NativeMemory;
import com.yahoo.sketches.quantiles.QuantilesSketch;

/**
 * This UDF is to get a list of quantile values from a QuantileSketch given a list of fractions.
 * The fractions represent normalized ranks and must be from 0 to 1 inclusive. For example,
 * the fraction of 0.5 corresponds to 50th percentile, which is the median value of the
 * distribution (the number separating the higher half of the probability distribution
 * from the lower half).
 */
public class GetQuantiles extends EvalFunc<Tuple> {

  @Override
  public Tuple exec(final Tuple input) throws IOException {
    if (input.size() < 2) throw new IllegalArgumentException("expected two or more inputs: sketch and list of fractions");

    if (!(input.get(0) instanceof DataByteArray)) throw new IllegalArgumentException("expected a DataByteArray as a sketch, got " + input.get(0).getClass().getSimpleName());
    final DataByteArray dba = (DataByteArray) input.get(0);
    final QuantilesSketch sketch = QuantilesSketch.heapify(new NativeMemory(dba.get()));

    double[] fractions = new double[input.size() - 1];
    for (int i = 1; i < input.size(); i++) {
      if (!(input.get(i) instanceof Double)) throw new IllegalArgumentException("expected a double value as a fraction, got " + input.get(i).getClass().getSimpleName());
      fractions[i - 1] = (double) input.get(i);
    }
    return Util.doubleArrayToTuple(sketch.getQuantiles(fractions));
  }

}
