package ba.unsa.etf.rma.rma2020_16570.Graphs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.List.ITransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListPresenter;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListResultReceiver;
import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Model.TransactionsModel;

public class GraphsPresenter implements IGraphsPresenter, TransactionListInteractor.OnTransactionsFetched, TransactionListResultReceiver.Receiver {
    private ITransactionListInteractor transactionListInteractor;
    private TransactionListResultReceiver transactionListResultReceiver;
    private ArrayList<Transaction> transactions;
    private String type;
    private String year;
    private Context context;
    private OnGraphDataFetched caller;
    private Boolean expenditure = false;
    private Month month;

    @Override
    public void onResultsReceived(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case TransactionListInteractor.STATUS_FINISHED:
                String type = resultData.getString("type");
                if(type.equals("GET")){
                    ArrayList<Transaction> results = resultData.getParcelableArrayList("result");
                    onDone(results);
                }
                break;
            case TransactionListInteractor.STATUS_ERROR:
                //transactionListView.setCursor();
                break;
        }
    }

    public interface OnGraphDataFetched{
        void setGraphValues(ArrayList<BarData> graphData);
    }

    private OnExpendituresFetched alertCaller;

    public interface OnExpendituresFetched{
        void callAlerts(ArrayList<Double> expenditures);
    }

    public GraphsPresenter(Context context, OnGraphDataFetched caller) {
        this.caller = caller;
        this.context = context;
    }
    public GraphsPresenter(Context context, OnExpendituresFetched alertCaller) {
        this.alertCaller = alertCaller;
        this.context = context;
    }
/*
    public ITransactionListInteractor getTransactionListInteractor(){
        if(transactionListInteractor==null){
            transactionListInteractor = null;
        }
        return transactionListInteractor;
    }

 */
/*
    @Override
    public Date getEarliestDate() {
        return getTransactionListInteractor().getEarliestDate();
    }

    @Override
    public Date getLatestDate() {
        return getTransactionListInteractor().getLatestDate();
    }
    */


    @Override
    public void fetchDataByMonth(String year) {
        this.type = "MONTH";
        this.year = year;
        //new TransactionListInteractor(context, this, "GET", null).execute("/transactions");
        String query = new String("/transactions");
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, TransactionListInteractor.class);
        intent.putExtra("type", "GET");
        intent.putExtra("query", query);
        transactionListResultReceiver = new TransactionListResultReceiver(new Handler());
        transactionListResultReceiver.setTransactionReceiver(GraphsPresenter.this);
        intent.putExtra("receiver", transactionListResultReceiver);
        context.getApplicationContext().startService(intent);
    }

    @Override
    public BarData getIncomeByMonth(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            cal.set(Integer.parseInt(year), i+1, 1);
            cal.set(Calendar.MONTH, i);

            entries.add(new BarEntry(i, (float)(getMonthIncome(new Month(cal.getTime()))*1.0)));

        }
        BarDataSet ds = new BarDataSet(entries, "Months");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        //d.setValueTypeface(tf);
        return d;
    }

    @Override
    public BarData getExpenditureByMonth(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            cal.set(Integer.parseInt(year), i+1, 1);
            cal.set(Calendar.MONTH, i);

            entries.add(new BarEntry(i, (float)(getMonthExpenditure(new Month(cal.getTime()))*1.0)));


        }
        BarDataSet ds = new BarDataSet(entries, "Months");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        //d.setValueTypeface(tf);
        return d;
    }

    @Override
    public BarData getAllByMonth(String year) {
        Calendar cal = Calendar.getInstance();
        float sum = 0f;

        ArrayList<IBarDataSet> sets = new ArrayList<>();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            cal.set(Integer.parseInt(year), i+1, 1);
            cal.set(Calendar.MONTH, i);
            sum += (float)((getMonthIncome(new Month(cal.getTime()))-getMonthExpenditure(new Month(cal.getTime())))*1.0);
            entries.add(new BarEntry(i, sum));


        }
        BarDataSet ds = new BarDataSet(entries, "Months");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        //d.setValueTypeface(tf);
        return d;
    }

    @Override
    public void fetchDataByWeek(String year) {
        this.type = "WEEK";
        this.year = year;
        //new TransactionListInteractor(context, this, "GET", null).execute("/transactions");
        String query = new String("/transactions");
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, TransactionListInteractor.class);
        intent.putExtra("type", "GET");
        intent.putExtra("query", query);
        transactionListResultReceiver = new TransactionListResultReceiver(new Handler());
        transactionListResultReceiver.setTransactionReceiver(GraphsPresenter.this);
        intent.putExtra("receiver", transactionListResultReceiver);
        context.getApplicationContext().startService(intent);
    }

    @Override
    public BarData getIncomeByWeek(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.WEEK_OF_YEAR, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 52; i++) {
            entries.add(new BarEntry(i+1, (float)(getWeeklyIncome(cal.getTime())*1.0)));
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Weeks");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public BarData getExpenditureByWeek(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.WEEK_OF_YEAR, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 52; i++) {
            entries.add(new BarEntry(i+1, (float)(getWeeklyExpenditure(cal.getTime())*1.0)));
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Weeks");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public BarData getAllByWeek(String year) {
        Calendar cal = Calendar.getInstance();
        float sum = 0f;

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.WEEK_OF_YEAR, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 52; i++) {
            sum += (float)((getWeeklyIncome(cal.getTime())-getWeeklyExpenditure(cal.getTime()))*1.0);
            entries.add(new BarEntry(i+1, sum));
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Weeks");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public void fetchDataByDay(String year) {
        this.type = "DAY";
        this.year = year;
        //new TransactionListInteractor(context, this, "GET", null).execute("/transactions");
        String query = new String("/transactions");
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, TransactionListInteractor.class);
        intent.putExtra("type", "GET");
        intent.putExtra("query", query);
        transactionListResultReceiver = new TransactionListResultReceiver(new Handler());
        transactionListResultReceiver.setTransactionReceiver(GraphsPresenter.this);
        intent.putExtra("receiver", transactionListResultReceiver);
        context.getApplicationContext().startService(intent);
    }

    @Override
    public BarData getIncomeByDay(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.DATE, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 365; i++) {
            entries.add(new BarEntry(i+1, (float)(getDailyIncome(cal.getTime())*1.0)));
            cal.add(Calendar.DATE, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Days");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public BarData getExpenditureByDay(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.DATE, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 365; i++) {
            entries.add(new BarEntry(i+1, (float)(getDailyExpenditure(cal.getTime())*1.0)));
            cal.add(Calendar.DATE, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Days");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public BarData getAllByDay(String year) {
        Calendar cal = Calendar.getInstance();
        float sum = 0f;

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.DATE, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 365; i++) {
            sum += (float)((getDailyIncome(cal.getTime())-getDailyExpenditure(cal.getTime()))*1.0);
            entries.add(new BarEntry(i+1, sum));
            cal.add(Calendar.DATE, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Days");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

    @Override
    public void fetchExpenditures(Month month) {
        this.expenditure = true;
        this.month = month;
        //new TransactionListInteractor(context, this, "GET", null).execute("/transactions");
        String query = new String("/transactions");
        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, TransactionListInteractor.class);
        intent.putExtra("type", "GET");
        intent.putExtra("query", query);
        transactionListResultReceiver = new TransactionListResultReceiver(new Handler());
        transactionListResultReceiver.setTransactionReceiver(GraphsPresenter.this);
        intent.putExtra("receiver", transactionListResultReceiver);
        context.getApplicationContext().startService(intent);

    }

    @Override
    public void onDone(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        if(expenditure){
            ArrayList<Double> expenditures = new ArrayList<Double>();
            expenditures.add(getTotalExpenditure());
            expenditures.add(getMonthExpenditure(month));
            alertCaller.callAlerts(expenditures);
        }
        else{
            ArrayList<BarData> list = new ArrayList<BarData>();
            if(type == "DAY"){
                list.add(getIncomeByDay(year));
                list.add(getExpenditureByDay(year));
                list.add(getAllByDay(year));
            }
            else if(type == "WEEK"){
                list.add(getIncomeByWeek(year));
                list.add(getExpenditureByWeek(year));
                list.add(getAllByWeek(year));
            }
            else{
                list.add(getIncomeByMonth(year));
                list.add(getExpenditureByMonth(year));
                list.add(getAllByMonth(year));
            }
            caller.setGraphValues(list);
        }

    }

    //Funkcije prebacene iz stare TransactionInteractor klase

    public Date getEarliestDate() {
        Date earliestDate = new Date(Long.MAX_VALUE);

        for (int i = 0; i < transactions.size(); i++){
            if(transactions.get(i).getDate().before(earliestDate)) earliestDate = transactions.get(i).getDate();
        }
        return earliestDate;
    }


    public Date getLatestDate() {
        Date latestDate = new Date(0L);

        for (int i = 0; i < transactions.size(); i++){
            if(transactions.get(i).getDate().after(latestDate)) latestDate = transactions.get(i).getDate();
        }
        return latestDate;
    }

    public Double getTotalIncome() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.REGULARINCOME) || (transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    public Double getTotalExpenditure() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if(!(transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    public Double getMonthExpenditure(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += transactions.get(i).getAmount();
                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());

                if((cal.get(Calendar.MONTH))<= (monthCal.get(Calendar.MONTH)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH))>= (monthCal.get(Calendar.MONTH)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.MONTH))== (monthCal.get(Calendar.MONTH)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }

            }
        }
        return sum;
    }

    public Double getMonthIncome(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += transactions.get(i).getAmount();

                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());

                if((cal.get(Calendar.MONTH))<= (monthCal.get(Calendar.MONTH)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH))>= (monthCal.get(Calendar.MONTH)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.MONTH)) == (monthCal.get(Calendar.MONTH)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    public Double getWeeklyIncome(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += transactions.get(i).getAmount();
                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(week);

                if((cal.get(Calendar.WEEK_OF_YEAR))<= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.WEEK_OF_YEAR))>= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.WEEK_OF_YEAR)) == (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    public Double getWeeklyExpenditure(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += transactions.get(i).getAmount();
                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(week);

                if((cal.get(Calendar.WEEK_OF_YEAR))<= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.WEEK_OF_YEAR))>= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.WEEK_OF_YEAR)) == (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    public Double getDailyIncome(Date day) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(day);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(cal.get(Calendar.DATE) == calCompare.get(Calendar.DATE) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += transactions.get(i).getAmount();
                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(day);

                if((cal.get(Calendar.DATE))<= (monthCal.get(Calendar.DATE)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.DATE))>= (monthCal.get(Calendar.DATE)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.DATE)) == (monthCal.get(Calendar.DATE)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    public Double getDailyExpenditure(Date day) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(day);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < transactions.size(); i++){
            cal.setTime(transactions.get(i).getDate());
            if((transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(cal.get(Calendar.DATE) == calCompare.get(Calendar.DATE) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += transactions.get(i).getAmount();
                }
            }
            else if(transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(day);

                if((cal.get(Calendar.DATE))<= (monthCal.get(Calendar.DATE)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.DATE))>= (monthCal.get(Calendar.DATE)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.DATE)) == (monthCal.get(Calendar.DATE)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }
}

