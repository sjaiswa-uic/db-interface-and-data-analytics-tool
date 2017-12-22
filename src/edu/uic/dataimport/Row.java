package edu.uic.dataimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Row implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private List<Object> rowValues;
    
    private Boolean validity;
    
    public Row() {
        
    }

    public Row(List<Object> rowValues) {
        super();
        this.rowValues = rowValues;
        setValidity(rowValues);
    }
    
    public Row(List<Object> rowValues, List<String> columnDetails) {
        super();
        this.rowValues = rowValues;
        setValidity(rowValues, columnDetails);
    }

    public List<Object> getRowValues() {
        return rowValues;
    }

    public void setRowValues(List<Object> rowValues) {
        this.rowValues = rowValues;
    }
    
    public void addRowValue(List<Object> rowValues) {
        this.rowValues = rowValues;
    }

    public Boolean getValidity() {
        return validity;
    }

    public void setValidity(Boolean validity) {
        this.validity = validity;
    }
    
    private void setValidity(List<Object> dataList){
        for (Object data : dataList) {
            if (data != null && (data instanceof Integer || data instanceof Double || data instanceof Long)){
                this.validity = true;
            }else{
                this.validity = false;
                break;
            }
        }
    }
    
    private void setValidity(List<Object> dataList, List<String> columnDetails){
        
        // If Imported data size is different than Column Length then invalid
        if (dataList.size() != columnDetails.size()){
            this.validity = false;
            return;
        }
        
        Boolean validity = null;
        this.rowValues = new ArrayList<>();
        for (int i=0; i < columnDetails.size(); i++) {
            Object[] resultArray = transformSpecificInput(dataList.get(i), columnDetails.get(i));
            rowValues.add(resultArray[0]);
            
            // Only Initialize validity when its false
            if(i == 0 || !(Boolean) resultArray[1]){
                validity = (Boolean) resultArray[1];
            }
        }
        this.validity = validity;
    }
    
    private Object[] transformSpecificInput(Object value, String type){

        if("INTEGER".equals(type)){
            try {
                return new Object[]{Integer.parseInt(value.toString()), true};
            } catch (Exception e) {
                
            }
        } else if ("BIGINT".equals(type)){
            try {
                return new Object[]{Long.parseLong(value.toString()), true};
            } catch (Exception e) {
                
            }
        } else if ("DECIMAL".equals(type)){
            try {
                return new Object[]{Double.parseDouble(value.toString()), true};
            } catch (Exception e) {
                
            }
        }else if("VARCHAR".equals(type)){
            try {
                return new Object[]{String.valueOf(value.toString()), true};
            } catch (Exception e) {
                
            }
        }

        return new Object[]{String.valueOf(value), false};
    }

}
