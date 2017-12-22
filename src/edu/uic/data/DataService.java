package edu.uic.data;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import edu.uic.dao.DatabaseAccess;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;

public class DataService {

    private DatabaseAccess databaseAccess;

    public List<String> getTableNames(LoginForm form, MessageModel messageModel) {
        databaseAccess = new DatabaseAccess();

        try {
            return databaseAccess.getTableNamesBySchema(form);
        } catch (Exception e) {
            messageModel.setStatus("Error occured while retrieving Database Tables. Schema has no Tables in it.");
        }
        return null;
    }

    public List<String> getColumnNames(LoginForm loginForm, MessageModel messageModel,
            String tableName) {
        databaseAccess = new DatabaseAccess();

        try {
            return databaseAccess.getColumnsByTable(loginForm, tableName);
        } catch (Exception e) {
            messageModel.setStatus("Error occured while retrieving Database Columns From Table. Table is empty.");
        }
        return null;
    }

    public TableData executeQuery(LoginForm loginForm, MessageModel messageModel,
            PageForm pageForm) {
        databaseAccess = new DatabaseAccess();
        if (pageForm.getQuery().toUpperCase().startsWith("SELECT")) {
            return executeSelect(loginForm, messageModel, pageForm);
        } else {
            executeUpdate(loginForm, messageModel, pageForm.getQuery());
        }
        return null;
    }

    public TableData executeSelect(LoginForm loginForm, MessageModel messageModel,
            PageForm pageForm) {
        databaseAccess = new DatabaseAccess();

        try {
            return databaseAccess.executeSelectQuery(loginForm, messageModel,
                    createQuery(pageForm));
        } catch (Exception e) {
            messageModel.setStatus("No Data to be Displayed. Table either does not exist or has no Rows." + e.getMessage());
        }
        return null;
    }

    public void executeUpdate(LoginForm loginForm, MessageModel messageModel, String query) {
        databaseAccess = new DatabaseAccess();
        if (!(query.toUpperCase().startsWith("UPDATE") || query.toUpperCase().startsWith("INSERT")
                || query.toUpperCase().startsWith("CREATE")
                || query.toUpperCase().startsWith("DROP")
                || query.toUpperCase().startsWith("DELETE"))) {

            messageModel.setStatus("Incorrect Query! Please Enter a Valid Query.");
            return;
        }

        try {
            if (databaseAccess.executeUpdateQuery(loginForm, query)) {
                messageModel.setStatus("Updated Successfully!");
            } else {
                messageModel.setStatus("Unable to Update at this time!");
            }
        } catch (Exception e) {
            messageModel.setStatus("Cannot Update at this time." + e.getMessage());
        }
    }

    private String createQuery(PageForm form) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (StringUtils.isNotEmpty(form.getQuery())) {

            query = new StringBuilder(form.getQuery());
        } else if (StringUtils.isNotEmpty(form.getTableName())
                && CollectionUtils.isEmpty(form.getColumnList())) {

            query.append("* FROM ").append(form.getTableName());
        } else if (StringUtils.isNotEmpty(form.getTableName())
                && CollectionUtils.isNotEmpty(form.getColumnList())) {

            query.append(StringUtils.join(form.getColumnList(), ",")).append(" FROM ")
                    .append(form.getTableName());
        }
        return query.toString();
    }
}
