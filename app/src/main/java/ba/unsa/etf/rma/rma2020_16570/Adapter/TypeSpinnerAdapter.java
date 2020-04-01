package ba.unsa.etf.rma.rma2020_16570.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;

public class TypeSpinnerAdapter extends ArrayAdapter<Transaction.Type> {
    private int resource;
    private ImageView transactionIconImageView;
    private TextView transactionType;

    public TypeSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Transaction.Type> objects) {
        super(context, resource, objects);
        this.resource = resource;
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
            newView = (LinearLayout) convertView;
        }

        //Get data from the list
        Transaction.Type type = getItem(position);

        //Get resources
        transactionIconImageView = (ImageView) newView.findViewById(R.id.filterSpinnerIcon);
        transactionType = (TextView) newView.findViewById(R.id.filterSpinnerText);

        //Fill resources with data
        transactionType.setText(type.toString());
        if(type ==Transaction.Type.REGULARINCOME){
            transactionIconImageView.setImageResource(R.drawable.regularincome);
        }
        else if(type == Transaction.Type.REGULARPAYMENT){
            transactionIconImageView.setImageResource(R.drawable.regularpayment);
        }
        else if(type == Transaction.Type.INDIVIDUALINCOME){
            transactionIconImageView.setImageResource(R.drawable.individualincome);
        }
        else if(type == Transaction.Type.INDIVIDUALPAYMENT){
            transactionIconImageView.setImageResource(R.drawable.individualpayment);
        }
        else if(type == Transaction.Type.PURCHASE){
            transactionIconImageView.setImageResource(R.drawable.purchase);
        }
        else {
            transactionIconImageView.setImageResource(R.drawable.transaction);
        }
        return newView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        return getView(position, convertView, parent);
    }
}
