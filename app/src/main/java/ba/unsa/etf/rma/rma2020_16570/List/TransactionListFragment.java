package ba.unsa.etf.rma.rma2020_16570.List;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Adapter.TransactionListAdapter;
import ba.unsa.etf.rma.rma2020_16570.Adapter.TransactionListCursorAdapter;
import ba.unsa.etf.rma.rma2020_16570.Adapter.TypeSpinnerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetPresenter;
import ba.unsa.etf.rma.rma2020_16570.Budget.IBudgetPresenter;
import ba.unsa.etf.rma.rma2020_16570.Budget.ISetBudget;
import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.View.IFragmentCommunication;
import ba.unsa.etf.rma.rma2020_16570.View.MainActivity;

public class TransactionListFragment extends Fragment implements ITransactionListView, ISetBudget {

    public static List<String> sortByList = Arrays.asList("Price - Ascending", "Price - Descending","Title - Ascending",
            "Title - Descending", "Date - Ascending", "Date - Descending");
    public static List<Transaction.Type> filterTypes =
            Arrays.asList(Transaction.Type.INDIVIDUALINCOME, Transaction.Type.INDIVIDUALPAYMENT,
                    Transaction.Type.PURCHASE, Transaction.Type.REGULARINCOME, Transaction.Type.REGULARPAYMENT);

    //Account
    private Account currentUser = new Account(1000.0, 1000.0, 100.0);

    public Account getCurrentUser() {return currentUser; };
    //Views
    private Spinner filterBySpinner;
    private Spinner sortBySpinner;
    private Button previousMonthButton;
    private Button nextMonthButton;
    private TextView globalAmountTextView;
    private TextView limitTextView;
    private TextView monthYearTextView;
    private ListView transactionListView;
    private Button addButton;

    //Adapters
    private ArrayAdapter<String> sortByAdapter;
    private TypeSpinnerAdapter filterByAdapter;
    private TransactionListAdapter transactionListAdapter;
    private TransactionListCursorAdapter transactionListCursorAdapter;

    //Presenters
    private TransactionListPresenter transactionListPresenter;

    //TransactionList position
    private int listPosition = 0;

    //Current month
    private Month currentMonth;

    public Month getCurrentMonth(){ return currentMonth;}

    private IBudgetPresenter budgetPresenter;
    public IBudgetPresenter getBudgetPresenter(){
        if(budgetPresenter==null){
            budgetPresenter = new BudgetPresenter(this.getContext(), this);
        }
        return budgetPresenter;
    }

    public ITransactionListPresenter getPresenter(){
        if(transactionListPresenter == null){
            this.transactionListPresenter = new TransactionListPresenter(this, getActivity());
        }
        return transactionListPresenter;
    }

    private OnItemClick onItemClick;

    @Override
    public void setAccount(Account account) {
        globalAmountTextView.setText(account.getTotalLimit().toString());
        limitTextView.setText(account.getMonthLimit().toString());
    }

    public interface OnItemClick {
        public void onItemClicked(Transaction transaction);
    }

    private TwoPaneMode twoPaneMode;
    public interface TwoPaneMode{
        public Boolean getPaneMode();
    }
    private IFragmentCommunication communication;

    public IFragmentCommunication getCommunication(){
        if(communication == null){
            communication = (MainActivity)getActivity();
        }
        return communication;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);

        currentMonth = new Month(Calendar.getInstance().getTime());
        //Get view resources
        filterBySpinner = (Spinner) fragmentView.findViewById(R.id.filterBySpinner);
        sortBySpinner = (Spinner) fragmentView.findViewById(R.id.sortBySpinner);
        previousMonthButton = (Button) fragmentView.findViewById(R.id.previousMonthButton);
        nextMonthButton = (Button) fragmentView.findViewById(R.id.nextMonthButton);
        globalAmountTextView = (TextView) fragmentView.findViewById(R.id.globalAmountTextView);
        limitTextView = (TextView) fragmentView.findViewById(R.id.limitTextView);
        monthYearTextView = (TextView) fragmentView.findViewById(R.id.monthYearTextView);
        transactionListView = (ListView) fragmentView.findViewById(R.id.transactionView);
        addButton = (Button) fragmentView.findViewById(R.id.addButton);

        //Set amounts
        //globalAmountTextView.setText(currentUser.getTotalLimit().toString());
        //limitTextView.setText(currentUser.getMonthLimit().toString());

        //Initialize sortBySpinner
        sortByAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortByList);
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortByAdapter);
        sortBySpinner.setOnItemSelectedListener(sortByOnItemSelectedListener);
        sortByAdapter.notifyDataSetChanged();

        //Initialize filterBySpinner
        filterByAdapter = new TypeSpinnerAdapter(getActivity(), R.layout.filter_spinner_item, filterTypes);
        filterByAdapter.setDropDownViewResource(R.layout.filter_spinner_item);
        filterBySpinner.setAdapter(filterByAdapter);
        filterBySpinner.setOnItemSelectedListener(filterByOnItemSelectedListener);
        filterByAdapter.notifyDataSetChanged();

        //Initialize transactionListView
        transactionListAdapter = new TransactionListAdapter(getActivity(), R.layout.transaction_list_item,new ArrayList<Transaction>());
        transactionListCursorAdapter = new TransactionListCursorAdapter(getContext(), R.layout.transaction_list_item, null, false);
        //transactionListView.setAdapter(transactionListAdapter);
        //transactionListView.setOnItemClickListener(onTransactionListItemClickListener);
        //getPresenter().refreshTransactions();
        transactionListView.setAdapter(transactionListCursorAdapter);
        transactionListView.setOnItemClickListener(onListCursorItemClickListener);
        transactionListCursorAdapter.notifyDataSetChanged();

        //Set onClickListeners
        previousMonthButton.setOnClickListener(previousButtonOnClickListener);
        nextMonthButton.setOnClickListener(nextButtonOnClickListener);
        addButton.setOnClickListener(addButtonOnClickListener);

        //Set month
        monthYearTextView.setText(currentMonth.toString());
        //getPresenter().filterByMonth(currentMonth);
        getPresenter().getMoviesCursor(currentMonth);


        onItemClick= (OnItemClick) getActivity();
        twoPaneMode = (TwoPaneMode) getActivity();
        /*Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    editText.setText(sharedText);
                }
            }
        }
         */
        getBudgetPresenter().getAccount();
        //refreshCurrentMonthTransactions();
        return fragmentView;
    }

    public Transaction getSelectedTransaction(){
        return (Transaction) transactionListView.getSelectedItem();
    }


    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        transactionListView.setAdapter(transactionListAdapter);
        transactionListView.setOnItemClickListener(onTransactionListItemClickListener);
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
        getPresenter().updateTransaction(selectedTransaction, transaction);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        getPresenter().addTransaction(transaction);
    }

    @Override
    public void deleteCurrentTransaction() {
        Transaction selectedTransaction = (Transaction) transactionListView.getItemAtPosition(listPosition);
        getPresenter().deleteTransaction(selectedTransaction);
    }

    @Override
    public void setCursor(Cursor cursor) {
        transactionListView.setAdapter(transactionListCursorAdapter);
        transactionListView.setOnItemClickListener(onListCursorItemClickListener);
        transactionListCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void refreshCurrentMonthTransactions(){
        transactionListPresenter.filterByMonth(currentMonth);
        refreshFilter();
    }
    @Override
    public void refreshFilter(){
        transactionListAdapter.getFilter().filter(filterBySpinner.getSelectedItem().toString());
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
            //transactionListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //transactionListPresenter.refreshTransactions();
            transactionListAdapter.getFilter().filter(parent.getItemAtPosition(1).toString());
        }
    };

    private AdapterView.OnItemClickListener onTransactionListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listPosition = position;
            Transaction transaction = (Transaction) parent.getItemAtPosition(position);
            /*Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "edit");
            bundle.putParcelable("transaction", transaction);
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent, 1);*/
            onItemClick.onItemClicked(transaction);
        }
    };
    private AdapterView.OnItemClickListener onListCursorItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listPosition = position;
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            cursor.moveToPosition(position);
            Transaction transaction = getPresenter().getDatabaseTransaction(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            onItemClick.onItemClicked(transaction);
        }
    };

    private View.OnClickListener addButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", "add");
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent, 1);
            */
            getCommunication().add();
        }
    };



    private View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentMonth.nextMonth();

            monthYearTextView.setText(currentMonth.toString());
            transactionListPresenter.filterByMonth(currentMonth);
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
}
