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
package org.instancio.internal.generator;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.Hints;
import org.instancio.generator.specs.SubtypeGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.lang.EnumGenerator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioSpiException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.instancio.internal.generator.GeneratorUtil.instantiateInternalGenerator;
import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

public class GeneratorResolver {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorResolver.class);

    private final GeneratorContext context;
    private final Generators generators;
    private final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries;
    private final AfterGenerate afterGenerate;

    public GeneratorResolver(
            final GeneratorContext context,
            final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries) {

        this.context = context;
        this.generators = new Generators(context);
        this.afterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.providerEntries = providerEntries;
    }

    private static Generator<?> loadByClassName(
            final GeneratorContext context,
            final String generatorClassName) {

        final Class<?> generatorClass = ReflectionUtils.loadClass(generatorClassName);
        return instantiateInternalGenerator(generatorClass, context);
    }

    @SuppressWarnings("all")
    public Optional<Generator<?>> get(final InternalNode node) {
        final Class<?> klass = node.getTargetClass();

        // Generators provided by SPI take precedence over built-in generators
        final Generator<?> spiGenerator = getSpiGenerator(node);
        if (spiGenerator != null) {
            return Optional.of(spiGenerator);
        }

        Generator<?> generator = getBuiltInGenerator(klass);

        if (generator == null) {
            if (klass.isArray()) {
                generator = new ArrayGenerator<>(context, klass);
            } else if (klass.isEnum()) {
                generator = new EnumGenerator(context, klass);
            } else {
                generator = getGeneratorForLegacyClass(klass);
            }
        }
        return Optional.ofNullable(generator);
    }

    /**
     * Java modules will not have these legacy classes unless explicitly
     * imported via e.g. "requires java.sql". In case the classes are not
     * available, load them by name to avoid class not found error
     */
    @Nullable
    @SuppressWarnings(Sonar.USE_INSTANCEOF)
    private Generator<?> getGeneratorForLegacyClass(final Class<?> klass) {
        if ("java.sql.Date".equals(klass.getName())) {
            return loadByClassName(context, "org.instancio.internal.generator.sql.SqlDateGenerator");
        }
        if ("java.sql.Timestamp".equals(klass.getName())) {
            return loadByClassName(context, "org.instancio.internal.generator.sql.TimestampGenerator");
        }
        if ("javax.xml.datatype.XMLGregorianCalendar".equals(klass.getName())) {
            return loadByClassName(context, "org.instancio.internal.generator.xml.XMLGregorianCalendarGenerator");
        }
        return null;
    }

    private Generator<?> getBuiltInGenerator(final Class<?> targetClass) {
        final Class<?> genClass = GeneratorResolverMaps.getGenerator(targetClass);
        if (genClass == null) {
            return null;
        }

        final Generator<?> generator = instantiateInternalGenerator(genClass, context);
        if (generator instanceof SubtypeGeneratorSpec<?>) {
            final Class<?> subtype = GeneratorResolverMaps.getSubtype(targetClass);
            if (subtype != null) {
                ((SubtypeGeneratorSpec<?>) generator).subtype(subtype);
            }
        }
        return generator;
    }

    @SuppressWarnings({"PMD.AvoidBranchingStatementAsLastInLoop", Sonar.GENERIC_WILDCARD_IN_RETURN})
    private Generator<?> getSpiGenerator(final InternalNode node) {
        for (ProviderEntry<InstancioServiceProvider.GeneratorProvider> entry : providerEntries) {
            final GeneratorSpec<?> spec = entry.getProvider().getGenerator(node, generators);

            if (spec != null) {
                validateSpec(entry, spec);
                LOG.trace("Custom generator '{}' found for {}", spec.getClass().getName(), node);

                final Generator<?> generator = processGenerator((Generator<?>) spec, node);
                generator.init(context);
                return GeneratorDecorator.decorateIfNullAfterGenerate(generator, afterGenerate);
            }
        }
        return null;
    }

    private static void validateSpec(
            final ProviderEntry<InstancioServiceProvider.GeneratorProvider> entry,
            final GeneratorSpec<?> spec) {

        if (spec instanceof Generator<?>) {
            return;
        }
        throw new InstancioSpiException(String.format("The GeneratorSpec %s returned by %s must implement %s",
                spec.getClass(), entry.getInstancioProviderClass(), Generator.class));
    }

    /**
     * Follows almost the same logic as {@code UserSuppliedGeneratorProcessor}
     * with some minor differences. Ideally, this should be refactored.
     */
    // TODO refactor
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> processGenerator(final Generator<?> generator, final InternalNode node) {

        if (generator instanceof ArrayGenerator) {
            ((ArrayGenerator<?>) generator).subtype(node.getTargetClass());
        } else if (generator instanceof AbstractGenerator) {
            final AbstractGenerator<?> g = (AbstractGenerator<?>) generator;

            if (!g.isDelegating()) {
                return g;
            }

            final Hints hints = generator.hints();
            final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);
            final Generator<?> delegate = getBuiltInGenerator(defaultIfNull(internalHint.targetClass(), node.getTargetClass()));
            if (delegate == null) {
                return generator;
            }

            if (delegate instanceof AbstractGenerator<?>) {
                final boolean nullable = ((AbstractGenerator<?>) generator).isNullable();
                ((AbstractGenerator<?>) delegate).nullable(nullable);
            }
            return GeneratorDecorator.replaceHints(delegate, hints);
        }

        return generator;
    }

}
