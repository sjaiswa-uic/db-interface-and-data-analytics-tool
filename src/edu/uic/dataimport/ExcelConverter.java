package edu.uic.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelConverter {

    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";


    static File convertToXls(FileInputStream inputFileStream) {
        // For storing data into CSV files
        File destinationFile = null; 
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;

        try {

            destinationFile = File.createTempFile("temp_csv" + new Date().getTime(), ".csv");

            // Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(inputFileStream);
            // Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);
            Cell cell;
            Row row;

            //Create the CSVFormat object with "\n" as a record delimiter
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

            //initialize FileWriter object
            fileWriter = new FileWriter(destinationFile);

            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);


            // Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();

                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                List rowValues = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    rowValues.add(cell);
                }
                csvFilePrinter.printRecord(rowValues.toArray(new Object[rowValues.size()]));
            }

        } catch (FileNotFoundException e) {
            System.err.println("Exception" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Exception" + e.getMessage());
        }finally{
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
            }

        }
        return destinationFile;
    }
}
