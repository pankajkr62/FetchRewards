package com.fetchrewards.app.credit.Services;
import com.fetchrewards.app.credit.Dao.CreditDao;
import com.fetchrewards.app.credit.Exceptions.ExcessSpendPointsException;
import com.fetchrewards.app.credit.Exceptions.ValidationFailedException;
import com.fetchrewards.app.credit.Models.*;
import com.fetchrewards.app.credit.Validation.ValidateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
@Slf4j
public class TransactionManagerService {

    @Autowired
    private CreditDao dao;

    final Logger logger = LoggerFactory.getLogger(TransactionManagerService.class);

    /**
     * Validates and add the transaction in database
     * @param txn Transaction object. Fields are (payer, points, timestamp)
     * @throws ValidationFailedException
     */
    public void addTransaction(Transaction txn) throws ValidationFailedException {
        ValidateRequest.validateTransaction(txn);
        logger.info("Adding transaction: "+"payer: "+txn.getPayer()+" points: "
                +txn.getPoints() + " timestamp: "+txn.getTimestamp());
        dao.addTransaction(txn);
    }


    /**
     * Algorithm used for handling Spend Points
     * 1. Validate the RequestOfSpend input
     * 2. Traverse through the sortedTransactionList from starting and only use those payer's txns with overall positive
     *    balance.
     * 3. Store the information of fully used txns and partially used transaction(last txn) if present.
     * 4. Check for ExcessSpendPointsException (left points to spend even after using all transactions).
     * 5. Apply Update the sortedTransaction list and balanceMap i.e remove fully used txns, update last partially
     *    used txns, update payer's points in balanceMap
     * @param spend ResponseOfSpend Object. Fields are ( points ).
     * @return List of payers with their spent points
     * @throws ExcessSpendPointsException
     * @throws ValidationFailedException
     */
    public ResponseOfSpend handleSpend(RequestOfSpend spend)
            throws ExcessSpendPointsException,ValidationFailedException {

        // validating the transaction
        ValidateRequest.validateRequestOfSpend(spend);
        logger.info("Spending Points: "+"points: "+spend.getPoints());

        int pointsToSpend = spend.getPoints();
        // getting the current sorted transactions list
        List<Transaction> transactionList = dao.getSortedTransactionList();

        // map to store points spent per payer
        HashMap<String, Integer> pointsSpentPerPayer = new HashMap<>();
        // Storing indexes of used transaction to remove later
        List<Integer> index_to_remove = new ArrayList<>();
        // Variables to store partially last transaction when transaction's points > left pointsToSpend
        int lastTransactionIndex = -1;
        int lastTransactionUsedAmount = 0;

        for(int i = 0;i< transactionList.size();i++){
            Transaction txn = transactionList.get(i);
            // use transaction only if respective payer's overall balance is positive
            if(dao.getBalanceMap().get(txn.getPayer()) <= 0){
                continue;
            }

            // fully used transactions
            if(txn.getPoints() <= pointsToSpend){
                index_to_remove.add(i);
                pointsToSpend -= txn.getPoints();

                // Update and store the points spent for each used transaction's payer
                if(!pointsSpentPerPayer.containsKey(txn.getPayer())){
                    pointsSpentPerPayer.put(txn.getPayer(),0);
                }
                pointsSpentPerPayer.put(txn.getPayer(), pointsSpentPerPayer.get(txn.getPayer())-txn.getPoints());

            }
            // partially used last transaction
            else{
                // Update and store the points spent for each used transaction's payer
                if(!pointsSpentPerPayer.containsKey(txn.getPayer())){
                    pointsSpentPerPayer.put(txn.getPayer(),0);
                }
                pointsSpentPerPayer.put(txn.getPayer(), pointsSpentPerPayer.get(txn.getPayer()) - pointsToSpend);
                // store the
                lastTransactionIndex = i;
                lastTransactionUsedAmount = pointsToSpend;
                pointsToSpend = 0;
                break;
            }
        }

        // Throws exception if not able spend all points.
        if(pointsToSpend > 0){
            throw new ExcessSpendPointsException("Low Balance. Spend points is more than overall balance of all payers.");
        }

        // update the partially used last transaction if present
        if(lastTransactionIndex != -1){
            Transaction txn = transactionList.get(lastTransactionIndex);
            transactionList.get(lastTransactionIndex).setPoints(transactionList.get(lastTransactionIndex).getPoints() - lastTransactionUsedAmount);
            // update the total balance of the transaction's payer
            dao.getBalanceMap().put(txn.getPayer(), dao.getBalanceMap().get(txn.getPayer()) - lastTransactionUsedAmount);
        }
        // remove the fully used transaction from the sorted transaction list
        for(int i = index_to_remove.size()-1;i>=0;i--){
            Transaction temp = transactionList.get(i);
            // update the total balance of the transaction's payer
            dao.getBalanceMap().put(temp.getPayer(), dao.getBalanceMap().get(temp.getPayer()) - temp.getPoints());
            transactionList.remove(i);
        }

        List<payerExpense> allPayerExpenses = new ArrayList<>();
        for(String payer : pointsSpentPerPayer.keySet()){
            allPayerExpenses.add(payerExpense.builder()
                        .payer(payer)
                        .points(pointsSpentPerPayer.get(payer))
                        .build());
        }
        // forming the ResponseOfSpend object
        return ResponseOfSpend.builder()
                .payerExpenses(allPayerExpenses)
                .build();
    }

    /**
     *
     * @return List of available balances for all payers.
     */
    public ResponseOfBalance getBalance(){
        return ResponseOfBalance.builder()
                .balance(dao.getBalanceMap())
                .build();
    }

}
