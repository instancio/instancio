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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * See: https://github.com/instancio/instancio/issues/1290
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignModelStateLeakTest {

    @Test
    void modelContainingAssignmentsShouldNotBeModified() {
        @Data
        class Pojo {
            BigDecimal amountA;
            BigDecimal amountB;
        }

        final Model<Pojo> model = Instancio.of(Pojo.class)
                .assign(Assign.valueOf(Pojo::getAmountA).to(field(Pojo::getAmountB)))
                .toModel();

        final Pojo result1 = Instancio.of(model)
                .assign(Assign.valueOf(Pojo::getAmountA)
                        .to(field(Pojo::getAmountB))
                        .as((BigDecimal amount) -> amount.subtract(BigDecimal.ONE)))
                .create();

        final Pojo result2 = Instancio.create(model);

        assertThat(result1.getAmountB()).isEqualTo(result1.getAmountA().subtract(BigDecimal.ONE));
        assertThat(result2.getAmountB()).isEqualTo(result2.getAmountA());
    }

}
