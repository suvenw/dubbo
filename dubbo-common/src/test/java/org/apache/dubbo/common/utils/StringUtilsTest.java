/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.INTERFACE_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;
import static org.apache.dubbo.common.utils.CollectionUtils.ofSet;
import static org.apache.dubbo.common.utils.StringUtils.splitToList;
import static org.apache.dubbo.common.utils.StringUtils.splitToSet;
import static org.apache.dubbo.common.utils.StringUtils.startsWithIgnoreCase;
import static org.apache.dubbo.common.utils.StringUtils.toCommaDelimitedString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {
    @Test
    void testLength() throws Exception {
        assertThat(StringUtils.length(null), equalTo(0));
        assertThat(StringUtils.length("abc"), equalTo(3));
    }

    @Test
    void testRepeat() throws Exception {
        assertThat(StringUtils.repeat(null, 2), nullValue());
        assertThat(StringUtils.repeat("", 0), equalTo(""));
        assertThat(StringUtils.repeat("", 2), equalTo(""));
        assertThat(StringUtils.repeat("a", 3), equalTo("aaa"));
        assertThat(StringUtils.repeat("ab", 2), equalTo("abab"));
        assertThat(StringUtils.repeat("a", -2), equalTo(""));
        assertThat(StringUtils.repeat(null, null, 2), nullValue());
        assertThat(StringUtils.repeat(null, "x", 2), nullValue());
        assertThat(StringUtils.repeat("", null, 0), equalTo(""));
        assertThat(StringUtils.repeat("", "", 2), equalTo(""));
        assertThat(StringUtils.repeat("", "x", 3), equalTo("xx"));
        assertThat(StringUtils.repeat("?", ", ", 3), equalTo("?, ?, ?"));
        assertThat(StringUtils.repeat('e', 0), equalTo(""));
        assertThat(StringUtils.repeat('e', 3), equalTo("eee"));
    }

    @Test
    void testStripEnd() throws Exception {
        assertThat(StringUtils.stripEnd(null, "*"), nullValue());
        assertThat(StringUtils.stripEnd("", null), equalTo(""));
        assertThat(StringUtils.stripEnd("abc", ""), equalTo("abc"));
        assertThat(StringUtils.stripEnd("abc", null), equalTo("abc"));
        assertThat(StringUtils.stripEnd("  abc", null), equalTo("  abc"));
        assertThat(StringUtils.stripEnd("abc  ", null), equalTo("abc"));
        assertThat(StringUtils.stripEnd(" abc ", null), equalTo(" abc"));
        assertThat(StringUtils.stripEnd("  abcyx", "xyz"), equalTo("  abc"));
        assertThat(StringUtils.stripEnd("120.00", ".0"), equalTo("12"));
    }

    @Test
    void testReplace() throws Exception {
        assertThat(StringUtils.replace(null, "*", "*"), nullValue());
        assertThat(StringUtils.replace("", "*", "*"), equalTo(""));
        assertThat(StringUtils.replace("any", null, "*"), equalTo("any"));
        assertThat(StringUtils.replace("any", "*", null), equalTo("any"));
        assertThat(StringUtils.replace("any", "", "*"), equalTo("any"));
        assertThat(StringUtils.replace("aba", "a", null), equalTo("aba"));
        assertThat(StringUtils.replace("aba", "a", ""), equalTo("b"));
        assertThat(StringUtils.replace("aba", "a", "z"), equalTo("zbz"));
        assertThat(StringUtils.replace(null, "*", "*", 64), nullValue());
        assertThat(StringUtils.replace("", "*", "*", 64), equalTo(""));
        assertThat(StringUtils.replace("any", null, "*", 64), equalTo("any"));
        assertThat(StringUtils.replace("any", "*", null, 64), equalTo("any"));
        assertThat(StringUtils.replace("any", "", "*", 64), equalTo("any"));
        assertThat(StringUtils.replace("any", "*", "*", 0), equalTo("any"));
        assertThat(StringUtils.replace("abaa", "a", null, -1), equalTo("abaa"));
        assertThat(StringUtils.replace("abaa", "a", "", -1), equalTo("b"));
        assertThat(StringUtils.replace("abaa", "a", "z", 0), equalTo("abaa"));
        assertThat(StringUtils.replace("abaa", "a", "z", 1), equalTo("zbaa"));
        assertThat(StringUtils.replace("abaa", "a", "z", 2), equalTo("zbza"));
    }

    @Test
    void testIsBlank() throws Exception {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertFalse(StringUtils.isBlank("abc"));
    }

    @Test
    void testIsEmpty() throws Exception {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("abc"));
    }

    @Test
    void testIsNoneEmpty() throws Exception {
        assertFalse(StringUtils.isNoneEmpty(null));
        assertFalse(StringUtils.isNoneEmpty(""));
        assertTrue(StringUtils.isNoneEmpty(" "));
        assertTrue(StringUtils.isNoneEmpty("abc"));
        assertTrue(StringUtils.isNoneEmpty("abc", "def"));
        assertFalse(StringUtils.isNoneEmpty("abc", null));
        assertFalse(StringUtils.isNoneEmpty("abc", ""));
        assertTrue(StringUtils.isNoneEmpty("abc", " "));
    }

    @Test
    void testIsAnyEmpty() throws Exception {
        assertTrue(StringUtils.isAnyEmpty(null));
        assertTrue(StringUtils.isAnyEmpty(""));
        assertFalse(StringUtils.isAnyEmpty(" "));
        assertFalse(StringUtils.isAnyEmpty("abc"));
        assertFalse(StringUtils.isAnyEmpty("abc", "def"));
        assertTrue(StringUtils.isAnyEmpty("abc", null));
        assertTrue(StringUtils.isAnyEmpty("abc", ""));
        assertFalse(StringUtils.isAnyEmpty("abc", " "));
    }

    @Test
    void testIsNotEmpty() throws Exception {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty("abc"));
    }

    @Test
    void testIsEquals() throws Exception {
        assertTrue(StringUtils.isEquals(null, null));
        assertFalse(StringUtils.isEquals(null, ""));
        assertTrue(StringUtils.isEquals("abc", "abc"));
        assertFalse(StringUtils.isEquals("abc", "ABC"));
    }

    @Test
    void testIsInteger() throws Exception {
        assertFalse(StringUtils.isNumber(null));
        assertFalse(StringUtils.isNumber(""));
        assertTrue(StringUtils.isNumber("123"));
    }

    @Test
    void testParseInteger() throws Exception {
        assertThat(StringUtils.parseInteger(null), equalTo(0));
        assertThat(StringUtils.parseInteger("123"), equalTo(123));
    }

    @Test
    void testIsJavaIdentifier() throws Exception {
        assertThat(StringUtils.isJavaIdentifier(""), is(false));
        assertThat(StringUtils.isJavaIdentifier("1"), is(false));
        assertThat(StringUtils.isJavaIdentifier("abc123"), is(true));
        assertThat(StringUtils.isJavaIdentifier("abc(23)"), is(false));
    }

    @Test
    void testExceptionToString() throws Exception {
        assertThat(
                StringUtils.toString(new RuntimeException("abc")), containsString("java.lang.RuntimeException: abc"));
    }

    @Test
    void testExceptionToStringWithMessage() throws Exception {
        String s = StringUtils.toString("greeting", new RuntimeException("abc"));
        assertThat(s, containsString("greeting"));
        assertThat(s, containsString("java.lang.RuntimeException: abc"));
    }

    @Test
    void testParseQueryString() throws Exception {
        assertThat(StringUtils.getQueryStringValue("key1=value1&key2=value2", "key1"), equalTo("value1"));
        assertThat(StringUtils.getQueryStringValue("key1=value1&key2=value2", "key2"), equalTo("value2"));
        assertThat(StringUtils.getQueryStringValue("", "key1"), isEmptyOrNullString());
    }

    @Test
    void testGetServiceKey() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GROUP_KEY, "dubbo");
        map.put(INTERFACE_KEY, "a.b.c.Foo");
        map.put(VERSION_KEY, "1.0.0");
        assertThat(StringUtils.getServiceKey(map), equalTo("dubbo/a.b.c.Foo:1.0.0"));
    }

    @Test
    void testToQueryString() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        String queryString = StringUtils.toQueryString(map);
        assertThat(queryString, containsString("key1=value1"));
        assertThat(queryString, containsString("key2=value2"));
    }

    @Test
    void testJoin() throws Exception {
        String[] s = {"1", "2", "3"};
        assertEquals(StringUtils.join(s), "123");
        assertEquals(StringUtils.join(s, ','), "1,2,3");
        assertEquals(StringUtils.join(s, ","), "1,2,3");
        assertEquals(StringUtils.join(s, ',', 0, 1), "1");
        assertEquals(StringUtils.join(s, ',', 0, 2), "1,2");
        assertEquals(StringUtils.join(s, ',', 0, 3), "1,2,3");
        assertEquals("", StringUtils.join(s, ',', 2, 0), "1,2");
    }

    @Test
    void testSplit() throws Exception {
        String str = "d,1,2,4";

        assertEquals(4, StringUtils.split(str, ',').length);
        assertArrayEquals(str.split(","), StringUtils.split(str, ','));

        assertEquals(1, StringUtils.split(str, 'a').length);
        assertArrayEquals(str.split("a"), StringUtils.split(str, 'a'));

        assertEquals(0, StringUtils.split("", 'a').length);
        assertEquals(0, StringUtils.split(null, 'a').length);
    }

    @Test
    void testSplitToList() throws Exception {
        String str = "d,1,2,4";

        assertEquals(4, splitToList(str, ',').size());
        assertEquals(asList(str.split(",")), splitToList(str, ','));

        assertEquals(1, splitToList(str, 'a').size());
        assertEquals(asList(str.split("a")), splitToList(str, 'a'));

        assertEquals(0, splitToList("", 'a').size());
        assertEquals(0, splitToList(null, 'a').size());
    }

    /**
     * Test {@link StringUtils#splitToSet(String, char, boolean)}
     *
     * @since 2.7.8
     */
    @Test
    void testSplitToSet() {
        String value = "1# 2#3 #4#3";
        Set<String> values = splitToSet(value, '#', false);
        assertEquals(ofSet("1", " 2", "3 ", "4", "3"), values);

        values = splitToSet(value, '#', true);
        assertEquals(ofSet("1", "2", "3", "4"), values);
    }

    @Test
    void testTranslate() throws Exception {
        String s = "16314";
        assertEquals(StringUtils.translate(s, "123456", "abcdef"), "afcad");
        assertEquals(StringUtils.translate(s, "123456", "abcd"), "acad");
    }

    @Test
    void testIsContains() throws Exception {
        assertThat(StringUtils.isContains("a,b, c", "b"), is(true));
        assertThat(StringUtils.isContains("", "b"), is(false));
        assertThat(StringUtils.isContains(new String[] {"a", "b", "c"}, "b"), is(true));
        assertThat(StringUtils.isContains((String[]) null, null), is(false));

        assertTrue(StringUtils.isContains("abc", 'a'));
        assertFalse(StringUtils.isContains("abc", 'd'));
        assertFalse(StringUtils.isContains("", 'a'));
        assertFalse(StringUtils.isContains(null, 'a'));

        assertTrue(StringUtils.isNotContains("abc", 'd'));
        assertFalse(StringUtils.isNotContains("abc", 'a'));
        assertTrue(StringUtils.isNotContains("", 'a'));
        assertTrue(StringUtils.isNotContains(null, 'a'));
    }

    @Test
    void testIsNumeric() throws Exception {
        assertThat(StringUtils.isNumeric("123", false), is(true));
        assertThat(StringUtils.isNumeric("1a3", false), is(false));
        assertThat(StringUtils.isNumeric(null, false), is(false));

        assertThat(StringUtils.isNumeric("0", true), is(true));
        assertThat(StringUtils.isNumeric("0.1", true), is(true));
        assertThat(StringUtils.isNumeric("DUBBO", true), is(false));
        assertThat(StringUtils.isNumeric("", true), is(false));
        assertThat(StringUtils.isNumeric(" ", true), is(false));
        assertThat(StringUtils.isNumeric("   ", true), is(false));

        assertThat(StringUtils.isNumeric("123.3.3", true), is(false));
        assertThat(StringUtils.isNumeric("123.", true), is(true));
        assertThat(StringUtils.isNumeric(".123", true), is(true));
        assertThat(StringUtils.isNumeric("..123", true), is(false));
    }

    @Test
    void testJoinCollectionString() throws Exception {
        List<String> list = new ArrayList<String>();
        assertEquals("", StringUtils.join(list, ","));

        list.add("v1");
        assertEquals("v1", StringUtils.join(list, "-"));

        list.add("v2");
        list.add("v3");
        String out = StringUtils.join(list, ":");
        assertEquals("v1:v2:v3", out);
    }

    @Test
    void testCamelToSplitName() throws Exception {
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("abCdEf", "-"));
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("AbCdEf", "-"));
        assertEquals("abcdef", StringUtils.camelToSplitName("abcdef", "-"));
        // assertEquals("name", StringUtils.camelToSplitName("NAME", "-"));

        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("ab-cd-ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.camelToSplitName("Ab-Cd-Ef", "-"));
        assertEquals("Ab_Cd_Ef", StringUtils.camelToSplitName("Ab_Cd_Ef", "-"));
        assertEquals("AB_CD_EF", StringUtils.camelToSplitName("AB_CD_EF", "-"));

        assertEquals("ab.cd.ef", StringUtils.camelToSplitName("AbCdEf", "."));
        // assertEquals("ab.cd.ef", StringUtils.camelToSplitName("ab-cd-ef", "."));
    }

    @Test
    void testSnakeCaseToSplitName() throws Exception {
        assertEquals("ab-cd-ef", StringUtils.snakeToSplitName("ab_Cd_Ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.snakeToSplitName("Ab_Cd_Ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.snakeToSplitName("ab_cd_ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.snakeToSplitName("AB_CD_EF", "-"));
        assertEquals("abcdef", StringUtils.snakeToSplitName("abcdef", "-"));
        assertEquals("qosEnable", StringUtils.snakeToSplitName("qosEnable", "-"));
        assertEquals("name", StringUtils.snakeToSplitName("NAME", "-"));
    }

    @Test
    void testConvertToSplitName() {
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("ab_Cd_Ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("Ab_Cd_Ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("ab_cd_ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("AB_CD_EF", "-"));
        assertEquals("abcdef", StringUtils.convertToSplitName("abcdef", "-"));
        assertEquals("qos-enable", StringUtils.convertToSplitName("qosEnable", "-"));
        assertEquals("name", StringUtils.convertToSplitName("NAME", "-"));

        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("abCdEf", "-"));
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("AbCdEf", "-"));

        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("ab-cd-ef", "-"));
        assertEquals("ab-cd-ef", StringUtils.convertToSplitName("Ab-Cd-Ef", "-"));
    }

    @Test
    void testToArgumentString() throws Exception {
        String s = StringUtils.toArgumentString(new Object[] {"a", 0, Collections.singletonMap("enabled", true)});
        assertThat(s, containsString("a,"));
        assertThat(s, containsString("0,"));
        assertThat(s, containsString("{\"enabled\":true}"));
    }

    @Test
    void testTrim() {
        assertEquals("left blank", StringUtils.trim(" left blank"));
        assertEquals("right blank", StringUtils.trim("right blank "));
        assertEquals("bi-side blank", StringUtils.trim(" bi-side blank "));
    }

    @Test
    void testToURLKey() {
        assertEquals("dubbo.tag1", StringUtils.toURLKey("dubbo_tag1"));
        assertEquals("dubbo.tag1.tag11", StringUtils.toURLKey("dubbo-tag1_tag11"));
    }

    @Test
    void testToOSStyleKey() {
        assertEquals("DUBBO_TAG1", StringUtils.toOSStyleKey("dubbo_tag1"));
        assertEquals("DUBBO_TAG1", StringUtils.toOSStyleKey("dubbo.tag1"));
        assertEquals("DUBBO_TAG1_TAG11", StringUtils.toOSStyleKey("dubbo.tag1.tag11"));
        assertEquals("DUBBO_TAG1", StringUtils.toOSStyleKey("tag1"));
    }

    @Test
    void testParseParameters() {
        String legalStr = "[{key1:value1},{key2:value2}]";
        Map<String, String> legalMap = StringUtils.parseParameters(legalStr);
        assertEquals(2, legalMap.size());
        assertEquals("value2", legalMap.get("key2"));

        String str = StringUtils.encodeParameters(legalMap);
        assertEqualsWithoutSpaces(legalStr, str);

        String legalSpaceStr = "[{key1: value1}, {key2 :value2}]";
        Map<String, String> legalSpaceMap = StringUtils.parseParameters(legalSpaceStr);
        assertEquals(2, legalSpaceMap.size());
        assertEquals("value2", legalSpaceMap.get("key2"));

        str = StringUtils.encodeParameters(legalSpaceMap);
        assertEqualsWithoutSpaces(legalSpaceStr, str);

        String legalSpecialStr = "[{key-1: value*.1}, {key.2 :value*.-_2}]";
        Map<String, String> legalSpecialMap = StringUtils.parseParameters(legalSpecialStr);
        assertEquals(2, legalSpecialMap.size());
        assertEquals("value*.1", legalSpecialMap.get("key-1"));
        assertEquals("value*.-_2", legalSpecialMap.get("key.2"));

        str = StringUtils.encodeParameters(legalSpecialMap);
        assertEqualsWithoutSpaces(legalSpecialStr, str);

        String illegalStr = "[{key=value},{aa:bb}]";
        Map<String, String> illegalMap = StringUtils.parseParameters(illegalStr);
        assertEquals(0, illegalMap.size());

        str = StringUtils.encodeParameters(illegalMap);
        assertEquals(null, str);

        String emptyMapStr = "[]";
        Map<String, String> emptyMap = StringUtils.parseParameters(emptyMapStr);
        assertEquals(0, emptyMap.size());
    }

    @Test
    void testEncodeParameters() {
        Map<String, String> nullValueMap = new LinkedHashMap<>();
        nullValueMap.put("client", null);
        String str = StringUtils.encodeParameters(nullValueMap);
        assertEquals("[]", str);

        Map<String, String> blankValueMap = new LinkedHashMap<>();
        blankValueMap.put("client", " ");
        str = StringUtils.encodeParameters(nullValueMap);
        assertEquals("[]", str);

        blankValueMap = new LinkedHashMap<>();
        blankValueMap.put("client", "");
        str = StringUtils.encodeParameters(nullValueMap);
        assertEquals("[]", str);
    }

    private void assertEqualsWithoutSpaces(String expect, String actual) {
        assertEquals(expect.replaceAll(" ", ""), actual.replaceAll(" ", ""));
    }

    /**
     * Test {@link StringUtils#toCommaDelimitedString(String, String...)}
     *
     * @since 2.7.8
     */
    @Test
    void testToCommaDelimitedString() {
        String value = toCommaDelimitedString(null);
        assertNull(value);

        value = toCommaDelimitedString(null, null);
        assertNull(value);

        value = toCommaDelimitedString("one", null);
        assertEquals("one", value);

        value = toCommaDelimitedString("");
        assertEquals("", value);

        value = toCommaDelimitedString("one");
        assertEquals("one", value);

        value = toCommaDelimitedString("one", "two");
        assertEquals("one,two", value);

        value = toCommaDelimitedString("one", "two", "three");
        assertEquals("one,two,three", value);
    }

    @Test
    void testStartsWithIgnoreCase() {
        assertTrue(startsWithIgnoreCase("dubbo.application.name", "dubbo.application."));
        assertTrue(startsWithIgnoreCase("dubbo.Application.name", "dubbo.application."));
        assertTrue(startsWithIgnoreCase("Dubbo.application.name", "dubbo.application."));
    }
}
