package edu.uic.regressionstat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.uic.dao.DatabaseAccess;
import edu.uic.data.TableData;
import edu.uic.login.LoginForm;

public class RegressionService {

    private static final String FILE_HEADER_SPLIT_PART_1[] =
        {"Variable", "Co-Efficient", "Standard Error Co-Efficient", "T-Statistic", "P-Value"};

    public RegressionModel computeMultipleRegression(RegressionForm regressionForm, LoginForm loginForm,
            RegressionModel regressionModel) {

        DatabaseAccess access = new DatabaseAccess();
        List<List<Double>> columnValues = new ArrayList<>();

        try {
            columnValues = access.getMultipleColumnValues(loginForm, 
                    createQuery(regressionForm.getDataLabel(), 
                            regressionForm.getResponseColumn(), 
                            regressionForm.getPredictorColumns()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        double[] response = getResponseArray(columnValues);
        double[][] predictorArrays = getPredictorArrays(columnValues);

        // Start regression analysis
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(response, predictorArrays);

        // Data for table 1
        TableData table1 = new TableData();
        table1.setColumnNames(Arrays.asList(FILE_HEADER_SPLIT_PART_1));

        List<List<String>> firstTableValues = new ArrayList<>();

        // Set index values
        List<String> variable = new ArrayList<>();
        variable.add("Intercept");
        variable.addAll(regressionForm.getPredictorColumns());

        double[] beta = regression.estimateRegressionParameters();
        double[] standardErrors = regression.estimateRegressionParametersStandardErrors();
        int residualdf = regression.estimateResiduals().length - beta.length;
        TDistribution tdistribution = new TDistribution(residualdf);

        // Ready the values for the first table to display
        for (int i = 0; i < beta.length; i++) {

            List<String> values = new ArrayList<>();
            values.add(variable.get(i));
            values.add(String.valueOf(beta[i]));
            values.add(String.valueOf(standardErrors[i]));

            // calculate p-value and create coefficient
            values.add(String.valueOf(beta[i] / standardErrors[i]));
            values.add(String.valueOf(tdistribution.cumulativeProbability(-FastMath.abs(beta[i] / standardErrors[i])) * 2));
            firstTableValues.add(values);
        }
        table1.setDataRows(firstTableValues);
        regressionModel.setTableData(table1);
        
        // Ready the values for table 2
        double rSquared = regression.calculateRSquared();
        double adjustedRSquared = regression.calculateAdjustedRSquared();
        double standardErrorModel = Math.sqrt(StatUtils.variance(response)) * (Math.sqrt(1 - adjustedRSquared));
        
        regressionModel.setrSquare(rSquared);
        regressionModel.setrSquareAdjusted(adjustedRSquared);
        regressionModel.setStandardErrorModel(standardErrorModel);
        
        //Ready values for table 3
        int predictorDF = regressionForm.getPredictorColumns().size();
        int totalDF = response.length-1;
        int residualErrorDF = totalDF - predictorDF;
        
        
        regressionModel.setPredictorDF((double) predictorDF);
        regressionModel.setTotalDF((double) totalDF);
        regressionModel.setResidualErrorDF((double) residualErrorDF);
        
        double totalSumOfSquares = regression.calculateTotalSumOfSquares();
        double residualSumOfSquares = regression.calculateResidualSumOfSquares();
        regressionModel.setTotalSumSquares(totalSumOfSquares);
        regressionModel.setRegressionSumSquares(totalSumOfSquares - residualSumOfSquares);
        regressionModel.setSumSquaredErrors(residualSumOfSquares);
        
        double meanSquare = 0;
        if(predictorDF!=0) {
            meanSquare = (totalSumOfSquares - residualSumOfSquares) / predictorDF;
        }
        regressionModel.setMeanSquare(meanSquare);
        
        double meanSquareError = 0;
        if(residualErrorDF != 0) {
            meanSquareError = (double)(residualSumOfSquares / residualErrorDF);
        }
        regressionModel.setMeanSquareError(meanSquareError);
        
        
        double fValue = 0;
        if(meanSquareError != 0) {
            fValue = meanSquare / meanSquareError;
        }
        regressionModel.setfValue(fValue);
        regressionModel.setpValue((1 - new FDistribution(predictorDF, residualErrorDF).cumulativeProbability(fValue)));
        
        return regressionModel;
    }

    public RegressionModel computeRegressionStats(RegressionForm regressionForm, LoginForm loginForm,
            RegressionModel regressionModel) {

        DatabaseAccess access = new DatabaseAccess();
        double[] predictor = null;
        double[] response = null;

        List<Object> valueList = null;
        try {
            valueList = access.getColumnValues(
                    loginForm, createQuery(regressionForm.getDataLabel(), regressionForm.getPredictorColumn()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        predictor = new double[valueList.size()];
        int arrayIndex1 = 0;
        for (Object rowValue : valueList) {
            predictor[arrayIndex1++] = (Double) rowValue;
        }        


        List<Object> valueList2 = null;
        try {
            valueList2 = access.getColumnValues(
                    loginForm, createQuery(regressionForm.getDataLabel(), regressionForm.getResponseColumn()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        response = new double[valueList2.size()];
        int arrayIndex2 = 0;
        for (Object rowValue : valueList2) {
            response[arrayIndex2++] = (Double) rowValue;
        }



        regressionModel.setResponseValue(regressionForm.getResponseColumn());
        regressionModel.setPredictorValue(regressionForm.getPredictorColumn());

        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i=0;i<predictor.length;i++){
            simpleRegression.addData(predictor[i], response[i]);
        }
        int totalDF = response.length-1;
        TDistribution tDistribution = new TDistribution(totalDF);
        double intercept = simpleRegression.getIntercept();
        double interceptStandardError = simpleRegression.getInterceptStdErr();
        double tStatistic = 0;
        int predictorDF = 1;
        int residualErrorDF = totalDF - predictorDF;
        double rSquare = simpleRegression.getRSquare();
        double rSquareAdjusted = rSquare - (1 - rSquare)/(totalDF - predictorDF - 1);
        if(interceptStandardError!=0){
            tStatistic = (double)intercept/interceptStandardError;
        }
        double interceptPValue =
                (double)2*tDistribution.cumulativeProbability(-Math.abs(tStatistic));
        double slope = simpleRegression.getSlope();
        double slopeStandardError = simpleRegression.getSlopeStdErr();
        double tStatisticpredict = 0;
        if(slopeStandardError != 0) {
            tStatisticpredict = (double)slope/slopeStandardError;
        }
        double pValuePredictor =
                (double)2*tDistribution.cumulativeProbability(-Math.abs(tStatisticpredict));
        double standardErrorModel = Math.sqrt(StatUtils.variance(response))*(Math.sqrt(1-rSquareAdjusted));
        double regressionSumSquares = simpleRegression.getRegressionSumSquares();
        double sumSquaredErrors = simpleRegression.getSumSquaredErrors();
        double totalSumSquares = simpleRegression.getTotalSumSquares();
        double meanSquare = 0;
        if(predictorDF!=0) {
            meanSquare = regressionSumSquares/predictorDF;
        }
        double meanSquareError = 0;
        if(residualErrorDF != 0) {
            meanSquareError = (double)(sumSquaredErrors/residualErrorDF);
        }

        regressionModel.setRegressionEquation(new StringBuilder()
                .append(regressionModel.getResponseValue())
                .append(" = ")
                .append(intercept)
                .append(" + ( ")
                .append(slope)
                .append(" ) ")
                .append(regressionModel.getPredictorValue())
                .toString());
        regressionModel.setIntercept(intercept);
        regressionModel.setInterceptStandardError(interceptStandardError);
        regressionModel.settStatistic(tStatistic);
        regressionModel.settStatisticPredictor(tStatisticpredict);
        regressionModel.setInterceptPValue(interceptPValue);
        regressionModel.setSlope(slope);
        regressionModel.setSlopeStandardError(slopeStandardError);
        regressionModel.setpValuePredictor(pValuePredictor);
        regressionModel.setStandardErrorModel(standardErrorModel);
        regressionModel.setrSquare(rSquare);
        regressionModel.setrSquareAdjusted((double)rSquareAdjusted);
        regressionModel.setPredictorDF((double)predictorDF);
        regressionModel.setResidualErrorDF((double)residualErrorDF);
        regressionModel.setTotalDF((double)totalDF);
        regressionModel.setRegressionSumSquares(regressionSumSquares);
        regressionModel.setSumSquaredErrors(sumSquaredErrors);
        regressionModel.setTotalSumSquares(totalSumSquares);
        regressionModel.setMeanSquare(meanSquare);
        regressionModel.setMeanSquareError(meanSquareError);

        double fValue = 0;
        if(meanSquareError != 0) {
            fValue = meanSquare/meanSquareError;
        }
        regressionModel.setfValue(fValue);
        regressionModel.setpValue((1 - new FDistribution(predictorDF, residualErrorDF).cumulativeProbability(fValue)));

        return regressionModel;
    }

    public JFreeChart createGraphicalRepresentation(RegressionForm form, LoginForm loginForm,
            RegressionModel regressionModel) {

        DatabaseAccess access = new DatabaseAccess();
        regressionModel.setResponseValue(form.getResponseColumn());
        regressionModel.setPredictorValue(form.getPredictorColumn());

        List<Object> predictorList = null;
        try {
            predictorList = access.getColumnValues(
                    loginForm, createQuery(form.getDataLabel(), form.getPredictorColumn()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Object> responseList = null;
        try {
            responseList = access.getColumnValues(
                    loginForm, createQuery(form.getDataLabel(), form.getResponseColumn()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        double[] predictorArray = new  double[predictorList.size()];
        double[] responseArray = new  double[responseList.size()];

        if("scatter-line".equalsIgnoreCase(form.getRenderType())){

            // Add column values to XYSeries side by side
            XYSeries series = new XYSeries("Random");
            for(int i=0; i < predictorList.size(); i++){
                series.add((double)predictorList.get(i), (double) responseList.get(i));
                predictorArray[i] = (double)predictorList.get(i);
                responseArray[i]  = (double)responseList.get(i);
            }

            // Deduce on the start & end points of the graph
            double startPredictor = StatUtils.min(predictorArray);
            double endPredictor = StatUtils.max(predictorArray);

            double startResponse = StatUtils.min(responseArray);
            double endResponse = StatUtils.max(responseArray);

            double start = startPredictor < startResponse ? startPredictor : startResponse;
            double end = endPredictor > endResponse ? endPredictor : endResponse;

            XYDataset dataset = new XYSeriesCollection(series);
            double[] regressionParameters = Regression.getOLSRegression(dataset, 0);
            LineFunction2D linefunction2d = new LineFunction2D(
                    regressionParameters[0], regressionParameters[1]);

            // Creates a dataset by taking sample values from the line function
            XYDataset dataset1 = DatasetUtilities.sampleFunction2D(linefunction2d,
                    start, end, predictorList.size(), "Fitted Regression Line");

            NumberAxis xAxis = new NumberAxis(form.getPredictorColumn());
            xAxis.setAutoRangeIncludesZero(false);
            NumberAxis yAxis = new NumberAxis(form.getResponseColumn());
            yAxis.setAutoRangeIncludesZero(false);

            XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer(false,
                    true);
            // Draw the line dataset
            XYPlot xyplot = new XYPlot(dataset, xAxis, yAxis, renderer1);
            xyplot.setDataset(1, dataset1);
            XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(
                    true, false);
            xylineandshaperenderer.setSeriesPaint(0, Color.BLACK);
            xyplot.setRenderer(1, xylineandshaperenderer);
            return new JFreeChart("Scatter Plot with Regression Line",
                    JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

        } else if ("scatter".equalsIgnoreCase(form.getRenderType())) {

            // Add column values to XYSeries side by side
            XYSeries series = new XYSeries("Random");
            for(int i=0; i < predictorList.size(); i++){
                series.add((double)predictorList.get(i), (double) responseList.get(i));
            }

            // Create & return the Jfree chart from the above deduced values
            return ChartFactory.createScatterPlot("Scatter Plot", form.getPredictorColumn(), form.getResponseColumn(),
                    new XYSeriesCollection(series), PlotOrientation.VERTICAL, true, true, false);
        } else {

            // Add column values to XYSeries side by side
            XYSeries predictorSeries = new XYSeries("Predictor");
            XYSeries resoponseSeries = new XYSeries("Response");
            for(int i=0; i < predictorList.size(); i++){
                predictorSeries.add( i+1, (double) predictorList.get(i));
                resoponseSeries.add( i+1, (double) responseList.get(i));
            }

            XYSeriesCollection collection = new XYSeriesCollection();
            collection.addSeries(predictorSeries);
            collection.addSeries(resoponseSeries);

            return ChartFactory.createXYLineChart("Time Series", "Number of Observations",
                    "Predictor/Response Values", collection);
        }
    }

    private String createQuery(String tableName, String columnName) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (StringUtils.isNotEmpty(tableName)
                && StringUtils.isNotEmpty(columnName)) {

            query.append(columnName).append(" FROM ")
            .append(tableName);
        }
        return query.toString();
    }

    private String createQuery(String tableName, String firstColumnName, List<String> columnNames) {
        StringBuilder query = new StringBuilder("SELECT ");

        query.append(firstColumnName).append(", ");

        if (StringUtils.isNotEmpty(tableName)
                && columnNames != null && columnNames.size() > 0) {

            query.append(StringUtils.join(columnNames, ",")).append(" FROM ")
            .append(tableName);
        }
        return query.toString();
    }

    private double[] getResponseArray(List<List<Double>> columnValues){
        List<Double> responseList = columnValues.get(0);
        double[] primitiveArray = new double[responseList.size()];

        for (int i = 0; i < responseList.size(); i++){
            primitiveArray[i] = responseList.get(i);
        }

        if(columnValues.size() > 0 ){
            columnValues.remove(0);
        }
        return primitiveArray;
    }

    private double[][] getPredictorArrays(List<List<Double>> columnValues){
        double[][] primitiveArray = new double[columnValues.size()][columnValues.get(0).size()];

        int size = columnValues.size();
        for (int i = 0; i < size; i++){
            primitiveArray[i] = getResponseArray(columnValues);
        }

        return transposeMatrix(primitiveArray);
    } 

    public double[][] transposeMatrix(double [][] m){

        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[0].length; j++){
                temp[j][i] = m[i][j];
            }
        }
        return temp;
    }
}
