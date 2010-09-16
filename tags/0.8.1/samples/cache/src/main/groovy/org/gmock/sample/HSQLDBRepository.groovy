package org.gmock.sample

import groovy.sql.Sql
import java.sql.SQLException

class HSQLDBRepository implements Repository {

    Sql sql

    HSQLDBRepository() {
        sql = Sql.newInstance("jdbc:hsqldb:mem:sample/cache", "sa", "", "org.hsqldb.jdbcDriver")
    }

    private getValue(String key) {
        try {
            return sql.firstRow("select value from repository where key = ?", [key])?.value
        } catch (SQLException e) {
            return null
        }
    }

    Object get(String key) {
        def value = getValue(key)
        if (!value) {
            throw new NotFoundException(key)
        } else {
            return value
        }
    }

    void put(String key, Object value) {
        if (getValue(key)) {
            sql.execute("update repository set value = ? where key = ?", [value, key])
        } else {
            sql.execute("insert into repository(key, value) values (?, ?)", [key, value])
        }
    }

}
