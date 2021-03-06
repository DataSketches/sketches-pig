/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.datasketches.pig.cpc;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.apache.datasketches.cpc.CpcSketch;

@SuppressWarnings("javadoc")
public class GetEstimateTest {

  private static final TupleFactory TUPLE_FACTORY = TupleFactory.getInstance();

  @Test
  public void nullInputTuple() throws Exception {
    final EvalFunc<Double> func = new GetEstimate();
    final Double result = func.exec(null);
    Assert.assertNull(result);
  }

  @Test
  public void emptyInputTuple() throws Exception {
    final EvalFunc<Double> func = new GetEstimate();
    final Double result = func.exec(TUPLE_FACTORY.newTuple());
    Assert.assertNull(result);
  }

  @Test
  public void normalCase() throws Exception {
    final EvalFunc<Double> func = new GetEstimate();
    final CpcSketch sketch = new CpcSketch();
    sketch.update(1);
    sketch.update(2);
    Double result = func.exec(TUPLE_FACTORY.newTuple(new DataByteArray(sketch.toByteArray())));
    Assert.assertNotNull(result);
    Assert.assertEquals(result, 2.0, 0.01);
  }

  @Test
  public void normalCaseCustomSeed() throws Exception {
    final EvalFunc<Double> func = new GetEstimate("123");
    final CpcSketch sketch = new CpcSketch(12, 123);
    sketch.update(1);
    sketch.update(2);
    Double result = func.exec(TUPLE_FACTORY.newTuple(new DataByteArray(sketch.toByteArray())));
    Assert.assertNotNull(result);
    Assert.assertEquals(result, 2.0, 0.01);
  }

}
