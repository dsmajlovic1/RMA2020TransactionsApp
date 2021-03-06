package ba.unsa.etf.rma.rma2020_16570.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Adapter.TypeSpinnerAdapter;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.R;

public class TransactionDetailActivity extends AppCompatActivity {
    private Context parent;
    private Transaction transaction;

    //Resources
    private EditText dateEditText;
    private EditText amountEditText;
    private EditText titleEditText;
    private Spinner typeSpinner;
    private EditText itemDescriptionEditText;
    private EditText transactionIntervalEditText;
    private EditText endDateEditText;

    private Button saveButton;
    private Button deleteButton;

    private TypeSpinnerAdapter typeSpinnerAdapter;
    private TransactionListInteractor transactionListInteractor;

    private String typeOfActivity;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_detail_activity);

        //Get data
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        typeOfActivity = bundle.getString("type");


        //Get resources
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        itemDescriptionEditText = (EditText) findViewById(R.id.itemDescriptionEditText);
        transactionIntervalEditText = (EditText) findViewById(R.id.transactionIntervalEditText);
        endDateEditText = (EditText) findViewById(R.id.endDateEditText);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);


        //Spinner
        typeSpinnerAdapter = new TypeSpinnerAdapter(this, R.layout.filter_spinner_item, TransactionListFragment.filterTypes);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.filter_spinner_item);
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinnerAdapter.notifyDataSetChanged();


        //Assign button onClickListeners
        saveButton.setOnClickListener(saveButtonOnClickListener);
        deleteButton.setOnClickListener(deleteButtonOnClickListener);

        if(typeOfActivity.equals("edit")){
            transaction = (Transaction) bundle.getParcelable("transaction");

            //Set resource value from data
            dateEditText.setText(simpleDateFormat.format(transaction.getDate()));
            amountEditText.setText(transaction.getAmount().toString());
            titleEditText.setText(transaction.getTittle());
            if(transaction.getItemDescription() != null) itemDescriptionEditText.setText(transaction.getItemDescription());
            if(transaction.getTransactionInterval()!= null) transactionIntervalEditText.setText(transaction.getTransactionInterval().toString());
            if(transaction.getEndDate()!= null) endDateEditText.setText(simpleDateFormat.format(transaction.getEndDate()));
            typeSpinner.setSelection(TransactionListFragment.filterTypes.indexOf(transaction.getType()));

            //Add textChangedListeners
            dateEditText.addTextChangedListener(new TextChangedWatcher(this, dateEditText));
            amountEditText.addTextChangedListener(new TextChangedWatcher(this, amountEditText));
            titleEditText.addTextChangedListener(new TextChangedWatcher(this, titleEditText));
            itemDescriptionEditText.addTextChangedListener(new TextChangedWatcher(this, itemDescriptionEditText));
            transactionIntervalEditText.addTextChangedListener(new TextChangedWatcher(this, transactionIntervalEditText));
            endDateEditText.addTextChangedListener(new TextChangedWatcher(this, endDateEditText));
        }
        else {
            transaction = new Transaction();
            deleteButton.setEnabled(false);
        }
    }

    private View.OnClickListener saveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Set default background color
            dateEditText.setBackgroundResource(android.R.drawable.editbox_background);
            amountEditText.setBackgroundResource(android.R.drawable.editbox_background);
            titleEditText.setBackgroundResource(android.R.drawable.editbox_background);
            itemDescriptionEditText.setBackgroundResource(android.R.drawable.editbox_background);
            transactionIntervalEditText.setBackgroundResource(android.R.drawable.editbox_background);
            endDateEditText.setBackgroundResource(android.R.drawable.editbox_background);

            Date date, endDate;
            try {
                Log.d("Date---", dateEditText.getText().toString());
                Log.d("Date---", dateEditText.getText().toString());
                date = simpleDateFormat.parse(dateEditText.getText().toString());
            } catch (ParseException e) {
                date = null;
            }
            transaction.setDate(date);
            transaction.setAmount(Double.parseDouble(amountEditText.getText().toString()));
            transaction.setTittle(titleEditText.getText().toString());
            transaction.setType(Transaction.Type.valueOf(typeSpinner.getSelectedItem().toString()));

            if(transaction.getType() == Transaction.Type.INDIVIDUALINCOME || transaction.getType() == Transaction.Type.REGULARINCOME) transaction.setItemDescription(null);
            else transaction.setItemDescription(itemDescriptionEditText.getText().toString());

            if(transaction.getType() == Transaction.Type.INDIVIDUALINCOME || transaction.getType() == Transaction.Type.INDIVIDUALPAYMENT || transaction.getType() == Transaction.Type.PURCHASE){
                transaction.setTransactionInterval(null);
                transaction.setEndDate(null);
            }
            else{
                transaction.setTransactionInterval(Integer.getInteger(transactionIntervalEditText.getText().toString()));
                try {
                    endDate = simpleDateFormat.parse(endDateEditText.getText().toString());
                } catch (ParseException e) {
                    endDate = null;
                }
                transaction.setEndDate(endDate);
            }
            //Or change directly in database
            Bundle bundle = new Bundle();
            if(typeOfActivity.equals("edit")) bundle.putString("action", "save");
            else bundle.putString("action", "add");
            bundle.putParcelable("transaction", transaction);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(TransactionDetailActivity.this).create();
            alertDialog.setTitle("Delete");
            alertDialog.setMessage("Are you sure you want to delete this transaction?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = new Bundle();
                    bundle.putString("action", "delete");
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    dialog.dismiss();
                    finish();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    };
}
