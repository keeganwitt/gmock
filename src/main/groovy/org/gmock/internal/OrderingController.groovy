/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmock.internal

import org.gmock.internal.expectation.OrderedExpectations
import org.gmock.internal.expectation.UnorderedExpectations



public class OrderingController {


  InternalMockController controller

  Order order = Order.NONE

  OrderedExpectations orderedExpectations
  UnorderedExpectations unorderedExpectations

  OrderingController(InternalMockController controller) {
    this.controller = controller
    orderedExpectations = new OrderedExpectations(controller)
    unorderedExpectations = new UnorderedExpectations(controller)
  }

  def ordered(Closure orderedClosure) {
    if (ordered) {
      throw new IllegalStateException("Cannot nest ordered closures.")
    }
    if (controller.replay) {
      throw new IllegalStateException("Ordered closures cannot be inside play closure.")
    }

    orderedExpectations.newStrictGroup()
    callClosureWithMockDelegate(orderedClosure, Order.STRICT)
  }

  def unordered(Closure unorderedClosure) {
    if (looseOrdered) {
      throw new IllegalStateException("Cannot nest unordered closures.")
    }
    if (!strictOrdered) {
      throw new IllegalStateException("Unordered closures can only be inside ordered closure.")
    }

    orderedExpectations.newLooseGroup()
    callClosureWithMockDelegate(unorderedClosure, Order.LOOSE)
  }

  def addToExpectations(expectation, expectations) {
    if (ordered) {
      orderedExpectations.add(expectation)
    } else {
      unorderedExpectations.add(expectation, expectations)
    }
  }



  private def callClosureWithMockDelegate(Closure closure, Order order) {
    Order backup = this.order
    try {
      this.order = order
      if (controller.mockDelegate != null) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = controller.mockDelegate
      }
      closure(controller.mockDelegate)
    } finally {
      this.order = backup
    }
  }

  def validate() {
    orderedExpectations.validate()
  }

  def verify() {
    orderedExpectations.verify()
  }

  def reset() {
    orderedExpectations.reset()
    unorderedExpectations.reset()
  }

  boolean isOrdered() {
    order != Order.NONE
  }

  boolean isStrictOrdered() {
    order == Order.STRICT
  }

  boolean isLooseOrdered() {
    order == Order.LOOSE
  }

}

enum Order {
  NONE, STRICT, LOOSE
}
