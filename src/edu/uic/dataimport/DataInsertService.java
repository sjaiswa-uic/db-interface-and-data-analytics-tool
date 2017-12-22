package edu.uic.dataimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.uic.dao.DatabaseAccess;
import edu.uic.data.TableData;
import edu.uic.login.LoginForm;

public class DataInsertService {

    public boolean createTableFromMetadata (DataImportModel model, LoginForm login){
        DatabaseAccess access = new DatabaseAccess();

        String createTableQuery = deduceCreateQueryFromMetadata(model);

        // Create the table dynamically based on Dataset
        try {
            access.executeUpdateQuery(login, createTableQuery);
        } catch (Exception e) {
            model.setStatus("Unable To Create Table : " +  e);
            return false;
        }

        return true;
    }

    public boolean insertDataSet(String[] selectedEntries, DataImportModel model, LoginForm login) throws Exception{
        DatabaseAccess access = new DatabaseAccess();

        // Filter only the rows that the user selected from the UI
        List<Row> genericList = model.getTableData().getRows();
        List<Row> filteredList = new ArrayList<>();
        for(String index : selectedEntries){
            filteredList.add(genericList.get(Integer.parseInt(index)));
        }
        
        List<String> columnNames = access.getColumnsByTable(login, getTableNameFromLabel(model.getLabel()));
        columnNames.remove("ROW_INDEX");
        String insertQuery = deduceInsertQueryFromColumnNames(model.getLabel(), columnNames, filteredList);

        // Insert the dataset into the created table
        try {
            access.executeUpdateQuery(login, insertQuery);
        } catch (Exception e) {
            model.setStatus("Unable To Insert Data Into the Table : " +  e);
            return false;
        }

        return true;
    }

    public boolean smartInsertDataSet(String[] selectedEntries, DataImportModel model, LoginForm login){
        DatabaseAccess access = new DatabaseAccess();

        String createTableQuery = 
                deduceCreateQueryFromDataRow(model, getValidRowForReference(model.getTableData()));

        // Create the table dynamically based on Dataset
        try {
            access.executeUpdateQuery(login, createTableQuery);
        } catch (Exception e) {
            model.setStatus("Unable To Create Table : " +  e);
            return false;
        }

        // Filter only the rows that the user selected from the UI
        List<Row> genericList = model.getTableData().getRows();
        List<Row> filteredList = new ArrayList<>(); 
        for(String index : selectedEntries){
            filteredList.add(genericList.get(Integer.parseInt(index)));
        }

        String insertQuery = deduceInsertQueryFromDataRow(model, filteredList);

        // Insert the dataset into the created table
        try {
            access.executeUpdateQuery(login, insertQuery);
        } catch (Exception e) {
            model.setStatus("Unable To Insert Data Into the Table : " +  e);
            return false;
        }

        return true;
    }

    private Row getValidRowForReference(TableData tableData){
        Row validRow = null; 
        for(Row temp : tableData.getRows() ){
            if(temp.getValidity()){
                validRow = temp;
                break;
            }
        }
        return validRow;
    }

    private String deduceCreateQueryFromDataRow(DataImportModel model, Row row){

        List<Object> rowValues = row.getRowValues();
        List<String> colummnNames = model.getTableData().getColumnNames(); 

        StringBuilder query = new StringBuilder("CREATE TABLE S17G307_");
        query.append(model.getLabel()).append("_STATS").append(" (")
             .append("ROW_INDEX INT NOT NULL AUTO_INCREMENT, ");

        for(int i=0; i < rowValues.size(); i++){
            query.append(colummnNames.get(i)).append(" ")
            .append(getDataType(rowValues.get(i))).append(",");
        }
        
        query.append("PRIMARY KEY (ROW_INDEX) );" );
        return query.toString(); 
    }

    private String deduceCreateQueryFromMetadata(DataImportModel model){

        StringBuilder query = new StringBuilder("CREATE TABLE S17G307_");
        query.append(model.getLabel()).append("_STATS").append(" (")
             .append("ROW_INDEX INT NOT NULL AUTO_INCREMENT, ");

        List<Row> dataRows = model.getTableData().getRows();

        for (Row dataRow : dataRows){
            if("input".equalsIgnoreCase(dataRow.getRowValues().get(2).toString())){
                query.append(dataRow.getRowValues().get(0).toString()).append(" ")
                .append(getDataType(dataRow.getRowValues().get(1).toString())).append(",");
            }
        }

        query.append("PRIMARY KEY (ROW_INDEX) );" );
        return query.toString(); 
    }

    private String deduceInsertQueryFromDataRow(DataImportModel model, List<Row> filteredList){

        List<String> colummnNames = model.getTableData().getColumnNames(); 

        StringBuilder query = new StringBuilder("INSERT INTO S17G307_");
        query.append(model.getLabel()).append("_STATS").append(" (");
        query.append(StringUtils.join(colummnNames, ",")).append(")");
        query.append(" VALUES ");

        List<String> tempDataRow = new ArrayList<>();
        for(Row tempRow : filteredList){
            tempDataRow.add("(" + convertRowToCSV(tempRow) + ")");
        }

        query.append(StringUtils.join(tempDataRow, ",")).append(";");

        return query.toString(); 
    }
    
    private String deduceInsertQueryFromColumnNames(String labelName, List<String> colummnNames, List<Row> filteredList){

        StringBuilder query = new StringBuilder("INSERT INTO S17G307_");
        query.append(labelName).append("_STATS").append(" (");

        query.append(StringUtils.join(colummnNames, ",")).append(")");
        query.append(" VALUES ");

        List<String> tempDataRow = new ArrayList<>();
        for(Row tempRow : filteredList){
            tempDataRow.add("(" + convertRowToCSV(tempRow) + ")");
        }

        query.append(StringUtils.join(tempDataRow, ",")).append(";");

        return query.toString(); 
    }

    //    INSERT INTO tableName
    //    (column1,column2,column3,column4)
    //    VALUES
    //    ('value1' , 'value2', 'value3','value4'),
    //    ('value1' , 'value2', 'value3','value4'),
    //    ('value1' , 'value2', 'value3','value4');

    private String getDataType(Object value){

        String datatype = "";

        if(value instanceof Integer){
            datatype = " INT ";
        } else if (value instanceof Double){
            datatype = " DECIMAL (10, 2) ";
        } else {
            datatype = " VARCHAR(50) ";
        }
        return datatype;
    }

    private String getDataType(String value){

        switch(value){
            case "int" :
                return " INT ";

            case "double" :
                return "  DECIMAL (10, 2) ";

            case "long" :
                return "  BIGINT ";
                
            default :
                return " VARCHAR(50) ";
        }
    }

    private String getDataForQuery(Object value){

        String data = "";

        try{
            if(value instanceof Integer || value instanceof Double){
                data = String.valueOf(value);
            } else {
                data = "0";
            }
        }catch(Exception e){
            data = "0";
        }
        return data;
    }

    // Convert each row into comma separated values
    private String convertRowToCSV(Row row){

        StringBuilder csvFormated = new StringBuilder();
        if(row.getValidity()){
            csvFormated =  new StringBuilder(StringUtils.join(row.getRowValues(), ","));
        }else{
            for (Object obj : row.getRowValues()){
                csvFormated.append(getDataForQuery(obj)).append(","); 
            }
            csvFormated = new StringBuilder(csvFormated.substring(0, csvFormated.length() -1));
        }
        return csvFormated.toString();
    }

    public boolean doesTableExist(LoginForm login, String datasetModelName) throws Exception{
        DatabaseAccess access = new DatabaseAccess();
        boolean exist = false;

        List<String> tableNames = access.getTableNamesBySchema(login);
        if(tableNames != null){
            exist = tableNames.contains(getTableNameFromLabel(datasetModelName));
        }
        return exist;
    }

    public String getTableNameFromLabel(String labelName){
        return ("S17G307_" + labelName + "_STATS").toLowerCase();
    }
}
