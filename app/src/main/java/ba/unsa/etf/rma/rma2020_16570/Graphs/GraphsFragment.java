package ba.unsa.etf.rma.rma2020_16570.Graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;

import com.github.mikephil.charting.formatter.ValueFormatter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.R;

import static java.util.concurrent.TimeUnit.DAYS;

public class GraphsFragment extends Fragment implements IUnitFilter, GraphsPresenter.OnGraphDataFetched {
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


    private Spinner unitSpinner;
    private Spinner yearSpinner;
    private BarChart expenditureBarChart;
    private BarChart incomeBarChart;
    private BarChart totalBarChart;


    private ArrayAdapter<String> unitSpinnerAdapter;
    private ArrayAdapter<String> yearSpinnerAdapter;

    private IGraphsPresenter graphsPresenter;

    public IGraphsPresenter getGraphsPresenter(){
        if(graphsPresenter == null) this.graphsPresenter = new GraphsPresenter(this.getContext(), this);
        return graphsPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.graphsPresenter = new GraphsPresenter(this.getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_graphs, container, false);


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


        expenditureBarChart.setDrawGridBackground(false);
        incomeBarChart.setDrawGridBackground(false);
        totalBarChart.setDrawGridBackground(false);

        expenditureBarChart.setDrawBarShadow(false);
        incomeBarChart.setDrawBarShadow(false);
        totalBarChart.setDrawBarShadow(false);


        expenditureBarChart.getAxisRight().setEnabled(false);
        incomeBarChart.getAxisRight().setEnabled(false);
        totalBarChart.getAxisRight().setEnabled(false);

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

        getGraphsPresenter().fetchDataByMonth(year);
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

        getGraphsPresenter().fetchDataByWeek(year);

    }

    @Override
    public void showByDay(String year) {
        ValueFormatter weekFormatter = new WeekValueFormatter();
        XAxis topAxisExp = expenditureBarChart.getXAxis();
        XAxis topAxisInc = incomeBarChart.getXAxis();
        XAxis topAxisTot = totalBarChart.getXAxis();

        topAxisExp.setValueFormatter(weekFormatter);
        topAxisInc.setValueFormatter(weekFormatter);
        topAxisTot.setValueFormatter(weekFormatter);

        getGraphsPresenter().fetchDataByDay(year);
    }

    @Override
    public void setGraphValues(ArrayList<BarData> graphData){
        expenditureBarChart.setData(graphData.get(0));
        incomeBarChart.setData(graphData.get(1));
        totalBarChart.setData(graphData.get(2));
        expenditureBarChart.notifyDataSetChanged();
        incomeBarChart.notifyDataSetChanged();

        invalidateCharts();
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
            if(unitSpinner.getSelectedItem().toString().equals("Month")) showByMonth(yearSpinner.getSelectedItem().toString());
            else if(unitSpinner.getSelectedItem().toString().equals("Week")) showByWeek(yearSpinner.getSelectedItem().toString());
            else if(unitSpinner.getSelectedItem().toString().equals("Day")) showByDay(yearSpinner.getSelectedItem().toString());
            else showByMonth(yearSpinner.getSelectedItem().toString());

            invalidateCharts();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            showByMonth(yearSpinner.getSelectedItem().toString());
            invalidateCharts();
        }
    };

    private void invalidateCharts(){
        expenditureBarChart.invalidate();
        incomeBarChart.invalidate();
        totalBarChart.invalidate();
    }
}
