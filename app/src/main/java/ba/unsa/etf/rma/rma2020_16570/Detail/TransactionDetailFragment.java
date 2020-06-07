package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Adapter.TypeSpinnerAdapter;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListFragment;
import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.Util.ConnectivityBroadcastReceiver;
import ba.unsa.etf.rma.rma2020_16570.View.IFragmentCommunication;
import ba.unsa.etf.rma.rma2020_16570.View.MainActivity;
import ba.unsa.etf.rma.rma2020_16570.View.TextChangedWatcher;

import static android.app.Activity.RESULT_OK;

public class TransactionDetailFragment extends Fragment implements ConnectivityBroadcastReceiver.ConnectionChange {
    private Context parent;
    private Transaction transaction;
    private Transaction oldTransaction;

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

    private Boolean toggleConnection = true;

    private ITransactionDetailPresenter presenter;

    public ITransactionDetailPresenter getPresenter() {
        if (presenter == null) {
            presenter = new TransactionDetailPresenter(getActivity());
        }
        return presenter;
    }
    private IFragmentCommunication communication;

    public IFragmentCommunication getCommunication(){
        if(communication == null){
            communication = (MainActivity)getActivity();
        }
        return communication;
    }

    private ITransactionDetailInteractor transactionDetailInteractor;

    public ITransactionDetailInteractor getTransactionDetailInteractor() {
        if(transactionDetailInteractor == null) transactionDetailInteractor = new TransactionDetailInteractor();
        return transactionDetailInteractor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);

        //Spinner
        typeSpinnerAdapter = new TypeSpinnerAdapter(getActivity(), R.layout.filter_spinner_item, TransactionListFragment.filterTypes);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.filter_spinner_item);
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinnerAdapter.notifyDataSetChanged();

        //Get resources
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        amountEditText = (EditText) view.findViewById(R.id.amountEditText);
        titleEditText = (EditText) view.findViewById(R.id.titleEditText);
        itemDescriptionEditText = (EditText) view.findViewById(R.id.itemDescriptionEditText);
        transactionIntervalEditText = (EditText) view.findViewById(R.id.transactionIntervalEditText);
        endDateEditText = (EditText) view.findViewById(R.id.endDateEditText);
        deleteButton = (Button) view.findViewById(R.id.deleteButton);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(saveButtonOnClickListener);

        if(getArguments()!= null && getArguments().containsKey("type")) typeOfActivity = getArguments().getString("type");
        if (getArguments() != null && getArguments().containsKey("transaction")) {
            //getPresenter().setMovie(getArguments().getParcelable("movie"));


            //Assign button onClickListeners
            deleteButton.setOnClickListener(deleteButtonOnClickListener);
            if(getArguments()!= null){
                typeOfActivity = getArguments().getString("type");

                if(typeOfActivity.equals("edit")){
                    getPresenter().setTransaction(getArguments().getParcelable("transaction"));
                    transaction = getPresenter().getTransaction();
                    oldTransaction = new Transaction(transaction);

                    getTransactionDetailInteractor().save(oldTransaction, getContext());

                    //Set resource value from data
                    dateEditText.setText(simpleDateFormat.format(transaction.getDate()));
                    amountEditText.setText(transaction.getAmount().toString());
                    titleEditText.setText(transaction.getTittle());
                    if(transaction.getItemDescription() != null) itemDescriptionEditText.setText(transaction.getItemDescription());
                    if(transaction.getTransactionInterval()!= null) transactionIntervalEditText.setText(transaction.getTransactionInterval().toString());
                    if(transaction.getEndDate()!= null) endDateEditText.setText(simpleDateFormat.format(transaction.getEndDate()));
                    typeSpinner.setSelection(TransactionListFragment.filterTypes.indexOf(transaction.getType()));

                    //Add textChangedListeners
                    dateEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), dateEditText));
                    amountEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), amountEditText));
                    titleEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), titleEditText));
                    itemDescriptionEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), itemDescriptionEditText));
                    transactionIntervalEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), transactionIntervalEditText));
                    endDateEditText.addTextChangedListener(new TextChangedWatcher(getActivity(), endDateEditText));
                }
                else{
                    transaction = new Transaction();
                    deleteButton.setEnabled(false);
                }
            }
        }
        else {
            transaction = new Transaction();
            deleteButton.setEnabled(false);
        }
        TextView connection = (TextView) view.findViewById(R.id.connectionText);
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null){
            toggleConnection = false;
            if(typeOfActivity.equals("edit")) connection.setText("Offline izmjena");
            else connection.setText("Offline dodavanje");
        }
        else{
            toggleConnection = true;
            connection.setText("Connected");
        }


        return view;
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
                date = simpleDateFormat.parse(dateEditText.getText().toString().trim());
            } catch (ParseException e) {
                date = null;
            }
            Log.e(dateEditText.getText().toString().trim(), date.toString());
            transaction.setDate(date);
            transaction.setAmount(Double.parseDouble(amountEditText.getText().toString()));
            transaction.setTittle(titleEditText.getText().toString());
            transaction.setType(Transaction.Type.valueOf(typeSpinner.getSelectedItem().toString()));

            if(transaction.getType() == Transaction.Type.INDIVIDUALINCOME || transaction.getType() == Transaction.Type.REGULARINCOME) transaction.setItemDescription(null);
            else transaction.setItemDescription(itemDescriptionEditText.getText().toString().trim());

            if(transaction.getType() == Transaction.Type.INDIVIDUALINCOME || transaction.getType() == Transaction.Type.INDIVIDUALPAYMENT || transaction.getType() == Transaction.Type.PURCHASE){
                transaction.setTransactionInterval(null);
                transaction.setEndDate(null);
            }
            else{
                int interval = new Integer(transactionIntervalEditText.getText().toString().trim());
                transaction.setTransactionInterval((interval));
                try {
                    endDate = simpleDateFormat.parse(endDateEditText.getText().toString().trim());
                } catch (ParseException e) {
                    endDate = null;
                }
                transaction.setEndDate(endDate);
            }
            TextView connectionText = (TextView) getView().findViewById(R.id.connectionText);
            if(connectionText.getText().toString().equals("Connected")) toggleConnection = true;
            else toggleConnection = false;
            //Change
            if(typeOfActivity.equals("edit")){
                if(toggleConnection) getCommunication().edit(transaction, oldTransaction);
                else{
                    getTransactionDetailInteractor().save(transaction, getContext());
                    getCommunication().back();
                }
            }
            else {
                if(toggleConnection) getCommunication().save(transaction);
                else {
                    getTransactionDetailInteractor().save(transaction, getContext());
                    getCommunication().back();
                }
            }
        }
    };

    private View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Delete");
            alertDialog.setMessage("Are you sure you want to delete this transaction?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(toggleConnection){
                        getCommunication().delete(transaction);
                    }
                    else {
                        Log.w("Delete", deleteButton.getText().toString());
                        if(deleteButton.getText().toString().trim().equals("Delete")){
                            getTransactionDetailInteractor().delete(transaction, getContext());
                            deleteButton.setText("Undo");
                        }
                        else {
                            getTransactionDetailInteractor().undoDelete(transaction, getContext());
                            deleteButton.setText("Delete");
                        }

                    }

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

    @Override
    public void changeConnectionStatus(Boolean connected) {
        if(getView()!= null){
            TextView connection = (TextView) getView().findViewById(R.id.connectionText);
            if(connected) {
                toggleConnection = true;
                connection.setText("Connected");
            }
            else{
                toggleConnection = false;
                if(typeOfActivity.equals("edit")) connection.setText("Offline izmjena");
                else connection.setText("Offline dodavanje");
            }
        }
    }

    @Override
    public void changeWorkMode(Boolean connected) {

    }
}
