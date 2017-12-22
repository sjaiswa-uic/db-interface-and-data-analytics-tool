package edu.uic.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uic.dataimport.Row;

public class TableData implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private List<String> columnNames;

    private List<List<String>> dataRows;
    
    private List<Row> rows;
    
    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<String>> getDataRows() {
        return dataRows;
    }

    public void setDataRows(List<List<String>> dataRows) {
        this.dataRows = dataRows;
    }

    public void setRow(List<String> row) {
        if (this.dataRows == null) {
            this.dataRows = new ArrayList<>();
        }
        this.dataRows.add(row);
    }
}
