package ba.unsa.etf.rma.rma2020_16570.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Adapter.ScreenSlidePagerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetFragment;
import ba.unsa.etf.rma.rma2020_16570.Detail.TransactionDetailFragment;
import ba.unsa.etf.rma.rma2020_16570.Graphs.GraphsFragment;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.List.ITransactionListView;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListPresenter;
import ba.unsa.etf.rma.rma2020_16570.R;

public class MainActivity extends FragmentActivity implements TransactionListFragment.OnItemClick,
                                                                TransactionListFragment.TwoPaneMode,
                                                                IFragmentCommunication {
    /*
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
    private TextView globalAmountTextView;
    private TextView limitTextView;
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
    Account currentUser = new Account(1000.0, 1000.0, 100.0);

    //Current month
    Month currentMonth;
     */
    private boolean twoPaneMode;

    private ViewPager2 viewPager;

    private ScreenSlidePagerAdapter pagerAdapter;

    private TransactionListFragment transactionListFragment;
    private BudgetFragment budgetFragment;
    private GraphsFragment graphsFragment;

    private ArrayList<Fragment> arrayList;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        transactionListPresenter = new TransactionListPresenter(this, this);

        currentMonth = new Month(Calendar.getInstance().getTime());

        //Get view resources
        filterBySpinner = (Spinner) findViewById(R.id.filterBySpinner);
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        previousMonthButton = (Button) findViewById(R.id.previousMonthButton);
        nextMonthButton = (Button) findViewById(R.id.nextMonthButton);
        globalAmountTextView = (TextView) findViewById(R.id.globalAmountTextView);
        limitTextView = (TextView) findViewById(R.id.limitTextView);
        monthYearTextView = (TextView) findViewById(R.id.monthYearTextView);
        transactionListView = (ListView) findViewById(R.id.transactionView);
        addButton = (Button) findViewById(R.id.addButton);

        //Set amounts
        globalAmountTextView.setText(currentUser.getTotalLimit().toString());
        limitTextView.setText(currentUser.getMonthLimit().toString());

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
        transactionListPresenter.filterByMonth(currentMonth);

 */
        graphsFragment = new GraphsFragment();
        transactionListFragment = new TransactionListFragment();
        budgetFragment = new BudgetFragment();

        arrayList = new ArrayList<Fragment>();
        arrayList.add(graphsFragment);
        arrayList.add(transactionListFragment);
        arrayList.add(budgetFragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout details = findViewById(R.id.transaction_detail);
        if (details != null) {
            twoPaneMode = true;
            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transaction_detail);
            if (detailFragment==null) {
                detailFragment = new TransactionDetailFragment();
                fragmentManager.beginTransaction().
                        replace(R.id.transaction_detail,detailFragment)
                        .commit();
            }
        } else {
            twoPaneMode = false;
        }
        if(twoPaneMode){
            Fragment listFragment =  fragmentManager.findFragmentById(R.id.transactions_list);
            if (listFragment==null){
                listFragment = arrayList.get(1);
                fragmentManager.beginTransaction()
                        .replace(R.id.transactions_list,listFragment, "list")
                        .commit();
                /*if(!twoPaneMode){
                    Log.v("First one", "if if");
                    viewPager = findViewById(R.id.transactions_list);
                    pagerAdapter = new ScreenSlidePagerAdapter(this);
                    pagerAdapter.setArrayList(arrayList);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.registerOnPageChangeCallback(pageChangeCallback);
                    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    viewPager.setCurrentItem(1,false);
                }
                else {
                    //listFragment = new TransactionListFragment();

                }*/


            }
            else {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                /*if (twoPaneMode)

                else {
                    Log.v("This one", "else else");
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    viewPager = findViewById(R.id.transactions_list);
                    pagerAdapter = new ScreenSlidePagerAdapter(this);
                    pagerAdapter.setArrayList(arrayList);
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.registerOnPageChangeCallback(pageChangeCallback);
                    viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    viewPager.setCurrentItem(1, false);
                }*/
            }
        }
        else {
            viewPager = findViewById(R.id.activityViewPager);
            pagerAdapter = new ScreenSlidePagerAdapter(this);
            pagerAdapter.setArrayList(arrayList);
            viewPager.setAdapter(pagerAdapter);
            viewPager.registerOnPageChangeCallback(pageChangeCallback);
            viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager.setCurrentItem(1,false);
        }



    }
    private ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(final int position) {
        }
        @Override
        public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels){
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            super.onPageScrollStateChanged(state);
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                currentItem = viewPager.getCurrentItem();
                if(arrayList.get(currentItem).getClass().equals(GraphsFragment.class)){
                    arrayList.set(0, budgetFragment);
                    arrayList.set(1,graphsFragment);
                    arrayList.set(2, transactionListFragment);
                    pagerAdapter.setArrayList(arrayList);
                }
                else if(arrayList.get(currentItem).getClass().equals(TransactionListFragment.class) ||
                        arrayList.get(currentItem).getClass().equals(TransactionDetailFragment.class)){
                    arrayList.set(0,graphsFragment);
                    arrayList.set(1, transactionListFragment);
                    arrayList.set(2, budgetFragment);
                    pagerAdapter.setArrayList(arrayList);

                }
                else if(arrayList.get(currentItem).getClass().equals(BudgetFragment.class)){
                    arrayList.set(0, transactionListFragment);
                    arrayList.set(1, budgetFragment);
                    arrayList.set(2,graphsFragment);
                    pagerAdapter.setArrayList(arrayList);
                }
                viewPager.setCurrentItem(1,false);
                //false da se ne vidi promjena
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putString("type", "edit");
        arguments.putParcelable("transaction", transaction);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,pagerAdapter.createFragment(3))
                    .addToBackStack(null)
                    .commit();
            //pagerAdapter.createFragment(3)
        }
    }



    @Override
    public Boolean getPaneMode() {
        return twoPaneMode;
    }

    @Override
    public void add() {
        Bundle arguments = new Bundle();
        arguments.putString("type", "add");
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            pagerAdapter.setChange(true);
            pagerAdapter.prepareChange("add");
            pagerAdapter.createFragment(0);

            pagerAdapter.notifyItemChanged(0);
            viewPager.setCurrentItem(3);
        }

    }

    @Override
    public void save(final Transaction transaction) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        if(listFragment!= null) listFragment.addTransaction(transaction);
        if(listFragment.getPresenter().getTotalExpenditure()> listFragment.getCurrentUser().getTotalLimit()
                || listFragment.getPresenter().getMonthExpenditure(listFragment.getCurrentMonth()) > listFragment.getCurrentUser().getMonthLimit()){
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
                    if(twoPaneMode == false){
                        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "edit");
                        bundle.putParcelable("transaction", transaction);
                        detailFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.transactions_list,detailFragment)
                                .addToBackStack(null)
                                .commit();
                    }

                }
            });
            alertDialog.show();

        }
    }

    @Override
    public void edit(Transaction changed) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        if(listFragment!= null){
            final Transaction oldTransaction = listFragment.getSelectedTransaction();
            listFragment.updateCurrentTransaction(changed);
            if(listFragment.getPresenter().getTotalExpenditure()> listFragment.getCurrentUser().getTotalLimit()
                    || listFragment.getPresenter().getMonthExpenditure(listFragment.getCurrentMonth()) > listFragment.getCurrentUser().getMonthLimit()) {
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
                        if (twoPaneMode == false) {
                            TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "edit");
                            bundle.putParcelable("transaction", oldTransaction);
                            detailFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.transactions_list, detailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }

                    }
                });
                alertDialog.show();
            }

        }
    }

    @Override
    public void delete() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        if(listFragment!= null) listFragment.deleteCurrentTransaction();
    }
}



/*
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
*/
/*
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
                                || transactionListPresenter.getMonthExpenditure(currentMonth) > currentUser.getMonthLimit()){
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
                                || transactionListPresenter.getMonthExpenditure(currentMonth) > currentUser.getMonthLimit()){
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
                    refreshCurrentMonthTransactions();
                }
            }
        }
    }

 */
/*
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

    @Override
    public void refreshCurrentMonthTransactions(){
        transactionListPresenter.filterByMonth(currentMonth);
        refreshFilter();
    }
    @Override
    public void refreshFilter(){
        transactionListAdapter.getFilter().filter(filterBySpinner.getSelectedItem().toString());
    }
*/