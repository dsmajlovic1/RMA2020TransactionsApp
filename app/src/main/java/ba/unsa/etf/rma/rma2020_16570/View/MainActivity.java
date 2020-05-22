package ba.unsa.etf.rma.rma2020_16570.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Adapter.ScreenSlidePagerAdapter;
import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetFragment;
import ba.unsa.etf.rma.rma2020_16570.Detail.TransactionDetailFragment;
import ba.unsa.etf.rma.rma2020_16570.Graphs.GraphsFragment;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;

public class MainActivity extends FragmentActivity implements TransactionListFragment.OnItemClick,
                                                                TransactionListFragment.TwoPaneMode,
                                                                IFragmentCommunication,
                                                                IAccount{

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
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,pagerAdapter.createFragment(3))
                    .addToBackStack(null)
                    .commit();
            //pagerAdapter.createFragment(3)*/
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
        if(twoPaneMode){
            FragmentManager fragmentManager = getSupportFragmentManager();
            listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
            if(listFragment!= null) listFragment.addTransaction(transaction);

        }
        else {
            listFragment = (TransactionListFragment) arrayList.get(arrayList.indexOf(transactionListFragment));
            listFragment.addTransaction(transaction);
        }/*
        if(listFragment.getPresenter().getTotalExpenditure()> listFragment.getCurrentUser().getTotalLimit()
                || listFragment.getPresenter().getMonthExpenditure(listFragment.getCurrentMonth()) > listFragment.getCurrentUser().getMonthLimit()){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Limit reached");
            alertDialog.setMessage("Are you sure you want to make these changes?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!twoPaneMode){
                        listFragment.notifyTransactionListDataSetChanged();
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
        else {
            listFragment.notifyTransactionListDataSetChanged();
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
        }*/

    }

    @Override
    public void edit(Transaction changed) {
        final TransactionListFragment listFragment;
        final Transaction oldTransaction;
        if(twoPaneMode){
            FragmentManager fragmentManager = getSupportFragmentManager();
            listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
            if(listFragment!= null){
                listFragment.updateCurrentTransaction(changed);
            }
        }
        else {
            listFragment = (TransactionListFragment) arrayList.get(arrayList.indexOf(transactionListFragment));
            listFragment.updateCurrentTransaction(changed);
        }
        oldTransaction = listFragment.getSelectedTransaction();
        /*
        if(listFragment.getPresenter().getTotalExpenditure()> listFragment.getCurrentUser().getTotalLimit()
                || listFragment.getPresenter().getMonthExpenditure(listFragment.getCurrentMonth()) > listFragment.getCurrentUser().getMonthLimit()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Limit reached");
            alertDialog.setMessage("Are you sure you want to make these changes?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listFragment.notifyTransactionListDataSetChanged();
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
        else {
            listFragment.notifyTransactionListDataSetChanged();
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

         */
    }

    @Override
    public void delete() {
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
}
