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
package org.instancio.test.support.asserts;

import org.assertj.core.api.AbstractAssert;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AssertJ-style assertion that walks an object graph reflectively and applies
 * an assertion to every <em>value</em> of a given type anywhere in the graph,
 * not only object fields, but also collection elements, array elements, and
 * map values.
 *
 * <h3>Scope filters</h3>
 *
 * <p>The set of values the assertion runs against can be restricted with sticky
 * inclusion/exclusion filters. Filters accumulate across calls and apply to
 * all subsequent assertions on the same instance.
 *
 * <ul>
 *   <li>{@link #includingPaths(String...)} - restrict to values whose path
 *       matches a pattern. If no inclusion is given, all values of the asserted
 *       type are in scope.</li>
 *   <li>{@link #includingSubtrees(String...)} - restrict to values at-or-under
 *       a matching path.</li>
 *   <li>{@link #excludingPaths(String...)} - skip the assertion on matching
 *       paths (descendants are still traversed).</li>
 *   <li>{@link #excludingSubtrees(String...)} - skip the entire subtree
 *       (no descendants are visited).</li>
 * </ul>
 *
 * <p>The effective filter is: {@code (no includes OR matches an include) AND NOT matches an exclude}.
 *
 * <h3>Path patterns</h3>
 * Dotted paths anchored at the graph root by default:
 * <ul>
 *   <li>{@code foo.bar.baz} - exact path</li>
 *   <li>{@code *} - any single segment name</li>
 *   <li>{@code **} - any number of segments (use {@code **.name} for "anywhere")</li>
 *   <li>{@code foo[0]}, {@code foo[*]}, {@code foo[1,3,4]}, {@code foo[1-4]} - index selectors</li>
 *   <li>{@code map[someKey].field} - map keys appear as the index</li>
 * </ul>
 *
 * <h3>Errors</h3>
 * Any inclusion or exclusion pattern that does not match a value-of-type in
 * the graph causes the assertion to fail (typo / stale-pattern guard).
 * Filters that leave zero values to assert will fail; use the raw
 * {@link #assertEachValueOfType} hook if vacuous passing is needed.
 *
 * <h3>Non-trivial examples</h3>
 *
 * <p><b>Verify a redacted user subtree, but allow id / username through, and
 * ensure the rest of the response still has no redaction markers:</b>
 * <pre>{@code
 * ObjectGraphAssert ga = assertThatGraph(response)
 *         .excludingSubtrees("audit", "metadata");
 *
 * ga.includingSubtrees("user")
 *         .excludingPaths("user.id", "user.username")
 *         .hasAllValuesOfTypeEqualTo(String.class, "***REDACTED***");
 *
 * ga.excludingSubtrees("user")
 *         .hasNoValueOfTypeEqualTo(String.class, "***REDACTED***");
 * }</pre>
 *
 * <p><b>Verify a slice of a list: first three items must have non-blank
 * names, ignoring volatile paths:</b>
 *
 * <pre>{@code
 * assertThatGraph(response)
 *         .includingSubtrees("results[0-2]")
 *         .excludingPaths("results[*].requestId", "results[*].timestamp")
 *         .allValuesOfTypeSatisfy(String.class, s -> assertThat(s).isNotBlank());
 * }</pre>
 *
 * <p><b>Different parts of the graph carry different expected values:</b>
 * <pre>{@code
 * ObjectGraphAssert ga = assertThatGraph(payload);
 *
 * ga.includingSubtrees("user")
 *         .hasAllValuesOfTypeEqualTo(String.class, "USER_DATA");
 *
 * ga.includingSubtrees("admin")
 *         .hasAllValuesOfTypeEqualTo(String.class, "ADMIN_DATA");
 *
 * ga.includingPaths("**.optional")
 *         .hasAllValuesOfTypeEqualTo(String.class, null);
 * }</pre>
 *
 * <p><b>Map-key targeting and index unions:</b>
 * <pre>{@code
 * assertThatGraph(config)
 *         .includingSubtrees("regions[us-east]", "regions[us-west]")
 *         .hasAllValuesOfTypeEqualTo(String.class, "primary");
 *
 * assertThatGraph(response)
 *         .includingPaths("results[0,5,10].status")
 *         .hasAllValuesOfTypeEqualTo(String.class, "OK");
 * }</pre>
 *
 * <p><b>Catch a sentinel domain object held inside a list - element-level
 * matching, not just field-level:</b>
 *
 * <pre>{@code
 * assertThatGraph(response)
 *         .hasNoValueOfTypeEqualTo(User.class, User.DELETED_PLACEHOLDER);
 * // matches response.user, response.users[*], response.lookup[someKey], etc.
 * }</pre>
 *
 * <p><b>Custom predicate via the extension hook:</b>
 * <pre>{@code
 * assertThatGraph(response)
 *         .includingSubtrees("user")
 *         .assertEachValueOfType(Long.class, (path, value) ->
 *                 assertThat((Long) value).as(path).isPositive());
 * }</pre>
 */
public class ObjectGraphAssert extends AbstractAssert<ObjectGraphAssert, Object> {

    private final List<PathPattern> includePathPatterns = new ArrayList<>();
    private final List<PathPattern> includeSubtreePatterns = new ArrayList<>();
    private final List<PathPattern> excludePathPatterns = new ArrayList<>();
    private final List<PathPattern> excludeSubtreePatterns = new ArrayList<>();

    // When set, null container elements are matched via their declared element type even
    // without an include filter. Used by hasValuesOfTypeEqualToExactlyIn so its exclusion-scoped
    // "no value outside the subtrees" phase can detect a null that leaks outside the expected
    // paths; see Traversal#elementDeclaredMatch.
    private boolean matchNullContainerElements;

    private ObjectGraphAssert(final Object actual) {
        super(actual, ObjectGraphAssert.class);
    }

    public static ObjectGraphAssert assertThatGraph(final Object actual) {
        return new ObjectGraphAssert(actual);
    }

    public ObjectGraphAssert includingPaths(final String... patterns) {
        for (String p : patterns) includePathPatterns.add(new PathPattern(p));
        return this;
    }

    public ObjectGraphAssert includingSubtrees(final String... patterns) {
        for (String p : patterns) includeSubtreePatterns.add(new PathPattern(p));
        return this;
    }

    public ObjectGraphAssert excludingPaths(final String... patterns) {
        for (String p : patterns) excludePathPatterns.add(new PathPattern(p));
        return this;
    }

    public ObjectGraphAssert excludingSubtrees(final String... patterns) {
        for (String p : patterns) excludeSubtreePatterns.add(new PathPattern(p));
        return this;
    }

    public ObjectGraphAssert hasAllValuesOfTypeEqualTo(final Class<?> valueType, @Nullable final Object value) {
        return assertEachValueOfType(valueType, (path, v) ->
                assertThat(v).as("value at <%s> of type <%s>", path, valueType.getName())
                        .isEqualTo(value), true);
    }

    public ObjectGraphAssert hasAllValuesEqualTo(final Object value) {
        return hasAllValuesOfTypeEqualTo(requireTypeOf(value), value);
    }

    public ObjectGraphAssert hasNoValueOfTypeEqualTo(final Class<?> valueType, @Nullable final Object value) {
        return assertEachValueOfType(valueType, (path, v) ->
                assertThat(v).as("value at <%s> of type <%s>", path, valueType.getName())
                        .isNotEqualTo(value));
    }

    public ObjectGraphAssert hasNoValueEqualTo(final Object value) {
        return hasNoValueOfTypeEqualTo(requireTypeOf(value), value);
    }

    /**
     * NOTE: using {@code String[]} instead of not vararg to prevent usage like
     * {@code hasValuesEqualToExactlyIn(value, "a", "b")} that would bind to
     * {@link #hasValuesOfTypeEqualToExactlyIn(Class, Object, String...)}
     * and result in a false positive failure.
     */
    public ObjectGraphAssert hasValuesEqualToExactlyIn(final Object value, final String[] subtrees) {
        return hasValuesOfTypeEqualToExactlyIn(requireTypeOf(value), value, subtrees);
    }

    public ObjectGraphAssert hasValuesEqualToExactlyIn(final Object value, final String subtree) {
        return hasValuesOfTypeEqualToExactlyIn(requireTypeOf(value), value, subtree);
    }

    public ObjectGraphAssert hasValuesOfTypeEqualToExactlyIn(
            final Class<?> valueType,
            @Nullable final Object value,
            final String... subtrees) {

        assertThat(subtrees).as("subtrees").isNotEmpty();

        final List<PathPattern> added = new ArrayList<>();
        for (String subtree : subtrees) {
            added.add(new PathPattern(subtree));
        }

        // A null `value` is asserted as a container element only when its path is explicitly
        // targeted. The second ("no value outside the subtrees") phase below scopes via
        // exclusion rather than inclusion, so without this flag a null element sitting outside
        // the given subtrees would be silently skipped and the "exactly in" contract would
        // pass even though the null leaks outside the expected paths.
        final boolean previousMatchNullContainerElements = matchNullContainerElements;
        matchNullContainerElements = true;
        try {
            includeSubtreePatterns.addAll(added);

            try {
                hasAllValuesOfTypeEqualTo(valueType, value);
            } finally {
                includeSubtreePatterns.removeAll(added);
            }

            excludeSubtreePatterns.addAll(added);

            try {
                hasNoValueOfTypeEqualTo(valueType, value);
            } finally {
                excludeSubtreePatterns.removeAll(added);
            }
        } finally {
            matchNullContainerElements = previousMatchNullContainerElements;
        }
        return this;
    }

    public <T> ObjectGraphAssert allValuesOfTypeSatisfy(
            final Class<T> valueType,
            final Consumer<T> requirement) {

        assertThat(requirement).as("requirement").isNotNull();
        return assertEachValueOfType(valueType, (path, v) -> requirement.accept(v), true);
    }

    /**
     * Extension hook: walks the graph and invokes {@code assertion} for every in-scope value
     * (field, collection element, array element, or map value) whose runtime or declared type
     * matches {@code valueType}.
     */
    public <T> ObjectGraphAssert assertEachValueOfType(
            final Class<T> valueType,
            final BiConsumer<String, T> assertion) {

        return assertEachValueOfType(valueType, assertion, false);
    }

    private <T> ObjectGraphAssert assertEachValueOfType(
            final Class<T> valueType,
            final BiConsumer<String, T> assertion,
            final boolean failOnEmpty) {

        assertThat(assertion).as("assertion").isNotNull();

        final List<Match<T>> matches = collectValuesOfType(valueType);
        if (failOnEmpty) {
            assertNotEmpty(matches, valueType);
        }
        for (Match<T> m : matches) {
            assertion.accept(m.path(), m.value());
        }
        return this;
    }

    private static Class<?> requireTypeOf(final Object value) {
        assertThat(value)
                .as("value (cannot infer type from null - use the explicit Class<?> overload)")
                .isNotNull();

        return value.getClass();
    }

    private <T> List<Match<T>> collectValuesOfType(final Class<T> valueType) {
        isNotNull();
        assertThat(valueType).as("valueType").isNotNull();
        resetPatternMatchedFlags();

        final List<Match<T>> matches = new Traversal<>(valueType).collect();
        verifyAllPatternsMatched();
        return matches;
    }

    private void assertNotEmpty(final List<?> matches, final Class<?> valueType) {
        if (matches.isEmpty()) {
            failWithMessage("Expected at least one value of type <%s> but found none in the object graph",
                    valueType.getName());
        }
    }

    private final class Traversal<T> {
        private final Class<T> valueType;
        private final List<Match<T>> matches = new ArrayList<>();
        private final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Deque<Node> stack = new ArrayDeque<>();

        Traversal(final Class<T> valueType) {
            this.valueType = valueType;
        }

        private List<Match<T>> collect() {
            enqueue("<root>", actual, false);

            while (!stack.isEmpty()) {
                final Node node = stack.pop();
                final Object current = node.value();
                if (current == null || isLeaf(current.getClass()) || !visited.add(current)) {
                    continue;
                }

                if (current instanceof Iterable<?>) {
                    visitIterable(node, (Iterable<?>) current);
                } else if (current instanceof Map<?, ?>) {
                    visitMap(node, (Map<?, ?>) current);
                } else if (current.getClass().isArray()) {
                    visitArray(node, current);
                } else {
                    visitFields(node, current);
                }
            }
            return matches;
        }

        private void visitIterable(final Node node, final Iterable<?> iterable) {
            int i = 0;
            for (Object element : iterable) {
                enqueue(node.path() + "[" + i++ + "]", element, elementDeclaredMatch(node, element));
            }
        }

        private void visitMap(final Node node, final Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                // Map keys are traversed for descendants but not asserted as values.
                final Object key = entry.getKey();
                final String keyPath = node.path() + ".<key>";
                if (key != null && !isLeaf(key.getClass()) && !matchesAnyPrefix(excludeSubtreePatterns, keyPath)) {
                    stack.push(new Node(key, keyPath));
                }
                final Object value = entry.getValue();
                enqueue(node.path() + "[" + key + "]", value, elementDeclaredMatch(node, value));
            }
        }

        private void visitArray(final Node node, final Object array) {
            if (array.getClass().getComponentType().isPrimitive()) {
                return;
            }
            final Object[] elements = (Object[]) array;
            for (int i = 0; i < elements.length; i++) {
                enqueue(node.path() + "[" + i + "]", elements[i], elementDeclaredMatch(node, elements[i]));
            }
        }

        // Whether a container element matches the target type via the container's declared
        // element type. A null element matches only when a test explicitly targets it - either
        // via an include filter (see hasIncludeFilters()) or when matchNullContainerElements is
        // set by an exclusion-scoped phase that must still detect leaked nulls.
        private boolean elementDeclaredMatch(final Node node, @Nullable final Object element) {
            final Class<?> elementType = node.elementType();
            return elementType != null
                    && valueType.isAssignableFrom(elementType)
                    && (element != null || hasIncludeFilters() || matchNullContainerElements);
        }

        private void visitFields(final Node node, final Object current) {
            for (Class<?> c = current.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                        continue;
                    }
                    field.setAccessible(true);

                    try {
                        final Object fieldValue = field.get(current);
                        final Class<?> fieldType = field.getType();
                        final Class<?> elementType = isContainer(fieldType) ? resolveElementType(field) : null;
                        enqueue(node.path() + "." + field.getName(), fieldValue,
                                valueType.isAssignableFrom(fieldType), elementType);
                    } catch (IllegalAccessException e) {
                        throw new AssertionError("Unable to access field " + field, e);
                    }
                }
            }
        }

        // A primitive valueType (e.g. int.class) matches its declared field type but the runtime
        // value is boxed, so Class.cast() cannot be used here.
        private void enqueue(final String path, @Nullable final Object value, final boolean declaredMatch) {
            enqueue(path, value, declaredMatch, null);
        }

        @SuppressWarnings("unchecked")
        private void enqueue(final String path, @Nullable final Object value,
                             final boolean declaredMatch, @Nullable final Class<?> elementType) {
            if (matchesAnyPrefix(excludeSubtreePatterns, path)) {
                return;
            }
            if ((declaredMatch || valueType.isInstance(value)) && passesFilters(path)) {
                matches.add(new Match<>(path, (T) value));
            }
            stack.push(new Node(value, path, elementType));
        }

        // A null value has no runtime type, so it can only be matched via its declared
        // (field/element) type. For container elements that means a null is asserted only
        // when a test explicitly targets its path with an include filter - otherwise an
        // unscoped assertion (e.g. allValuesOfTypeSatisfy / hasNoValue...) would silently
        // pick up incidental nulls. Field nulls keep their unconditional matching.
        private boolean hasIncludeFilters() {
            return !includePathPatterns.isEmpty() || !includeSubtreePatterns.isEmpty();
        }

        private boolean passesFilters(final String path) {
            final boolean included;
            if (includePathPatterns.isEmpty() && includeSubtreePatterns.isEmpty()) {
                included = true;
            } else {
                final boolean matchesPath = matchesAnyExact(includePathPatterns, path);
                final boolean matchesSubtree = matchesAnyPrefix(includeSubtreePatterns, path);
                included = matchesPath || matchesSubtree;
            }
            return included && !matchesAnyExact(excludePathPatterns, path);
        }
    }

    private static boolean matchesAnyExact(final List<PathPattern> patterns, final String path) {
        boolean any = false;
        for (PathPattern p : patterns) {
            if (p.matches(path)) {
                p.matched = true;
                any = true;
            }
        }
        return any;
    }

    private static boolean matchesAnyPrefix(final List<PathPattern> patterns, final String path) {
        boolean any = false;
        for (PathPattern p : patterns) {
            if (p.matchesPrefixOf(path)) {
                p.matched = true;
                any = true;
            }
        }
        return any;
    }

    private void resetPatternMatchedFlags() {
        for (PathPattern p : includePathPatterns) p.matched = false;
        for (PathPattern p : includeSubtreePatterns) p.matched = false;
        for (PathPattern p : excludePathPatterns) p.matched = false;
        for (PathPattern p : excludeSubtreePatterns) p.matched = false;
    }

    private void verifyAllPatternsMatched() {
        List<String> unmatched = new ArrayList<>();
        for (PathPattern p : includePathPatterns) if (!p.matched) unmatched.add("includingPaths:" + p.pattern);
        for (PathPattern p : includeSubtreePatterns) if (!p.matched) unmatched.add("includingSubtrees:" + p.pattern);
        for (PathPattern p : excludePathPatterns) if (!p.matched) unmatched.add("excludingPaths:" + p.pattern);
        for (PathPattern p : excludeSubtreePatterns) if (!p.matched) unmatched.add("excludingSubtrees:" + p.pattern);
        if (!unmatched.isEmpty()) {
            failWithMessage("The following filter patterns did not match any field in the graph: %s", unmatched);
        }
    }

    private static boolean isLeaf(final Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type == String.class
                || type.getName().startsWith("java.time.")
                || type == Class.class;
    }

    private static boolean isContainer(final Class<?> type) {
        return Iterable.class.isAssignableFrom(type)
                || type.isArray()
                || Map.class.isAssignableFrom(type);
    }

    /**
     * Extracts the element type of a container field (collection/array/map)
     * from its declared generic signature, so that null element values can be
     * matched via their declared type.
     *
     * @return the element type, or {@code null} if the field is not a
     *         generic container or the type cannot be resolved
     */
    private static @Nullable Class<?> resolveElementType(final Field field) {
        final Type genericType = field.getGenericType();

        if (genericType instanceof Class<?> clazz) {
            if (clazz.isArray()) {
                return clazz.getComponentType();
            }
            return null;
        }

        if (genericType instanceof ParameterizedType pt) {
            final Class<?> rawType = (Class<?>) pt.getRawType();
            final Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length == 0) {
                return null;
            }
            final int valueIndex;
            if (Map.class.isAssignableFrom(rawType)) {
                if (typeArgs.length < 2) {
                    return null;
                }
                valueIndex = 1; // Map<K, V> → V
            } else if (Iterable.class.isAssignableFrom(rawType)) {
                valueIndex = 0; // Iterable<T> → T
            } else {
                return null;
            }
            return toClass(typeArgs[valueIndex]);
        }

        if (genericType instanceof GenericArrayType gat) {
            return toClass(gat.getGenericComponentType());
        }

        return null;
    }

    /**
     * Converts a reflective {@link Type} to its erasure {@link Class},
     * resolving wildcards and type variables where possible.
     *
     * @return the erased class, or {@code null} if unresolved
     */
    private static @Nullable Class<?> toClass(final Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }
        if (type instanceof ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }
        if (type instanceof WildcardType wt) {
            final Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds.length > 0 && !upperBounds[0].equals(Object.class)) {
                return toClass(upperBounds[0]);
            }
            return null;
        }
        return null;
    }

    private record Node(@Nullable Object value, String path, @Nullable Class<?> elementType) {
        Node(@Nullable final Object value, final String path) {
            this(value, path, null);
        }
    }

    private record Match<T>(String path, @Nullable T value) {}

    /**
     * Dotted path pattern anchored at the graph root.
     */
    private static final class PathPattern {
        private final String pattern;
        private final List<Segment> segments;
        private boolean matched;

        PathPattern(final String pattern) {
            this.pattern = pattern;
            this.segments = parse(pattern);
        }

        boolean matches(final String fullPath) {
            return matchFrom(0, tokenize(stripRoot(fullPath)), 0, /*allowSuffix*/ false);
        }

        boolean matchesPrefixOf(final String fullPath) {
            return matchFrom(0, tokenize(stripRoot(fullPath)), 0, /*allowSuffix*/ true);
        }

        private static String stripRoot(final String s) {
            return s.startsWith("<root>.") ? s.substring("<root>.".length()) : s;
        }

        private boolean matchFrom(
                final int pi,
                final List<String> tokens,
                final int xi,
                final boolean allowSuffix) {

            if (pi == segments.size()) {
                return allowSuffix ? xi <= tokens.size() : xi == tokens.size();
            }
            final Segment s = segments.get(pi);
            if (s == DoubleStar.INSTANCE) {
                for (int k = xi; k <= tokens.size(); k++) {
                    if (matchFrom(pi + 1, tokens, k, allowSuffix)) {
                        return true;
                    }
                }
                return false;
            }

            return xi < tokens.size()
                    && ((TokenSegment) s).matches(tokens.get(xi))
                    && matchFrom(pi + 1, tokens, xi + 1, allowSuffix);
        }

        private static List<Segment> parse(final String pattern) {
            final List<Segment> results = new ArrayList<>();
            for (String token : tokenize(pattern)) {
                if ("**".equals(token)) {
                    results.add(DoubleStar.INSTANCE);
                } else if (token.startsWith("[") && token.endsWith("]")) {
                    results.add(new IndexSeg(IndexMatcher.parse(token.substring(1, token.length() - 1))));
                } else {
                    results.add(new NameSeg(token));
                }
            }
            return results;
        }

        // Example:
        // "leaves[0].name" -> ["leaves", "[0]", "name"],
        // "a.**.b" -> ["a", "**", "b"]
        private static List<String> tokenize(final String s) {
            final List<String> out = new ArrayList<>();
            final int n = s.length();
            int i = 0;
            int start = 0;
            while (i < n) {
                final char c = s.charAt(i);
                if (c == '.') {
                    if (i > start) {
                        out.add(s.substring(start, i));
                    }
                    i++;
                    start = i;
                } else if (c == '[') {
                    if (i > start) {
                        out.add(s.substring(start, i));
                    }
                    final int close = s.indexOf(']', i);
                    if (close < 0) {
                        throw new IllegalArgumentException("Unbalanced '[' in path: " + s);
                    }
                    out.add(s.substring(i, close + 1));
                    i = close + 1;
                    start = i;
                } else {
                    i++;
                }
            }
            if (i > start) {
                out.add(s.substring(start, i));
            }
            return out;
        }
    }

    private interface Segment {}

    private interface TokenSegment extends Segment {
        boolean matches(String token);
    }

    private enum DoubleStar implements Segment {INSTANCE}

    private record NameSeg(String name) implements TokenSegment {
        @Override
        public boolean matches(final String token) {
            return !token.startsWith("[") && (name.equals("*") || name.equals(token));
        }
    }

    private record IndexSeg(IndexMatcher matcher) implements TokenSegment {
        @Override
        public boolean matches(final String token) {
            return token.startsWith("[")
                    && token.endsWith("]")
                    && matcher.matches(token.substring(1, token.length() - 1));
        }
    }

    private interface IndexMatcher {
        boolean matches(String idx);

        static IndexMatcher parse(final String spec) {
            if ("*".equals(spec)) {
                return idx -> true;
            }
            if (spec.contains(",")) {
                final Set<Integer> set = new HashSet<>();
                for (String p : spec.split(",")) {
                    set.add(Integer.parseInt(p.trim()));
                }
                return idx -> isInt(idx) && set.contains(Integer.parseInt(idx));
            }
            final int dash = spec.indexOf('-');
            if (dash > 0) {
                final int lo = Integer.parseInt(spec.substring(0, dash).trim());
                final int hi = Integer.parseInt(spec.substring(dash + 1).trim());
                return idx -> isInt(idx) && Integer.parseInt(idx) >= lo && Integer.parseInt(idx) <= hi;
            }
            return idx -> idx.equals(spec);
        }

        static boolean isInt(final String s) {
            if (s.isEmpty()) {
                return false;
            }
            int i = s.charAt(0) == '-' ? 1 : 0;
            if (i == s.length()) {
                return false;
            }
            for (; i < s.length(); i++) {
                if (!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
