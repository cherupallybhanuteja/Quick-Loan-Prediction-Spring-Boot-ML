package com.gonnect.deeplearning.loanapprover.api;

import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;
import io.swagger.v3.oas.annotations.Operation; // Replace with springdoc
import com.gonnect.deeplearning.loanapprover.model.AtrociousLoanModel;
import com.gonnect.deeplearning.loanapprover.model.LoanInterestRateModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/loan/approver")
public class LoanApproverApi {

    private EasyPredictModelWrapper badLoanModel;
    private EasyPredictModelWrapper interestRateModel;

    @GetMapping("/version")
    @Operation(summary = "Get API Version") // Added Operation annotation for better documentation
    public String version() {
        return "0.0.1";
    }

    @PostMapping(path = "/predict", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Predict Loan Approval") // Added Operation annotation for predict endpoint
    public ResponseEntity<LoanApprovalPrediction> predict(@RequestBody LoanApprovalDependentVariables dv) throws Exception {

        RowData rowData = new RowData();
        rowData.put("loan_amnt", dv.getLoan_amnt());
        rowData.put("term", dv.getTerm());
        rowData.put("int_rate", dv.getInt_rate());
        rowData.put("emp_length", dv.getEmp_length());
        rowData.put("home_ownership", dv.getHome_ownership());
        rowData.put("annual_inc", dv.getAnnual_inc());
        rowData.put("purpose", dv.getPurpose());
        rowData.put("addr_state", dv.getAddr_state());
        rowData.put("dti", dv.getDti());
        rowData.put("delinq_2yrs", dv.getDelinq_2yrs());
        rowData.put("revol_util", dv.getRevol_util());
        rowData.put("total_acc", dv.getTotal_acc());
        rowData.put("bad_loan", dv.getBad_loan());
        rowData.put("longest_credit_length", dv.getLongest_credit_length());
        rowData.put("verification_status", dv.getVerification_status());

        BinomialModelPrediction binomialModelPrediction = predictBadLoan(rowData);
        RegressionModelPrediction regressionModelPrediction = predictInterestRate(rowData);

        return new ResponseEntity<>(getLoanApprovalPrediction(binomialModelPrediction, regressionModelPrediction), OK);
    }

    @PostConstruct
    public void postConstruct() {
        badLoanModel = new EasyPredictModelWrapper(new AtrociousLoanModel());
        interestRateModel = new EasyPredictModelWrapper(new LoanInterestRateModel());
    }

    private BinomialModelPrediction predictBadLoan(RowData row) throws Exception {
        return badLoanModel.predictBinomial(row);
    }

    private RegressionModelPrediction predictInterestRate(RowData row) throws Exception {
        return interestRateModel.predictRegression(row);
    }

    private LoanApprovalPrediction getLoanApprovalPrediction(BinomialModelPrediction p, RegressionModelPrediction p2) {
        LoanApprovalPrediction prediction = new LoanApprovalPrediction();
        prediction.setLabel(p.label);
        prediction.setLabelIndex(p.labelIndex);

        prediction.setClassProbabilities(new ArrayList<>());
        for (double prob : p.classProbabilities) {
            prediction.addClassProbability(prob);
        }

        prediction.setInterestRate(p2.value);

        return prediction;
    }
}
