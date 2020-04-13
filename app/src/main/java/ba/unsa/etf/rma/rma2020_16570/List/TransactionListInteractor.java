package ba.unsa.etf.rma.rma2020_16570.List;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
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
    public Date getEarliestDate() {
        Date earliestDate = new Date(Long.MAX_VALUE);

        for (int i = 0; i < TransactionsModel.transactions.size(); i++){
            if(TransactionsModel.transactions.get(i).getDate().before(earliestDate)) earliestDate = TransactionsModel.transactions.get(i).getDate();
        }
        return earliestDate;
    }

    @Override
    public Date getLatestDate() {
        Date latestDate = new Date(0L);

        for (int i = 0; i < TransactionsModel.transactions.size(); i++){
            if(TransactionsModel.transactions.get(i).getDate().after(latestDate)) latestDate = TransactionsModel.transactions.get(i).getDate();
        }
        return latestDate;
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
    public Double getMonthExpenditure(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());
                //maybe poređenje sa jos jednim cal
                if((cal.get(Calendar.MONTH)+1)<= (monthCal.get(Calendar.MONTH)+1) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH)+1)>= (monthCal.get(Calendar.MONTH)+1) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    Log.e("If in interval", "entered");
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.MONTH)+1)== (monthCal.get(Calendar.MONTH)+1) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            Log.v(String.valueOf(cal.get(Calendar.YEAR))+"-"+String.valueOf(cal.get(Calendar.MONTH)+1), String.valueOf(TransactionsModel.transactions.get(i).getAmount()));
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }

            }
            /*if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                if(!(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }*/
        }
        return sum;
    }

    @Override
    public Double getMonthIncome(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += TransactionsModel.transactions.get(i).getAmount();

                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());
                //maybe poređenje sa jos jednim cal
                if((cal.get(Calendar.MONTH))<= (monthCal.get(Calendar.MONTH)+1) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH)+1)>= (monthCal.get(Calendar.MONTH)+1) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    Log.e("If in interval", "entered");
                    while (cal.getTime().before(endCal.getTime())){
                        Log.e("While in interval", String.valueOf(cal.get(Calendar.MONTH)+1)+" "+ String.valueOf(monthCal.get(Calendar.MONTH)+1));
                        if((cal.get(Calendar.MONTH)+1) == (monthCal.get(Calendar.MONTH)+1) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            Log.v(String.valueOf(cal.get(Calendar.YEAR))+"-"+String.valueOf(cal.get(Calendar.MONTH)+1), String.valueOf(TransactionsModel.transactions.get(i).getAmount()));
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
            /*if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }*/
        }
        return sum;
    }

    @Override
    public Double getWeeklyIncome(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
        }
        return sum;
    }

    @Override
    public Double getWeeklyExpenditure(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                if(!(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
        }
        return sum;
    }

    @Override
    public Double getDailyIncome(Date week) {
        return null;
    }

    @Override
    public Double getDailyExpenditure(Date week) {
        return null;
    }

}
