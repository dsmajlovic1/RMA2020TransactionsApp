package ba.unsa.etf.rma.rma2020_16570.Presenter;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionListView {
    void setTransactions(ArrayList<Transaction> transactions);
    void notifyTransactionListDataSetChanged();
    ArrayList<Transaction> getTransactions();
}
