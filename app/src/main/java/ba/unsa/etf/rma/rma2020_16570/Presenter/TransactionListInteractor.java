package ba.unsa.etf.rma.rma2020_16570.Presenter;

import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;

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

    @Override
    public Double getTotalIncome() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += TransactionsModel.transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    @Override
    public Double getTotalExpenditure() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if(!(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += TransactionsModel.transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    @Override
    public Double getMonthExpenditure(String month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month)){
                if(!(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
        }
        return sum;
    }
}
