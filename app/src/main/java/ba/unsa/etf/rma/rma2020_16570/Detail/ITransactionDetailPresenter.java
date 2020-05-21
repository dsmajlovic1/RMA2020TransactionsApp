package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.os.Parcelable;

import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionDetailPresenter {
    //void create(String date, Double amount, String tittle, Transaction.Type type, String itemDescription, Integer transactionInterval, String endDate);
    void create(Integer id, Date date, String tittle, Double amount, String itemDescription, Integer transactionInterval, Date endDate, Transaction.Type type);
    void create(Integer id, String  date, String tittle, Double amount, String itemDescription, Integer transactionInterval, String endDate, Transaction.Type type);
    void setTransaction(Parcelable transaction);
    Transaction getTransaction();
}
