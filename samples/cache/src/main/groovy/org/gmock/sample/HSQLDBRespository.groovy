package org.gmock.sample

import groovy.sql.Sql
import java.sql.SQLException

class HSQLDBRespository implements Respository {

    Sql sql

    HSQLDBRespository() {
        sql = Sql.newInstance("jdbc:hsqldb:mem:sample/cache", "sa", "", "org.hsqldb.jdbcDriver")
    }

    private getValue(String key) {
        try {
            return sql.firstRow("select value from respository where key = ?", [key])?.value
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
            sql.execute("update respository set value = ? where key = ?", [value, key])
        } else {
            sql.execute("insert into respository(key, value) values (?, ?)", [key, value])
        }
    }

}
