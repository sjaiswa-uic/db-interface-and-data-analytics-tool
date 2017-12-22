package edu.uic.descriptivestat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import edu.uic.dao.DatabaseAccess;
import edu.uic.data.DataService;
import edu.uic.data.TableData;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;
import sun.util.logging.resources.logging;

public class DescriptiveModelService {

    private static final String FILE_HEADER[] =
        {"Dataset", "Variable", "NumberObs", "MinValue", "Q1", "Median", "Q3", "MaxValue",
                "Mean", "Variance", "StandardDeviation", "Iqr", "Range"};

    private static final String FILE_HEADER_SPLIT_PART_1[] =
        {"Variable", "MinValue", "Q1", "Median", "Q3", "MaxValue"};

    private static final String FILE_HEADER_SPLIT_PART_2[] = {"Variable", "Mean", "Variance", "STD Deviation", "Iqr", "Range"};

    //Get names of all the processed dataset from filesystem
    public List<String> getAllDataset(LoginForm loginForm, DescriptiveModel model, MessageModel messageModel) {

        DataService service = new DataService();
        List<String> datasetNames = new ArrayList<>();

        List<String> tableNames = service.getTableNames(loginForm, messageModel);
        try {
            for (String tableName : tableNames){
                if(tableName.startsWith("S17G307_".toLowerCase()) 
                        && tableName.endsWith("_STATS".toLowerCase())){
                    datasetNames.add(tableName);
                }
            }
        } catch (Exception e) {
            model.setStatus("No Dataset Exists : \n" + e.getMessage());
        }
        return datasetNames;
    }

    //Get names of all the processed dataset from filesystem
    public List<String> getAllOperations() {
        return Arrays.asList(FILE_HEADER);
    }

    //Read the descriptive analysis and return the data in tabular format
    public TableData getDataFromFileSystem(String dataSetName) {

        TableData tableData = new TableData();

        // initialize FileReader object and make it fancy
        try (FileReader fileReader = new FileReader(getFilePath(dataSetName, "processed"))) {

            // Create the CSVFormat object with the header mapping
            Iterable<CSVRecord> csvRecords = CSVFormat.DEFAULT.parse(fileReader);

            // Read the CSV file records starting from the second record to skip the header
            tableData = convertToReadableFormat(csvRecords, false);

            //read the header file and populate it into tableData
            populateHeaderIfExists(tableData, dataSetName);
        } catch (Exception e) {
        }
        return tableData;
    }

    public TableData processDataSet(DescriptiveForm descriptiveForm, LoginForm login, DescriptiveModel model) {
        DatabaseAccess access = new DatabaseAccess();
        TableData tableData = new TableData();

        TableData part1 = new TableData();
        part1.setColumnNames(Arrays.asList(FILE_HEADER_SPLIT_PART_1));

        TableData part2  = new TableData();
        part2.setColumnNames(Arrays.asList(FILE_HEADER_SPLIT_PART_2));

        boolean filterByOperation = descriptiveForm.getOperations() != null 
                && descriptiveForm.getOperations().size() > 0;

                if(filterByOperation){
                    tableData.setColumnNames(descriptiveForm.getOperations());
                }else {
                    tableData.setColumnNames(Arrays.asList(FILE_HEADER));
                }

                try {

                    for (String columnName : descriptiveForm.getColumnList()){
                        List<Object> valueList = access.getColumnValues(
                                login, createQuery(descriptiveForm.getDataLabel(), columnName));

                        double[] column = new double[valueList.size()];
                        int arrayIndex = 0;
                        for (Object rowValue : valueList) {
                            Double temp = 0.00d;
                            try{
                                temp = (Double) rowValue;
                            } catch (Exception e){}
                            column[arrayIndex++] = temp;
                        }

                        model.setDatasetName(descriptiveForm.getDataLabel());
                        model.setNumberOfObservations(String.valueOf(valueList.size()));

                        if(true){
                            part1.setRow(filteredCalculate(column, descriptiveForm.getDataLabel(), columnName, Arrays.asList(FILE_HEADER_SPLIT_PART_1)));
                            part2.setRow(filteredCalculate(column, descriptiveForm.getDataLabel(), columnName, Arrays.asList(FILE_HEADER_SPLIT_PART_2)));

                            tableData.setRow(calculate(column, descriptiveForm.getDataLabel(), columnName));
                        }else {
                            tableData.setRow(calculate(column, descriptiveForm.getDataLabel(), columnName));
                        }
                    }
                }catch (Exception e){
                    model.setStatus("Error Occured while trying to retrieve dataset and perform stastical analysis. Select a Component to Proceed.");
                }

                model.setDataPart1(part1);
                model.setDataPart2(part2);

                return tableData;
    }

    public Object processGraphicalData(DescriptiveForm form, LoginForm login, DescriptiveModel model, String type) {
        DatabaseAccess access = new DatabaseAccess();

        try {

            if(form.getColumnName() == null || StringUtils.isEmpty(form.getColumnName())){
                model.setStatus("You should select only/atleast 1 column to Compute");
                //return;
            }

            List<Object> valueList = access.getColumnValues(
                    login, createQuery(form.getDataLabel(), form.getColumnName()));

            double[] columnValues = new double[valueList.size()];
            int arrayIndex = 0;
            for (Object rowValue : valueList) {
                columnValues[arrayIndex++] = (Double) rowValue;
            }

            double q1 = StatUtils.percentile(columnValues, 25.0);
            double median = StatUtils.percentile(columnValues, 50.0);
            double q3 = StatUtils.percentile(columnValues, 75.0);
            int q1Count = 0, medianCount = 0, q3Count = 0, outOfRange = 0;

            for(double value : columnValues) {
                if(value <= q1) {
                    q1Count ++;
                }
                if(value > q1 && value <= median) {
                    medianCount ++;
                }
                if(value > median && value <= q3) {
                    q3Count ++;
                }
                if(value >  q3) {
                    outOfRange ++;
                }
            }

            if("pie".equalsIgnoreCase(type)) {
                DefaultPieDataset pieModel = new DefaultPieDataset();
                pieModel.setValue("Quartile Q1", q1Count);
                pieModel.setValue("Lies between Quartile Q1 and Median", medianCount);
                pieModel.setValue("Lies between Median and Quartile Q3", q3Count);
                pieModel.setValue("Greater than Q3", outOfRange);

                return pieModel;
            } else {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                dataset.addValue(q1Count," Quartile Q1 ", "Category 1");
                dataset.addValue(medianCount," Lies between Quartile Q1 and Median ", "Category 2");
                dataset.addValue(q3Count,"Lies between Median and Quartile Q3","Category 3");
                dataset.addValue(outOfRange," Greater than Q3 ", "Category 4");

                return dataset;
            }
        }catch (Exception e){
            model.setStatus("Error Occured while trying to Create graphical Representation. Select a Component to proceed.");
        }

        return null;
    }

    public TableData processRegression(DescriptiveForm descriptiveForm, LoginForm login, DescriptiveModel model) {
        DatabaseAccess access = new DatabaseAccess();
        TableData tableData = new TableData();
        tableData.setColumnNames(Arrays.asList(FILE_HEADER));

        try {

            for (String columnName : descriptiveForm.getColumnList()){
                List<Object> valueList = access.getColumnValues(
                        login, createQuery(descriptiveForm.getDataLabel(), columnName));

                double[] column = new double[valueList.size()];
                int arrayIndex = 0;
                for (Object rowValue : valueList) {
                    Double temp = 0.00d;
                    try{
                        temp = (Double) rowValue;
                    } catch (Exception e){}
                    column[arrayIndex++] = temp;
                }

                tableData.setRow(calculate(column, descriptiveForm.getDataLabel(), columnName));
            }
        }catch (Exception e){
            model.setStatus("Error Occured while trying to retrieve dataset and perform stastical analysis.Select a Component to proceed.");
        }
        return tableData;
    }

    public boolean computeColumn(DescriptiveForm form, LoginForm loginForm, DescriptiveModel model){
        DatabaseAccess access = new DatabaseAccess();
        DataService dataService = new DataService();

        if(! ifValidInput(model, form, loginForm)){
            return false;
        }

        // Check if the computed column already exists if not then create new
        if(!dataService.getColumnNames(loginForm, new MessageModel(), 
                form.getDataLabel()).contains("R_" + form.getColumnList().get(0))){

            // Add the new column to database dynamically
            try {
                access.executeUpdateQuery(loginForm, getAlterQuery(
                        form.getDataLabel(), form.getColumnList().get(0)));
            } catch (Exception e) {
                model.setStatus("Unable To Alter Table / Add a new column : " +  e);
                return false;
            }
        }


        try {
            // Get column values from database and calculate the Logarithmic difference
            List<Object> computedValues = computeLograthims(access.getColumnValues(
                    loginForm, createQuery(form.getDataLabel(), form.getColumnList().get(0))));

            // Prepare insert queries and do a batch update for better performance
            access.executeBatchUpdate(loginForm, getBatchUpdateQueries(
                    form.getDataLabel(), form.getColumnList().get(0), computedValues));
        } catch (Exception e) {
            model.setStatus("Exception occured while trying to insert the computed values into database. Query connection failed.");
            return false;
        }

        return true;
    }

    private List<String> calculate(double[] dataArray, String datasetLabel, String columnName) {

        List<String> dataValues = new ArrayList<>();
        dataValues.add(datasetLabel);
        dataValues.add(columnName);
        dataValues.add(String.valueOf(dataArray.length));

        double min = StatUtils.min(dataArray);
        dataValues.add(String.valueOf(min));
        double q1 = StatUtils.percentile(dataArray, 25.0);
        dataValues.add(String.valueOf(q1));
        dataValues.add(String.valueOf(StatUtils.percentile(dataArray, 50.0)));

        double q3 = StatUtils.percentile(dataArray, 75.0);
        dataValues.add(String.valueOf(q3));


        double max = StatUtils.max(dataArray);
        dataValues.add(String.valueOf(max));
        dataValues.add(String.valueOf(StatUtils.mean(dataArray)));
        dataValues.add(String.valueOf(StatUtils.variance(dataArray)));
        dataValues.add(String.valueOf(Math.sqrt(StatUtils.variance(dataArray))));




        double iqr = q3 - q1;
        dataValues.add(String.valueOf(iqr));
        dataValues.add(String.valueOf(max - min));

        return dataValues;
    }

    private List<String> filteredCalculate(double[] dataArray, String datasetLabel, String columnName, List<String> operations) {

        List<String> dataValues = new ArrayList<>();
        if(operations.contains("Dataset")){
            dataValues.add(datasetLabel);
        }

        if(operations.contains("Variable")){
            dataValues.add(columnName);
        }

        if(operations.contains("NumberObs")){
            dataValues.add(String.valueOf(dataArray.length));
        }

        double min = StatUtils.min(dataArray);
        if(operations.contains("MinValue")){
            dataValues.add(String.valueOf(min));
        }
        
        double q1 = StatUtils.percentile(dataArray, 25.0);
        if(operations.contains("Q1")){
            dataValues.add(String.valueOf(q1));
        }
        
        if(operations.contains("Median")){
            dataValues.add(String.valueOf(StatUtils.percentile(dataArray, 50.0)));
        }

        double q3 = StatUtils.percentile(dataArray, 75.0);
        if(operations.contains("Q3")){
            dataValues.add(String.valueOf(q3));
        }
        
        double max = StatUtils.max(dataArray);
        if(operations.contains("MaxValue")){
            dataValues.add(String.valueOf(max));
        }

        if(operations.contains("Mean")){
            dataValues.add(String.valueOf(StatUtils.mean(dataArray)));
        }

        if(operations.contains("Variance")){
            dataValues.add(String.valueOf(StatUtils.variance(dataArray)));
        }

        if(operations.contains("STD Deviation")){
            dataValues.add(String.valueOf(Math.sqrt(StatUtils.variance(dataArray))));
        }

        double iqr = q3 - q1;
        if(operations.contains("Iqr")){
            dataValues.add(String.valueOf(iqr));
        }

        if(operations.contains("Range")){
            dataValues.add(String.valueOf(max - min));
        }

        return dataValues;
    }

    public String createQuery(String tableName, String columnName) {
        StringBuilder query = new StringBuilder("SELECT ");

        if (StringUtils.isNotEmpty(tableName)
                && StringUtils.isNotEmpty(columnName)) {

            query.append(columnName).append(" FROM ")
            .append(tableName);
        }
        return query.toString();
    }


    //Create the fully qualified path for all the processed file
    private String getFilePath(String dataSetName, String directory) throws IOException {
        StringBuilder path = new StringBuilder(((ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext()).getRealPath(""));
        path.append("/WebContent/data-import/");
        path.append(directory).append("/");
        if(dataSetName != null){
            path.append(dataSetName).append(".csv");
        }
        return path.toString();
    }

    private void populateHeaderIfExists(TableData fileData, String directory){

        try {
            List<String> header = findColumnHeaderFromMetadata(directory);
            if(header != null || header.size() > 0) {
                int startIndex = 0;
                if(fileData.getDataRows().size() < header.size()){
                    startIndex = 1;
                }

                for(List<String> rowData : fileData.getDataRows()){
                    rowData.set(1, header.get(startIndex ++));
                }
            }
        } catch (Exception e) {
        }
    }

    // Get list of headers as String from Metadata file
    private List<String> findColumnHeaderFromMetadata(String directory) throws Exception{
        File header = findMetaDataOnFileSystem(directory);
        TableData data = null;
        List<String> headers = new ArrayList<>();
        if(header != null){
            data = convertToReadableFormat(evaluateFileExtension(
                    FilenameUtils.getExtension(header.getName())).parse(new FileReader(header)), true);

            for (List<String> row : data.getDataRows()){
                headers.add(row.get(0));
            }
        }
        return headers;
    }

    //find all the files in a given directory
    private List<File> findFiles(String directory) throws IOException{
        return (List<File>) FileUtils.listFiles(
                new File(getFilePath(null, directory)), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }

    // Check if metadata file is present on File System
    private File findMetaDataOnFileSystem(String fileLabel){
        List<File> files = null;
        try {
            files = findFiles("metadata");
            for(File file : files){
                if(fileLabel.equalsIgnoreCase(file.getName().split("\\.")[0])){
                    return file;
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    // Based on file extension return CSVFormat
    private CSVFormat evaluateFileExtension(String extension) {
        CSVFormat format = null;
        switch (extension) {
            case "csv":
                format = CSVFormat.DEFAULT;
                break;

            case "xls":
                format = CSVFormat.EXCEL;
                break;

            case "txt":
                format = CSVFormat.TDF;
                break;
        }
        return format;
    }

    // Read the CSV file records starting from the second record to skip the header
    private TableData convertToReadableFormat(Iterable<CSVRecord> records, boolean isMetaData){

        TableData tableData = new TableData();
        for (CSVRecord record : records) {
            Iterator<String> iterateRow = record.iterator();
            List<String> row = new ArrayList<>();

            while (iterateRow.hasNext()) {
                row.add(iterateRow.next());
            }

            // Read the First row as header
            if (tableData.getColumnNames() == null && !isMetaData) {
                tableData.setColumnNames(row);
            } else {
                tableData.setRow(row);
            }
        }
        return tableData;
    }

    private List<Object> computeLograthims(List<Object> valueList) {
        List<Object> computedValues = new ArrayList<>();
        computedValues.add(0);

        for (int i = 1; i < valueList.size(); i++){
            computedValues.add(Math.log( (Double) valueList.get(i)) - Math.log( (Double) valueList.get(i - 1))); 
        }

        return computedValues;
    }

    private String getAlterQuery(String tableName, String columnName){
        return new StringBuilder("ALTER TABLE ").append(tableName)
                .append(" ADD R_").append(columnName)
                .append(" DECIMAL (40, 20);").toString();
    }

    private List<String> getBatchUpdateQueries(String tableName, String columnName, List<Object> computedValues){
        List<String> queries = new ArrayList<>();
        for(int i = 0; i < computedValues.size(); i++){
            queries.add(new StringBuilder("UPDATE ")
                    .append(tableName).append(" SET ")
                    .append("R_").append(columnName).append(" = ") 
                    .append(computedValues.get(i)).append(" WHERE ")
                    .append(" ROW_INDEX = ").append(i + 1).append(" ;").toString());
        }
        return queries;
    }

    private boolean ifValidInput(DescriptiveModel model, DescriptiveForm form, LoginForm loginForm){
        // Only One column can be computed
        if(form.getColumnList().size() != 1 || StringUtils.isEmpty(form.getColumnList().get(0))){
            model.setStatus("You should select only/atleast 1 column to Compute");
            return false;
        }

        // Check if it is a computed column
        if(form.getColumnList().get(0).startsWith("R_") ){

            model.setStatus("You cannot initiate Computation on an already computed Column.");
            return false;
        }

        return true;
    }
}
