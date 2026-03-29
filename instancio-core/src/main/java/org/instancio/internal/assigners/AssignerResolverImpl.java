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
package org.instancio.internal.assigners;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProvider.InternalAssignerSettingsProvider;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AssignerResolverImpl implements AssignerResolver {

    private final ModelContext context;
    private final Assigner primaryAssigner;
    private final SetterMethodResolverFacade setterMethodResolverFacade;
    private final List<InternalAssignerSettingsProvider> assignerSettingsProviders;
    private final Map<Class<?>, Assigner> assignerByTargetClass = new HashMap<>();

    AssignerResolverImpl(final ModelContext context) {
        this.context = context;
        this.setterMethodResolverFacade = new SetterMethodResolverFacade(
                context.getServiceProviders().getSetterMethodResolvers());
        this.primaryAssigner = resolvePrimaryAssigner();
        this.assignerSettingsProviders = getAssignerSettingsProviders(
                context.getInternalServiceProviders());
    }

    @Override
    public Assigner resolve(final GeneratorResult generatorResult) {
        final Object value = generatorResult.getValue();
        if (value == null || assignerSettingsProviders.isEmpty()) {
            return primaryAssigner;
        }
        final Class<?> targetClass = value.getClass();
        return assignerByTargetClass.computeIfAbsent(targetClass, this::createAssignerForClass);
    }

    private static List<InternalAssignerSettingsProvider> getAssignerSettingsProviders(
            final List<InternalServiceProvider> internalServiceProviders) {

        final List<InternalAssignerSettingsProvider> providers = new ArrayList<>(
                internalServiceProviders.size());

        for (InternalServiceProvider p : internalServiceProviders) {
            final InternalAssignerSettingsProvider provider = p.getAssignerSettingsProvider();
            if (provider != null) {
                providers.add(provider);
            }
        }
        return Collections.unmodifiableList(providers);
    }

    private Assigner resolvePrimaryAssigner() {
        final Settings settings = context.getSettings();
        final AssignmentType assignment = settings.get(Keys.ASSIGNMENT_TYPE);

        return switch (assignment) {
            case FIELD -> new FieldAssigner(settings);
            case METHOD -> new MethodAssigner(settings, setterMethodResolverFacade);
        };
    }

    private Assigner createAssignerForClass(final Class<?> targetClass) {
        for (InternalAssignerSettingsProvider provider : assignerSettingsProviders) {
            final Settings overrides = provider.getAssignerSettings(targetClass);
            if (overrides != null) {
                final Settings effectiveSettings = context.getSettings().merge(overrides).lock();
                return new MethodAssigner(effectiveSettings, setterMethodResolverFacade);
            }
        }
        return primaryAssigner;
    }
}
