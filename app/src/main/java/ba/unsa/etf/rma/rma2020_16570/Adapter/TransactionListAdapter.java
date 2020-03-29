package ba.unsa.etf.rma.rma2020_16570.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;

public class TransactionListAdapter extends ArrayAdapter<Transaction> implements Filterable{

    private int resource;
    private ImageView transactionIconImageView;
    private TextView titleTextView;
    private   TextView amountTextView;
    private ArrayList<Transaction> originalData;
    private  ArrayList<Transaction> filteredData;
    private TransactionFilter filter;

    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Transaction> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.originalData = new ArrayList<Transaction>();
        this.filteredData = new ArrayList<Transaction>();
        this.filter = new TransactionFilter();
    }

    public void setTransactions(ArrayList<Transaction> transactions){
        this.originalData = new ArrayList<Transaction>(transactions);
        this.filteredData = new ArrayList<Transaction>(transactions);
        this.addAll(transactions);
    }

    public Transaction getTransaction(int position){
        return this.getItem(position);
    }
    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Transaction getItem(int position) {
        return filteredData.get(position);
    }

    public ArrayList<Transaction> getItems(){
        ArrayList<Transaction> items = new ArrayList<Transaction>();
        for(int i = 0; i < this.getCount(); i++){
            items.add(this.getItem(i));
        }
        return  items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LinearLayout newView;
        //if doesn't exist - create and inflate
        if(convertView == null){
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }
        //Get data from the list
        Transaction transaction =getItem(position);

        //Get the resources
        transactionIconImageView = (ImageView) newView.findViewById(R.id.transactionIconImageView);
        titleTextView = (TextView) newView.findViewById(R.id.titleTextView);
        amountTextView = (TextView) newView.findViewById(R.id.amountTextView);

        //Fill the view with data
        titleTextView.setText(transaction.getTittle());
        amountTextView.setText(transaction.getAmount().toString());
        if(transaction.getType()==Transaction.Type.REGULARINCOME){
            transactionIconImageView.setImageResource(R.drawable.regularincome);
        }
        else if(transaction.getType()== Transaction.Type.REGULARPAYMENT){
            transactionIconImageView.setImageResource(R.drawable.regularpayment);
        }
        else {
            transactionIconImageView.setImageResource(R.drawable.transaction);
        }
        return newView;
    }

    @Override
    public Filter getFilter(){
        if(filter == null){
            filter = new TransactionFilter();
        }
        return filter;
    }

    private class TransactionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString();
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                ArrayList<Transaction> newList = new ArrayList<Transaction>();

                for(int i = 0; i < originalData.size(); i++){
                    Transaction transaction = originalData.get(i);
                    if(transaction.getType().toString().equals(constraint)) newList.add(transaction);
                }
                results.count = newList.size();
                results.values = newList;
            }
            else {
                results.count = originalData.size();
                results.values = originalData;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Transaction>) results.values;
            notifyDataSetChanged();
        }
    }
}
