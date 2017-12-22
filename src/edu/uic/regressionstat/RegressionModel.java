package edu.uic.regressionstat;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import edu.uic.data.TableData;

@ManagedBean
@ViewScoped
public class RegressionModel  implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> dataSet;
    
    private String regressionEquation;
    private String predictorValue;
    private String responseValue;
    private Double intercept;
    private Double interceptStandardError;
    private Double tStatistic;
    private Double interceptPValue;
    private Double slope;
    private Double predictorDF;
    private Double residualErrorDF;
    private Double totalDF;
    private Double regressionSumSquares;
    private Double sumSquaredErrors;
    private Double totalSumSquares;
    private Double meanSquare;
    private Double meanSquareError;
    private Double fValue;
    private Double pValue;
    private Double slopeStandardError;
    private Double tStatisticPredictor;
    private Double pValuePredictor;
    private Double standardErrorModel;
    private Double rSquare;
    private double rSquareAdjusted;
    private String status;
    private String imagePath;
    private TableData tableData;
    
    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getrSquareAdjusted() {
        return rSquareAdjusted;
    }

    public void setrSquareAdjusted(double rSquareAdjusted) {
        this.rSquareAdjusted = rSquareAdjusted;
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

    public String getRegressionEquation() {
        return regressionEquation;
    }

    public void setRegressionEquation(String regressionEquation) {
        this.regressionEquation = regressionEquation;
    }

    public String getPredictorValue() {
        return predictorValue;
    }

    public void setPredictorValue(String predictorValue) {
        this.predictorValue = predictorValue;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }

    public Double getIntercept() {
        return intercept;
    }

    public void setIntercept(Double intercept) {
        this.intercept = intercept;
    }

    public Double getInterceptStandardError() {
        return interceptStandardError;
    }

    public void setInterceptStandardError(Double interceptStandardError) {
        this.interceptStandardError = interceptStandardError;
    }

    public Double gettStatistic() {
        return tStatistic;
    }

    public void settStatistic(Double tStatistic) {
        this.tStatistic = tStatistic;
    }

    public Double getInterceptPValue() {
        return interceptPValue;
    }

    public void setInterceptPValue(Double interceptPValue) {
        this.interceptPValue = interceptPValue;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public Double getPredictorDF() {
        return predictorDF;
    }

    public void setPredictorDF(Double predictorDF) {
        this.predictorDF = predictorDF;
    }

    public Double getResidualErrorDF() {
        return residualErrorDF;
    }

    public void setResidualErrorDF(Double residualErrorDF) {
        this.residualErrorDF = residualErrorDF;
    }

    public Double getTotalDF() {
        return totalDF;
    }

    public void setTotalDF(Double totalDF) {
        this.totalDF = totalDF;
    }

    public Double getRegressionSumSquares() {
        return regressionSumSquares;
    }

    public void setRegressionSumSquares(Double regressionSumSquares) {
        this.regressionSumSquares = regressionSumSquares;
    }

    public Double getSumSquaredErrors() {
        return sumSquaredErrors;
    }

    public void setSumSquaredErrors(Double sumSquaredErrors) {
        this.sumSquaredErrors = sumSquaredErrors;
    }

    public Double getTotalSumSquares() {
        return totalSumSquares;
    }

    public void setTotalSumSquares(Double totalSumSquares) {
        this.totalSumSquares = totalSumSquares;
    }

    public Double getMeanSquare() {
        return meanSquare;
    }

    public void setMeanSquare(Double meanSquare) {
        this.meanSquare = meanSquare;
    }

    public Double getMeanSquareError() {
        return meanSquareError;
    }

    public void setMeanSquareError(Double meanSquareError) {
        this.meanSquareError = meanSquareError;
    }

    public Double getfValue() {
        return fValue;
    }

    public void setfValue(Double fValue) {
        this.fValue = fValue;
    }

    public Double getpValue() {
        return pValue;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
    }

    public Double getSlopeStandardError() {
        return slopeStandardError;
    }

    public void setSlopeStandardError(Double slopeStandardError) {
        this.slopeStandardError = slopeStandardError;
    }

    public Double gettStatisticPredictor() {
        return tStatisticPredictor;
    }

    public void settStatisticPredictor(Double tStatisticPredictor) {
        this.tStatisticPredictor = tStatisticPredictor;
    }

    public Double getpValuePredictor() {
        return pValuePredictor;
    }

    public void setpValuePredictor(Double pValuePredictor) {
        this.pValuePredictor = pValuePredictor;
    }

    public Double getStandardErrorModel() {
        return standardErrorModel;
    }

    public void setStandardErrorModel(Double standardErrorModel) {
        this.standardErrorModel = standardErrorModel;
    }

    public Double getrSquare() {
        return rSquare;
    }

    public void setrSquare(Double rSquare) {
        this.rSquare = rSquare;
    }
    
    public void reset(){
        this.dataSet = null;
        this.regressionEquation = null;
        this.predictorValue = null;
        this.responseValue = null;
        this.intercept = null;
        this.interceptStandardError = null;
        this.tStatistic = null;
        this.interceptPValue = null;
        this.slope = null;
        this.predictorDF = null;
        this.residualErrorDF = null;
        this.totalDF = null;
        this.regressionSumSquares = null;
        this.sumSquaredErrors = null;
        this.totalSumSquares = null;
        this.meanSquare = null;
        this.meanSquareError = null;
        this.fValue = null;
        this.pValue = null;
        this.slopeStandardError = null;
        this.tStatisticPredictor = null;
        this.pValuePredictor = null;
        this.standardErrorModel = null;
        this.rSquare = null;
        this.rSquareAdjusted = 0.00d;
        this.status = null;
        this.imagePath = null;
        this.tableData = null;
    }
}
