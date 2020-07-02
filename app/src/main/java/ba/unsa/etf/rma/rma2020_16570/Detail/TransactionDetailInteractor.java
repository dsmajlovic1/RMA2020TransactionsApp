package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ba.unsa.etf.rma.rma2020_16570.List.TransactionListInteractor;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Util.TransactionContentProvider;
import ba.unsa.etf.rma.rma2020_16570.Util.TransactionDBOpenHelper;

public class TransactionDetailInteractor extends IntentService implements ITransactionDetailInteractor {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public TransactionDetailInteractor(){ super(null);}
    public TransactionDetailInteractor(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String type = intent.getStringExtra("type");
        Transaction transaction = (Transaction) intent.getParcelableExtra("transaction");
    }

    @Override
    public void save(Transaction transaction, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();

        values.put(TransactionDBOpenHelper.TRANSACTION_DATE, dateFormat.format(transaction.getDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transaction.getTittle());
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transaction.getAmount());
        if(transaction.getItemDescription()!= null) values.put(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION, transaction.getItemDescription());
        if(transaction.getTransactionInterval()!= null) values.put(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL, transaction.getTransactionInterval());
        if(transaction.getEndDate()!= null) values.put(TransactionDBOpenHelper.TRANSACTION_ENDDATE, dateFormat.format(transaction.getEndDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE_ID, convertTypeToInt(transaction.getType()));

        if(transaction.getId()!= null){
            values.put(TransactionDBOpenHelper.TRANSACTION_ID, transaction.getId());
            String[] kolone = null;
            Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), TransactionContentProvider.ALL_ROWS);
            String where = "id = ?";
            String whereArgs[] = {String.valueOf(transaction.getId())};
            String order = null;
            Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);

            if(cursor.getCount()== 0){
                cr.insert(transactionsURI, values);
            }
            else {
                cr.update(transactionsURI, values, where, whereArgs);
            }
        }
        else {
            if(transaction.getInternalId() == null) cr.insert(transactionsURI, values);
            else{
                String where = "_id = ?";
                String whereArgs[] = {transaction.getInternalId().toString()};
                cr.update(transactionsURI, values, where, whereArgs);
            }
        }

    }

    @Override
    public void update(Transaction transaction, Context context) {

    }

    @Override
    public void delete(Transaction transaction, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.deletes/elements");
        ContentValues values = new ContentValues();

        String[] kolone = null;
        Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.deletes/elements"), TransactionContentProvider.ALL_ROWS);
        String where = "id = ?";
        String whereArgs[] = {transaction.getId().toString()};
        String order = null;
        Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);

        values.put(TransactionDBOpenHelper.DELETE_TRANSACTION_ID, transaction.getId());

        //queue up for deletion
        if(cursor.getCount()== 0){
            cr.insert(transactionsURI, values);
        }
        else {
            //cr.update(transactionsURI, values, where, whereArgs);
        }
    }

    @Override
    public void undoDelete(Transaction transaction, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.deletes/elements");
        ContentValues values = new ContentValues();

        String[] kolone = null;
        Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.deletes/elements"), TransactionContentProvider.ALL_ROWS);
        String where = "id = ?";
        String whereArgs[] = {transaction.getId().toString()};
        String order = null;
        Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);

        values.put(TransactionDBOpenHelper.DELETE_TRANSACTION_ID, transaction.getId());

        //remove from queue
        if(cursor.getCount()== 0){
        }
        else {
            cr.delete(transactionsURI, where, whereArgs);
        }
    }

    private Integer convertTypeToInt(Transaction.Type type){
        if(type == Transaction.Type.REGULARPAYMENT)
            return 1;
        else if(type == Transaction.Type.REGULARINCOME)
            return 2;
        else if(type == Transaction.Type.PURCHASE)
            return 3;
        else if(type == Transaction.Type.INDIVIDUALINCOME)
            return 4;
        else
            return 5;
    }
}
