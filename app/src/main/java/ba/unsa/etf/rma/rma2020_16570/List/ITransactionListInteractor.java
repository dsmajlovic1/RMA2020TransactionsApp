package ba.unsa.etf.rma.rma2020_16570.List;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ITransactionListInteractor {
    //ArrayList<Transaction> get();
    void update(Transaction transaction, Transaction newTransaction);
    void delete(Transaction transaction);
    void add(Transaction transaction);
    Transaction getDatabaseTransaction(int id, Context context);
    Cursor getMonthTransactionsCursor();
    Date getEarliestDate();
    Date getLatestDate();
    Double getTotalIncome();
    Double getTotalExpenditure();
    Double getMonthExpenditure(Month month);
    Double getMonthIncome(Month month);
    Double getWeeklyIncome(Date week);
    Double getWeeklyExpenditure(Date week);
    Double getDailyIncome(Date day);
    Double getDailyExpenditure(Date day);
}
