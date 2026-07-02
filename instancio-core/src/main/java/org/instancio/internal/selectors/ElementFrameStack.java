/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.selectors;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.nodes.InternalNode;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

@InternalApi
public interface ElementFrameStack {

    /**
     * Describes the array/collection element currently being generated.
     * The <i>active frame</i> is the frame on top of the
     * {@link ElementFrameStack} at the moment a selector is being matched.
     *
     * @param containerNode the array/collection node whose element is being built
     * @param index         the element's position
     * @param containerSize the size of the array/collection
     */
    record Frame(InternalNode containerNode, int index, int containerSize) {}

    void push(InternalNode containerNode, int index, int containerSize);

    default void push(Frame frame) {
        push(frame.containerNode(), frame.index(), frame.containerSize());
    }

    void pop();

    @Nullable
    Frame peek();

    static ElementFrameStack create() {
        return new ElementFrameStackImpl();
    }

    final class ElementFrameStackImpl implements ElementFrameStack {

        private final Deque<Frame> stack = new ArrayDeque<>();

        @Override
        public void push(final InternalNode containerNode, final int index, final int containerSize) {
            stack.push(new Frame(containerNode, index, containerSize));
        }

        @Override
        public void pop() {
            stack.pop();
        }

        @Nullable
        @Override
        public Frame peek() {
            return stack.isEmpty() ? null : stack.peek();
        }
    }

    ElementFrameStack NOOP = new ElementFrameStack() {
        //@formatter:off
        @Override public void push(InternalNode containerNode, int index, int containerSize) {}
        @Override public void pop() {}
        @Override public @Nullable Frame peek() { return null; }
        //@formatter:on
    };
}
