/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.ruleunit;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.rule.EntryPointId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.ruleunit.RuleUnitTestUtil.createPackage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RuleUnitDescriptionTest {

    private RuleUnitDescription ruleUnitDescr;

    @BeforeEach
    public void prepareRuleUnitDescr() {
        ruleUnitDescr = new RuleUnitDescription(createPackage("test"), TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitClass() {
        assertThat(ruleUnitDescr.getRuleUnitClass()).isEqualTo(TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitName() {
        assertThat(ruleUnitDescr.getRuleUnitName()).isEqualTo(TestRuleUnit.class.getName());
    }

    @Test
    public void getEntryPointId() {
        final Optional<EntryPointId> entryPointId = ruleUnitDescr.getEntryPointId("nonexisting");
        assertThat(entryPointId).isNotPresent();

        assertEntryPointIdExists("numbersArray");
        assertEntryPointIdExists("number");
        assertEntryPointIdExists("stringList");
        assertEntryPointIdExists("simpleFactList");
    }

    @Test
    public void getDatasourceType() {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType("nonexisting");
        assertThat(dataSourceType).isNotPresent();

        assertDataSourceType("number", BigDecimal.class);
        assertDataSourceType("numbersArray", Integer.class);
        assertDataSourceType("stringList", String.class);
        assertDataSourceType("simpleFactList", SimpleFact.class);
    }

    @Test
    public void getVarType() {
        final Optional<Class<?>> varType = ruleUnitDescr.getVarType("nonexisting");
        assertThat(varType).isNotPresent();

        assertVarType("number", BigDecimal.class);
        assertVarType("numbersArray", Integer[].class);
        assertVarType("stringList", List.class);
        assertVarType("simpleFactList", List.class);
    }

    @Test
    public void hasVar() {
        assertThat(ruleUnitDescr.hasVar("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasVar("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasVar("number")).isTrue();
        assertThat(ruleUnitDescr.hasVar("numbersArray")).isTrue();
        assertThat(ruleUnitDescr.hasVar("stringList")).isTrue();
        assertThat(ruleUnitDescr.hasVar("simpleFactList")).isTrue();
    }

    @Test
    public void getUnitVars() {
        final Collection<String> unitVars = ruleUnitDescr.getUnitVars();
        assertThat(unitVars).isNotEmpty();
        assertThat(unitVars).hasSize(5);
        assertThat(unitVars).containsExactlyInAnyOrder("bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void getUnitVarAccessors() {
        final Map<String, Method> unitVarAccessors = ruleUnitDescr.getUnitVarAccessors();
        assertThat(unitVarAccessors).isNotEmpty();
        assertThat(unitVarAccessors).hasSize(5);
        assertThat(unitVarAccessors).containsKeys("bound", "number", "numbersArray", "stringList", "simpleFactList");
        assertThat(unitVarAccessors.values())
                .extracting("name", String.class)
                .containsExactlyInAnyOrder("getBound", "getNumber", "getNumbersArray", "getStringList", "getSimpleFactList");
    }

    @Test
    public void hasDataSource() {
        assertThat(ruleUnitDescr.hasDataSource("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("number")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("numbersArray")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("stringList")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("simpleFactList")).isTrue();
    }

    @Test
    public void getValue() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{1, 2, 5}, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);

        Object value = ruleUnitDescr.getValue(testRuleUnit, "nonexisting");
        assertThat(value).isNull();

        value = ruleUnitDescr.getValue(testRuleUnit, "number");
        assertThat(value).isInstanceOf(BigDecimal.class);
        assertThat(value).isEqualTo(BigDecimal.TEN);

        value = ruleUnitDescr.getValue(testRuleUnit, "numbersArray");
        assertThat(value).isInstanceOf(Integer[].class);
        assertThat(value).isEqualTo(new Integer[]{1, 2, 5});

        value = ruleUnitDescr.getValue(testRuleUnit, "simpleFactList");
        assertThat(value).isInstanceOfSatisfying(List.class, list -> assertThat(list).containsExactly(simpleFact));
    }

    private void assertEntryPointIdExists(final String entryPointIdName) {
        final Optional<EntryPointId> entryPointId = ruleUnitDescr.getEntryPointId(entryPointIdName);
        assertTrue(entryPointId.isPresent());
        assertEquals(entryPointIdName, entryPointId.get().getEntryPointId());
    }

    private void assertDataSourceType(final String dataSourceName, final Class<?> expectedType) {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType(dataSourceName);
        assertTrue(dataSourceType.isPresent());
        assertEquals(expectedType, dataSourceType.get());
    }

    private void assertVarType(final String varName, final Class<?> expectedType) {
        final Optional<Class<?>> variableTable = ruleUnitDescr.getVarType(varName);
        assertTrue(variableTable.isPresent());
        assertEquals(expectedType, variableTable.get());
    }
}
