package ba.unsa.etf.rma.rma2020_16570.List;

import android.content.Context;
import android.util.Log;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;

public class TransactionListPresenter implements ITransactionListPresenter, TransactionListInteractor.OnTransactionsFetched {
    private ITransactionListView transactionListView;
    private ITransactionListInteractor transactionListInteractor;
    private ArrayList<Transaction> transactions;
    private Boolean filter;
    private Month filterMonth;
    private Context context;

    public TransactionListPresenter(ITransactionListView transactionListView, Context context){
        this.transactionListView = transactionListView;
        this.transactionListInteractor = null;
        this.filterMonth = null;
        filter = false;
        this.context = context;
    }
    @Override
    public void refreshTransactions() {
        //transactionListView.setTransactions(transactionListInteractor.get());
        filter = false;
        //transactionListInteractor = new TransactionListInteractor(this, "GET", null);
        String query = new String("/transactions");
        new TransactionListInteractor(context, this, "GET", null).execute(query);
        transactionListView.notifyTransactionListDataSetChanged();
    }

    @Override
    public void sortByPrice(Boolean ascending) {
        ArrayList<Transaction> sorted = new ArrayList<>(transactionListView.getTransactions());
        Collections.sort(sorted, Transaction.compareByAmount);
        if(!ascending) Collections.reverse(sorted);

        transactionListView.setTransactions(sorted);
        transactionListView.notifyTransactionListDataSetChanged();
    }

    @Override
    public void sortByTitle(Boolean ascending) {
        ArrayList<Transaction> sorted = new ArrayList<>(transactionListView.getTransactions());
        Collections.sort(sorted, Transaction.compareByTitle);
        if(!ascending) Collections.reverse(sorted);

        transactionListView.setTransactions(sorted);
        transactionListView.notifyTransactionListDataSetChanged();
    }

    @Override
    public void sortByDate(Boolean ascending) {
        ArrayList<Transaction> sorted = new ArrayList<>(transactionListView.getTransactions());
        Collections.sort(sorted, Transaction.compareByDate);
        if(!ascending) Collections.reverse(sorted);

        transactionListView.setTransactions(sorted);
        transactionListView.notifyTransactionListDataSetChanged();
    }

    @Override
    public void filterByMonth(Month month) {
        filterMonth = month;
        filter = true;
        //transactionListInteractor = new TransactionListInteractor(this, "GET", null);
        String query = new String("/transactions");
        new TransactionListInteractor(context, this, "GET", null).execute(query);
    }

    @Override
    public void updateTransaction(Transaction transaction, Transaction newTransaction) {
        transactionListInteractor.update(transaction, newTransaction);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        transactionListInteractor.delete(transaction);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        transactionListInteractor.add(transaction);
    }

    @Override
    public Double getTotalIncome() {
        return transactionListInteractor.getTotalIncome();
    }

    @Override
    public Double getTotalExpenditure() {
        return transactionListInteractor.getTotalExpenditure();
    }

    @Override
    public Double getMonthExpenditure(Month month) {
        return transactionListInteractor.getMonthExpenditure(month);
    }

    @Override
    public void onDone(ArrayList<Transaction> transactions) {
        //this.transactions = transactions;
        Log.w("Presenter", "onDone");
        if(filter){
            doFiltering(transactions);
        }
        else{
            transactionListView.setTransactions(transactions);
            transactionListView.notifyTransactionListDataSetChanged();
        }
    }
    private void doFiltering(ArrayList<Transaction> originalData){
        //ArrayList<Transaction> originalData = new ArrayList<>(transactionListInteractor.get());
        Log.i("Month", "doFiltering");
        ArrayList<Transaction> filtered = new ArrayList<Transaction>();
        for(int i = 0; i < originalData.size(); i++){
            Transaction transaction = originalData.get(i);
            Calendar cal = Calendar.getInstance();
            cal.setTime(transaction.getDate());
            if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(filterMonth.getMonthNumberString()) && (String.valueOf(cal.get(Calendar.YEAR)).equals(filterMonth.getYearNumberString()))){
                filtered.add(transaction);
            }
        }
        transactionListView.setTransactions(filtered);
        transactionListView.notifyTransactionListDataSetChanged();

    }
}
