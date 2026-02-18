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
package org.instancio.internal.util;

import org.instancio.Scope;
import org.instancio.internal.nodes.InternalNode;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

@SuppressWarnings({"StringBufferReplaceableByString", "UnnecessaryStringBuilder"})
public final class Format {
    private static final int SB_SMALL = 60;
    private static final String METHOD_PADDING = "  │ ";
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\w+\\.");

    public static @Nullable String formatNode(final InternalNode node) {
        return node.getField() == null
                ? withoutPackage(node.getType())
                : formatField(node.getField());
    }

    public static String nodePathToRootBlock(final InternalNode node) {
        return new StringBuilder()
                .append(node.toDisplayString()).append(" (depth=").append(node.getDepth()).append(')').append(NL)
                .append(NL)
                .append(" │ Path to root:").append(NL)
                .append(nodePathToRoot(node, " │   ")).append("   <-- Root").append(NL)
                .append(" │").append(NL)
                .append(" │ Format: <depth:class: field>")
                .toString();
    }

    public static String nodePathToRoot(final InternalNode node, final String prefix) {
        String padding = "";

        final StringBuilder sb = new StringBuilder(prefix)
                .append(formatAsTreeNode(node));

        for (InternalNode n = node.getParent(); n != null; n = n.getParent()) {
            sb.append(NL)
                    .append(prefix)
                    .append(padding)
                    .append(" └──")
                    .append(formatAsTreeNode(n));

            padding += "    "; // NOSONAR
        }
        return sb.toString();
    }

    /**
     * Formats given {@code node} as {@code <depth:class: field>}.
     *
     * <p>Example:
     * <pre>{@code
     * <2:Address: List<Phone> phoneNumbers>
     * }</pre>
     */
    public static String formatAsTreeNode(final InternalNode node) {
        final StringBuilder sb = new StringBuilder(SB_SMALL);
        sb.append('<').append(node.getDepth()).append(':');

        if (node.getField() == null && node.getSetter() == null) {
            sb.append(withoutPackage(node.getTargetClass()));
        } else {
            if (node.getParent() != null) {
                sb.append(withoutPackage(node.getParent().getTargetClass()));
            }
            sb.append(": ");

            if (node.getField() != null) {
                sb.append(withoutPackage(node.getType()))
                        .append(' ')
                        .append(node.getField().getName());
            }
            if (node.getSetter() != null) {
                if (node.getField() != null) {
                    sb.append("; ");
                }
                final Type paramType = node.getSetter().getGenericParameterTypes()[0];
                sb.append(node.getSetter().getName())
                        .append('(').append(withoutPackage(paramType)).append(')');
            }
        }
        if (node.isIgnored()) {
            sb.append(" [IGNORED]");
        }
        if (node.isCyclic()) {
            sb.append(" [CYCLIC]");
        }
        return sb.append('>').toString();
    }

    public static @Nullable String formatField(final @Nullable Field field) {
        return field == null ? null : String.format("%s %s (in %s)",
                withoutPackage(ObjectUtils.defaultIfNull(field.getGenericType(), field.getType())),
                field.getName(),
                field.getDeclaringClass().getName());
    }

    /**
     * Formats given setter {@code method}.
     *
     * @param method setter method with exactly one argument
     * @return method formatted as a string
     */
    public static @Nullable String formatSetterMethod(final @Nullable Method method) {
        return method == null ? null : String.format("%s.%s(%s)",
                withoutPackage(method.getDeclaringClass()),
                method.getName(),
                withoutPackage(method.getParameterTypes()[0]));
    }

    public static String formatMethodWithFence(final Method method) {
        final StringBuilder sb = new StringBuilder(SB_SMALL);
        final String params = Arrays.stream(method.getGenericParameterTypes())
                .map(Format::withoutPackage)
                .collect(joining(", "));

        sb.append(METHOD_PADDING).append(withoutPackage(method.getGenericReturnType()))
                .append(' ').append(method.getName())
                .append('(').append(params).append(')');

        if (Modifier.isAbstract(method.getModifiers())) {
            sb.append(';');
        } else {
            sb.append(" {").append(NL)
                    .append(METHOD_PADDING).append("    ...").append(NL)
                    .append(METHOD_PADDING).append('}');
        }
        return sb.toString();
    }

    public static String methodNameWithParams(final Method method) {
        final String params = Arrays.stream(method.getParameterTypes())
                .map(Format::withoutPackage)
                .collect(joining(", "));

        return String.format("%s(%s)", method.getName(), params);
    }

    public static String simpleNameWithTypeParameters(final Class<?> type) {
        final TypeVariable<?>[] typeParams = type.getTypeParameters();
        if (typeParams.length == 0) {
            return type.getSimpleName();
        }
        final String typeVariablesCsv = Arrays.stream(typeParams)
                .map(TypeVariable::getName)
                .collect(joining(", "));

        return String.format("%s<%s>", type.getSimpleName(), typeVariablesCsv);
    }

    public static String withoutPackage(final Type type) {
        return PACKAGE_PATTERN.matcher(type.getTypeName()).replaceAll("");
    }

    public static String formatScopes(final List<Scope> scopes) {
        return scopes.stream()
                .map(Object::toString)
                .collect(joining(", "));
    }

    /**
     * Returns the first stacktrace element that is not 'org.instancio' as a string.
     * Used for reporting unused selectors.
     */
    public static String firstNonInstancioStackTraceLine(final Throwable throwable) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            final String className = element.getClassName();
            if (!className.startsWith("org.instancio")
                    && !className.startsWith("java.")
                    && !className.contains("$Proxy")) {
                return element.toString();
            }
        }
        return "<unknown location>";
    }

    private Format() {
        // non-instantiable
    }
}
