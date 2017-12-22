package edu.uic.descriptivestat;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import edu.uic.data.TableData;

/**
 * @ Backing Managed-Bean Description: This type of managed-bean participates in the "View" concern
 * of the MVC design pattern. The purpose of a backing-bean is to support UI logic, and has a 1::1
 * relationship with a JSF view, or a JSF form in a Facelet composition. Although it typically has
 * JavaBean-style properties with associated getters/setters, these are properties of the View --
 * not of the underlying application data model.
 * 
 **/
@ManagedBean
@ViewScoped
public class DescriptiveModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> dataSet;
    
    private List<String> columnList;
    
    private List<String> operationList;
    
    private TableData tableData;
    
    private String imagePath;
    
    private String status;
    
    private TableData dataPart1;
    
    private TableData dataPart2;
    
    private String datasetName;
    
    private String numberOfObservations;
    
    
    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getNumberOfObservations() {
        return numberOfObservations;
    }

    public void setNumberOfObservations(String numberOfObservations) {
        this.numberOfObservations = numberOfObservations;
    }

    public TableData getDataPart1() {
        return dataPart1;
    }

    public void setDataPart1(TableData dataPart1) {
        this.dataPart1 = dataPart1;
    }

    public TableData getDataPart2() {
        return dataPart2;
    }

    public void setDataPart2(TableData dataPart2) {
        this.dataPart2 = dataPart2;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<String> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<String> operationList) {
        this.operationList = operationList;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public List<String> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<String> dataSet) {
        this.dataSet = dataSet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }
}
