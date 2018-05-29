package com.uas.dingzikaifa.dao;

import com.uas.dingzikaifa.util.BaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository("baseDao")
public class BaseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询结果集
     *
     * @param sql
     *            查询语句
     */
    public int getCount(String sql, Object...args) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, args);
        if (rowSet.next()) {
            return rowSet.getInt(1);
        }
            return 0;
    }

    public void execute(List<String> sqls) {
        if (sqls.size() > 0) {
            StringBuffer sb = new StringBuffer("begin ");
            for (String sql : sqls) {
                sb.append("execute immediate '").append(sql.replace("'", "''")).append("';");
            }
            sb.append("end;");
            jdbcTemplate.execute(sb.toString());
        }
    }

    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

    public String callProcedure(final String procedureName, final Object...args) throws IllegalAccessException{
        try {
            return jdbcTemplate.execute(new CallableStatementCreator() {

                @Override
                public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                    StringBuffer storedProcName = new StringBuffer("{call ");
                    int i = 0;
                    storedProcName.append(procedureName + "(");
                    for (i = 0; i < args.length; i++) {
                        if (storedProcName.toString().contains("?")) {
                            storedProcName.append(",");
                        }
                        storedProcName.append("?");
                    }
                    if (storedProcName.toString().contains("?")) {
                        storedProcName.append(",");
                    }
                    storedProcName.append("?");
                    storedProcName.append(")}");
                    CallableStatement cs = connection.prepareCall(storedProcName.toString());
                    for (i = 0; i < args.length; i++) {
                        cs.setObject(i + 1, args[i]);
                    }
                    cs.registerOutParameter(args.length + 1, java.sql.Types.VARCHAR);
                    return cs;
                }
            }, new CallableStatementCallback<String>() {
                @Nullable
                @Override
                public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                    cs.execute();
                    return cs.getString(args.length + 1);
                }
            });
        }catch (Exception e){
            throw new IllegalAccessException(e.getMessage());
        }
    }

    public synchronized String sGetMaxNumber(String myTable, int thisType) throws IllegalAccessException{
        return callProcedure("Sp_GetMaxNumber", new Object[] { myTable, thisType });
    }

    public int getSeq(String seq) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select " + seq + ".nextval from dual");
        if (rowSet.next()) {
            return rowSet.getInt(1);
        }
        return 0;
    }

    public Object[] getFieldsData(String table,String[] fields, String condition) {
        StringBuffer sql = new StringBuffer("select ");
        sql.append(BaseUtil.parseArray2Str(fields, ","));
        sql.append(" from " + table + " where " + condition);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
        Iterator<Map<String, Object>> iter = list.iterator();
        int length = fields.length;
        Object[] results = new Object[length];
        Object value = null;
        if (iter.hasNext()) {
            Map<String, Object> m = iter.next();
            for (int i = 0; i < length; i++) {
                String upperField = fields[i].toUpperCase();
                if (upperField.indexOf(" AS ") > 0) {
                    upperField = upperField.split(" AS ")[1].trim();
                }
                value = m.get(upperField);
                if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
                    Timestamp time = (Timestamp) value;
                    try {
                        value = BaseUtil.parseDateToString(new Date(time.getTime()), "yyyy-MM-dd HH:mm:ss");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                results[i] = value;
            }
            return results;
        }
        return null;
    }

    /**
     * 一个字段，多条结果
     *
     * @param tableName
     *            对应要查询的表
     * @param field
     *            要查询的字段
     * @param condition
     *            查询条件
     * @return field对应的数据
     */
    public List<Object> getFieldDatasByCondition(String tableName, String field, String condition) {
        StringBuffer sb = new StringBuffer("SELECT ");
        sb.append(field);
        sb.append(" FROM ");
        sb.append(tableName);
        sb.append(((condition == null || "".equals(condition)) ? "" : (" WHERE " + condition)));
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sb.toString());
        List<Object> list = new ArrayList<Object>();
        while (srs.next()) {
            list.add(srs.getObject(1));
        }
        return list;
    }

    /**
     * 一个字段，一条结果
     *
     * @param tableName
     *            对应要查询的表
     * @param field
     *            要查询的字段
     * @param condition
     *            查询条件
     * @return field对应的数据
     */
    public Object getFieldDataByCondition(String tableName, String field, String condition) {
        StringBuffer sql = new StringBuffer("SELECT ");
        sql.append(field);
        sql.append(" FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        sql.append(condition);
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql.toString());
        if (srs.next()) {
            return srs.getObject(1);
        } else {
            return null;
        }
    }
}
