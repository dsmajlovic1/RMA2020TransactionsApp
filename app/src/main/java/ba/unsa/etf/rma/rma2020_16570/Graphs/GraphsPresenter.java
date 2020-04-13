package ba.unsa.etf.rma.rma2020_16570.Graphs;

import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.List.ITransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public class GraphsPresenter implements IGraphsPresenter {
    private ITransactionListInteractor transactionListInteractor;
    public ITransactionListInteractor getTransactionListInteractor(){
        if(transactionListInteractor==null){
            transactionListInteractor = new TransactionListInteractor();
        }
        return transactionListInteractor;
    }

    @Override
    public Date getEarliestDate() {
        return getTransactionListInteractor().getEarliestDate();
    }

    @Override
    public Date getLatestDate() {
        return getTransactionListInteractor().getLatestDate();
    }

    @Override
    public BarData getIncomeByMonth(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            cal.set(Integer.parseInt(year), i+1, 1);
            cal.set(Calendar.MONTH, i);

            entries.add(new BarEntry(i, (float)(getTransactionListInteractor().getMonthIncome(new Month(cal.getTime()))*1.0)));

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
            Log.e(year, year.toString());
            cal.set(Integer.parseInt(year), i+1, 1);
            cal.set(Calendar.MONTH, i);

            entries.add(new BarEntry(i, (float)(getTransactionListInteractor().getMonthExpenditure(new Month(cal.getTime()))*1.0)));


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
            sum += (float)((getTransactionListInteractor().getMonthIncome(new Month(cal.getTime()))-getTransactionListInteractor().getMonthExpenditure(new Month(cal.getTime())))*1.0);
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
    public BarData getIncomeByWeek(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.WEEK_OF_YEAR, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 52; i++) {
            entries.add(new BarEntry(i+1, (float)(getTransactionListInteractor().getWeeklyIncome(cal.getTime())*1.0)));
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
            entries.add(new BarEntry(i+1, (float)(getTransactionListInteractor().getWeeklyExpenditure(cal.getTime())*1.0)));
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
            sum += (float)((getTransactionListInteractor().getWeeklyIncome(cal.getTime())-getTransactionListInteractor().getWeeklyExpenditure(cal.getTime()))*1.0);
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
    public BarData getIncomeByDay(String year) {
        Calendar cal = Calendar.getInstance();

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        cal.set(Integer.parseInt(year), 1, 1);
        cal.set(Calendar.DATE, 1);
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < 365; i++) {
            entries.add(new BarEntry(i+1, (float)(getTransactionListInteractor().getDailyIncome(cal.getTime())*1.0)));
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
            entries.add(new BarEntry(i+1, (float)(getTransactionListInteractor().getDailyExpenditure(cal.getTime())*1.0)));
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
            sum += (float)((getTransactionListInteractor().getDailyIncome(cal.getTime())-getTransactionListInteractor().getDailyExpenditure(cal.getTime()))*1.0);
            entries.add(new BarEntry(i+1, sum));
            cal.add(Calendar.DATE, 1);
        }
        BarDataSet ds = new BarDataSet(entries, "Days");
        ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        sets.add(ds);

        BarData d = new BarData(sets);
        return d;
    }

}

