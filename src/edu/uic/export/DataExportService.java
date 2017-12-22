package edu.uic.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uic.dao.DatabaseAccess;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;


public class DataExportService {
    
    public File exportDataToXML(LoginForm loginForm, MessageModel messageModel, String datasetName){
       
        try{
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element results = doc.createElement("Results");
            doc.appendChild(results);

            DatabaseAccess databaseAccess = new DatabaseAccess();
            ResultSet rs = databaseAccess.getTableToExport(loginForm, datasetName);


            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            while (rs.next()) {
                Element row = doc.createElement("Row");
                results.appendChild(row);
                for (int i = 1; i <= colCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(i);
                    Element node = doc.createElement(columnName);
                    node.appendChild(doc.createTextNode(value.toString()));
                    row.appendChild(node);
                }
            }
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            //StringWriter sw = new StringWriter();
            
            // Create Temporary file
            File tempFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), ".tmp");
            tempFile.deleteOnExit();
            transformer.transform(domSource, new StreamResult(tempFile));
            
            return tempFile;

        }catch (Exception e) {

        }
        return null;
    }
    
    public File exportDataToCSV(LoginForm loginForm, String datasetName) {

        try {
            
            // Create Temporary file
            File tempFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), ".tmp");
            tempFile.deleteOnExit();
            
            FileWriter fw = new FileWriter(tempFile);
            DatabaseAccess databaseAccess = new DatabaseAccess();
            ResultSet rs;
            rs = databaseAccess.getTableToExport(loginForm, datasetName);
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int numberOfColumns = resultSetMetaData.getColumnCount();
            //String dataHeaders = "\"" + resultSetMetaData.getColumnName(1) + "\"" ;
            for (int i=1; i<(numberOfColumns+1); i++) {
                fw.append(rs.getMetaData().getColumnName(i));
                if ( i < numberOfColumns) {
                    fw.append(","); 
                } else { 
                    fw.append("\r\n");
                }
            }

            while (rs.next()) {
                for (int i=1; i<(numberOfColumns+1); i++) {
                    fw.append(rs.getString(i));

                    if (i < numberOfColumns){ 
                        fw.append(","); 
                    } else {
                        fw.append("\r\n");
                    }
                }

            }
            fw.flush();
            fw.close();
            
            return tempFile;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    public File exportDataToExcel(LoginForm loginForm, MessageModel messageModel, String datasetName){

        try {
            
            // Create Temporary file
            File tempFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), ".tmp");
            tempFile.deleteOnExit();
            FileOutputStream fileOut = new FileOutputStream(tempFile);
            
            DatabaseAccess databaseAccess = new DatabaseAccess();
            ResultSet rs;
            rs = databaseAccess.getTableToExport(loginForm, datasetName);
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            
            //Create an empty excel
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("GROUP-307");
            HSSFRow rowhead = sheet.createRow((short) 0);
            
            // Write column Headers First
            int numberOfColumns = resultSetMetaData.getColumnCount();
            for (int i=1; i<(numberOfColumns+1); i++) {
                rowhead.createCell(i - 1).setCellValue(rs.getMetaData().getColumnName(i));
            }
            
            //Write column values next
            int rowNum = 1;
            while (rs.next()) {
                HSSFRow row = sheet.createRow((short) rowNum);
                for (int i=1; i<(numberOfColumns+1); i++) {
                    row.createCell(i - 1).setCellValue(rs.getString(i));
                }
                rowNum ++;
            }
            
            // Write to the actual physical file
            workbook.write(fileOut);
            fileOut.close();
            
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}

