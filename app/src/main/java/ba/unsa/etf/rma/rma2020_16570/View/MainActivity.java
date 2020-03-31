package ba.unsa.etf.rma.rma2020_16570.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Adapter.TypeSpinnerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Adapter.TransactionListAdapter;
import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Presenter.ITransactionListView;
import ba.unsa.etf.rma.rma2020_16570.Presenter.TransactionListPresenter;
import ba.unsa.etf.rma.rma2020_16570.R;

public class MainActivity extends AppCompatActivity implements ITransactionListView {
    public static List<String> sortByList = Arrays.asList("Price - Ascending", "Price - Descending","Title - Ascending",
                                                    "Title - Descending", "Date - Ascending", "Date - Descending");
    public static List<Transaction.Type> filterTypes =
            Arrays.asList(Transaction.Type.INDIVIDUALINCOME, Transaction.Type.INDIVIDUALPAYMENT,
            Transaction.Type.PURCHASE, Transaction.Type.REGULARINCOME, Transaction.Type.REGULARPAYMENT);

    //Views
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private Button previousMonthButton;
    private Button nextMonthButton;
    private TextView globalAmountText;
    private TextView limitText;
    private TextView monthYearTextView;
    private ListView transactionListView;
    private Button addButton;

    //Adapters
    private ArrayAdapter<String> sortByAdapter;
    private TypeSpinnerAdapter filterByAdapter;
    private TransactionListAdapter transactionListAdapter;

    //Presenters
    private TransactionListPresenter transactionListPresenter;

    //TransactionList position
    private int listPosition = 0;

    //Account
    Account currentUser = new Account(1000.0, 1000.0, 10.0);

    //Current month
    Month currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionListPresenter = new TransactionListPresenter(this, this);

        currentMonth = new Month(Calendar.getInstance().getTime());

        //Get view resources
        filterBySpinner = (Spinner) findViewById(R.id.filterBySpinner);
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        previousMonthButton = (Button) findViewById(R.id.previousMonthButton);
        nextMonthButton = (Button) findViewById(R.id.nextMonthButton);
        globalAmountText = (TextView) findViewById(R.id.globalAmountText);
        limitText = (TextView) findViewById(R.id.limitText);
        monthYearTextView = (TextView) findViewById(R.id.monthYearTextView);
        transactionListView = (ListView) findViewById(R.id.transactionView);
        addButton = (Button) findViewById(R.id.addButton);

        //Initialize sortBySpinner
        sortByAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortByList);
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortByAdapter);
        sortBySpinner.setOnItemSelectedListener(sortByOnItemSelectedListener);
        sortByAdapter.notifyDataSetChanged();

        //Initialize filterBySpinner
        filterByAdapter = new TypeSpinnerAdapter(this, R.layout.filter_spinner_item, filterTypes);
        filterByAdapter.setDropDownViewResource(R.layout.filter_spinner_item);
        filterBySpinner.setAdapter(filterByAdapter);
        filterBySpinner.setOnItemSelectedListener(filterByOnItemSelectedListener);
        filterByAdapter.notifyDataSetChanged();

        //Initialize transactionListView
        transactionListAdapter = new TransactionListAdapter(this, R.layout.transaction_list_item,new ArrayList<Transaction>());
        transactionListView.setAdapter(transactionListAdapter);
        transactionListView.setOnItemClickListener(onTransactionListItemClickListener);
        transactionListPresenter.refreshTransactions();

        //Set onClickListeners
        previousMonthButton.setOnClickListener(previousButtonOnClickListener);
        nextMonthButton.setOnClickListener(nextButtonOnClickListener);
        addButton.setOnClickListener(addButtonOnClickListener);

        //Set month
        monthYearTextView.setText(currentMonth.toString());
        transactionListPresenter.filterByMonth(currentMonth.getMonthNumberString());

    }

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        transactionListAdapter.setTransactions(transactions);
    }

    @Override
    public void notifyTransactionListDataSetChanged() {
        transactionListAdapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return transactionListAdapter.getItems();
    }

    @Override
    public void updateCurrentTransaction(Transaction transaction) {
        Transaction selectedTransaction = (Transaction) transactionListView.getItemAtPosition(listPosition);
        //Change the transaction
        transactionListPresenter.updateTransaction(selectedTransaction, transaction);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        transactionListPresenter.addTransaction(transaction);
    }

    @Override
    public void deleteCurrentTransaction() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        final Transaction transaction;

        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                if(data != null){
                    Bundle bundle = data.getExtras();
                    String action = bundle.getString("action");
                    if(action.equals("save")){
                        Transaction changedTransaction = (Transaction)data.getExtras().getParcelable("transaction");
                        updateCurrentTransaction(changedTransaction);
                        if(transactionListPresenter.getTotalExpenditure()> currentUser.getTotalLimit()
                                || transactionListPresenter.getMonthExpenditure(currentMonth.getMonthNumberString()) > currentUser.getMonthLimit()){
                                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    alertDialog.setTitle("Limit reached");
                                    alertDialog.setMessage("Are you sure you want to make these changes?");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Do nothing
                                        }
                                    });
                                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Transaction transaction = (Transaction) transactionListView.getItemAtPosition(listPosition);
                                            Intent intent = new Intent(MainActivity.this, TransactionDetailActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("type", "edit");
                                            bundle.putParcelable("transaction", transaction);
                                            intent.putExtras(bundle);
                                            MainActivity.this.startActivityForResult(intent, 1);
                                        }
                                    });
                                    alertDialog.show();

                        }
                    }
                    else if(action.equals("add")){
                        transaction = (Transaction)data.getExtras().getParcelable("transaction");
                        //Add the transaction
                        addTransaction(transaction);
                        if(transactionListPresenter.getTotalExpenditure()> currentUser.getTotalLimit()
                                || transactionListPresenter.getMonthExpenditure(currentMonth.getMonthNumberString()) > currentUser.getMonthLimit()){
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Limit reached");
                            alertDialog.setMessage("Are you sure you want to make these changes?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Do nothing
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transactionListAdapter.remove(transaction);
                                    transactionListPresenter.deleteTransaction(transaction);
                                    refreshCurrentMonthTransactions();
                                }
                            });
                            alertDialog.show();
                        }
                    }
                    else{
                        //Delete the transaction
                        transaction = (Transaction) transactionListView.getItemAtPosition(listPosition);
                        transactionListPresenter.deleteTransaction(transaction);
                    }
                    transactionListPresenter.refreshTransactions();
                }
            }
        }
    }

    private AdapterView.OnItemSelectedListener sortByOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selection = parent.getItemAtPosition(position).toString();
            if(selection.equals("Price - Ascending")) transactionListPresenter.sortByPrice(true);
            else if(selection.equals("Price - Descending")) transactionListPresenter.sortByPrice(false);
            else if(selection.equals("Title - Ascending")) transactionListPresenter.sortByTitle(true);
            else if(selection.equals("Title - Descending")) transactionListPresenter.sortByTitle(false);
            else if(selection.equals("Date - Ascending")) transactionListPresenter.sortByDate(true);
            else if(selection.equals("Date - Descending")) transactionListPresenter.sortByDate(false);
            else transactionListPresenter.refreshTransactions();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            transactionListPresenter.refreshTransactions();
        }
    };

    private AdapterView.OnItemSelectedListener filterByOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            transactionListAdapter.getFilter().filter(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            transactionListPresenter.refreshTransactions();
        }
    };

    private AdapterView.OnItemClickListener onTransactionListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listPosition = position;
            Transaction transaction = (Transaction) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, TransactionDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "edit");
            bundle.putParcelable("transaction", transaction);
            intent.putExtras(bundle);
            MainActivity.this.startActivityForResult(intent, 1);
        }
    };

    private View.OnClickListener addButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, TransactionDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "add");
            intent.putExtras(bundle);
            MainActivity.this.startActivityForResult(intent, 1);
        }
    };



    private View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentMonth.nextMonth();

            monthYearTextView.setText(currentMonth.toString());
            transactionListPresenter.filterByMonth(currentMonth.getMonthNumberString());
            refreshCurrentMonthTransactions();
        }
    };

    private View.OnClickListener previousButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentMonth.previousMonth();

            monthYearTextView.setText(currentMonth.toString());
            refreshCurrentMonthTransactions();
        }
    };

    public void refreshCurrentMonthTransactions(){
        transactionListPresenter.filterByMonth(currentMonth.getMonthNumberString());
        refreshFilter();
    }

    private void refreshFilter(){
        transactionListAdapter.getFilter().filter(filterBySpinner.getSelectedItem().toString());
    }

}
