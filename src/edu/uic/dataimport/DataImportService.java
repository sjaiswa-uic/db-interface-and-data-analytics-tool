package edu.uic.dataimport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import edu.uic.dao.DatabaseAccess;
import edu.uic.login.LoginForm;

public class DataImportService {

    // Save the file to disk using CommonsIO
    public String writeFileToDisk(DataImportForm form, DataImportModel model) {

        try {
            
            File tempDataFile = new File(getQualifiedPath(form));
            FileUtils.copyInputStreamToFile(
                    new BufferedInputStream(form.getData().getInputStream()), tempDataFile);
            model.setFilePath(tempDataFile.getAbsolutePath());
            model.setFileSize(tempDataFile.length());

        } catch (IOException e) {
            return "faliure";
        }
        return "success";
    }

    // Convert UserInputData into List of energies to easily calculate rowCount
    public List<Row> transformFile(DataImportForm form, LoginForm loginForm) throws Exception {

        DatabaseAccess access = new DatabaseAccess();
        DataInsertService insertService = new DataInsertService();
        Iterable<CSVRecord> records = parseFileByType(form);
        List<String> columnDetails = null;

        boolean verifyDataIntigrity = "data".equals(form.getFileType()) 
                && insertService.doesTableExist(loginForm, form.getFileLabel());

        // Get the column details to verify data intigrity
        if(verifyDataIntigrity){
            columnDetails = access.getColumnDetailsByTable(loginForm, getTableNameFromLabel(form.getFileLabel()));
            columnDetails.remove(0);
        }

        // Each row of data has been represented as a list
        // So all data becomes a List-of-List
        List<Row> tableRows = new ArrayList<>();

        for (CSVRecord record : records) {
            Iterator<String> recordString = record.iterator();
            List<Object> row = new ArrayList<>();

            if(verifyDataIntigrity){
                for (int i=1; recordString.hasNext(); i++) {
                    String value = recordString.next();
                    
                    // If dataset size exceeds the column length 
                    // Then only "Not-Ignore" the value if its not empty
                    if(i <= columnDetails.size()){
                        row.add(value);
                    } else if (i > columnDetails.size() 
                            && value != null && !value.isEmpty()){
                        row.add(value);
                    }
                }
                tableRows.add(new Row(row, columnDetails));
            }else{
                while (recordString.hasNext()) {
                    row.add(transformGenericInput(recordString.next()));
                }
                tableRows.add(new Row(row));
            }
        }
        return tableRows;
    }

    //Convert the user uploaded file into Iterable<CSVRecord> so that it can be easily read now
    private Iterable<CSVRecord> parseFileByType(DataImportForm form)
            throws FileNotFoundException, IOException {

        // Prepare the file stats
        File tempFile = File.createTempFile("temp" + form.getFileLabel(), form.getFileFormat());
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(form.getData().getInputStream(), out);
        }
        
        if("xls".equalsIgnoreCase(form.getFileFormat())){
            tempFile = ExcelConverter.convertToXls(new FileInputStream(tempFile));
            form.setFileFormat("csv");
        }
        
        Iterable<CSVRecord> records = evaluateHeader(form).parse(new FileReader(tempFile));
        tempFile.delete();


        return records;
    }

    // Add the indicator to CSV format weather to skip header or not
    private CSVFormat evaluateHeader(DataImportForm form) {
        return evaluateFileExtension(form.getFileFormat()).withSkipHeaderRecord(
                "Y".equalsIgnoreCase(form.getHeaderIndicator()) ? true : false);
    }

    // Based on file extension return CSVFormat
    private CSVFormat evaluateFileExtension(String extension) {
        CSVFormat format = null;
        switch (extension) {
            case "csv":
                format = CSVFormat.DEFAULT;
                break;

            case "txt":
                format = CSVFormat.TDF;
                break;
        }
        return format;
    }

    // Create the fully qualified path for this file
    private String getQualifiedPath(DataImportForm form) throws IOException {

        StringBuilder path = new StringBuilder(((ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext()).getRealPath(""));
        path.append("/WebContent/data-import/");
        path.append(form.getFileType()).append("/");
        path.append(form.getFileLabel()).append(".");
        path.append(form.getFileFormat());
        return path.toString();
    }

    private String getTableNameFromLabel(String labelName){
        return "S17G307_" + labelName + "_STATS";
    }

    private Object transformGenericInput(String value){

        try {return Integer.parseInt(value);} catch (Exception e) {}

        try {return Long.parseLong(value);} catch (Exception e) {}

        try {return Double.parseDouble(value);} catch (Exception e) {}

        return value;
    }
}
