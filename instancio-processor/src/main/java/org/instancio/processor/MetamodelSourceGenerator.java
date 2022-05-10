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

import javax.annotation.Nullable;
import java.util.stream.Collectors;

class MetamodelSourceGenerator {
    private static final String PACKAGE_TEMPLATE = "package %s;";
    private static final String IMPORTS = String.format(""
            + "import org.instancio.Select;%n"
            + "import org.instancio.Selector;"
    );
    private static final String CLASS_BODY_TEMPLATE = "public class %s {%n%s%n}"; // class name, class body
    private static final String FIELD_TEMPLATE = "\tpublic static final Selector %s = Select.field(%s, \"%s\");";
    private static final String CLASS_TEMPLATE = "%s%n%n%s%n%n%s"; // package, imports, class body

    String getSource(final MetamodelClass modelClass) {
        return String.format(CLASS_TEMPLATE,
                packageDeclaration(modelClass.getPackageName()),
                IMPORTS,
                classBody(modelClass));
    }

    private String classBody(final MetamodelClass modelClass) {
        final String metaModelClassName = modelClass.getMetamodelSimpleName();
        return String.format(CLASS_BODY_TEMPLATE, metaModelClassName, getFields(modelClass));
    }

    private String getFields(final MetamodelClass type) {
        return type.getFieldNames().stream()
                .map(field -> String.format(FIELD_TEMPLATE, field, type.getName() + ".class", field))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String packageDeclaration(@Nullable final String packageName) {
        return packageName == null ? "" : String.format(PACKAGE_TEMPLATE, packageName);
    }
}