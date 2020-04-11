package ba.unsa.etf.rma.rma2020_16570.List;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public class TransactionListPresenter implements ITransactionListPresenter {
    private ITransactionListView transactionListView;
    private ITransactionListInteractor transactionListInteractor;
    private Context context;

    public TransactionListPresenter(ITransactionListView transactionListView, Context context){
        this.transactionListView = transactionListView;
        this.transactionListInteractor = new TransactionListInteractor();
        this.context = context;
    }
    @Override
    public void refreshTransactions() {
        transactionListView.setTransactions(transactionListInteractor.get());
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
        ArrayList<Transaction> originalData = new ArrayList<>(transactionListInteractor.get());
        ArrayList<Transaction> filtered = new ArrayList<Transaction>();
        for(int i = 0; i < originalData.size(); i++){
            Transaction transaction = originalData.get(i);
            Calendar cal = Calendar.getInstance();
            cal.setTime(transaction.getDate());
            if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && (String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString()))){
                filtered.add(transaction);
            }
        }
        transactionListView.setTransactions(filtered);
        transactionListView.notifyTransactionListDataSetChanged();
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
}
