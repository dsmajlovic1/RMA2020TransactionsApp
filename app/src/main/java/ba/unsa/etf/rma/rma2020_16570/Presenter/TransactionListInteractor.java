package ba.unsa.etf.rma.rma2020_16570.Presenter;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Model.TransactionsModel;

public class TransactionListInteractor implements ITransactionListInteractor {
    @Override
    public ArrayList<Transaction> get() { return TransactionsModel.transactions; }

    @Override
    public void update(Transaction transaction, Transaction newTransaction){
        TransactionsModel.transactions.remove(transaction);
        TransactionsModel.transactions.add(newTransaction);
    }

    @Override
    public void delete(Transaction transaction){
        TransactionsModel.transactions.remove(transaction);
    }

    @Override
    public void add(Transaction transaction) {
        TransactionsModel.transactions.add(transaction);
    }
}
