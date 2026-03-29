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
package org.instancio.protobuf;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.protobuf.generator.specs.ProtoDurationGeneratorSpec;
import org.instancio.protobuf.generator.specs.ProtoTimestampGeneratorSpec;
import org.instancio.protobuf.internal.generator.ProtoDurationGenerator;
import org.instancio.protobuf.internal.generator.ProtoTimestampGenerator;

/**
 * Provides access to generators for Protobuf classes.
 *
 * @since 6.0.0
 */
@ExperimentalApi
public final class GenProto {

    /**
     * Generator spec for {@link com.google.protobuf.Timestamp} values.
     *
     * @return generator spec
     * @since 6.0.0
     */
    @ExperimentalApi
    public static ProtoTimestampGeneratorSpec timestamp() {
        return new ProtoTimestampGenerator();
    }

    /**
     * Generator spec for {@link com.google.protobuf.Duration} values.
     *
     * @return generator spec
     * @since 6.0.0
     */
    @ExperimentalApi
    public static ProtoDurationGeneratorSpec duration() {
        return new ProtoDurationGenerator();
    }

    private GenProto() {
        // non-instantiable
    }
}
