package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.content.Context;
import android.os.Parcelable;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {

    private Context context;

    private Transaction transaction;

    public TransactionDetailPresenter(Context context){ this.context = context; }
    @Override
    public void create(String date, Double amount, String tittle, Transaction.Type type, String itemDescription, Integer transactionInterval, String endDate) {
        this.transaction = new Transaction(date, amount, tittle, type, itemDescription, transactionInterval, endDate);
    }

    @Override
    public void setTransaction(Parcelable transaction) { this.transaction = (Transaction) transaction; }

    @Override
    public Transaction getTransaction() { return transaction; }
}
