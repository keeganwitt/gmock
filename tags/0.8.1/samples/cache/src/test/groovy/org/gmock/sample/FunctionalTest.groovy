package org.gmock.sample

import groovy.sql.Sql
import org.gmock.GMockTestCase
import static org.hamcrest.Matchers.anything

class FunctionalTest extends GMockTestCase {

    Sql sql

    void setUp() {
        sql = Sql.newInstance("jdbc:hsqldb:mem:sample/cache", "sa", "", "org.hsqldb.jdbcDriver")
        sql.execute("create table repository (key char(10) primary key, value char(10))")
        (1..5).each { i ->
            sql.execute("insert into repository(key, value) values (?, ?)", ["key $i", "value $i"])
        }
    }

    void tearDown() {
        sql.execute("drop table repository")
    }

    void testLRUCache() {
        Repository hr = new HSQLDBRepository()
        Cache cache = new ExpiringCache(new SimpleCache(hr), new LRUStrategy(), 5)

        assertEquals "value 1", cache.get("key 1") // cache: [key 1: value 1]
        cache.put "key 1", "new value"             // cache: [key 1: new value]
        assertEquals "value 1", sql.firstRow("select value from repository where key = 'key 1'").value

        assertEquals "value 2", cache.get("key 2") // cache: [key 1: new value, key 2: value 2]

        cache.put "key 6", "value 6"               // cache: [key 1: new value, key 2: value 2, key 6: value 6]
        assertNull sql.firstRow("select * from repository where key = 'key 6'")

        assertEquals "value 3", cache.get("key 3") // cache: [key 1: new value, key 2: value 2, key 6: value 6, key 3: value 3]
        assertEquals "value 4", cache.get("key 4") // cache: [key 1: new value, key 2: value 2, key 6: value 6, key 3: value 3, key 4: value 4]

        assertEquals "value 5", cache.get("key 5") // cache: [key 2: value 2, key 6: value 6, key 3: value 3, key 4: value 4, key 5: value 5]
        assertEquals "new value", sql.firstRow("select value from repository where key = 'key 1'").value

        cache.put "key 2", "new value"             // cache: [key 6: value 6, key 3: value 3, key 4: value 4, key 5: value 5, key 2: new value]
        cache.put "key 7", "value 7"               // cache: [key 3: value 3, key 4: value 4, key 5: value 5, key 2: new value, key 7: value 7]
        assertEquals "value 6", sql.firstRow("select value from repository where key = 'key 6'").value
        assertEquals "value 2", sql.firstRow("select value from repository where key = 'key 2'").value
        assertNull sql.firstRow("select * from repository where key = 'key 7'")

        cache.flush()
        assertEquals "new value", sql.firstRow("select value from repository where key = 'key 2'").value
        assertEquals "value 7", sql.firstRow("select value from repository where key = 'key 7'").value
    }

    void testRandomCache() {
        [3, 2].each { i ->
            mock(Random, constructor()).nextInt(anything()).returns(i)
        }

        Repository hr = new HSQLDBRepository()
        Cache cache = new ExpiringCache(new SimpleCache(hr), new RandomStrategy(), 5)

        play {
            assertEquals "value 1", cache.get("key 1") // cache: [key 1: value 1]
            assertEquals "value 2", cache.get("key 2") // cache: [key 1: value 1, key 2: value 2]

            cache.put "key 6", "value 6"               // cache: [key 1: value 1, key 2: value 2, key 6: value 6]
            assertNull sql.firstRow("select * from repository where key = 'key 6'")

            assertEquals "value 3", cache.get("key 3") // cache: [key 1: value 1, key 2: value 2, key 6: value 6, key 3: value 3]
            cache.put "key 3", "new value"             // cache: [key 1: value 1, key 2: value 2, key 6: value 6, key 3: new value]
            assertEquals "value 3", sql.firstRow("select value from repository where key = 'key 3'").value

            assertEquals "value 4", cache.get("key 4") // cache: [key 1: value 1, key 2: value 2, key 6: value 6, key 3: new value, key 4: value 4]
            assertEquals "value 5", cache.get("key 5") // cache: [key 1: value 1, key 2: value 2, key 6: value 6, key 4: value 4, key 5: value 5]
            assertEquals "new value", sql.firstRow("select value from repository where key = 'key 3'").value

            cache.put "key 2", "new value"             // cache: [key 1: value 1, key 2: new value, key 6: value 6, key 4: value 4, key 5: value 5]
            cache.put "key 7", "value 7"               // cache: [key 1: value 1, key 2: new value, key 4: value 4, key 5: value 5, key 7: value 7]
            assertEquals "value 6", sql.firstRow("select value from repository where key = 'key 6'").value
            assertNull sql.firstRow("select * from repository where key = 'key 7'")

            cache.flush()
            assertEquals "value 7", sql.firstRow("select value from repository where key = 'key 7'").value
        }
    }

}
