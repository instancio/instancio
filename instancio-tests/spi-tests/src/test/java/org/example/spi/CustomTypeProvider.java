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
package org.example.spi;

import org.instancio.spi.InstancioServiceProvider;
import org.instancio.test.support.pojo.basic.BooleanHolder;
import org.instancio.test.support.pojo.basic.CharacterHolder;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.MultipleInterfaceImpls;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;

import java.util.Map;

public class CustomTypeProvider implements InstancioServiceProvider {
    public static final MultipleInterfaceImpls.WidgetB WIDGET = new MultipleInterfaceImpls.WidgetB();

    public static final long LONG_HOLDER_INITIAL_VALUE = -1;

    // Initial value should be overwritten
    public static final LongHolder LONG_HOLDER = LongHolder.builder()
            .primitive(LONG_HOLDER_INITIAL_VALUE)
            .wrapper(LONG_HOLDER_INITIAL_VALUE)
            .build();

    private static final Map<Class<?>, Class<?>> SUBTYPE_MAP = Map.of(
            StringHolderInterface.class, StringHolderAlternativeImpl.class,
            ItemInterface.class, ItemAlternativeImpl.class,

            // Note: although Widget implementation is instantiated via custom TypeInstantiator,
            // the subtype mapping is still required to populate the Node structure
            MultipleInterfaceImpls.Widget.class, MultipleInterfaceImpls.WidgetB.class,

            // Error handling: invalid subtype
            BaseClassSubClassInheritance.SubClass.class, BaseClassSubClassInheritance.BaseClass.class
    );

    private boolean getTypeInstantiatorInvoked;
    private boolean getTypeResolverInvoked;

    private static class CustomTypeInstantiator implements TypeInstantiator {
        @Override
        public Object instantiate(final Class<?> type) {
            if (MultipleInterfaceImpls.Widget.class.isAssignableFrom(type)) {
                return WIDGET;
            }
            if (type == LongHolder.class) {
                return LONG_HOLDER;
            }
            if (type == BooleanHolder.class) {
                // wrong type returned to verify error-handling
                return new CharacterHolder();
            }
            return null;
        }
    }

    @Override
    public TypeResolver getTypeResolver() {
        if (getTypeResolverInvoked) {
            throw new AssertionError("getTypeResolver should not be invoked more than once!");
        }

        getTypeResolverInvoked = true;
        return SUBTYPE_MAP::get;
    }

    @Override
    public TypeInstantiator getTypeInstantiator() {
        if (getTypeInstantiatorInvoked) {
            throw new AssertionError("getTypeInstantiator should not be invoked more than once!");
        }

        getTypeInstantiatorInvoked = true;
        return new CustomTypeInstantiator();
    }
}