package ba.unsa.etf.rma.rma2020_16570.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.Util.TransactionDBOpenHelper;

public class TransactionListCursorAdapter extends ResourceCursorAdapter {

    private ImageView transactionIcon;
    private TextView title;
    private TextView amount;
    public TransactionListCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        transactionIcon = view.findViewById(R.id.transactionIconImageView);
        title = view.findViewById(R.id.titleTextView);
        amount = view.findViewById(R.id.amountTextView);

        title.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE)));
        amount.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT)));
        Integer typeId = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE_ID));
        Transaction.Type type = convertIntToType(typeId);

        if(type == Transaction.Type.REGULARINCOME){
            transactionIcon.setImageResource(R.drawable.regularincome);
        }
        else if(type == Transaction.Type.REGULARPAYMENT){
            transactionIcon.setImageResource(R.drawable.regularpayment);
        }
        else if(type == Transaction.Type.INDIVIDUALINCOME){
            transactionIcon.setImageResource(R.drawable.individualincome);
        }
        else if(type == Transaction.Type.INDIVIDUALPAYMENT){
            transactionIcon.setImageResource(R.drawable.individualpayment);
        }
        else if(type == Transaction.Type.PURCHASE){
            transactionIcon.setImageResource(R.drawable.purchase);
        }
        else {
            transactionIcon.setImageResource(R.drawable.transaction);
        }
    }
    private Transaction.Type convertIntToType(Integer index){
        switch (index){
            case 1:
                return Transaction.Type.REGULARPAYMENT;
            case 2:
                return Transaction.Type.REGULARINCOME;
            case 3:
                return Transaction.Type.PURCHASE;
            case 4:
                return Transaction.Type.INDIVIDUALINCOME;
            default:
                return Transaction.Type.INDIVIDUALPAYMENT;

        }
    }
}

