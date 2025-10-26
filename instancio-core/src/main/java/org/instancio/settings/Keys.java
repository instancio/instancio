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
package org.instancio.settings;

import org.instancio.FilterPredicate;
import org.instancio.InstancioApi;
import org.instancio.InstancioObjectApi;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.feed.Feed;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.settings.InternalKey;
import org.instancio.internal.settings.RangeAdjuster;
import org.instancio.settings.SettingKey.SettingKeyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.util.Constants.MAX_SIZE;
import static org.instancio.internal.util.Constants.MIN_SIZE;
import static org.instancio.internal.util.Constants.NUMERIC_MAX;
import static org.instancio.internal.util.Constants.NUMERIC_MIN;

/**
 * Defines all keys supported by Instancio.
 *
 * @see SettingKey
 * @see Settings
 * @since 1.1.10
 */
public final class Keys {

    private static final RangeAdjuster MIN_ADJUSTER = RangeAdjuster.MIN_ADJUSTER;
    private static final RangeAdjuster MAX_ADJUSTER = RangeAdjuster.MAX_ADJUSTER;
    private static final List<SettingKey<Object>> ALL_KEYS = new ArrayList<>();

    /**
     * Specifies whether to assign values using fields or methods;
     * default is {@link AssignmentType#FIELD}; property name {@code assignment.type}.
     *
     * <p>This setting does not apply to fields that are {@code final} and
     * {@code record} classes, since those cannot have setters.
     *
     * @see AssignmentType
     * @see #ON_SET_METHOD_ERROR
     * @see #ON_SET_METHOD_NOT_FOUND
     * @see #SETTER_EXCLUDE_MODIFIER
     * @see #SETTER_STYLE
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<AssignmentType> ASSIGNMENT_TYPE = registerRequiredNonAdjustable(
            "assignment.type", AssignmentType.class, AssignmentType.FIELD);

    /**
     * Specifies the default value of the {@link AfterGenerate} hint
     * supplied from custom generators to the engine;
     * default is {@link AfterGenerate#POPULATE_NULLS_AND_DEFAULT_PRIMITIVES};
     * property name {@code hint.after.generate}.
     *
     * @see AfterGenerate
     * @since 2.0.0
     */
    public static final SettingKey<AfterGenerate> AFTER_GENERATE_HINT = registerRequiredNonAdjustable(
            "hint.after.generate", AfterGenerate.class, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

    /**
     * Specifies whether a {@code null} can be generated for array elements;
     * default is {@code false}; property name {@code array.elements.nullable}.
     */
    public static final SettingKey<Boolean> ARRAY_ELEMENTS_NULLABLE = registerRequiredNonAdjustable(
            "array.elements.nullable", Boolean.class, false);

    /**
     * Specifies minimum length for arrays;
     * default is 2; property name {@code array.min.length}.
     */
    public static final SettingKey<Integer> ARRAY_MIN_LENGTH = registerRequiredAdjustable(
            "array.min.length", Integer.class, MIN_SIZE, MIN_ADJUSTER, false);

    /**
     * Specifies maximum length for arrays;
     * default is 6; property name {@code array.max.length}.
     */
    public static final SettingKey<Integer> ARRAY_MAX_LENGTH = registerRequiredAdjustable(
            "array.max.length", Integer.class, MAX_SIZE, MAX_ADJUSTER, false);

    /**
     * Specifies whether a null can be generated for arrays;
     * default is {@code false}; property name {@code array.nullable}.
     */
    public static final SettingKey<Boolean> ARRAY_NULLABLE = registerRequiredNonAdjustable(
            "array.nullable", Boolean.class, false);

    /**
     * Specifies whether values should be generated based on
     * <a href="https://beanvalidation.org/3.0/">Jakarta Bean Validation 3.0</a>
     * annotations, if present;
     * default is {@code false}; property name {@code bean.validation.api.enabled}.
     *
     * @see #BEAN_VALIDATION_TARGET
     * @since 2.7.0
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> BEAN_VALIDATION_ENABLED = registerRequiredNonAdjustable(
            "bean.validation.enabled", Boolean.class, false);

    /**
     * Specifies whether Bean Validation annotations are declared on fields or getters;
     * default is {@link BeanValidationTarget#FIELD}; property name {@code bean.validation.target}.
     *
     * @see #BEAN_VALIDATION_ENABLED
     * @since 3.4.0
     */
    @ExperimentalApi
    public static final SettingKey<BeanValidationTarget> BEAN_VALIDATION_TARGET = registerRequiredNonAdjustable(
            "bean.validation.target", BeanValidationTarget.class, BeanValidationTarget.FIELD);

    /**
     * Specifies the {@code scale} for generating {@code BigDecimal};
     * default is {@code 2}; property name {@code bigdecimal.scale}.
     *
     * @since 3.3.0
     */
    @ExperimentalApi
    public static final SettingKey<Integer> BIG_DECIMAL_SCALE = registerRequiredNonAdjustable(
            "bigdecimal.scale", Integer.class, 2);

    /**
     * Specifies whether a {@code null} can be generated for Boolean type;
     * default is {@code false}; property name {@code boolean.nullable}.
     */
    public static final SettingKey<Boolean> BOOLEAN_NULLABLE = registerRequiredNonAdjustable(
            "boolean.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for bytes;
     * default is 1; property name {@code byte.min}.
     */
    public static final SettingKey<Byte> BYTE_MIN = registerRequiredAdjustable(
            "byte.min", Byte.class, (byte) NUMERIC_MIN, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for bytes;
     * default is 127; property name {@code byte.max}.
     */
    public static final SettingKey<Byte> BYTE_MAX = registerRequiredAdjustable(
            "byte.max", Byte.class, (byte) 127, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Byte type;
     * default is {@code false}; property name {@code byte.nullable}.
     */
    public static final SettingKey<Boolean> BYTE_NULLABLE = registerRequiredNonAdjustable(
            "byte.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for Character type;
     * default is {@code false}; property name {@code character.nullable}.
     */
    public static final SettingKey<Boolean> CHARACTER_NULLABLE = registerRequiredNonAdjustable(
            "character.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for collection elements;
     * default is {@code false}; property name {@code collection.elements.nullable}.
     */
    public static final SettingKey<Boolean> COLLECTION_ELEMENTS_NULLABLE = registerRequiredNonAdjustable(
            "collection.elements.nullable", Boolean.class, false);

    /**
     * Specifies minimum size for collections;
     * default is 2; property name {@code collection.min.size}.
     */
    public static final SettingKey<Integer> COLLECTION_MIN_SIZE = registerRequiredAdjustable(
            "collection.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER, false);

    /**
     * Specifies maximum size for collections;
     * default is 6; property name {@code collection.max.size}.
     */
    public static final SettingKey<Integer> COLLECTION_MAX_SIZE = registerRequiredAdjustable(
            "collection.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER, false);

    /**
     * Specifies whether a {@code null} can be generated for collections;
     * default is {@code false}; property name {@code collection.nullable}.
     */
    public static final SettingKey<Boolean> COLLECTION_NULLABLE = registerRequiredNonAdjustable(
            "collection.nullable", Boolean.class, false);

    /**
     * Specifies minimum value for doubles;
     * default is 1; property name {@code double.min}.
     */
    public static final SettingKey<Double> DOUBLE_MIN = registerRequiredAdjustable(
            "double.min", Double.class, (double) NUMERIC_MIN, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for doubles;
     * default is 10000; property name {@code double.max}.
     */
    public static final SettingKey<Double> DOUBLE_MAX = registerRequiredAdjustable(
            "double.max", Double.class, (double) NUMERIC_MAX, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Double type;
     * default is {@code false}; property name {@code double.nullable}.
     */
    public static final SettingKey<Boolean> DOUBLE_NULLABLE = registerRequiredNonAdjustable(
            "double.nullable", Boolean.class, false);

    /**
     * Specifies whether internal exceptions should be propagated up;
     * default is {@code false}; property name {@code fail.on.error}.
     *
     * @since 3.0.1
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> FAIL_ON_ERROR = registerRequiredNonAdjustable(
            "fail.on.error", Boolean.class, false);

    /**
     * Specifies what should happen if the configured {@link #MAX_DEPTH} is reached;
     * default is {@code false}; property name {@code fail.on.max.depth.reached}.
     *
     * @since 6.0.0
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> FAIL_ON_MAX_DEPTH_REACHED = registerRequiredNonAdjustable(
            "fail.on.max.depth.reached", Boolean.class, false);

    /**
     * Specifies minimum value for floats;
     * default is 1; property name {@code float.min}.
     */
    public static final SettingKey<Float> FLOAT_MIN = registerRequiredAdjustable(
            "float.min", Float.class, (float) NUMERIC_MIN, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for floats;
     * default is 10000; property name {@code float.max}.
     */
    public static final SettingKey<Float> FLOAT_MAX = registerRequiredAdjustable(
            "float.max", Float.class, (float) NUMERIC_MAX, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Float type;
     * default is {@code false}; property name {@code float.nullable}.
     */
    public static final SettingKey<Boolean> FLOAT_NULLABLE = registerRequiredNonAdjustable(
            "float.nullable", Boolean.class, false);

    /**
     * Specifies a comma-separated list of regexes for field names that
     * should be ignored; default is an empty string (no fields ignored);
     * property name {@code ignore.field.name.regexes}.
     *
     * @since 5.5.0
     */
    @ExperimentalApi
    public static final SettingKey<String> IGNORE_FIELD_NAME_REGEXES = registerRequiredNonAdjustable(
            "ignore.field.name.regexes", String.class, "");

    /**
     * Specifies the number of samples for the {@code @InstancioSource}
     * annotation from the {@code instancio-junit} module;
     * default is 100; property name {@code instancio.source.samples}.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<Integer> INSTANCIO_SOURCE_SAMPLES = registerRequiredNonAdjustable(
            "instancio.source.samples", Integer.class, 100);

    /**
     * Specifies minimum value for integers;
     * default is 1; property name {@code integer.min}.
     */
    public static final SettingKey<Integer> INTEGER_MIN = registerRequiredAdjustable(
            "integer.min", Integer.class, NUMERIC_MIN, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for integers;
     * default is 10000; property name {@code integer.max}.
     */
    public static final SettingKey<Integer> INTEGER_MAX = registerRequiredAdjustable(
            "integer.max", Integer.class, NUMERIC_MAX, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Integer type;
     * default is {@code false}; property name {@code integer.nullable}.
     */
    public static final SettingKey<Boolean> INTEGER_NULLABLE = registerRequiredNonAdjustable(
            "integer.nullable", Boolean.class, false);

    /**
     * Specifies whether values should be generated based on
     * JPA annotations, such as {@code @Column.length};
     * default is {@code false}; property name {@code jpa.enabled}.
     *
     * @since 3.3.0
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> JPA_ENABLED = registerRequiredNonAdjustable(
            "jpa.enabled", Boolean.class, false);

    /**
     * Specifies minimum value for longs;
     * default is 1; property name {@code long.min}.
     */
    public static final SettingKey<Long> LONG_MIN = registerRequiredAdjustable(
            "long.min", Long.class, (long) NUMERIC_MIN, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for longs;
     * default is 10000; property name {@code long.max}.
     */
    public static final SettingKey<Long> LONG_MAX = registerRequiredAdjustable(
            "long.max", Long.class, (long) NUMERIC_MAX, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Long type;
     * default is {@code false}; property name {@code long.nullable}.
     */
    public static final SettingKey<Boolean> LONG_NULLABLE = registerRequiredNonAdjustable(
            "long.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for map keys;
     * default is {@code false}; property name {@code map.keys.nullable}.
     */
    public static final SettingKey<Boolean> MAP_KEYS_NULLABLE = registerRequiredNonAdjustable(
            "map.keys.nullable", Boolean.class, false);

    /**
     * Specifies minimum size for maps;
     * default is 2; property name {@code map.min.size}.
     */
    public static final SettingKey<Integer> MAP_MIN_SIZE = registerRequiredAdjustable(
            "map.min.size", Integer.class, MIN_SIZE, MIN_ADJUSTER, false);

    /**
     * Specifies maximum size for maps;
     * default is 6; property name {@code map.max.size}.
     */
    public static final SettingKey<Integer> MAP_MAX_SIZE = registerRequiredAdjustable(
            "map.max.size", Integer.class, MAX_SIZE, MAX_ADJUSTER, false);

    /**
     * Specifies whether a {@code null} can be generated for maps;
     * default is {@code false}; property name {@code map.nullable}.
     */
    public static final SettingKey<Boolean> MAP_NULLABLE = registerRequiredNonAdjustable(
            "map.nullable", Boolean.class, false);

    /**
     * Specifies whether a {@code null} can be generated for map values;
     * default is {@code false}; property name {@code map.values.nullable}.
     */
    public static final SettingKey<Boolean> MAP_VALUES_NULLABLE = registerRequiredNonAdjustable(
            "map.values.nullable", Boolean.class, false);

    /**
     * Specifies the maximum depth of the generated object tree;
     * default is {@code 8}; property name {@code max.depth}.
     *
     * @since 2.7.0
     */
    public static final SettingKey<Integer> MAX_DEPTH = registerRequiredNonAdjustable(
            "max.depth", Integer.class, 8);

    /**
     * The maximum number of attempts to generate an object for a given node;
     * default is {@code 1000}; property name {@code max.generation.attempts}.
     *
     * <p>This setting applicable to:
     *
     * <ul>
     *   <li>{@link InstancioApi#filter(TargetSelector, FilterPredicate)} method</li>
     *   <li>when generating values for hash-based collection</li>
     * </ul>
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<Integer> MAX_GENERATION_ATTEMPTS = registerRequiredNonAdjustable(
            "max.generation.attempts", Integer.class, 1000);

    /**
     * Specifies the mode: strict (unused selectors will trigger an exception) or lenient;
     * default is {@link Mode#STRICT}; property name {@code mode}.
     *
     * @see Mode
     * @since 1.3.3
     */
    public static final SettingKey<Mode> MODE = registerRequiredNonAdjustable("mode", Mode.class, Mode.STRICT);

    /**
     * Specifies what should happen if a feed property is unmatched when using the {@code applyFeed()} method;
     * default is {@link OnFeedPropertyUnmatched#FAIL}; property name {@code on.feed.property.unmatched}.
     *
     * @see OnFeedPropertyUnmatched
     * @since 5.2.0
     */
    @ExperimentalApi
    public static final SettingKey<OnFeedPropertyUnmatched> ON_FEED_PROPERTY_UNMATCHED = registerRequiredNonAdjustable(
            "on.feed.property.unmatched", OnFeedPropertyUnmatched.class, OnFeedPropertyUnmatched.FAIL);

    /**
     * Specifies what should happen if an error occurs setting a field's value;
     * default is {@link OnSetFieldError#IGNORE}; property name {@code on.set.field.error}.
     *
     * <p><b>Warning:</b> an error caused by assigning an incompatible type is
     * considered a user error and is never ignored, despite this setting.
     *
     * @see OnSetFieldError
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetFieldError> ON_SET_FIELD_ERROR = registerRequiredNonAdjustable(
            "on.set.field.error", OnSetFieldError.class, OnSetFieldError.IGNORE);

    /**
     * Specifies what should happen if an error occurs invoking a setter;
     * default is {@link OnSetMethodError#ASSIGN_FIELD}; property name {@code on.set.method.error}.
     *
     * @see OnSetMethodError
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetMethodError> ON_SET_METHOD_ERROR = registerRequiredNonAdjustable(
            "on.set.method.error", OnSetMethodError.class, OnSetMethodError.ASSIGN_FIELD);

    /**
     * Specifies what should happen if a setter method for a field cannot be resolved;
     * default is {@link OnSetMethodNotFound#ASSIGN_FIELD}; property name {@code on.set.method.not.found}.
     *
     * <p>Warning: {@link OnSetMethodNotFound#FAIL} is not applied to {@code final} fields
     * since a field declared as {@code final} cannot have a setter.
     *
     * @see OnSetMethodNotFound
     * @see #ON_SET_METHOD_UNMATCHED
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetMethodNotFound> ON_SET_METHOD_NOT_FOUND = registerRequiredNonAdjustable(
            "on.set.method.not.found", OnSetMethodNotFound.class, OnSetMethodNotFound.ASSIGN_FIELD);

    /**
     * Specifies what should happen if a setter without a matching field is encountered;
     * default is {@link OnSetMethodUnmatched#IGNORE}; property name {@code on.set.method.unmatched}.
     *
     * <p>This setting is only applicable if {@link #ASSIGNMENT_TYPE}
     * is set to {@link AssignmentType#METHOD}.
     *
     * <p>The matching of fields and setter methods is based on the configured
     * {@link #SETTER_STYLE} setting.
     *
     * @see OnSetMethodUnmatched
     * @see #ASSIGNMENT_TYPE
     * @see #ON_SET_METHOD_NOT_FOUND
     * @see #SETTER_STYLE
     * @since 4.0.0
     */
    @ExperimentalApi
    public static final SettingKey<OnSetMethodUnmatched> ON_SET_METHOD_UNMATCHED = registerRequiredNonAdjustable(
            "on.set.method.unmatched", OnSetMethodUnmatched.class, OnSetMethodUnmatched.IGNORE);

    /**
     * Specifies whether initialised fields can be overwritten by the engine to random values;
     * default is {@code true}; property name {@code overwrite.existing.values}.
     *
     * <p>If this setting is set to {@code false}, then initialised values
     * will not be overwritten by the engine, but they can still be
     * overwritten via the API using a selector.
     *
     * <p>Below are a few examples based on the following class:
     *
     * <pre>{@code
     * class Foo {
     *     String value = "initial";
     * }
     * }</pre>
     * <br/>
     *
     * <p><b>Example 1:</b> initialised value is overwritten with a random value
     * because by default {@code OVERWRITE_EXISTING_VALUES} is {@code true}.
     *
     * <pre>{@code
     * // Sample output: Foo[value=VEQHJ]
     * Foo foo = Instancio.create(Foo.class);
     * }</pre>
     * <br/>
     *
     * <p><b>Example 2:</b> initialised value is preserved when
     * {@code OVERWRITE_EXISTING_VALUES} is {@code false}.
     *
     * <pre>{@code
     * // Output: Foo[value=initial]
     * Foo foo = Instancio.of(Foo.class)
     *     .set(Keys.OVERWRITE_EXISTING_VALUES, false)
     *     .create();
     * }</pre>
     * <br/>
     *
     * <p><b>Example 3:</b> initialised value can be overwritten using a selector
     * regardless of the {@code OVERWRITE_EXISTING_VALUES} setting.
     *
     * <pre>{@code
     * // Output: Foo[value=Hello]
     * Foo foo = Instancio.of(Foo.class)
     *     .set(Keys.OVERWRITE_EXISTING_VALUES, false)
     *     .set(field(Foo::getValue), "Hello")
     *     .create();
     * }</pre>
     *
     * @since 2.0.0
     */
    public static final SettingKey<Boolean> OVERWRITE_EXISTING_VALUES = registerRequiredNonAdjustable(
            "overwrite.existing.values", Boolean.class, true);

    /**
     * Specifies whether {@link Feed} data is retrieved sequentially or randomly;
     * default is {@link FeedDataAccess#SEQUENTIAL}; property name {@code feed.data.access}.
     *
     * @see FeedDataAccess
     * @see #FEED_DATA_END_ACTION
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<FeedDataAccess> FEED_DATA_ACCESS = registerRequiredNonAdjustable(
            "feed.data.access", FeedDataAccess.class, FeedDataAccess.SEQUENTIAL);

    /**
     * Specifies the behaviour when {@link Feed} end of data has been reached
     * (only applicable if {@link #FEED_DATA_ACCESS} is set to {@link FeedDataAccess#SEQUENTIAL});
     * default is {@link FeedDataEndAction#FAIL}; property name {@code feed.data.end.action}.
     *
     * @see FeedDataEndAction
     * @see #FEED_DATA_ACCESS
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<FeedDataEndAction> FEED_DATA_END_ACTION = registerRequiredNonAdjustable(
            "feed.data.end.action", FeedDataEndAction.class, FeedDataEndAction.FAIL);

    /**
     * Specifies the trimming mode for feed data;
     * default is {@link FeedDataTrim#UNQUOTED}; property name {@code feed.data.trim}.
     *
     * @see FeedDataTrim
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<FeedDataTrim> FEED_DATA_TRIM = registerRequiredNonAdjustable(
            "feed.data.trim", FeedDataTrim.class, FeedDataTrim.UNQUOTED);

    /**
     * Specifies the feed format type;
     * default is {@link FeedFormatType#CSV}; property name {@code feed.format.type}.
     *
     * @see FeedFormatType
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<FeedFormatType> FEED_FORMAT_TYPE = registerRequiredNonAdjustable(
            "feed.format.type", FeedFormatType.class, FeedFormatType.CSV);

    /**
     * Specifies the tag key for a {@link Feed};
     * default is {@code null}; property name {@code feed.tag.key}.
     *
     * @see #FEED_TAG_VALUE
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<String> FEED_TAG_KEY = register(
            "feed.tag.key", String.class, null, null, true, false);

    /**
     * Specifies the default tag value for a {@link Feed};
     * default is {@code null} (includes all tags); property name {@code feed.tag.value}.
     *
     * @see #FEED_TAG_KEY
     * @since 5.0.0
     */
    @ExperimentalApi
    public static final SettingKey<String> FEED_TAG_VALUE = register(
            "feed.tag.value", String.class, null, null, true, false);

    /**
     * Specifies the default value for {@link FillType} which is used when
     * populating objects via the {@link InstancioObjectApi#fill()} method;
     * default is {@link FillType#POPULATE_NULLS_AND_DEFAULT_PRIMITIVES};
     * property name {@code fill.type}.
     *
     * @see FillType
     * @see InstancioObjectApi#withFillType(FillType)
     * @since 5.3.0
     */
    @ExperimentalApi
    public static final SettingKey<FillType> FILL_TYPE = registerRequiredNonAdjustable(
            "fill.type", FillType.class, FillType.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

    /**
     * Specifies the seed value;
     * default is {@code null}; property name {@code seed}.
     *
     * @since 1.5.1
     */
    public static final SettingKey<Long> SEED = register(
            "seed", Long.class, null, null, true, true);

    /**
     * Specifies whether back references should be set for cyclic classes;
     * default is {@code false} (cycles are terminated with {@code null});
     * property name {@code set.back.references}.
     *
     * <p>For example, given the following classes:
     * <pre>{@code
     * class Order {
     *     List<OrderItem> items;
     * }
     * class OrderItem {
     *     Order order;
     * }
     * }</pre>
     *
     * <p>If {@code SET_BACK_REFERENCES} is disabled,
     * creating an instance of {@code Order} would result in the
     * {@code OrderItem.order} field being {@code null}:
     *
     * <pre>{@code
     * Order order = Instancio.create(Order.class);
     *
     * assertThat(order.getItems()).allSatisfy(item ->
     *     assertThat(item.getOrder()).isNull()
     * );
     * }</pre>
     *
     * <p>If {@code SET_BACK_REFERENCES} is enabled,
     * creating an instance of {@code Order} would result in the
     * {@code OrderItem.order} field being set to the parent order:
     *
     * <pre>{@code
     * Settings settings = Settings.create().set(Keys.SET_BACK_REFERENCES, true);
     * Order order = Instancio.of(Order.class)
     *     .withSettings(settings)
     *     .create();
     *
     * assertThat(order.getItems()).allSatisfy(item ->
     *     assertThat(item.getOrder()).isSameAs(order)
     * );
     * }</pre>
     *
     * @since 3.0.0
     */
    @ExperimentalApi
    public static final SettingKey<Boolean> SET_BACK_REFERENCES = registerRequiredNonAdjustable(
            "set.back.references", Boolean.class, false);

    /**
     * Specifies modifier exclusions for setter-methods;
     * default is {@code 0} (no exclusions);
     * property name {@code setter.exclude.modifier}.
     *
     * <p>This setting can be used to control which setter methods are allowed
     * to be invoked (based on method modifiers) when {@link #ASSIGNMENT_TYPE}
     * is set to {@link AssignmentType#METHOD}). For instance, using this
     * setting, it is possible to restrict method assignment to {@code public}
     * setters only (by default, a setter is invoked even if it is {@code private}).
     *
     * <p>Multiple modifiers can be specified using logical {@code OR}
     * operator. For example, the following allows only {@code public} methods:
     *
     * <pre>{@code
     *   int exclusions = MethodModifier.PACKAGE_PRIVATE
     *                  | MethodModifier.PROTECTED
     *                  | MethodModifier.PRIVATE;
     *
     *   Settings.create().set(Keys.SETTER_EXCLUDE_MODIFIER, exclusions);
     * }</pre>
     *
     * @see #ASSIGNMENT_TYPE
     * @see MethodModifier
     * @since 2.16.0
     */
    @ExperimentalApi
    public static final SettingKey<Integer> SETTER_EXCLUDE_MODIFIER = registerRequiredNonAdjustable(
            "setter.exclude.modifier", Integer.class, 0);

    /**
     * Indicates the naming convention of setter methods to use;
     * default is {@link SetterStyle#SET}; property name {@code setter.style}.
     *
     * @see SetterStyle
     * @since 2.1.0
     */
    @ExperimentalApi
    public static final SettingKey<SetterStyle> SETTER_STYLE = registerRequiredNonAdjustable(
            "setter.style", SetterStyle.class, SetterStyle.SET);

    /**
     * Specifies minimum value for shorts;
     * default is 1; property name {@code short.min}.
     */
    public static final SettingKey<Short> SHORT_MIN = registerRequiredAdjustable(
            "short.min", Short.class, (short) 1, MIN_ADJUSTER, true);

    /**
     * Specifies maximum value for shorts;
     * default is 10000; property name {@code short.max}.
     */
    public static final SettingKey<Short> SHORT_MAX = registerRequiredAdjustable(
            "short.max", Short.class, (short) NUMERIC_MAX, MAX_ADJUSTER, true);

    /**
     * Specifies whether a {@code null} can be generated for Short type;
     * default is {@code false}; property name {@code short.nullable}.
     */
    public static final SettingKey<Boolean> SHORT_NULLABLE = registerRequiredNonAdjustable(
            "short.nullable", Boolean.class, false);

    /**
     * Specifies whether an empty string can be generated;
     * default is {@code false}; property name {@code string.allow.empty}.
     */
    public static final SettingKey<Boolean> STRING_ALLOW_EMPTY = registerRequiredNonAdjustable(
            "string.allow.empty", Boolean.class, false);

    /**
     * Specifies whether generated Strings should be prefixed with field names;
     * default is {@code false}; property name {@code string.field.prefix.enabled}.
     *
     * @since 2.4.0
     */
    public static final SettingKey<Boolean> STRING_FIELD_PREFIX_ENABLED = registerRequiredNonAdjustable(
            "string.field.prefix.enabled", Boolean.class, false);

    /**
     * Specifies minimum length of strings;
     * default is 3; property name {@code string.min.length}.
     */
    public static final SettingKey<Integer> STRING_MIN_LENGTH = registerRequiredAdjustable(
            "string.min.length", Integer.class, 3, MIN_ADJUSTER, false);

    /**
     * Specifies maximum length of strings;
     * default is 10; property name {@code string.max.length}.
     */
    public static final SettingKey<Integer> STRING_MAX_LENGTH = registerRequiredAdjustable(
            "string.max.length", Integer.class, 10, MAX_ADJUSTER, false);

    /**
     * Specifies whether a {@code null} can be generated for String type;
     * default is {@code false}; property name {@code string.nullable}.
     */
    public static final SettingKey<Boolean> STRING_NULLABLE = registerRequiredNonAdjustable(
            "string.nullable", Boolean.class, false);

    /**
     * Specifies the case of generated strings;
     * default is {@link StringCase#UPPER}; property name {@code string.case}.
     *
     * @since 4.8.0
     */
    @ExperimentalApi
    public static final SettingKey<StringCase> STRING_CASE = registerRequiredNonAdjustable(
            "string.case", StringCase.class, StringCase.UPPER);

    /**
     * Specifies the String type to generate;
     * default is {@link StringType#ALPHABETIC}; property name {@code string.type}.
     *
     * @since 4.7.0
     */
    @ExperimentalApi
    public static final SettingKey<StringType> STRING_TYPE = registerRequiredNonAdjustable(
            "string.type", StringType.class, StringType.ALPHABETIC);

    // Note: keys must be collected after all keys have been initialised
    private static final Map<String, SettingKey<?>> SETTING_KEY_MAP = Collections.unmodifiableMap(settingKeyMap());

    private static Map<String, SettingKey<?>> settingKeyMap() {
        final Map<String, SettingKey<?>> map = new HashMap<>();
        for (SettingKey<?> key : ALL_KEYS) {
            map.put(key.propertyKey(), key);
        }
        return map;
    }

    /**
     * Returns all keys supported by Instancio.
     *
     * @return all keys
     */
    public static List<SettingKey<Object>> all() {
        return Collections.unmodifiableList(ALL_KEYS);
    }

    /**
     * Returns a {@link SettingKey} instance with the given property key.
     *
     * @param key to lookup
     * @return the setting key, or {@code null} if none found
     */
    @SuppressWarnings("unchecked")
    public static <T> SettingKey<T> get(@NotNull final String key) {
        return (SettingKey<T>) SETTING_KEY_MAP.get(key);
    }

    /**
     * A builder for creating custom setting keys.
     *
     * <p>When defining custom keys, specifying
     * {@link SettingKeyBuilder#withPropertyKey(String)} is optional since
     * not all settings will be defined in a properties file.
     * If {@code withPropertyKey()} is not specified, then a random
     * property key will be assigned.
     *
     * @param type of the value the key is associated with, not {@code null}
     * @param <T>  the value type
     * @return key builder
     * @since 2.12.0
     */
    @ExperimentalApi
    public static <T> SettingKeyBuilder<T> ofType(final Class<T> type) {
        return InternalKey.builder(type);
    }

    private static <T> SettingKey<T> register(
            @NotNull final String propertyKey,
            @NotNull final Class<T> type,
            @Nullable final Object defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster,
            final boolean allowsNullValue,
            final boolean allowsNegative) {

        final SettingKey<T> settingKey = new InternalKey<>(
                propertyKey, type, defaultValue, rangeAdjuster, allowsNullValue, allowsNegative);

        ALL_KEYS.add((SettingKey<Object>) settingKey);
        return settingKey;
    }

    private static <T> SettingKey<T> registerRequiredAdjustable(
            @NotNull final String propertyKey,
            @NotNull final Class<T> type,
            @Nullable final Object defaultValue,
            @Nullable final RangeAdjuster rangeAdjuster,
            final boolean allowsNegative) {

        return register(propertyKey, type, defaultValue, rangeAdjuster, false, allowsNegative);
    }

    private static <T> SettingKey<T> registerRequiredNonAdjustable(
            @NotNull final String key,
            @NotNull final Class<T> type,
            @NotNull final Object defaultValue) {

        return register(key, type, defaultValue, null, false, false);
    }

    private Keys() {
        // non-instantiable
    }
}
