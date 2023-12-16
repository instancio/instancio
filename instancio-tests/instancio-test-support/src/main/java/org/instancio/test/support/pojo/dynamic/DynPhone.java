/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.support.pojo.dynamic;

public class DynPhone extends DynPojoBase {

    private static final String COUNTRY_CODE = "countryCode";
    private static final String NUMBER = "number";

    public String getCountryCode() {
        return get(COUNTRY_CODE);
    }

    public void setCountryCode(final String countryCode) {
        set(COUNTRY_CODE, countryCode);
    }

    public String getNumber() {
        return get(NUMBER);
    }

    public void setNumber(final String number) {
        set(NUMBER, number);
    }

    @Override
    public String toString() {
        return String.format("%s[countryCode=%s, number=%s]",
                getClass().getSimpleName(), getCountryCode(), getNumber());
    }
}