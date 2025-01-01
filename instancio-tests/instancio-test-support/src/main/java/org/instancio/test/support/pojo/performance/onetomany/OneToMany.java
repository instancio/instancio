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
package org.instancio.test.support.pojo.performance.onetomany;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class OneToMany {
    private Long id;

    private List<ChildA> childrenA;
    private List<ChildB> childrenB;
    private List<ChildC> childrenC;
    private ChildD[] childrenD;
    private Map<UUID, ChildE> childrenE;

    private String parentStringA;
    private String parentStringB;
    private String parentStringC;
    private String parentStringD;
    private String parentStringE;
    private String parentStringF;
    private String parentStringG;

    private Long parentLongA;
    private Long parentLongB;
    private Long parentLongC;
    private Long parentLongD;
    private Long parentLongE;
    private Long parentLongF;
    private Long parentLongG;

    private Instant createdOn;
    private Instant updatedOn;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
