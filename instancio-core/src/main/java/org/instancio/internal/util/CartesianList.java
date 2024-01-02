/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2012 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.instancio.internal.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * A copy of {@code com.google.common.collect.CartesianList} from
 * the <a href="https://github.com/google/guava>Guava</a> library.
 *
 * <p>This version was modified to use {@code ArrayList}
 * instead of {@code ImmutableList} to allow for {@code null} values.
 */
// equals not needed as this list is never returned to users
@SuppressWarnings(Sonar.OVERRIDE_EQUALS)
public final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {

    private final List<List<E>> axes;
    private final int[] axesSizeProduct;

    /**
     * Returns every possible list that can be formed by choosing one element from each of the given
     * lists in order; the "n-ary <a href="http://en.wikipedia.org/wiki/Cartesian_product">Cartesian
     * product</a>" of the lists. For example:
     *
     * <pre>{@code
     * CartesianList.create(Arrays.asList(
     *     Arrays.asList(1, 2),
     *     Arrays.asList("A", "B", "C")))
     * }</pre>
     *
     * <p>returns a list containing six lists in the following order:
     *
     * <ul>
     *   <li>{@code [1, "A"]}
     *   <li>{@code [1, "B"]}
     *   <li>{@code [1, "C"]}
     *   <li>{@code [2, "A"]}
     *   <li>{@code [2, "B"]}
     *   <li>{@code [2, "C"]}
     * </ul>
     *
     * <p>The result is guaranteed to be in the "traditional", lexicographical order for Cartesian
     * products that you would get from nesting for loops:
     *
     * <pre>{@code
     * for (B b0 : lists.get(0)) {
     *   for (B b1 : lists.get(1)) {
     *     ...
     *   }
     * }
     * }</pre>
     *
     * <p>Note: the following describes the original behaviour in Guava.
     * This version has been modified, so that an empty list is not supported.
     * <p><strike>Note that if any input list is empty, the Cartesian product will also be empty. If no lists
     * at all are provided (an empty list), the resulting Cartesian product has one element, an empty
     * list (counter-intuitive, but mathematically consistent).</strike>
     *
     * <p><i>Performance notes:</i> while the cartesian product of lists of size {@code m, n, p} is a
     * list of size {@code m x n x p}, its actual memory consumption is much smaller. When the
     * cartesian product is constructed, the input lists are merely copied. Only as the resulting list
     * is iterated are the individual lists created, and these are not retained after iteration.
     *
     * @param lists the lists to choose elements from, in the order that the elements chosen from
     *              those lists should appear in the resulting lists
     * @param <E>   any common base class shared by all axes (often just {@link Object})
     * @return the Cartesian product, as a list containing lists
     * @throws IllegalArgumentException if the size of the cartesian product would be greater than
     *                                  {@link Integer#MAX_VALUE}
     * @throws NullPointerException     if {@code lists}, any one of the {@code lists}, or any element of
     *                                  a provided list is null
     */
    public static <E> List<List<E>> create(List<? extends List<? extends E>> lists) {
        List<List<E>> axesBuilder = new ArrayList<>(lists.size());

        for (List<? extends E> list : lists) {
            List<E> copy = new ArrayList<>(list);
            Verify.isFalse(copy.isEmpty(), "Input list must not be empty");
            axesBuilder.add(copy);
        }
        return new CartesianList<>(axesBuilder);
    }

    private CartesianList(List<List<E>> axes) {
        this.axes = axes;
        int[] axesSize = new int[axes.size() + 1];
        axesSize[axes.size()] = 1;
        try {
            for (int i = axes.size() - 1; i >= 0; i--) {
                axesSize[i] = checkedMultiply(axesSize[i + 1], axes.get(i).size());
            }
        } catch (ArithmeticException ex) {
            throw Fail.withUsageError(
                    "Cartesian product too large; must have size at most Integer.MAX_VALUE", ex);
        }
        this.axesSizeProduct = axesSize;
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> get(int index) {
        return new ArrayList<E>() {
            @Override
            public int size() {
                return axes.size();
            }

            @Override
            public E get(int axis) {
                int axisIndex = getAxisIndexForProductIndex(index, axis);
                return axes.get(axis).get(axisIndex);
            }
        };
    }

    @Override
    public int size() {
        return axesSizeProduct[0];
    }

    @Override
    public boolean contains(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the product of {@code a} and {@code b}, provided it does not overflow.
     *
     * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
     */
    private static int checkedMultiply(int a, int b) {
        long result = (long) a * b;
        checkNoOverflow(result == (int) result, "checkedMultiply", a, b);
        return (int) result;
    }

    private static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
        if (!condition) {
            throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")");
        }
    }
}
