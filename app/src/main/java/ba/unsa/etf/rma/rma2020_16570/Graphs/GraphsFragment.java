package ba.unsa.etf.rma.rma2020_16570.Graphs;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.R;

import static java.util.concurrent.TimeUnit.DAYS;

public class GraphsFragment extends Fragment implements IUnitFilter {
    private ArrayList<String> unitItems = new ArrayList<String>(){
        {
            add("Month");
            add("Week");
            add("Day");
        }
    };
    private ArrayList<String> yearsList = new ArrayList<String>(){
        {
            add("2020");
            add("2019");
            add("2018");
            add("2017");
        }
    };

    private Date earliestDate;
    private Date latestDate;

    private Spinner unitSpinner;
    private Spinner yearSpinner;
    private BarChart expenditureBarChart;
    private BarChart incomeBarChart;
    private BarChart totalBarChart;
    //private SeekBar minSeekBar;
    //private SeekBar maxSeekBar;

    private ArrayAdapter<String> unitSpinnerAdapter;
    private ArrayAdapter<String> yearSpinnerAdapter;

    private IGraphsPresenter graphsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.graphsPresenter = new GraphsPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_graphs, container, false);

        earliestDate = graphsPresenter.getEarliestDate();
        latestDate = graphsPresenter.getLatestDate();

        //Get resource
        unitSpinner = fragmentView.findViewById(R.id.unitSpinner);
        yearSpinner = fragmentView.findViewById(R.id.yearSpinner);
        expenditureBarChart = fragmentView.findViewById(R.id.expenditureBarChart);
        incomeBarChart = fragmentView.findViewById(R.id.incomeBarChart);
        totalBarChart = fragmentView.findViewById(R.id.totalBarChart);
        //minSeekBar = fragmentView.findViewById(R.id.seekBar1);
        //maxSeekBar = fragmentView.findViewById(R.id.seekBar2);

        unitSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, unitItems);
        unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitSpinnerAdapter);
        unitSpinner.setOnItemSelectedListener(unitSpinnerOnItemSelectedListener);
        unitSpinnerAdapter.notifyDataSetChanged();

        yearSpinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, yearsList);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearSpinnerAdapter);
        yearSpinner.setOnItemSelectedListener(unitSpinnerOnItemSelectedListener);
        yearSpinnerAdapter.notifyDataSetChanged();

        expenditureBarChart.getDescription().setEnabled(false);
        incomeBarChart.getDescription().setEnabled(false);
        totalBarChart.getDescription().setEnabled(false);

        /*MarkerView mv = new MarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(expenditureBarChart); // For bounds control
        expenditureBarChart.setMarker(mv);
        incomeBarChart.setMarker(mv);
        totalBarChart.setMarker(mv);*/

        expenditureBarChart.setDrawGridBackground(false);
        incomeBarChart.setDrawGridBackground(false);
        totalBarChart.setDrawGridBackground(false);

        expenditureBarChart.setDrawBarShadow(false);
        incomeBarChart.setDrawBarShadow(false);
        totalBarChart.setDrawBarShadow(false);

        //Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");

        //chart.setData(generateBarData(1, 20000, 12));
        /*expenditureBarChart.setData(graphsPresenter.getExpenditureByMonth());
        incomeBarChart.setData(graphsPresenter.getIncomeByMonth());
        totalBarChart.setData(graphsPresenter.getAllByMonth());
         */


        //leftAxis.setTypeface(tf);
        //leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        expenditureBarChart.getAxisRight().setEnabled(false);
        incomeBarChart.getAxisRight().setEnabled(false);
        totalBarChart.getAxisRight().setEnabled(false);

        //SeekBars
        //minSeekBar.setMax((int)(latestDate.getTime()-earliestDate.getTime())/(24*60*60*1000));
        //minSeekBar.setOnSeekBarChangeListener(minOnSeekBarChangeListener);


        return fragmentView;
    }

    @Override
    public void showByMonth(String year) {
        MonthValueFormatter monthValueFormatter = new MonthValueFormatter();
        XAxis topAxisExp = expenditureBarChart.getXAxis();
        XAxis topAxisInc = incomeBarChart.getXAxis();
        XAxis topAxisTot = totalBarChart.getXAxis();

        topAxisExp.setValueFormatter(monthValueFormatter);
        topAxisInc.setValueFormatter(monthValueFormatter);
        topAxisTot.setValueFormatter(monthValueFormatter);

        expenditureBarChart.setData(graphsPresenter.getExpenditureByMonth(year));
        incomeBarChart.setData(graphsPresenter.getIncomeByMonth(year));
        totalBarChart.setData(graphsPresenter.getAllByMonth(year));
    }

    @Override
    public void showByWeek(String year) {
        ValueFormatter weekFormatter = new WeekValueFormatter();
        XAxis topAxisExp = expenditureBarChart.getXAxis();
        XAxis topAxisInc = incomeBarChart.getXAxis();
        XAxis topAxisTot = totalBarChart.getXAxis();

        topAxisExp.setValueFormatter(weekFormatter);
        topAxisInc.setValueFormatter(weekFormatter);
        topAxisTot.setValueFormatter(weekFormatter);

        expenditureBarChart.setData(graphsPresenter.getExpenditureByWeek(year));
        incomeBarChart.setData(graphsPresenter.getIncomeByWeek(year));
        totalBarChart.setData(graphsPresenter.getAllByWeek(year));

    }

    @Override
    public void showByDay(String year) {

    }

    private class MonthValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM");
            Calendar cal = Calendar.getInstance();
            cal.set(2020,(int)value, 1);
            return simpleDateFormat.format(cal.getTime());
        }

    }
    private class WeekValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf(value);
        }

    }

    private AdapterView.OnItemSelectedListener unitSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.e(unitSpinner.getSelectedItem().toString(), yearSpinner.getSelectedItem().toString());
            if(unitSpinner.getSelectedItem().toString().equals("Month")) showByMonth(yearSpinner.getSelectedItem().toString());
            else if(unitSpinner.getSelectedItem().toString().equals("Week")) showByWeek(yearSpinner.getSelectedItem().toString());
            else if(unitSpinner.getSelectedItem().toString().equals("Day")) showByDay(yearSpinner.getSelectedItem().toString());
            else showByMonth(yearSpinner.getSelectedItem().toString());

            invalidateCharts();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            showByMonth(yearSpinner.getItemAtPosition(0).toString());
            invalidateCharts();
        }
    };
    private AdapterView.OnItemSelectedListener yearSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            invalidateCharts();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            showByMonth(yearSpinner.getSelectedItem().toString());
        }
    };
/*
    private SeekBar.OnSeekBarChangeListener minOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            XAxis topAxisExp = expenditureBarChart.getXAxis();
            XAxis topAxisInc = incomeBarChart.getXAxis();
            XAxis topAxisTot = totalBarChart.getXAxis();

            Log.e("Change", String.valueOf(progress)+" - "+String.valueOf((expenditureBarChart.getBarData().getEntryCount()*progress/100.0)));

            topAxisExp.setAxisMinimum((float) (expenditureBarChart.getBarData().getEntryCount()*progress/100.0));
            topAxisInc.setAxisMinimum((float) (incomeBarChart.getBarData().getEntryCount()*progress/100.0));
            topAxisTot.setAxisMinimum((float) (totalBarChart.getBarData().getEntryCount()*progress/100.0));

            expenditureBarChart.setVisibleXRangeMinimum((float) (expenditureBarChart.getBarData().getEntryCount()*(100-progress)/100.0));
            incomeBarChart.setVisibleXRangeMinimum((float) (incomeBarChart.getBarData().getEntryCount()*progress/100.0));
            totalBarChart.setVisibleXRangeMinimum((float) (totalBarChart.getBarData().getEntryCount()*progress/100.0));

            //expenditureBarChart.setScaleMinima((float)(progress/10.0), 1f);
            BarData expBarData = expenditureBarChart.getBarData();

            invalidateCharts();
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };*/

    private void invalidateCharts(){
        expenditureBarChart.invalidate();
        incomeBarChart.invalidate();
        totalBarChart.invalidate();
    }
}
