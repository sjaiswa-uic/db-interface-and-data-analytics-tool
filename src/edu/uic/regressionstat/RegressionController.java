package edu.uic.regressionstat;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import edu.uic.data.PageForm;
import edu.uic.descriptivestat.DescriptiveForm;
import edu.uic.descriptivestat.DescriptiveModel;
import edu.uic.login.LoginForm;

@ManagedBean
@RequestScoped
public class RegressionController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{regressionModel}")
    private RegressionModel regressionModel;

    @ManagedProperty(value = "#{regressionForm}")
    private RegressionForm regressionForm;
    

    @ManagedProperty(value = "#{descriptiveModel}")
    private DescriptiveModel descriptiveModel;

    @ManagedProperty(value = "#{descriptiveForm}")
    private DescriptiveForm descriptiveForm;

    // Since LoginForm is session scopped I am injecting it into
    // all the controllers for the ease of connecting to database
    @ManagedProperty(value = "#{loginForm}")
    LoginForm loginForm;
    
    @ManagedProperty(value = "#{pageForm}")
    PageForm pageForm;
    
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

    public PageForm getPageForm() {
        return pageForm;
    }

    public void setPageForm(PageForm pageForm) {
        this.pageForm = pageForm;
    }

    public RegressionModel getRegressionModel() {
        return regressionModel;
    }

    public void setRegressionModel(RegressionModel regressionModel) {
        this.regressionModel = regressionModel;
    }

    public RegressionForm getRegressionForm() {
        return regressionForm;
    }

    public void setRegressionForm(RegressionForm regressionForm) {
        this.regressionForm = regressionForm;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    //Get processed data into Model to display in UI
    public void computeRegression() {
        RegressionService service = new RegressionService();
        this.regressionModel.reset();
        this.regressionForm.setDataLabel(this.descriptiveForm.getDataLabel());
        
        try{
        // Check if user has selected any columns at all
        // Else present the user with an error message
        if (this.regressionForm.getPredictorColumns() != null &&
                this.regressionForm.getPredictorColumns().size() > 0 ){
            
            if (this.regressionForm.getPredictorColumns().size() > 1){
                service.computeMultipleRegression(this.regressionForm, this.loginForm, this.regressionModel);
            } else {
                
                // Set the predictor column name from list into String
                this.regressionForm.setPredictorColumn(this.regressionForm.getPredictorColumns().get(0));
                this.setRegressionModel(service.computeRegressionStats(this.regressionForm, this.loginForm, this.regressionModel));
            }
        } else {
            this.regressionModel.setStatus("Please select Predictor Columns.");;
        }
        }catch (Exception e) {
           this.regressionModel.setStatus("Cannot Process Your Request");
        }
    }
    
    // Prepare the graphical representation of the regression data
    public void getGraphicalRegression() {
        RegressionService service = new RegressionService();
        this.regressionForm.setDataLabel(this.descriptiveForm.getDataLabel());

        JFreeChart chart = service.createGraphicalRepresentation(
                                this.regressionForm, this.loginForm, this.regressionModel);
        
        String fileName = Calendar.getInstance().getTimeInMillis() + "_series.png";
        String absolutePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/jfree/") + fileName;
        
        File outputFile = new File(absolutePath);
        outputFile.getParentFile().mkdirs();
        try {
            ChartUtilities.saveChartAsPNG(outputFile, chart, 800, 800);
        } catch (IOException e) {
            this.regressionModel.setStatus("Exception while trying to create the Jfree Chart " + e);
        }
        this.regressionModel.setImagePath("/jfree/" + fileName);
    }
}
