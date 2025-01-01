/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.support.pojo.basic;

import lombok.Data;

@Data
public class UnsupportedJdkTypes {

    private java.lang.reflect.Field field;
    private java.net.Inet6Address inet6Address;
    private java.net.InetSocketAddress inetSocketAddress;
    private java.util.concurrent.CompletableFuture<String> completableFuture;
    private java.util.concurrent.Semaphore semaphore;
    private java.util.concurrent.atomic.AtomicIntegerArray atomicIntegerArray;
    private java.util.concurrent.atomic.AtomicLongArray atomicLongArray;
    private java.util.concurrent.atomic.AtomicMarkableReference<String> atomicMarkableReference;
    private java.util.concurrent.atomic.AtomicReference<String> atomicReference;
    private java.util.concurrent.atomic.AtomicReferenceArray<String> atomicReferenceArray;
    private java.util.concurrent.atomic.AtomicStampedReference<String> atomicStampedReference;

}
