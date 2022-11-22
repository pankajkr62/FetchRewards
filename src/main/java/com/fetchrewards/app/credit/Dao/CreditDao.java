package com.fetchrewards.app.credit.Dao;

import com.fetchrewards.app.credit.Models.Transaction;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class CreditDao {

    private List<Transaction> sortedTransactionList;
    private HashMap<String, Integer> payerBalanceMap;

    @PostConstruct
    public void init(){
        sortedTransactionList = new ArrayList<>();
        payerBalanceMap = new HashMap<>();
    }

    public List<Transaction> getSortedTransactionList(){
        return sortedTransactionList;
    }

    public HashMap<String, Integer> getBalanceMap(){
        return payerBalanceMap;
    }

    // Add transaction in sortedTransactionList as per timestamp order

    /**
     * First process the positive and negative txns and then add it to the sortedTransactionList
     * as per timestamp order.
     * @param txn Transaction Object. Contains payer, points and timestamp.
     */
    public void addTransaction(Transaction txn){

        if(txn.getPoints() > 0){
            int leftPositiveBalance = processPositiveTransaction(txn);
            if(leftPositiveBalance == 0) return;
            else txn.setPoints(leftPositiveBalance);
        }
        else if(txn.getPoints() < 0){
            int leftNegativeBalance = processNegativeTransaction(txn);
            if(leftNegativeBalance == 0) return;
            else txn.setPoints(leftNegativeBalance);
        }

        int i = 0;
        // getting the correct index to insert the new transaction
        while( i < sortedTransactionList.size()){
            // skipping transactions with lower timestamps
            if(sortedTransactionList.get(i).getTimestamp().compareTo(txn.getTimestamp()) <= 0 ){
                i += 1;
            }
            else{
                break;
            }
        }
        // inserting the transaction
        // if new transaction's timestamp is greatest, add it to end of list
        // otherwise add it to the calculated index
        if( i == sortedTransactionList.size())
            sortedTransactionList.add(txn);
        else
            sortedTransactionList.add(i, txn);

        // Updating the payerBalanceMap for the new transaction payer
        if(payerBalanceMap.containsKey(txn.getPayer())){
            payerBalanceMap.put(txn.getPayer(), payerBalanceMap.get(txn.getPayer()) + txn.getPoints());
        }
        else{
            payerBalanceMap.put(txn.getPayer(), txn.getPoints());
        }
    }


    /**
     * Utility function : Given a payer's txn with positive balance, this utility function cleans up the payer's earlier transactions
     * with negative balance till we don't have any positive balance left or we reached end of transaction list.
     * It returns left positive balance if we reached the end of transaction list( i.e all negative transactions cleaned).
     * @param txn Transaction Object. Contains payer, points and timestamp.
     * @return Left positive balance
     */
    public int processPositiveTransaction(Transaction txn){
        // positive balance to exhaust
        int leftPositiveBalance = txn.getPoints();
        // storing used negative transactions indexes to remove later
        List<Integer> negativeTransactionToRemove = new ArrayList<>();
        for(int i = 0;i<sortedTransactionList.size();i++){
            Transaction currNegTxn = sortedTransactionList.get(i);
            if(currNegTxn.getPayer().equals(txn.getPayer()) && currNegTxn.getPoints() < 0 && Math.abs(currNegTxn.getPoints()) <= leftPositiveBalance){
                // store the negative transaction index
                negativeTransactionToRemove.add(i);
                // update the left positive balance
                leftPositiveBalance += currNegTxn.getPoints();
            }
            // case when negative transaction's amount is greater then leftPositiveBalance
            else if(currNegTxn.getPayer().equals(txn.getPayer()) && currNegTxn.getPoints() < 0 && Math.abs(currNegTxn.getPoints()) > leftPositiveBalance){
                // updating the transaction since we are not removing it like earlier transactions.
                currNegTxn.setPoints(currNegTxn.getPoints() + leftPositiveBalance);
                // update balanceMap for the payer
                payerBalanceMap.put(txn.getPayer(), payerBalanceMap.get(txn.getPayer()) + leftPositiveBalance);
                leftPositiveBalance = 0;
                break;
            }

        }

        // removing the stored payer's negative transactions.
        for(int j = negativeTransactionToRemove.size()-1;j>=0;j--){
            Transaction currNegTxn = sortedTransactionList.get(j);
            payerBalanceMap.put(currNegTxn.getPayer(), payerBalanceMap.get(currNegTxn.getPayer()) - currNegTxn.getPoints());
            sortedTransactionList.remove(currNegTxn);
        }

        return leftPositiveBalance;
    }

    /**
     * Utility function : Given a payer's txn with negative balance, this utility function cleans up the payer's earlier transactions
     * with positive balance till we don't have any negative balance left or we reached end of transaction list.
     * It returns left negative balance if we reached the end of transaction list( i.e all positive transactions cleaned).
     * @param txn Transaction Object. Contains payer, points and timestamp.
     * @return Left negative balance
     */
    public int processNegativeTransaction(Transaction txn){
        // positive balance to exhaust
        int leftNegativeBalance = txn.getPoints();
        // storing used negative transactions indexes to remove later
        List<Integer> positiveTransactionToRemove = new ArrayList<>();
        for(int i = 0;i<sortedTransactionList.size();i++){
            Transaction currPosTxn = sortedTransactionList.get(i);
            if(currPosTxn.getPayer().equals(txn.getPayer()) && currPosTxn.getPoints() > 0
                    && currPosTxn.getPoints() <= Math.abs(leftNegativeBalance)){
                // store the negative transaction index
                positiveTransactionToRemove.add(i);
                // update the left positive balance
                leftNegativeBalance += currPosTxn.getPoints();
            }
            // case when negative transaction's absolute points is greater then leftPositiveBalance
            else if(currPosTxn.getPayer().equals(txn.getPayer()) && currPosTxn.getPoints() > 0
                    && currPosTxn.getPoints() > Math.abs(leftNegativeBalance)){
                // updating the transaction since we are not removing it like earlier transactions.
                currPosTxn.setPoints(currPosTxn.getPoints() + leftNegativeBalance);
                // update balanceMap for the payer
                payerBalanceMap.put(txn.getPayer(), payerBalanceMap.get(txn.getPayer()) + leftNegativeBalance);
                leftNegativeBalance = 0;
                break;
            }

        }
        // removing the stored payer's positive transactions.
        for(int j = positiveTransactionToRemove.size()-1;j>=0;j--){
            Transaction currPosTxn = sortedTransactionList.get(j);
            payerBalanceMap.put(currPosTxn.getPayer(),
                    payerBalanceMap.get(currPosTxn.getPayer()) - currPosTxn.getPoints());
            sortedTransactionList.remove(currPosTxn);
        }

        return leftNegativeBalance;
    }

}

