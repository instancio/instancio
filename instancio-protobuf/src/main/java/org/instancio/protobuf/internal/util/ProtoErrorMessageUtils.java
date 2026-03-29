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
package org.instancio.protobuf.internal.util;

import org.instancio.settings.SettingKey;

import java.util.Locale;

public final class ProtoErrorMessageUtils {

    private ProtoErrorMessageUtils() {
        // non-instantiable
    }

    public static String unsignedFieldNegativeMax(
            final SettingKey<?> key,
            final Number value,
            final String protoType) {

        final String keyName = keyDesc(key);

        return """
                invalid Settings configuration for an unsigned Protobuf type
                
                 -> Protobuf type: %s (non-negative values only)
                
                This error was thrown because:
                
                 -> Protobuf unsigned types store only non-negative values
                 -> %s ('%s') is set to %s, therefore no valid value can be generated
                
                To resolve this error:
                
                 -> Set %s to a non-negative value (>= 0)
                 -> Or remove the %s override to use the Instancio default
                """.formatted(protoType, keyName, key.propertyKey(), value, keyName, keyName);
    }

    private static String keyDesc(final SettingKey<?> key) {
        return "Keys." + key.propertyKey().replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
