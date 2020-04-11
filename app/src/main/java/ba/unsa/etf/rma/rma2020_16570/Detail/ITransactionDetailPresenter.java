package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.os.Parcelable;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionDetailPresenter {
    void create(String date, Double amount, String tittle, Transaction.Type type, String itemDescription, Integer transactionInterval, String endDate);
    void setTransaction(Parcelable transaction);
    Transaction getTransaction();
}
