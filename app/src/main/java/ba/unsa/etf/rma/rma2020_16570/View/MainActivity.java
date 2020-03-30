package ba.unsa.etf.rma.rma2020_16570.View;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Adapter.TypeSpinnerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Adapter.TransactionListAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionListPresenter = new TransactionListPresenter(this, this);

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

        //Set addButton onClickListener
        addButton.setOnClickListener(addButtonOnClickListener);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Transaction transaction;

        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                if(data != null){
                    Bundle bundle = data.getExtras();
                    String action = bundle.getString("action");
                    if(action.equals("save")){
                        Transaction changedTransaction = (Transaction)data.getExtras().getParcelable("transaction");
                        transaction = (Transaction) transactionListView.getItemAtPosition(listPosition);
                        //Change the transaction
                        transactionListPresenter.updateTransaction(transaction, changedTransaction);
                    }
                    else if(action.equals("add")){
                        transaction = (Transaction)data.getExtras().getParcelable("transaction");
                        //Add the transaction
                        transactionListPresenter.addTransaction(transaction);
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
}
