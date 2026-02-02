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
package org.instancio.test.support.pojo.interfaces;

/**
 * This interface is used for verifying subtypes defined via {@code instancio.properties}.
 *
 * <p>This module's properties file has the following subtype mapping
 * which should be picked up automatically when creating an instance
 * of {@code PropertiesInterface}:
 *
 * <pre>{@code
 * subtype.org.instancio.test.support.pojo.interfaces.PropertiesInterface=\
 *   org.instancio.test.support.pojo.interfaces.PropertiesInterfaceImpl
 * }</pre>
 */
public interface PropertiesInterface {

    String getValue();
}
