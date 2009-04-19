package org.gmock.sample

import groovy.sql.Sql
import java.sql.SQLException
import org.gmock.GMockTestCase
import static org.hamcrest.Matchers.anything

class HSQLDBRespositoryTest extends GMockTestCase {

    Sql sql

    void setUp() {
        sql = mock(Sql)
        sql.static.newInstance(match { it.startsWith("jdbc:hsqldb") }, anything(), anything(), "org.hsqldb.jdbcDriver").returns(sql)
    }

    void testGet() {
        sql.firstRow("select value from repository where key = ?", ["johnny"]).returns([value: "jian"])
        play {
            def hr = new HSQLDBRepository()
            assertEquals "jian", hr.get("johnny")
        }
    }

    void testGetButNotFound() {
        sql.firstRow("select value from repository where key = ?", ["johnny"]).returns(null)
        play {
            def hr = new HSQLDBRepository()
            shouldFail(NotFoundException) {
                hr.get("johnny")
            }
        }
    }

    void testGetButFailed() {
        sql.firstRow("select value from repository where key = ?", ["johnny"]).raises(SQLException)
        play {
            def hr = new HSQLDBRepository()
            shouldFail(NotFoundException) {
                hr.get("johnny")
            }
        }
    }

    void testPutWhileKeyFound() {
        sql.firstRow("select value from repository where key = ?", ["johnny"]).returns([value: "jian"])
        sql.execute("update repository set value = ? where key = ?", ["new", "johnny"])
        play {
            def hr = new HSQLDBRepository()
            hr.put("johnny", "new")
        }
    }

    void testPutWhileKeyNotFound() {
        sql.firstRow("select value from repository where key = ?", ["johnny"]).returns(null)
        sql.execute("insert into repository(key, value) values (?, ?)", ["johnny", "new"])
        play {
            def hr = new HSQLDBRepository()
            hr.put("johnny", "new")
        }
    }

}
