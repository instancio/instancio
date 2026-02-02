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
package org.instancio.test.support.pojo.dynamic;

import java.util.List;

public class DynAddress extends DynPojoBase {

    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String PHONE_NUMBERS = "phoneNumbers";

    public String getAddress() {
        return get(ADDRESS);
    }

    public void setAddress(final String address) {
        set(ADDRESS, address);
    }

    public String getCity() {
        return get(CITY);
    }

    public void setCity(final String city) {
        set(CITY, city);
    }

    public String getCountry() {
        return get(COUNTRY);
    }

    public void setCountry(final String country) {
        set(COUNTRY, country);
    }

    public List<DynPhone> getPhoneNumbers() {
        return get(PHONE_NUMBERS);
    }

    public void setPhoneNumbers(final List<DynPhone> phoneNumbers) {
        set(PHONE_NUMBERS, phoneNumbers);
    }
}