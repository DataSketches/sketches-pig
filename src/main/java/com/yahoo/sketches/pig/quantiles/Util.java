package com.yahoo.sketches.pig.quantiles;

import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

public class Util {

  static Tuple doubleArrayToTuple(double[] array) throws ExecException {
    Tuple tuple = TupleFactory.getInstance().newTuple(array.length);
    for (int i = 0; i < array.length; i++) {
      tuple.set(i, array[i]);
    }
    return tuple;
  }

}
