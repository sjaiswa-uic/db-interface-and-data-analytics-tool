package edu.uic.dataimport;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.Part;

/**
 * @ Backing Managed Form Bean Description: This type of managed-bean participates in the "View"
 * concern of the MVC design pattern. But specifically dealing with transfering the user input data
 * fields from the Web UI into the controllers.
 * 
 **/
@ManagedBean
@RequestScoped
public class DataImportForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private Part data;

    private String fileLabel;

    private String datasetLabel;

    private String fileFormat;

    private String fileType;

    private String headerIndicator;
    
    private String[] selectedDataEntries;
    
    public String[] getSelectedDataEntries() {
        return selectedDataEntries;
    }

    public void setSelectedDataEntries(String[] selectedDataEntries) {
        this.selectedDataEntries = selectedDataEntries;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getHeaderIndicator() {
        return headerIndicator;
    }

    public void setHeaderIndicator(String headerIndicator) {
        this.headerIndicator = headerIndicator;
    }

    public Part getData() {
        return data;
    }

    public void setData(Part data) {
        this.data = data;
    }

    public String getFileLabel() {
        return fileLabel;
    }

    public void setFileLabel(String fileLabel) {
        this.fileLabel = fileLabel;
    }

    public String getDatasetLabel() {
        return datasetLabel;
    }

    public void setDatasetLabel(String datasetLabel) {
        this.datasetLabel = datasetLabel;
    }
}
