package ba.unsa.etf.rma.rma2020_16570.List;

import android.os.ResultReceiver;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionListPresenter {
    void refreshTransactions();
    void sortByPrice(Boolean ascending);
    void sortByTitle(Boolean ascending);
    void sortByDate(Boolean ascending);
    void filterByMonth(Month month);
    void updateTransaction(Transaction transaction, Transaction newTransaction);
    void deleteTransaction(Transaction transaction);
    void addTransaction(Transaction transaction);
    Transaction getDatabaseTransaction(int id);
    void getMoviesCursor(Month month);
    void uploadAllData(ResultReceiver mainActivityReceiver);
    Double getTotalIncome();
    Double getTotalExpenditure();
    Double getMonthExpenditure(Month month);
}
