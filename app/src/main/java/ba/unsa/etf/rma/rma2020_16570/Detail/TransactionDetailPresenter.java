package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.content.Context;
import android.os.Parcelable;

import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {

    private Context context;

    private Transaction transaction;

    public TransactionDetailPresenter(Context context){ this.context = context; }
    @Override
    public void create(Integer id, Date date, String tittle, Double amount, String itemDescription, Integer transactionInterval, Date endDate, Transaction.Type type) {
        this.transaction = new Transaction(id, date, tittle, amount, itemDescription, transactionInterval, endDate, type);
    }

    @Override
    public void create(Integer id, String  date, String tittle, Double amount, String itemDescription, Integer transactionInterval, String endDate, Transaction.Type type) {
        this.transaction = new Transaction(id, date, tittle, amount, itemDescription, transactionInterval, endDate, type);
    }

    @Override
    public void setTransaction(Parcelable transaction) { this.transaction = (Transaction) transaction; }

    @Override
    public Transaction getTransaction() { return transaction; }
}
