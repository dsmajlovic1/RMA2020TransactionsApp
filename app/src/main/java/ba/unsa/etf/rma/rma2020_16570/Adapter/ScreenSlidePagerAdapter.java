package ba.unsa.etf.rma.rma2020_16570.Adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Budget.BudgetFragment;
import ba.unsa.etf.rma.rma2020_16570.Detail.TransactionDetailFragment;
import ba.unsa.etf.rma.rma2020_16570.Graphs.GraphsFragment;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> arrayList;
    private ArrayList<String> createdFragments;

    public ArrayList<Fragment> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Fragment> arrayList) {
        this.arrayList = arrayList;
    }
    private FragmentManager fragmentManager;
    private static final int NUM_PAGES = 3;

    private Boolean change = false;

    public Boolean getChange() { return change;}
    public void setChange(Boolean change) { this.change = change;}

    //private TransactionListFragment transactionListFragment;


    public ScreenSlidePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragmentManager = fragmentManager;
        this.createdFragments = new ArrayList<String>();
    }
    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
        this.createdFragments = new ArrayList<String>();
    }

    public void addFragment(Fragment fragment){
        arrayList.add(fragment);
    }

    public void prepareChange(String type, TransactionDetailFragment detailFragment){
        Log.e("Position 4", String.valueOf(arrayList.size()));
        if(arrayList.size()==NUM_PAGES+1){
            arrayList.remove(NUM_PAGES);
        }
        Bundle arguments = new Bundle();
        if(type.equals("edit")){
            Transaction transaction = ((TransactionListFragment)arrayList.get(1)).getSelectedTransaction();
            arguments.putString("type", "edit");
            arguments.putParcelable("transaction", transaction);
        }
        else {
            arguments.putString("type", "add");
        }
        //TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        //detailFragment.setArguments(arguments);
        arrayList.add(detailFragment);
        //this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.e("Create", String.valueOf(position));
        if(change || position == 3){
            Log.v("Recreate", String.valueOf(position));
            createdFragments.add(arrayList.get(3).getClass().getName());
            return arrayList.get(3);
        }
        else {
            createdFragments.add(arrayList.get(position).getClass().getName());
            return arrayList.get(position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
    @Override
    public long getItemId (int position){
        Log.e("getItemId:", String.valueOf(position));
        Log.e("Array", arrayList.get(0).getClass().toString());
        Log.e("Array", arrayList.get(1).getClass().toString());
        Log.e("Array", arrayList.get(2).getClass().toString());
        if(arrayList.get(position).getClass().equals(GraphsFragment.class)) return 0;
        else if(arrayList.get(position).getClass().equals(TransactionListFragment.class)) return 1;
        else if(arrayList.get(position).getClass().equals(BudgetFragment.class)) return 2;
        else if(arrayList.get(position).getClass().equals(TransactionDetailFragment.class)) return 3;
        return (long)position;
    }
    @Override
    public boolean containsItem (long itemId){
        Log.e("containsItem:", String.valueOf(itemId));
        return createdFragments.contains(arrayList.get((int) itemId).getClass().getName());
    }
}
