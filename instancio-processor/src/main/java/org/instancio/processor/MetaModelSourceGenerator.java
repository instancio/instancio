/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.processor;

import java.util.stream.Collectors;

class MetaModelSourceGenerator {
    private static final String PACKAGE_TEMPLATE = "package %s;";
    private static final String IMPORTS = ""
            + "import org.instancio.Binding;\n"
            + "import org.instancio.Bindings;";
    private static final String CLASS_BODY_TEMPLATE = "public class %s {\n%s\n}"; // class name, class body
    private static final String FIELD_TEMPLATE = "\tpublic static final Binding %s = Bindings.field(%s, \"%s\");";
    private static final String CLASS_TEMPLATE = "%s\n\n%s\n\n%s"; // package, imports, class body
    private static final String META_MODEL_FILE_SUFFIX = "_";

    String getSource(final MetaModelClass type) {
        return String.format(CLASS_TEMPLATE,
                packageDeclaration(type.getPackageName()),
                IMPORTS,
                classBody(type));
    }

    private String classBody(final MetaModelClass type) {
        final String metaModelClassNAme = type.getSimpleName() + META_MODEL_FILE_SUFFIX;
        return String.format(CLASS_BODY_TEMPLATE, metaModelClassNAme, getFields(type));
    }

    private String getFields(final MetaModelClass type) {
        return type.getFieldNames().stream()
                .map(field -> String.format(FIELD_TEMPLATE, field, type.getSimpleName() + ".class", field))
                .collect(Collectors.joining("\n\n"));
    }

    private String packageDeclaration(final String packageName) {
        return packageName == null ? "" : String.format(PACKAGE_TEMPLATE, packageName);
    }
}
