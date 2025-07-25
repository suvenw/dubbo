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

import org.apache.dubbo.common.model.Person;
import org.apache.dubbo.common.model.SerializablePerson;
import org.apache.dubbo.common.model.User;
import org.apache.dubbo.common.model.person.Ageneric;
import org.apache.dubbo.common.model.person.Bgeneric;
import org.apache.dubbo.common.model.person.BigPerson;
import org.apache.dubbo.common.model.person.Cgeneric;
import org.apache.dubbo.common.model.person.Dgeneric;
import org.apache.dubbo.common.model.person.FullAddress;
import org.apache.dubbo.common.model.person.PersonInfo;
import org.apache.dubbo.common.model.person.PersonMap;
import org.apache.dubbo.common.model.person.PersonStatus;
import org.apache.dubbo.common.model.person.Phone;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PojoUtilsTest {

    BigPerson bigPerson;

    {
        bigPerson = new BigPerson();
        bigPerson.setPersonId("id1");
        bigPerson.setLoginName("name1");
        bigPerson.setStatus(PersonStatus.ENABLED);
        bigPerson.setEmail("abc@123.com");
        bigPerson.setPenName("pname");

        ArrayList<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("86", "0571", "11223344", "001");
        Phone phone2 = new Phone("86", "0571", "11223344", "002");
        phones.add(phone1);
        phones.add(phone2);

        PersonInfo pi = new PersonInfo();
        pi.setPhones(phones);
        Phone fax = new Phone("86", "0571", "11223344", null);
        pi.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "1234", "Road1", "333444");
        pi.setFullAddress(addr);
        pi.setMobileNo("1122334455");
        pi.setMale(true);
        pi.setDepartment("b2b");
        pi.setHomepageUrl("www.abc.com");
        pi.setJobTitle("dev");
        pi.setName("name2");

        bigPerson.setInfoProfile(pi);
    }

    private static Child newChild(String name, int age) {
        Child result = new Child();
        result.setName(name);
        result.setAge(age);
        return result;
    }

    public void assertObject(Object data) {
        assertObject(data, null);
    }

    public void assertObject(Object data, Type type) {
        Object generalize = PojoUtils.generalize(data);
        Object realize = PojoUtils.realize(generalize, data.getClass(), type);
        assertEquals(data, realize);
    }

    public <T> void assertArrayObject(T[] data) {
        Object generalize = PojoUtils.generalize(data);
        @SuppressWarnings("unchecked")
        T[] realize = (T[]) PojoUtils.realize(generalize, data.getClass());
        assertArrayEquals(data, realize);
    }

    @Test
    void test_primitive() throws Exception {
        assertObject(Boolean.TRUE);
        assertObject(Boolean.FALSE);

        assertObject(Byte.valueOf((byte) 78));

        assertObject('a');
        assertObject('中');

        assertObject(Short.valueOf((short) 37));

        assertObject(78);

        assertObject(123456789L);

        assertObject(3.14F);
        assertObject(3.14D);
    }

    @Test
    void test_pojo() throws Exception {
        assertObject(new Person());
        assertObject(new BasicTestData(false, '\0', (byte) 0, (short) 0, 0, 0L, 0F, 0D));
        assertObject(new SerializablePerson(Character.MIN_VALUE, false));
    }

    @Test
    void test_has_no_nullary_constructor_pojo() {
        assertObject(new User(1, "fibbery"));
    }

    @Test
    void test_Map_List_pojo() throws Exception {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();

        List<Object> list = new ArrayList<Object>();
        list.add(new Person());
        list.add(new SerializablePerson(Character.MIN_VALUE, false));

        map.put("k", list);

        Object generalize = PojoUtils.generalize(map);
        Object realize = PojoUtils.realize(generalize, Map.class);
        assertEquals(map, realize);
    }

    @Test
    void test_PrimitiveArray() throws Exception {
        assertObject(new boolean[] {true, false});
        assertObject(new Boolean[] {true, false, true});

        assertObject(new byte[] {1, 12, 28, 78});
        assertObject(new Byte[] {1, 12, 28, 78});

        assertObject(new char[] {'a', '中', '无'});
        assertObject(new Character[] {'a', '中', '无'});

        assertObject(new short[] {37, 39, 12});
        assertObject(new Short[] {37, 39, 12});

        assertObject(new int[] {37, -39, 12456});
        assertObject(new Integer[] {37, -39, 12456});

        assertObject(new long[] {37L, -39L, 123456789L});
        assertObject(new Long[] {37L, -39L, 123456789L});

        assertObject(new float[] {37F, -3.14F, 123456.7F});
        assertObject(new Float[] {37F, -39F, 123456.7F});

        assertObject(new double[] {37D, -3.14D, 123456.7D});
        assertObject(new Double[] {37D, -39D, 123456.7D});

        assertArrayObject(new Boolean[] {true, false, true});

        assertArrayObject(new Byte[] {1, 12, 28, 78});

        assertArrayObject(new Character[] {'a', '中', '无'});

        assertArrayObject(new Short[] {37, 39, 12});

        assertArrayObject(new Integer[] {37, -39, 12456});

        assertArrayObject(new Long[] {37L, -39L, 123456789L});

        assertArrayObject(new Float[] {37F, -39F, 123456.7F});

        assertArrayObject(new Double[] {37D, -39D, 123456.7D});

        assertObject(new int[][] {{37, -39, 12456}});
        assertObject(new Integer[][][] {{{37, -39, 12456}}});

        assertArrayObject(new Integer[] {37, -39, 12456});
    }

    @Test
    void test_PojoArray() throws Exception {
        Person[] array = new Person[2];
        array[0] = new Person();
        {
            Person person = new Person();
            person.setName("xxxx");
            array[1] = person;
        }
        assertArrayObject(array);
    }

    @Test
    void testArrayToCollection() throws Exception {
        Person[] array = new Person[2];
        Person person1 = new Person();
        person1.setName("person1");
        Person person2 = new Person();
        person2.setName("person2");
        array[0] = person1;
        array[1] = person2;
        Object o = PojoUtils.realize(PojoUtils.generalize(array), LinkedList.class);
        assertTrue(o instanceof LinkedList);
        assertEquals(((List) o).get(0), person1);
        assertEquals(((List) o).get(1), person2);
    }

    @Test
    void testCollectionToArray() throws Exception {
        Person person1 = new Person();
        person1.setName("person1");
        Person person2 = new Person();
        person2.setName("person2");
        List<Person> list = new LinkedList<Person>();
        list.add(person1);
        list.add(person2);
        Object o = PojoUtils.realize(PojoUtils.generalize(list), Person[].class);
        assertTrue(o instanceof Person[]);
        assertEquals(((Person[]) o)[0], person1);
        assertEquals(((Person[]) o)[1], person2);
    }

    @Test
    void testMapToEnum() throws Exception {
        Map map = new HashMap();
        map.put("name", "MONDAY");
        Object o = PojoUtils.realize(map, Day.class);
        assertEquals(o, Day.MONDAY);
    }

    @Test
    void testGeneralizeEnumArray() throws Exception {
        Object days = new Enum[] {Day.FRIDAY, Day.SATURDAY};
        Object o = PojoUtils.generalize(days);
        assertTrue(o instanceof String[]);
        assertEquals(((String[]) o)[0], "FRIDAY");
        assertEquals(((String[]) o)[1], "SATURDAY");
    }

    @Test
    void testGeneralizePersons() throws Exception {
        Object persons = new Person[] {new Person(), new Person()};
        Object o = PojoUtils.generalize(persons);
        assertTrue(o instanceof Object[]);
        assertEquals(((Object[]) o).length, 2);
    }

    @Test
    void testMapToInterface() throws Exception {
        Map map = new HashMap();
        map.put("content", "greeting");
        map.put("from", "dubbo");
        map.put("urgent", true);
        Object o = PojoUtils.realize(map, Message.class);
        Message message = (Message) o;
        assertThat(message.getContent(), equalTo("greeting"));
        assertThat(message.getFrom(), equalTo("dubbo"));
        assertTrue(message.isUrgent());
    }

    @Test
    void testJsonObjectToMap() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setMap", Map.class);
        assertNotNull(method);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", "test");
        @SuppressWarnings("unchecked")
        Map<Integer, Object> value = (Map<Integer, Object>)
                PojoUtils.realize(jsonObject, method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
        method.invoke(new PojoUtilsTest(), value);
        assertEquals("test", value.get(1));
    }

    @Test
    void testListJsonObjectToListMap() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setListMap", List.class);
        assertNotNull(method);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("1", "test");
        List<JSONObject> list = new ArrayList<>(1);
        list.add(jsonObject);
        @SuppressWarnings("unchecked")
        List<Map<Integer, Object>> result = (List<Map<Integer, Object>>)
                PojoUtils.realize(list, method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
        method.invoke(new PojoUtilsTest(), result);
        assertEquals("test", result.get(0).get(1));
    }

    public void setMap(Map<Integer, Object> map) {}

    public void setListMap(List<Map<Integer, Object>> list) {}

    @Test
    void testException() throws Exception {
        Map map = new HashMap();
        map.put("message", "dubbo exception");
        Object o = PojoUtils.realize(map, RuntimeException.class);
        assertEquals(((Throwable) o).getMessage(), "dubbo exception");
    }

    @Test
    void testIsPojo() throws Exception {
        assertFalse(PojoUtils.isPojo(boolean.class));
        assertFalse(PojoUtils.isPojo(Map.class));
        assertFalse(PojoUtils.isPojo(List.class));
        assertTrue(PojoUtils.isPojo(Person.class));
    }

    public List<Person> returnListPersonMethod() {
        return null;
    }

    public BigPerson returnBigPersonMethod() {
        return null;
    }

    public Type getType(String methodName) {
        Method method;
        try {
            method = getClass().getDeclaredMethod(methodName, new Class<?>[] {});
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        Type gtype = method.getGenericReturnType();
        return gtype;
    }

    @Test
    void test_simpleCollection() throws Exception {
        Type gtype = getType("returnListPersonMethod");
        List<Person> list = new ArrayList<Person>();
        list.add(new Person());
        {
            Person person = new Person();
            person.setName("xxxx");
            list.add(person);
        }
        assertObject(list, gtype);
    }

    @Test
    void test_total() throws Exception {
        Object generalize = PojoUtils.generalize(bigPerson);
        Type gtype = getType("returnBigPersonMethod");
        Object realize = PojoUtils.realize(generalize, BigPerson.class, gtype);
        assertEquals(bigPerson, realize);
    }

    @Test
    void test_total_Array() throws Exception {
        Object[] persons = new Object[] {bigPerson, bigPerson, bigPerson};

        Object generalize = PojoUtils.generalize(persons);
        Object[] realize = (Object[]) PojoUtils.realize(generalize, Object[].class);
        assertArrayEquals(persons, realize);
    }

    @Test
    void test_Loop_pojo() throws Exception {
        Parent p = new Parent();
        p.setAge(10);
        p.setName("jerry");

        Child c = new Child();
        c.setToy("haha");

        p.setChild(c);
        c.setParent(p);

        Object generalize = PojoUtils.generalize(p);
        Parent parent = (Parent) PojoUtils.realize(generalize, Parent.class);

        assertEquals(10, parent.getAge());
        assertEquals("jerry", parent.getName());

        assertEquals("haha", parent.getChild().getToy());
        assertSame(parent, parent.getChild().getParent());
    }

    @Test
    void test_Loop_Map() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("k", "v");
        map.put("m", map);
        assertSame(map, map.get("m"));
        Object generalize = PojoUtils.generalize(map);
        @SuppressWarnings("unchecked")
        Map<String, Object> ret = (Map<String, Object>) PojoUtils.realize(generalize, Map.class);

        assertEquals("v", ret.get("k"));
        assertSame(ret, ret.get("m"));
    }

    @Test
    void test_LoopPojoInMap() throws Exception {
        Parent p = new Parent();
        p.setAge(10);
        p.setName("jerry");

        Child c = new Child();
        c.setToy("haha");

        p.setChild(c);
        c.setParent(p);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k", p);

        Object generalize = PojoUtils.generalize(map);
        @SuppressWarnings("unchecked")
        Map<String, Object> realize =
                (Map<String, Object>) PojoUtils.realize(generalize, Map.class, getType("getMapGenericType"));

        Parent parent = (Parent) realize.get("k");

        assertEquals(10, parent.getAge());
        assertEquals("jerry", parent.getName());

        assertEquals("haha", parent.getChild().getToy());
        assertSame(parent, parent.getChild().getParent());
    }

    @Test
    void test_LoopPojoInList() throws Exception {
        Parent p = new Parent();
        p.setAge(10);
        p.setName("jerry");

        Child c = new Child();
        c.setToy("haha");

        p.setChild(c);
        c.setParent(p);

        List<Object> list = new ArrayList<Object>();
        list.add(p);

        Object generalize = PojoUtils.generalize(list);
        @SuppressWarnings("unchecked")
        List<Object> realize = (List<Object>) PojoUtils.realize(generalize, List.class, getType("getListGenericType"));

        Parent parent = (Parent) realize.get(0);

        assertEquals(10, parent.getAge());
        assertEquals("jerry", parent.getName());

        assertEquals("haha", parent.getChild().getToy());
        assertSame(parent, parent.getChild().getParent());

        Object[] objects = PojoUtils.realize(
                new Object[] {generalize}, new Class[] {List.class}, new Type[] {getType("getListGenericType")});
        assertTrue(((List) objects[0]).get(0) instanceof Parent);
    }

    @Test
    void test_PojoInList() throws Exception {
        Parent p = new Parent();
        p.setAge(10);
        p.setName("jerry");

        List<Object> list = new ArrayList<Object>();
        list.add(p);

        Object generalize = PojoUtils.generalize(list);
        @SuppressWarnings("unchecked")
        List<Object> realize = (List<Object>) PojoUtils.realize(generalize, List.class, getType("getListGenericType"));

        Parent parent = (Parent) realize.get(0);

        assertEquals(10, parent.getAge());
        assertEquals("jerry", parent.getName());
    }

    public void setLong(long l) {}

    public void setInt(int l) {}

    public List<Parent> getListGenericType() {
        return null;
    }

    public Map<String, Parent> getMapGenericType() {
        return null;
    }

    // java.lang.IllegalArgumentException: argument type mismatch
    @Test
    void test_realize_LongPararmter_IllegalArgumentException() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setLong", long.class);
        assertNotNull(method);

        Object value = PojoUtils.realize(
                "563439743927993", method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);

        method.invoke(new PojoUtilsTest(), value);
    }

    // java.lang.IllegalArgumentException: argument type mismatch
    @Test
    void test_realize_IntPararmter_IllegalArgumentException() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setInt", int.class);
        assertNotNull(method);

        Object value = PojoUtils.realize("123", method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);

        method.invoke(new PojoUtilsTest(), value);
    }

    @Test
    void testStackOverflow() throws Exception {
        Parent parent = Parent.getNewParent();
        parent.setAge(Integer.MAX_VALUE);
        String name = UUID.randomUUID().toString();
        parent.setName(name);
        Object generalize = PojoUtils.generalize(parent);
        assertTrue(generalize instanceof Map);
        Map map = (Map) generalize;
        assertEquals(Integer.MAX_VALUE, map.get("age"));
        assertEquals(name, map.get("name"));

        Parent realize = (Parent) PojoUtils.realize(generalize, Parent.class);
        assertEquals(Integer.MAX_VALUE, realize.getAge());
        assertEquals(name, realize.getName());
    }

    @Test
    void testGenerializeAndRealizeClass() throws Exception {
        Object generalize = PojoUtils.generalize(Integer.class);
        assertEquals(Integer.class.getName(), generalize);
        Object real = PojoUtils.realize(generalize, Integer.class.getClass());
        assertEquals(Integer.class, real);

        generalize = PojoUtils.generalize(int[].class);
        assertEquals(int[].class.getName(), generalize);
        real = PojoUtils.realize(generalize, int[].class.getClass());
        assertEquals(int[].class, real);
    }

    @Test
    void testPublicField() throws Exception {
        Parent parent = new Parent();
        parent.gender = "female";
        parent.email = "email@host.com";
        parent.setEmail("securityemail@host.com");
        Child child = new Child();
        parent.setChild(child);
        child.gender = "male";
        child.setAge(20);
        child.setParent(parent);
        Object obj = PojoUtils.generalize(parent);
        Parent realizedParent = (Parent) PojoUtils.realize(obj, Parent.class);
        Assertions.assertEquals(parent.gender, realizedParent.gender);
        Assertions.assertEquals(child.gender, parent.getChild().gender);
        Assertions.assertEquals(child.age, realizedParent.getChild().getAge());
        Assertions.assertEquals(parent.getEmail(), realizedParent.getEmail());
        Assertions.assertNull(realizedParent.email);
    }

    @Test
    void testMapField() throws Exception {
        TestData data = new TestData();
        Child child = newChild("first", 1);
        data.addChild(child);
        child = newChild("second", 2);
        data.addChild(child);
        child = newChild("third", 3);
        data.addChild(child);

        data.setList(Arrays.asList(newChild("forth", 4)));

        Object obj = PojoUtils.generalize(data);
        Assertions.assertEquals(3, data.getChildren().size());
        assertSame(data.getChildren().get("first").getClass(), Child.class);
        Assertions.assertEquals(1, data.getList().size());
        assertSame(data.getList().get(0).getClass(), Child.class);

        TestData realizadData = (TestData) PojoUtils.realize(obj, TestData.class);
        Assertions.assertEquals(
                data.getChildren().size(), realizadData.getChildren().size());
        Assertions.assertEquals(
                data.getChildren().keySet(), realizadData.getChildren().keySet());
        for (Map.Entry<String, Child> entry : data.getChildren().entrySet()) {
            Child c = realizadData.getChildren().get(entry.getKey());
            Assertions.assertNotNull(c);
            Assertions.assertEquals(entry.getValue().getName(), c.getName());
            Assertions.assertEquals(entry.getValue().getAge(), c.getAge());
        }

        Assertions.assertEquals(1, realizadData.getList().size());
        Assertions.assertEquals(
                data.getList().get(0).getName(), realizadData.getList().get(0).getName());
        Assertions.assertEquals(
                data.getList().get(0).getAge(), realizadData.getList().get(0).getAge());
    }

    @Test
    void testRealize() throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("key", "value");
        Object obj = PojoUtils.generalize(map);
        assertTrue(obj instanceof LinkedHashMap);
        Object outputObject = PojoUtils.realize(map, LinkedHashMap.class);
        assertTrue(outputObject instanceof LinkedHashMap);
        Object[] objects = PojoUtils.realize(new Object[] {map}, new Class[] {LinkedHashMap.class});
        assertTrue(objects[0] instanceof LinkedHashMap);
        assertEquals(objects[0], outputObject);
    }

    @Test
    void testRealizeLinkedList() throws Exception {
        LinkedList<Person> input = new LinkedList<Person>();
        Person person = new Person();
        person.setAge(37);
        input.add(person);
        Object obj = PojoUtils.generalize(input);
        assertTrue(obj instanceof List);
        assertTrue(input.get(0) instanceof Person);
        Object output = PojoUtils.realize(obj, LinkedList.class);
        assertTrue(output instanceof LinkedList);
    }

    @Test
    void testPojoList() throws Exception {
        ListResult<Parent> result = new ListResult<Parent>();
        List<Parent> list = new ArrayList<Parent>();
        Parent parent = new Parent();
        parent.setAge(Integer.MAX_VALUE);
        parent.setName("zhangsan");
        list.add(parent);
        result.setResult(list);

        Object generializeObject = PojoUtils.generalize(result);
        Object realizeObject = PojoUtils.realize(generializeObject, ListResult.class);
        assertTrue(realizeObject instanceof ListResult);
        ListResult listResult = (ListResult) realizeObject;
        List l = listResult.getResult();
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof Parent);
        Parent realizeParent = (Parent) l.get(0);
        Assertions.assertEquals(parent.getName(), realizeParent.getName());
        Assertions.assertEquals(parent.getAge(), realizeParent.getAge());
    }

    @Test
    void testListPojoListPojo() throws Exception {
        InnerPojo<Parent> parentList = new InnerPojo<Parent>();
        Parent parent = new Parent();
        parent.setName("zhangsan");
        parent.setAge(Integer.MAX_VALUE);
        parentList.setList(Arrays.asList(parent));

        ListResult<InnerPojo<Parent>> list = new ListResult<InnerPojo<Parent>>();
        list.setResult(Arrays.asList(parentList));

        Object generializeObject = PojoUtils.generalize(list);
        Object realizeObject = PojoUtils.realize(generializeObject, ListResult.class);

        assertTrue(realizeObject instanceof ListResult);
        ListResult realizeList = (ListResult) realizeObject;
        List realizeInnerList = realizeList.getResult();
        Assertions.assertEquals(1, realizeInnerList.size());
        assertTrue(realizeInnerList.get(0) instanceof InnerPojo);
        InnerPojo realizeParentList = (InnerPojo) realizeInnerList.get(0);
        Assertions.assertEquals(1, realizeParentList.getList().size());
        assertTrue(realizeParentList.getList().get(0) instanceof Parent);
        Parent realizeParent = (Parent) realizeParentList.getList().get(0);
        Assertions.assertEquals(parent.getName(), realizeParent.getName());
        Assertions.assertEquals(parent.getAge(), realizeParent.getAge());
    }

    @Test
    void testDateTimeTimestamp() throws Exception {
        String dateStr = "2018-09-12";
        String timeStr = "10:12:33";
        String dateTimeStr = "2018-09-12 10:12:33";
        String[] dateFormat = new String[] {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

        // java.util.Date
        Object date = PojoUtils.realize(dateTimeStr, Date.class, (Type) Date.class);
        assertEquals(Date.class, date.getClass());
        assertEquals(dateTimeStr, new SimpleDateFormat(dateFormat[0]).format(date));

        // java.sql.Time
        Object time = PojoUtils.realize(dateTimeStr, java.sql.Time.class, (Type) java.sql.Time.class);
        assertEquals(java.sql.Time.class, time.getClass());
        assertEquals(timeStr, new SimpleDateFormat(dateFormat[2]).format(time));

        // java.sql.Date
        Object sqlDate = PojoUtils.realize(dateTimeStr, java.sql.Date.class, (Type) java.sql.Date.class);
        assertEquals(java.sql.Date.class, sqlDate.getClass());
        assertEquals(dateStr, new SimpleDateFormat(dateFormat[1]).format(sqlDate));

        // java.sql.Timestamp
        Object timestamp = PojoUtils.realize(dateTimeStr, java.sql.Timestamp.class, (Type) java.sql.Timestamp.class);
        assertEquals(java.sql.Timestamp.class, timestamp.getClass());
        assertEquals(dateTimeStr, new SimpleDateFormat(dateFormat[0]).format(timestamp));
    }

    @Test
    void testIntToBoolean() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "myname");
        map.put("male", 1);
        map.put("female", 0);

        PersonInfo personInfo = (PersonInfo) PojoUtils.realize(map, PersonInfo.class);

        assertEquals("myname", personInfo.getName());
        assertTrue(personInfo.isMale());
        assertFalse(personInfo.isFemale());
    }

    @Test
    void testRealizeCollectionWithNullElement() {
        LinkedList<String> listStr = new LinkedList<>();
        listStr.add("arrayValue");
        listStr.add(null);
        HashSet<String> setStr = new HashSet<>();
        setStr.add("setValue");
        setStr.add(null);

        Object listResult = PojoUtils.realize(listStr, LinkedList.class);
        assertEquals(LinkedList.class, listResult.getClass());
        assertEquals(listResult, listStr);

        Object setResult = PojoUtils.realize(setStr, HashSet.class);
        assertEquals(HashSet.class, setResult.getClass());
        assertEquals(setResult, setStr);
    }

    @Test
    void testJava8Time() {

        Object localDateTimeGen = PojoUtils.generalize(LocalDateTime.now());
        Object localDateTime = PojoUtils.realize(localDateTimeGen, LocalDateTime.class);
        assertEquals(localDateTimeGen, localDateTime.toString());

        Object localDateGen = PojoUtils.generalize(LocalDate.now());
        Object localDate = PojoUtils.realize(localDateGen, LocalDate.class);
        assertEquals(localDateGen, localDate.toString());

        Object localTimeGen = PojoUtils.generalize(LocalTime.now());
        Object localTime = PojoUtils.realize(localTimeGen, LocalTime.class);
        assertEquals(localTimeGen, localTime.toString());
    }

    @Test
    public void testJSONObjectToPersonMapPojo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personId", "1");
        jsonObject.put("personName", "hand");
        Object result = PojoUtils.realize(jsonObject, PersonMap.class);
        assertEquals(PersonMap.class, result.getClass());
    }

    protected PersonInfo createPersonInfoByName(String name) {
        PersonInfo dataPerson = new PersonInfo();
        dataPerson.setName(name);
        return dataPerson;
    }

    protected Ageneric<PersonInfo> createAGenericPersonInfo(String name) {
        Ageneric<PersonInfo> ret = new Ageneric();
        ret.setData(createPersonInfoByName(name));
        return ret;
    }

    protected Bgeneric<PersonInfo> createBGenericPersonInfo(String name) {
        Bgeneric<PersonInfo> ret = new Bgeneric();
        ret.setData(createPersonInfoByName(name));
        return ret;
    }

    @Test
    public void testPojoGeneric1() throws NoSuchMethodException {
        String personName = "testName";

        {
            Ageneric<PersonInfo> genericPersonInfo = createAGenericPersonInfo(personName);

            Object o = JSON.toJSON(genericPersonInfo);
            {
                Ageneric personInfo = (Ageneric) PojoUtils.realize(o, Ageneric.class);

                assertEquals(Ageneric.NAME, personInfo.getName());
                assertTrue(personInfo.getData() instanceof Map);
            }
            {
                Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                        PojoUtilsTest.class.getDeclaredMethod("createAGenericPersonInfo", String.class));
                Ageneric personInfo = (Ageneric)
                        PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

                assertEquals(Ageneric.NAME, personInfo.getName());
                assertEquals(personInfo.getData().getClass(), PersonInfo.class);
                assertEquals(personName, ((PersonInfo) personInfo.getData()).getName());
            }
        }
        {
            Bgeneric<PersonInfo> genericPersonInfo = createBGenericPersonInfo(personName);

            Object o = JSON.toJSON(genericPersonInfo);
            {
                Bgeneric personInfo = (Bgeneric) PojoUtils.realize(o, Bgeneric.class);

                assertEquals(Bgeneric.NAME, personInfo.getName());
                assertTrue(personInfo.getData() instanceof Map);
            }
            {
                Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                        PojoUtilsTest.class.getDeclaredMethod("createBGenericPersonInfo", String.class));
                Bgeneric personInfo = (Bgeneric)
                        PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

                assertEquals(Bgeneric.NAME, personInfo.getName());
                assertEquals(personInfo.getData().getClass(), PersonInfo.class);
                assertEquals(personName, ((PersonInfo) personInfo.getData()).getName());
            }
        }
    }

    protected Ageneric<Ageneric<PersonInfo>> createAGenericLoop(String name) {
        Ageneric<Ageneric<PersonInfo>> ret = new Ageneric();
        ret.setData(createAGenericPersonInfo(name));
        return ret;
    }

    protected Bgeneric<Ageneric<PersonInfo>> createBGenericWithAgeneric(String name) {
        Bgeneric<Ageneric<PersonInfo>> ret = new Bgeneric();
        ret.setData(createAGenericPersonInfo(name));
        return ret;
    }

    @Test
    public void testPojoGeneric2() throws NoSuchMethodException {
        String personName = "testName";

        {
            Ageneric<Ageneric<PersonInfo>> generic2PersonInfo = createAGenericLoop(personName);
            Object o = JSON.toJSON(generic2PersonInfo);
            {
                Ageneric personInfo = (Ageneric) PojoUtils.realize(o, Ageneric.class);

                assertEquals(Ageneric.NAME, personInfo.getName());
                assertTrue(personInfo.getData() instanceof Map);
            }
            {
                Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                        PojoUtilsTest.class.getDeclaredMethod("createAGenericLoop", String.class));
                Ageneric personInfo = (Ageneric)
                        PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

                assertEquals(Ageneric.NAME, personInfo.getName());
                assertEquals(personInfo.getData().getClass(), Ageneric.class);
                assertEquals(Ageneric.NAME, ((Ageneric) personInfo.getData()).getName());
                assertEquals(((Ageneric) personInfo.getData()).getData().getClass(), PersonInfo.class);
                assertEquals(personName, ((PersonInfo) ((Ageneric) personInfo.getData()).getData()).getName());
            }
        }
        {
            Bgeneric<Ageneric<PersonInfo>> generic = createBGenericWithAgeneric(personName);
            Object o = JSON.toJSON(generic);
            {
                Ageneric personInfo = (Ageneric) PojoUtils.realize(o, Ageneric.class);

                assertEquals(Bgeneric.NAME, personInfo.getName());
                assertTrue(personInfo.getData() instanceof Map);
            }
            {
                Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                        PojoUtilsTest.class.getDeclaredMethod("createBGenericWithAgeneric", String.class));
                Bgeneric personInfo = (Bgeneric)
                        PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

                assertEquals(Bgeneric.NAME, personInfo.getName());
                assertEquals(personInfo.getData().getClass(), Ageneric.class);
                assertEquals(Ageneric.NAME, ((Ageneric) personInfo.getData()).getName());
                assertEquals(((Ageneric) personInfo.getData()).getData().getClass(), PersonInfo.class);
                assertEquals(personName, ((PersonInfo) ((Ageneric) personInfo.getData()).getData()).getName());
            }
        }
    }

    protected Cgeneric<PersonInfo> createCGenericPersonInfo(String name) {
        Cgeneric<PersonInfo> ret = new Cgeneric();
        ret.setData(createPersonInfoByName(name));
        ret.setA(createAGenericPersonInfo(name));
        ret.setB(createBGenericPersonInfo(name));
        return ret;
    }

    @Test
    public void testPojoGeneric3() throws NoSuchMethodException {
        String personName = "testName";

        Cgeneric<PersonInfo> generic = createCGenericPersonInfo(personName);
        Object o = JSON.toJSON(generic);
        {
            Cgeneric personInfo = (Cgeneric) PojoUtils.realize(o, Cgeneric.class);

            assertEquals(Cgeneric.NAME, personInfo.getName());
            assertTrue(personInfo.getData() instanceof Map);
            assertTrue(personInfo.getA().getData() instanceof Map);
            assertTrue(personInfo.getB().getData() instanceof PersonInfo);
        }
        {
            Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                    PojoUtilsTest.class.getDeclaredMethod("createCGenericPersonInfo", String.class));
            Cgeneric personInfo =
                    (Cgeneric) PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

            assertEquals(Cgeneric.NAME, personInfo.getName());
            assertEquals(personInfo.getData().getClass(), PersonInfo.class);
            assertEquals(personName, ((PersonInfo) personInfo.getData()).getName());

            assertEquals(personInfo.getA().getClass(), Ageneric.class);
            assertEquals(personInfo.getA().getData().getClass(), PersonInfo.class);
            assertEquals(personInfo.getB().getClass(), Bgeneric.class);
            assertEquals(personInfo.getB().getData().getClass(), PersonInfo.class);
        }
    }

    protected Dgeneric<Ageneric<PersonInfo>, Bgeneric<PersonInfo>, Cgeneric<PersonInfo>> createDGenericPersonInfo(
            String name) {
        Dgeneric<Ageneric<PersonInfo>, Bgeneric<PersonInfo>, Cgeneric<PersonInfo>> ret = new Dgeneric();
        ret.setT(createAGenericPersonInfo(name));
        ret.setY(createBGenericPersonInfo(name));
        ret.setZ(createCGenericPersonInfo(name));
        return ret;
    }

    @Test
    public void testPojoGeneric4() throws NoSuchMethodException {
        String personName = "testName";

        Dgeneric generic = createDGenericPersonInfo(personName);
        Object o = JSON.toJSON(generic);
        {
            Dgeneric personInfo = (Dgeneric) PojoUtils.realize(o, Dgeneric.class);

            assertEquals(Dgeneric.NAME, personInfo.getName());
            assertTrue(personInfo.getT() instanceof Map);
            assertTrue(personInfo.getY() instanceof Map);
            assertTrue(personInfo.getZ() instanceof Map);
        }
        {
            Type[] createGenericPersonInfos = ReflectUtils.getReturnTypes(
                    PojoUtilsTest.class.getDeclaredMethod("createDGenericPersonInfo", String.class));
            Dgeneric personInfo =
                    (Dgeneric) PojoUtils.realize(o, (Class) createGenericPersonInfos[0], createGenericPersonInfos[1]);

            assertEquals(Dgeneric.NAME, personInfo.getName());

            assertEquals(personInfo.getT().getClass(), Ageneric.class);
            assertEquals(((Ageneric) personInfo.getT()).getData().getClass(), PersonInfo.class);
            assertEquals(personInfo.getY().getClass(), Bgeneric.class);
            assertEquals(((Bgeneric) personInfo.getY()).getData().getClass(), PersonInfo.class);
            assertEquals(personInfo.getZ().getClass(), Cgeneric.class);
            assertEquals(((Cgeneric) personInfo.getZ()).getData().getClass(), PersonInfo.class);

            assertEquals(personInfo.getZ().getClass(), Cgeneric.class);
            assertEquals(((Cgeneric) personInfo.getZ()).getA().getClass(), Ageneric.class);
            assertEquals(((Cgeneric) personInfo.getZ()).getA().getData().getClass(), PersonInfo.class);
            assertEquals(((Cgeneric) personInfo.getZ()).getB().getClass(), Bgeneric.class);
            assertEquals(((Cgeneric) personInfo.getZ()).getB().getData().getClass(), PersonInfo.class);
        }
    }

    @Test
    void testNameNotMatch() {
        NameNotMatch origin = new NameNotMatch();
        origin.setNameA("test123");
        origin.setNameB("test234");

        Object generalized = PojoUtils.generalize(origin);

        Assertions.assertInstanceOf(Map.class, generalized);
        Assertions.assertEquals("test123", ((Map) generalized).get("nameA"));
        Assertions.assertEquals("test234", ((Map) generalized).get("nameB"));

        NameNotMatch target1 =
                (NameNotMatch) PojoUtils.realize(PojoUtils.generalize(origin), NameNotMatch.class, NameNotMatch.class);
        Assertions.assertEquals(origin, target1);

        Map<String, String> map = new HashMap<>();
        map.put("nameA", "test123");
        map.put("nameB", "test234");

        NameNotMatch target2 = (NameNotMatch) PojoUtils.realize(map, NameNotMatch.class, NameNotMatch.class);
        Assertions.assertEquals(origin, target2);
    }

    class NameNotMatch implements Serializable {
        private String NameA;
        private String NameAbsent;

        public void setNameA(String nameA) {
            this.NameA = nameA;
        }

        public String getNameA() {
            return NameA;
        }

        public void setNameB(String nameB) {
            this.NameAbsent = nameB;
        }

        public String getNameB() {
            return NameAbsent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NameNotMatch that = (NameNotMatch) o;
            return Objects.equals(NameA, that.NameA) && Objects.equals(NameAbsent, that.NameAbsent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(NameA, NameAbsent);
        }
    }

    public enum Day {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }

    public static class BasicTestData implements Serializable {

        public boolean a;
        public char b;
        public byte c;
        public short d;
        public int e;
        public long f;
        public float g;
        public double h;

        public BasicTestData(boolean a, char b, byte c, short d, int e, long f, float g, double h) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
            this.g = g;
            this.h = h;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (a ? 1 : 2);
            result = prime * result + b;
            result = prime * result + c;
            result = prime * result + c;
            result = prime * result + e;
            result = (int) (prime * result + f);
            result = (int) (prime * result + g);
            result = (int) (prime * result + h);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            BasicTestData other = (BasicTestData) obj;
            if (a != other.a) {
                return false;
            }
            if (b != other.b) {
                return false;
            }
            if (c != other.c) {
                return false;
            }
            if (e != other.e) {
                return false;
            }
            if (f != other.f) {
                return false;
            }
            if (g != other.g) {
                return false;
            }
            if (h != other.h) {
                return false;
            }
            return true;
        }
    }

    public static class Parent implements Serializable {
        public String gender;
        public String email;
        String name;
        int age;
        Child child;
        private String securityEmail;

        public static Parent getNewParent() {
            return new Parent();
        }

        public String getEmail() {
            return this.securityEmail;
        }

        public void setEmail(String email) {
            this.securityEmail = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Child getChild() {
            return child;
        }

        public void setChild(Child child) {
            this.child = child;
        }
    }

    public static class Child implements Serializable {
        public String gender;
        public int age;
        String toy;
        Parent parent;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getToy() {
            return toy;
        }

        public void setToy(String toy) {
            this.toy = toy;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }
    }

    public static class TestData implements Serializable {
        private Map<String, Child> children = new HashMap<String, Child>();
        private List<Child> list = new ArrayList<Child>();

        public List<Child> getList() {
            return list;
        }

        public void setList(List<Child> list) {
            if (CollectionUtils.isNotEmpty(list)) {
                this.list.addAll(list);
            }
        }

        public Map<String, Child> getChildren() {
            return children;
        }

        public void setChildren(Map<String, Child> children) {
            if (CollectionUtils.isNotEmptyMap(children)) {
                this.children.putAll(children);
            }
        }

        public void addChild(Child child) {
            this.children.put(child.getName(), child);
        }
    }

    public static class InnerPojo<T> implements Serializable {
        private List<T> list;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }
    }

    public static class ListResult<T> implements Serializable {
        List<T> result;

        public List<T> getResult() {
            return result;
        }

        public void setResult(List<T> result) {
            this.result = result;
        }
    }

    interface Message {
        String getContent();

        String getFrom();

        boolean isUrgent();
    }
}
