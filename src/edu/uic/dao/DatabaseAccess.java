package edu.uic.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uic.data.TableData;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;

public class DatabaseAccess {

    private String jdbcDriver;
    private static final String[] TABLE_TYPES = {"TABLE"};

    public Connection getConnection(LoginForm form) throws Exception { 

        // Evaluate JDBC Driver & URL from user Input
        String url = "";
        switch(form.getDbms().toLowerCase()){
            case "mysql": 

                jdbcDriver ="com.mysql.jdbc.Driver"; 
                url = "jdbc:mysql://" + form.getDbmsHost() + ":3306/" + form.getDatabaseSchema() + "?useSSL=false";
                break;

            case "db2": 
                jdbcDriver ="com.ibm.db2.jcc.DB2Driver";
                url = "jdbc:db2://" + form.getDbmsHost() + ":50000/" + form.getDatabaseSchema() + "?useSSL=false";
                break;

            case "oracle": 
                jdbcDriver ="oracle.jdbc.driver.OracleDriver";
                url = "jdbc:oracle:thin:@" + form.getDbmsHost() + ":1521:" + form.getDatabaseSchema() + "?useSSL=false";
                break;

            default:
                jdbcDriver=null;
                url=null;
                break;
        }

        Class.forName(jdbcDriver);
        return DriverManager.getConnection(url, form.getUsername(), form.getPassword());

    }

    public boolean login(LoginForm form, MessageModel model){
        Connection connection = null;
        boolean isValidUser = false;
        Statement statement = null;

        try {
            connection = getConnection(form);
            isValidUser = connection.isValid(10);

            statement = connection.createStatement();

            try{
                // Create Table if not found
                statement.executeUpdate(LOG_TABLE);
                statement.executeUpdate(getUpdateMemoQuery(form, "LOGIN"));
            }catch(Exception e){}
        } catch (ClassNotFoundException e) {
            model.setStatus("Driver is missing!" + e.getMessage());
        } catch (SQLException e) {
            model.setStatus("Error logging in or Connecting to Database" + e.getMessage()
            + "Error Code:" + e.getErrorCode() + "SQL State:" + e.getSQLState());
        } catch (Exception e) {
            model.setStatus("Cannot Login to Database" + e.getMessage());
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (Exception e) {
                model.setStatus("Cannot Close connection" + e.getMessage());
            }
        }

        return isValidUser;
    }
    
    public boolean logout (LoginForm form, MessageModel model){
        Connection connection = null;
        boolean isValidUser = false;
        Statement statement = null;

        try {
            connection = getConnection(form);
            statement = connection.createStatement();
            try{
                statement.executeUpdate(getUpdateMemoQuery(form, "LOGOUT"));
            }catch(Exception e){}
        } catch (Exception e) {
            model.setStatus("Couldnt log LogOut Memo into Database" + e.getMessage());
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (Exception e) {
                model.setStatus("Cannot Close connection" + e.getMessage());
            }
        }
        return isValidUser;
    }

    public List<String> getTableNamesBySchema(LoginForm form) throws Exception {

        Connection connection = null;
        ResultSet resultSet = null;

        try {

            connection = getConnection(form);
            DatabaseMetaData metaData = connection.getMetaData();
            List<String> list = new ArrayList<>();

            resultSet = metaData.getTables(null, null, "%", TABLE_TYPES);
            resultSet.beforeFirst();
            if (resultSet != null) {
                while (resultSet.next()) {
                    list.add(resultSet.getString("TABLE_NAME"));
                }
            }
            return list;

        } finally {
            resultSet.close();
            connection.close();
        }
    }

    public List<String> getColumnsByTable(LoginForm loginForm, String tableName) throws Exception {

        Connection connection = null;
        ResultSet resultSet = null;

        try {

            connection = getConnection(loginForm);
            DatabaseMetaData metaData = connection.getMetaData();
            List<String> list = new ArrayList<>();

            resultSet = metaData.getColumns(null, null, tableName, null);
            resultSet.beforeFirst();
            if (resultSet != null) {
                while (resultSet.next()) {
                    list.add(resultSet.getString("COLUMN_NAME"));
                }
            }
            return list;

        } finally {
            resultSet.close();
            connection.close();
        }
    }

    public List<String> getColumnDetailsByTable(LoginForm loginForm, String tableName) throws Exception {

        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {

            connection = getConnection(loginForm);
            statement = connection.prepareStatement("SELECT * FROM " + tableName);
            resultSet = statement.executeQuery();

            List<String> resultList = new ArrayList<>();
            int counter = 1;
            int size = resultSet.getMetaData().getColumnCount();

            if (resultSet != null) {
                for (counter = 1; counter <= size; counter++) {
                    int type = resultSet.getMetaData().getColumnType(counter);
                    switch (type){
                        case Types.BIGINT:
                            resultList.add("BIGINT");
                            break;
                        case Types.INTEGER:
                            resultList.add("INTEGER");
                            break;
                        case Types.DECIMAL:
                            resultList.add("DECIMAL");
                            break;
                        case Types.VARCHAR:
                            resultList.add("VARCHAR");
                            break;
                        default :
                            resultList.add("VARCHAR");
                            break;
                    }
                }
            }
            return resultList;

        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }
    }

    public TableData executeSelectQuery(LoginForm loginForm, MessageModel messageModel,
            String query) throws Exception {

        TableData tableData = new TableData();
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {

            connection = getConnection(loginForm);
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            tableData.setColumnNames(getColumnNames(resultSet));
            while (resultSet.next()) {
                List<String> rowData = new ArrayList<>();
                for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
                    rowData.add(String.valueOf(resultSet.getObject(i)));
                }
                tableData.setRow(rowData);
            }
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        // set the rowCount, columnCount & the query into the model bean to display
        // this in the UI
        messageModel.setStatus(query);
        messageModel.setRowCount(tableData.getDataRows().size());
        messageModel.setColumnCount(tableData.getColumnNames().size());

        return tableData;
    }

    public boolean executeUpdateQuery(LoginForm loginForm, String query) throws Exception {

        Statement statement = null;
        Connection connection = null;

        try {

            connection = getConnection(loginForm);
            statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            
            // Add Memo to table
            try{
                statement.executeUpdate(getUpdateMemoQuery(loginForm, query));
            }catch(Exception e){}
            
            return result > -1 ? true : false;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public boolean executeBatchUpdate(LoginForm loginForm, List<String> queries) throws Exception {

        Statement statement = null;
        Connection connection = null;

        try {

            connection = getConnection(loginForm);
            connection.setAutoCommit(false);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            for(String query : queries){
                statement.addBatch(query);
            }

            int[] result = statement.executeBatch();
            connection.commit();
            
            // Add Memo to table
            try{
                statement.executeUpdate(getUpdateMemoQuery(loginForm, queries.get(0)));
            }catch(Exception e){}
            
            return result[0] > -1 ? true : false;
        } finally {
            statement.close();
            connection.close();
        }
    }

    public List<Object> getColumnValues(LoginForm loginForm, String query) throws Exception {

        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        List<Object> resultList = new ArrayList<>();

        try {

            connection = getConnection(loginForm);
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object object = resultSet.getObject(1);
                try{
                    resultList.add(Double.valueOf(object.toString()));
                }
                catch(Exception e){
                    resultList.add(String.valueOf(object));
                }

            }
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        // set the rowCount, columnCount & the query into the model bean to display
        // this in the UI
        return resultList;
    }

    public List<List<Double>> getMultipleColumnValues(LoginForm loginForm, String query) throws Exception {

        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        List<List<Double>> columnValues = new ArrayList<>(); 
        
        try {

            connection = getConnection(loginForm);
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            int count = resultSet.getMetaData().getColumnCount();
            
            int rowCount = 0;
            while (resultSet.next()) {
                List<Double> rowValue = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Object object = resultSet.getObject(i + 1);
                    Double value = null;
                    try{
                        value = Double.valueOf(object.toString());
                    }
                    catch(Exception e){
                        value = 0.00d;
                    }
                    rowValue.add(value);
                    // Create a new list on the first node
                    if(rowCount == 0){
                        columnValues.add(i, new ArrayList<>());
                    }
                    columnValues.get(i).add(value);
                }
                rowCount ++;
            }
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

        // set the rowCount, columnCount & the query into the model bean to display
        // this in the UI
        return columnValues;
    }

    private List<String> getColumnNames(ResultSet resultSet) throws Exception {
        List<String> returnList = new ArrayList<>();

        for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
            returnList.add(resultSet.getMetaData().getColumnName(i));
        }
        return returnList;
    }

    public ResultSet getTableToExport(LoginForm loginForm, String tableName) throws Exception {


        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(loginForm);
            statement = connection.prepareStatement("SELECT * FROM " + tableName);
            resultSet = statement.executeQuery();
        }
        catch (Exception e) {
        }
        return resultSet;
    }

    // Create the Memo Query 
    private String getUpdateMemoQuery(LoginForm form, String query) throws Exception{
        
        String activity  = ""; 
        if(query.toUpperCase().indexOf("INSERT") >= 0){
            activity = ACTIVITIES.get("INSERT") + query.split(" ")[2];
        } else if(query.toUpperCase().indexOf("UPDATE") >= 0){
            activity = ACTIVITIES.get("UPDATE") + query.split(" ")[1];
        } else if(query.toUpperCase().indexOf("DELETE") >= 0){
            activity = ACTIVITIES.get("DELETE") + query.split(" ")[2];
        } else if(query.toUpperCase().indexOf("CREATE") >= 0){
            activity = ACTIVITIES.get("CREATE") + query.split(" ")[2];
        } else if(query.toUpperCase().indexOf("DROP") >= 0){
            activity = ACTIVITIES.get("DROP") + query.split(" ")[2];
        } else if(query.toUpperCase().indexOf("ALTER") >= 0){
            activity = ACTIVITIES.get("ALTER") + query.split(" ")[2];
        } else if(query.toUpperCase().indexOf("LOGIN") >= 0){
            activity = ACTIVITIES.get("LOGIN");
        } else if(query.toUpperCase().indexOf("LOGOUT") >= 0){
            activity = ACTIVITIES.get("LOGOUT");
        } 
        
        StringBuilder memoQuery = new StringBuilder("INSERT INTO S17G307_SYSTEM_MEMO");
        memoQuery.append("(USER_NAME, DB_SCHEMA, ACTIVITY, WHEN_, IP_ADDRESS, SESSION_ID) VALUES ");
        memoQuery.append("(\"");
        memoQuery.append(form.getUsername() + "\",\"" + form.getDbms() + "\",\"" + activity + "\",");
        memoQuery.append("CURRENT_TIMESTAMP()" + ",\"" + form.getIpAddress() + "\",\"");
        memoQuery.append(form.getSessionId() + "\");");
        
        return memoQuery.toString();
    }
    
    private static final String LOG_TABLE = "CREATE TABLE IF NOT EXISTS S17G307_SYSTEM_MEMO "
            + "(LOG_ID INT(6) NOT NULL AUTO_INCREMENT ,"
            + "USER_NAME VARCHAR(50) not null, "
            + "DB_SCHEMA VARCHAR(50), "
            + "ACTIVITY VARCHAR(80), "
            + "WHEN_ TIMESTAMP null, "
            + "IP_ADDRESS VARCHAR(50), "
            + "SESSION_ID VARCHAR(60), "
            + "PRIMARY KEY (LOG_ID));";
    
    private static final Map<String, String> ACTIVITIES = new HashMap<>();
    
    static{
        ACTIVITIES.put("LOGIN", "User Logged In Successfully");
        ACTIVITIES.put("LOGOUT", "User Logged Out Successfully");
        ACTIVITIES.put("INSERT", "User Inserted Rows into the Table :");
        ACTIVITIES.put("UPDATE", "User updated Rows on the Table :");
        ACTIVITIES.put("DELETE", "User Deleted Rows from the Table :");
        ACTIVITIES.put("CREATE", "User Created the new Table :");
        ACTIVITIES.put("DROP", "User Dropped the Table :");
        ACTIVITIES.put("ALTER", "User Added columns to the Table :");
    }
}



