package ba.unsa.etf.rma.rma2020_16570.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Adapter.ScreenSlidePagerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetFragment;
import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetPresenter;
import ba.unsa.etf.rma.rma2020_16570.Budget.ISetBudget;
import ba.unsa.etf.rma.rma2020_16570.Detail.TransactionDetailFragment;
import ba.unsa.etf.rma.rma2020_16570.Graphs.GraphsFragment;
import ba.unsa.etf.rma.rma2020_16570.Graphs.GraphsPresenter;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListPresenter;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListResultReceiver;
import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.Util.ConnectivityBroadcastReceiver;
import ba.unsa.etf.rma.rma2020_16570.Util.TransactionDBOpenHelper;

public class MainActivity extends FragmentActivity implements TransactionListFragment.OnItemClick,
                                                                TransactionListFragment.TwoPaneMode,
                                                                IFragmentCommunication,
                                                                GraphsPresenter.OnExpendituresFetched,
                                                                ISetBudget,
                                                                IAccount,
                                                                ConnectivityBroadcastReceiver.ConnectionChange,
                                                                TransactionListResultReceiver.Receiver {

    private boolean twoPaneMode;

    private ViewPager2 viewPager;

    private ScreenSlidePagerAdapter pagerAdapter;

    private TransactionListFragment transactionListFragment;
    private BudgetFragment budgetFragment;
    private GraphsFragment graphsFragment;

    private ArrayList<Fragment> arrayList;
    private int currentItem;
    private Transaction oldTransaction;
    private Transaction newTransaction;
    private String saveActionType = "save";
    private Double totalExpenditure;
    private Double totalMonthExpenditure;

    private Double accountTotalLimit;
    private Double accountMonthLimit;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private IntentFilter iFilter;

    private TransactionListResultReceiver transactionListResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphsFragment = new GraphsFragment();
        transactionListFragment = new TransactionListFragment();
        budgetFragment = new BudgetFragment();

        Bundle accountBundle = new Bundle();
        accountBundle.putParcelable("account",transactionListFragment.getCurrentUser());
        budgetFragment.setArguments(accountBundle);

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
            }
            else {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

        transactionListResultReceiver = new TransactionListResultReceiver(new Handler());
        transactionListResultReceiver.setTransactionReceiver(MainActivity.this);

        connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver();
        connectivityBroadcastReceiver.setMainActivity(MainActivity.this);
        iFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityBroadcastReceiver, iFilter);

    }

    @Override
    public void onResume() {

        super.onResume();
        registerReceiver(connectivityBroadcastReceiver, iFilter);
    }

    @Override
    public void onPause() {

        unregisterReceiver(connectivityBroadcastReceiver);
        super.onPause();
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
                    transactionListFragment.refreshCurrentMonthTransactions();
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
        if (viewPager.getCurrentItem() == 1 ) {
            if(pagerAdapter.getChange()){
                pagerAdapter.setChange(false);

                getSupportFragmentManager().beginTransaction()
                        .remove(graphsFragment)
                        .remove(transactionListFragment)
                        .remove(budgetFragment)
                        .commit();
                arrayList.set(0,graphsFragment);
                arrayList.set(1, transactionListFragment);
                arrayList.set(2, budgetFragment);
                transactionListFragment.notifyTransactionListDataSetChanged();
                transactionListFragment.refreshCurrentMonthTransactions();

                pagerAdapter.setArrayList(arrayList);
                pagerAdapter.notifyItemRemoved(1);
                viewPager.invalidate();
                viewPager.setCurrentItem(1,false);
            }
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
            pagerAdapter.setChange(true);
            pagerAdapter.prepareChange("edit", detailFragment);
            pagerAdapter.createFragment(3);

            pagerAdapter.notifyItemChanged(1);
            viewPager.setCurrentItem(3);
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
            pagerAdapter.prepareChange("add", detailFragment);
            pagerAdapter.createFragment(0);

            pagerAdapter.notifyItemChanged(0);
            viewPager.setCurrentItem(3);
        }

    }

    @Override
    public void save(final Transaction transaction) {
        final TransactionListFragment listFragment;

        saveActionType = "save";

        if(twoPaneMode){
            FragmentManager fragmentManager = getSupportFragmentManager();
            listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
            //if(listFragment!= null) listFragment.addTransaction(transaction);

        }
        else {
            listFragment = (TransactionListFragment) arrayList.get(arrayList.indexOf(transactionListFragment));
            //listFragment.addTransaction(transaction);
        }


        newTransaction = transaction;
        GraphsPresenter alerts = new GraphsPresenter(getApplicationContext(), this);
        alerts.fetchExpenditures(listFragment.getCurrentMonth());
    }

    @Override
    public void edit(Transaction changed, Transaction old) {
        final TransactionListFragment listFragment;
        Log.e("Start", "Edit");

        oldTransaction = old;

        saveActionType = "edit";

        if(twoPaneMode){
            FragmentManager fragmentManager = getSupportFragmentManager();
            listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
            if(listFragment!= null){
                listFragment.updateCurrentTransaction(changed);
                listFragment.refreshCurrentMonthTransactions();
            }
        }
        else {
            listFragment = (TransactionListFragment) arrayList.get(arrayList.indexOf(transactionListFragment));
            listFragment.updateCurrentTransaction(changed);
            listFragment.refreshCurrentMonthTransactions();
        }
        newTransaction = changed;
        GraphsPresenter alerts = new GraphsPresenter(getApplicationContext(), this);
        alerts.fetchExpenditures(listFragment.getCurrentMonth());

    }

    @Override
    public void delete(Transaction transaction) {
        Log.e("Call", "delete");
        new TransactionListPresenter(transactionListFragment, getApplicationContext()).deleteTransaction(transaction);
        if(twoPaneMode){
            FragmentManager fragmentManager = getSupportFragmentManager();
            TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
            if(listFragment!= null) {
                listFragment.deleteCurrentTransaction();
                transactionListFragment.refreshCurrentMonthTransactions();
            }
        }
        else{
            ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).deleteCurrentTransaction();
            ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).notifyTransactionListDataSetChanged();
            pagerAdapter.setChange(false);

            arrayList.set(0,graphsFragment);
            arrayList.set(1, transactionListFragment);
            arrayList.set(2, budgetFragment);
            pagerAdapter.setArrayList(arrayList);
            pagerAdapter.notifyItemRemoved(3);
            pagerAdapter.notifyDataSetChanged();
            viewPager.invalidate();
            viewPager.setCurrentItem(1,false);
            transactionListFragment.refreshCurrentMonthTransactions();
        }
    }

    @Override
    public void back() {
        this.onBackPressed();
    }

    @Override
    public void callAlerts(ArrayList<Double> expenditures) {
        totalExpenditure = expenditures.get(0);
        totalMonthExpenditure = expenditures.get(1);

        Log.e("Middle", "callAlerts");

        new BudgetPresenter(getApplicationContext(), this).getAccount();
    }

    @Override
    public void setBudget(Double budget) {
        ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().setBudget(budget);
    }

    @Override
    public void setTotalLimit(Double totalLimit) {
        ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().setTotalLimit(totalLimit);

    }

    @Override
    public void setMonthLimit(Double monthLimit) {
        ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().setMonthLimit(monthLimit);

    }

    @Override
    public Double getBudget() {
        return ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().getBudget();
    }

    @Override
    public Double getTotalLimit() {
        return ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().getTotalLimit();
    }

    @Override
    public Double getMonthLimit() {
        return ((TransactionListFragment)arrayList.get(arrayList.indexOf(transactionListFragment))).getCurrentUser().getMonthLimit();
    }

    @Override
    public void setAccount(Account account) {
        this.accountTotalLimit = account.getTotalLimit();
        this.accountMonthLimit = account.getMonthLimit();

        Log.e("End", "setAccount");

        //final TransactionListFragment listFragment;
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");

        if(transactionListFragment == null) transactionListFragment = new TransactionListFragment();

        if(saveActionType.equals("edit")){
            if(totalExpenditure> accountTotalLimit
                    || totalMonthExpenditure > accountMonthLimit) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Limit reached");
                alertDialog.setMessage("Are you sure you want to make these changes?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transactionListFragment.notifyTransactionListDataSetChanged();
                        pagerAdapter.setChange(false);

                        getSupportFragmentManager().beginTransaction()
                                .remove(graphsFragment)
                                .remove(transactionListFragment)
                                .remove(budgetFragment)
                                .commit();
                        arrayList.set(0,graphsFragment);
                        arrayList.set(1, transactionListFragment);
                        arrayList.set(2, budgetFragment);

                        pagerAdapter.setArrayList(arrayList);
                        pagerAdapter.notifyItemRemoved(1);
                        viewPager.invalidate();
                        viewPager.setCurrentItem(1,false);
                        transactionListFragment.refreshCurrentMonthTransactions();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!twoPaneMode) {
                            new TransactionListPresenter(transactionListFragment, transactionListFragment.getContext()).updateTransaction(oldTransaction, oldTransaction);
                            /*TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "edit");
                            bundle.putParcelable("transaction", oldTransaction);
                            detailFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.transactions_list, detailFragment)
                                    .addToBackStack(null)
                                    .commit();*/

                        }

                    }
                });
                alertDialog.show();

            }
            else {
                transactionListFragment.notifyTransactionListDataSetChanged();
                pagerAdapter.setChange(false);

                getSupportFragmentManager().beginTransaction()
                        .remove(graphsFragment)
                        .remove(transactionListFragment)
                        .remove(budgetFragment)
                        .commit();
                arrayList.set(0,graphsFragment);
                arrayList.set(1, transactionListFragment);
                arrayList.set(2, budgetFragment);

                pagerAdapter.setArrayList(arrayList);
                pagerAdapter.notifyItemRemoved(1);
                viewPager.invalidate();
                viewPager.setCurrentItem(1,false);
                transactionListFragment.refreshCurrentMonthTransactions();
            }
        }
        else{
            if(totalExpenditure> accountTotalLimit
                    || totalMonthExpenditure > accountMonthLimit){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Limit reached");
                alertDialog.setMessage("Are you sure you want to make these changes?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!twoPaneMode){
                            transactionListFragment.notifyTransactionListDataSetChanged();
                            new TransactionListPresenter(transactionListFragment, getApplicationContext()).addTransaction(newTransaction);
                            pagerAdapter.setChange(false);

                            getSupportFragmentManager().beginTransaction()
                                    .remove(graphsFragment)
                                    .remove(transactionListFragment)
                                    .remove(budgetFragment)
                                    .commit();
                            arrayList.set(0,graphsFragment);
                            arrayList.set(1, transactionListFragment);
                            arrayList.set(2, budgetFragment);

                            pagerAdapter.setArrayList(arrayList);
                            pagerAdapter.notifyItemRemoved(1);
                            viewPager.invalidate();
                            viewPager.setCurrentItem(1,false);
                            transactionListFragment.refreshCurrentMonthTransactions();
                        }
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(twoPaneMode == false){
                            /*TransactionDetailFragment detailFragment = new TransactionDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "edit");
                            bundle.putParcelable("transaction", newTransaction);
                            detailFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.transactions_list,detailFragment)
                                    .addToBackStack(null)
                                    .commit();*/
                            //new TransactionListPresenter(transactionListFragment, getApplicationContext()).deleteTransaction(newTransaction);
                        }

                    }
                });
                alertDialog.show();

            }
            else {
                transactionListFragment.notifyTransactionListDataSetChanged();
                pagerAdapter.setChange(false);

                getSupportFragmentManager().beginTransaction()
                        .remove(graphsFragment)
                        .remove(transactionListFragment)
                        .remove(budgetFragment)
                        .commit();
                arrayList.set(0,graphsFragment);
                arrayList.set(1, transactionListFragment);
                arrayList.set(2, budgetFragment);

                pagerAdapter.setArrayList(arrayList);
                pagerAdapter.notifyItemRemoved(1);
                viewPager.invalidate();
                viewPager.setCurrentItem(1,false);
                transactionListFragment.refreshCurrentMonthTransactions();
            }
        }
    }

    @Override
    public void changeConnectionStatus(Boolean connected) {
        if(connected){
            transactionListFragment.getPresenter().uploadAllData(transactionListResultReceiver);
            //Update online
            //Delete online
        }
        else changeWorkMode(connected);

    }

    @Override
    public void changeWorkMode(Boolean connected) {
        if (twoPaneMode){
            ((TransactionDetailFragment)getSupportFragmentManager().findFragmentById(R.id.transaction_detail)).changeConnectionStatus(connected);
        }
        else{
            if(pagerAdapter.containsItem(3)){
                ((TransactionDetailFragment)pagerAdapter.getItemAtPosition(3)).changeConnectionStatus(connected);
            }
        }
    }

    @Override
    public void onResultsReceived(int resultCode, Bundle resultData) {
        Boolean connected = resultData.getBoolean("connected");
        switch (resultCode){
            case TransactionListInteractor.STATUS_FINISHED:
                if(!connected)changeWorkMode(connected);
                if(resultData.getString("type").equals("UPLOAD")) transactionListFragment.refreshCurrentMonthTransactions();
                transactionListFragment.notifyTransactionListDataSetChanged();
                break;
            case TransactionListInteractor.STATUS_ERROR:
            default:break;
        }
    }
}
