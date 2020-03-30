package ba.unsa.etf.rma.rma2020_16570.Presenter;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Model.TransactionsModel;

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
}