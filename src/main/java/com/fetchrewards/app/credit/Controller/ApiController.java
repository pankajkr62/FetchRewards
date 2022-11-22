package com.fetchrewards.app.credit.Controller;

import com.fetchrewards.app.credit.Exceptions.ExcessSpendPointsException;
import com.fetchrewards.app.credit.Exceptions.ValidationFailedException;
import com.fetchrewards.app.credit.Models.*;
import com.fetchrewards.app.credit.Services.TransactionManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/credit")
public class ApiController {

    @Autowired
    private TransactionManagerService manager;

    /**
     * For adding new Transactions
     * @param request Transaction Object. Fields are (payer, points, timestamp)
     * @return ResponseObject with success message and HttpStatus
     * @throws ValidationFailedException if request is invalid. Return error msg too.
     */
    @PostMapping(value = "/addTransaction")
    public ResponseEntity<String> addTransaction(@RequestBody Transaction request)
            throws ValidationFailedException {
        manager.addTransaction( request );
        return new ResponseEntity<>("Added Transaction Successfully", HttpStatus.OK);
    }

    /**
     * For spending points
     * @param request RequestOfSpend object. Fields are (points)
     * @return List of payers with their spent points.
     * @throws ExcessSpendPointsException if points to spend is greater than available balance
     * @throws ValidationFailedException if request is invalid. Return error msg too.
     */
    @PostMapping(value = "/spendPoints")
    public ResponseEntity<ResponseOfSpend> spend(@RequestBody RequestOfSpend request)
            throws ExcessSpendPointsException, ValidationFailedException{
        ResponseOfSpend response = manager.handleSpend( request );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * For getting balance of all payers
     * @return List of available points for all payers.
     */
    @GetMapping(value = "/getBalance")
    public ResponseEntity<ResponseOfBalance> balance(){
        ResponseOfBalance response = manager.getBalance();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
