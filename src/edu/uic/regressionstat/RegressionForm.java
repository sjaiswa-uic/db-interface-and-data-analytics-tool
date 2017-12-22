package edu.uic.regressionstat;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class RegressionForm implements Serializable{

    private static final long serialVersionUID = 1L;

    private String dataLabel;
    
    private String predictorColumn;
    
    private String responseColumn;
    
    private List<String> predictorColumns;
    
    private String renderType;

    public List<String> getPredictorColumns() {
        return predictorColumns;
    }

    public void setPredictorColumns(List<String> predictorColumns) {
        this.predictorColumns = predictorColumns;
    }

    public String getRenderType() {
        return renderType;
    }

    public void setRenderType(String renderType) {
        this.renderType = renderType;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
    }

    public String getPredictorColumn() {
        return predictorColumn;
    }

    public void setPredictorColumn(String predictorColumn) {
        this.predictorColumn = predictorColumn;
    }

    public String getResponseColumn() {
        return responseColumn;
    }

    public void setResponseColumn(String responseColumn) {
        this.responseColumn = responseColumn;
    }
}
