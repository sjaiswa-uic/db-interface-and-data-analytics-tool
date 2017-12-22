package edu.uic.descriptivestat;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import edu.uic.data.DataService;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;


/*
 * Controller Managed-Bean
 * 
 * This type of managed-bean participates in the "Controller" concern of the MVC design pattern. The
 * purpose of a controller bean is to execute some kind of business logic and return a navigation
 * outcome to the JSF navigation-handler. JSF controller-beans typically have JSF action methods.
 * 
 */
@ManagedBean
@RequestScoped
public class DescriptiveModelController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{descriptiveModel}")
    private DescriptiveModel descriptiveModel;

    @ManagedProperty(value = "#{descriptiveForm}")
    private DescriptiveForm descriptiveForm;

    // Since LoginForm is session scopped I am injecting it into
    // all the controllers for the ease of connecting to database
    @ManagedProperty(value = "#{loginForm}")
    LoginForm loginForm;

    @ManagedProperty(value = "#{messageModel}")
    MessageModel messageModel;

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public DescriptiveModel getDescriptiveModel() {
        return descriptiveModel;
    }

    public void setDescriptiveModel(DescriptiveModel descriptiveModel) {
        this.descriptiveModel = descriptiveModel;
    }

    public DescriptiveForm getDescriptiveForm() {
        return descriptiveForm;
    }

    public void setDescriptiveForm(DescriptiveForm descriptiveForm) {
        this.descriptiveForm = descriptiveForm;
    }

    //Get all the dataset names and populate on the page
    public void processBeforePageLoad(ComponentSystemEvent componentSystemEvent) {
        DescriptiveModelService dataService = new DescriptiveModelService();
        this.descriptiveModel.setDataSet(
                dataService.getAllDataset(this.loginForm, this.descriptiveModel, this.messageModel));
    }

    // Get all the column names for the given dataseet
    public void getColumns(AjaxBehaviorEvent behaviorEvent) {
        DescriptiveModelService modelService = new DescriptiveModelService();
        DataService dataService = new DataService();
        this.descriptiveModel.setColumnList(dataService.getColumnNames(this.loginForm, this.messageModel,
                this.descriptiveForm.getDataLabel()));
        this.descriptiveModel.setOperationList(modelService.getAllOperations());
    }
    
    public void getColumnsForRegression() {
        DataService dataService = new DataService();
        this.descriptiveModel.setColumnList(dataService.getColumnNames(this.loginForm, this.messageModel,
                this.descriptiveForm.getDataLabel()));
    }

    // Compute logarithmic difference between each progressive values
    public void computeColumn() {
        DescriptiveModelService modelService = new DescriptiveModelService();
        if(modelService.computeColumn(this.descriptiveForm, this.loginForm, this.descriptiveModel)) {
            this.descriptiveModel.setStatus("Successfully computed the column " + this.descriptiveForm.getColumnList().get(0));
        }
    }

    // Get processed data into Model to display in UI
    public void getDataSetByLabel() {
        DescriptiveModelService dataService = new DescriptiveModelService();
        this.descriptiveModel.setTableData(
                dataService.processDataSet(this.descriptiveForm, this.loginForm, this.descriptiveModel));
    }

    // Get processed data into Model to display in UI
    public void getAllStasticalData() {
        DescriptiveModelService dataService = new DescriptiveModelService();
        DataService service = new DataService();

        this.descriptiveForm.setColumnList(service.getColumnNames(this.loginForm, this.messageModel,
                this.descriptiveForm.getDataLabel()));
        this.descriptiveModel.setTableData(
                dataService.processDataSet(this.descriptiveForm, this.loginForm, this.descriptiveModel));
    }
    
    // Get processed data into Model to display in UI
    public void getGraphicalAnalysis() {
        
        JFreeChart chart = getChart(this.descriptiveForm.getRenderType());
        
        String fileName = Calendar.getInstance().getTimeInMillis() + "_bargraph.png";
        String absolutePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/jfree/") + fileName;
        
        File outputFile = new File(absolutePath);
        outputFile.getParentFile().mkdirs();
        try {
            ChartUtilities.saveChartAsPNG(outputFile, chart, 800, 800);
        } catch (IOException e) {
            this.descriptiveModel.setStatus("Exception while trying to create the Jfree Chart " + e);
        }
        this.descriptiveModel.setImagePath("/jfree/" + fileName); 
    }
    
    private JFreeChart getChart (String type){
        DescriptiveModelService dataService = new DescriptiveModelService();
        Object result = dataService.processGraphicalData(this.descriptiveForm, this.loginForm, this.descriptiveModel, type);
        
        if("pie".equalsIgnoreCase(type)){
            return ChartFactory.createPieChart (
                    this.descriptiveForm.getColumnName(), (DefaultPieDataset) result, true, true, false);
        }else{
            return ChartFactory.createBarChart (
                    this.descriptiveForm.getColumnName(), "Category","Value", 
                    (DefaultCategoryDataset) result, PlotOrientation.VERTICAL, true, true, false);
        }
    }
}
