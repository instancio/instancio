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
package org.instancio.internal.nodes;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.tags.NodeTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NodeTag
class NodeImplTest {

    @Test
    void verifyEqualsAndHashCode() {
        EqualsVerifier.forClass(NodeImpl.class).verify();
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> new NodeImpl(null, null, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("targetClass is null");
    }

    @Test
    void verifyToString() {
        assertThat(new NodeImpl(Foo.class, ReflectionUtils.getField(Foo.class, "fooValue"), Foo.class))
                .hasToString("Node[targetClass=org.instancio.test.support.pojo.generics.foobarbaz.Foo, field=fooValue, parentTargetClass=org.instancio.test.support.pojo.generics.foobarbaz.Foo]");

        assertThat(new NodeImpl(Foo.class, null, null))
                .hasToString("Node[targetClass=org.instancio.test.support.pojo.generics.foobarbaz.Foo, field=null, parentTargetClass=null]");
    }
}
