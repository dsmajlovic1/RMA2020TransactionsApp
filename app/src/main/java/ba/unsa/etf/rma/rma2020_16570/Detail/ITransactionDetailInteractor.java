package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.content.Context;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionDetailInteractor {
    void save(Transaction transaction, Context context);
    void update(Transaction transaction, Context context);
    void delete(Transaction transaction, Context context);
    void undoDelete(Transaction transaction, Context context);
}
