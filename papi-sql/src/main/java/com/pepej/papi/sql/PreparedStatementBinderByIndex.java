package com.pepej.papi.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementBinderByIndex<T> {

    void bind(PreparedStatement statement, int index, T value) throws SQLException;
}