package ba.unsa.etf.rma.rma2020_16570.Graphs;

import com.github.mikephil.charting.data.BarData;

import java.util.Date;

public interface IGraphsPresenter {
    Date getEarliestDate();
    Date getLatestDate();
    BarData getIncomeByMonth(String year);
    BarData getExpenditureByMonth(String year);
    BarData getAllByMonth(String year);
    BarData getIncomeByWeek(String year);
    BarData getExpenditureByWeek(String year);
    BarData getAllByWeek(String year);
    BarData getIncomeByDay(String year);
    BarData getExpenditureByDay(String year);
    BarData getAllByDay(String year);
}
