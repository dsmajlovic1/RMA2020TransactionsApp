package ba.unsa.etf.rma.rma2020_16570.List;


import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Model.TransactionsModel;
import ba.unsa.etf.rma.rma2020_16570.R;
import ba.unsa.etf.rma.rma2020_16570.Util.TransactionDBOpenHelper;

//public class TransactionListInteractor extends AsyncTask<String, Void, Void> implements ITransactionListInteractor {
public class TransactionListInteractor extends IntentService implements ITransactionListInteractor {

    final public static int STATUS_FINISHED=0;
    final public static int STATUS_ERROR=1;

    private String type;
    private String query;
    private JSONObject postData;
    private ArrayList<Transaction> transactions;
    private OnTransactionsFetched caller;
    private Context context;

    public interface  OnTransactionsFetched{
    public void onDone(ArrayList<Transaction> transactions);
    }
    public TransactionListInteractor(){
        super(null);
    }

    public TransactionListInteractor(Context context) {
        super(null);
        this.context = context;
    }
    public TransactionListInteractor(Context context, OnTransactionsFetched caller, String type, Transaction postData) {
        super(null);
        this.context = context;
        this.caller = caller;
        this.type = type;
        if (postData != null) {
            this.postData = convertTransactionToJSON(postData);
        }

        //this.postData = postData;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public JSONObject convertTransactionToJSON(Transaction transaction){
        JSONObject object = new JSONObject();
        try {
            //object.put("id", transaction.getId());
            object.put("date", transaction.getDate());
            object.put("title", transaction.getTittle());
            object.put("amount", transaction.getAmount());
            object.put("itemDescription", transaction.getItemDescription());
            object.put("transactionInterval", transaction.getTransactionInterval());
            object.put("endDate", transaction.getEndDate());
            object.put("typeId", convertTypeToInt(transaction.getType()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
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
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final ResultReceiver receiver =intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();
        String type = intent.getStringExtra("type");
        /*String params = intent.getStringExtra("query");
        String query = null;
        try {
            query = URLEncoder.encode(params, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        String query = intent.getStringExtra("query");
        String url1 = this.getApplicationContext().getString(R.string.root)+"/account/"+this.getApplicationContext().getString(R.string.api_id).trim()+query.trim();
        try{
            if(type.trim().equals("GET")){
                Log.e("Interactor", "GET");
                transactions =new ArrayList<Transaction>();
                int j = 0;
                while (true){
                    URL url = new URL( url1+"?page="+j);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String object = convertStreamToString(in);
                    JSONObject jo = new JSONObject(object);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject transaction = results.getJSONObject(i);
                        Integer id = transaction.getInt("id");
                        String date = transaction.getString("date");
                        String title = transaction.getString("title");
                        Double amount = transaction.getDouble("amount");
                        Transaction.Type transactionType = convertIntToType(transaction.getInt("TransactionTypeId"));
                        Integer transactionInterval = null;
                        String endDate = null;
                        String itemDescription = null;
                        if (transactionType != Transaction.Type.INDIVIDUALINCOME && transactionType != Transaction.Type.REGULARINCOME) {
                            itemDescription = transaction.getString("itemDescription");
                        }
                        if (transactionType == Transaction.Type.REGULARINCOME || transactionType == Transaction.Type.REGULARPAYMENT) {
                            transactionInterval = transaction.getInt("transactionInterval");
                            endDate = transaction.getString("endDate");
                        }

                        transactions.add(new Transaction(id, date, title, amount, itemDescription, transactionInterval, endDate, transactionType));
                    }
                    urlConnection.disconnect();
                    j++;
                }
                //URL url = new URL( url1);
                bundle.putString("type", "GET");
                bundle.putParcelableArrayList("result", transactions);
            }
            else if (type.equals("POST")){
                postData = convertTransactionToJSON((Transaction) intent.getParcelableExtra("transaction"));
                Log.e("POST", postData.toString());
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod(type);

                //OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
                if(postData != null){
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();


                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode ==  200) {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertStreamToString(inputStream);

                    } else {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertStreamToString(inputStream);
                    }

                }
                bundle.putString("type", "POST");
            }
            else if (type.equals("DELETE")){
                postData = convertTransactionToJSON((Transaction) intent.getParcelableExtra("transaction"));
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(type);

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(inputStream);

                } else {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(inputStream);
                }
                bundle.putString("type", "DELETE");
            }
            else if(type.equals("UPLOAD")){
                //Upload changes

                //Get local changes
                ContentResolver cr = getApplicationContext().getContentResolver();
                String[] kolone = null;
                Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
                String where = null;
                String whereArgs[] = null;
                String order = null;
                Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);

                Transaction transaction;


                Boolean continueCur = cursor.moveToFirst();
                while (continueCur){
                    int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
                    int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID);
                    int datePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
                    int titlePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
                    int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
                    int itemDescPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION);
                    int intervalPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL);
                    int endDatePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ENDDATE);
                    int typePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE_ID);
                    Transaction.Type type1 = convertIntToType(cursor.getInt(typePos));
                    transaction = new Transaction(cursor.getInt(idPos), cursor.getString(datePos), cursor.getString(titlePos), cursor.getDouble(amountPos),
                            cursor.getString(itemDescPos), cursor.getInt(intervalPos), cursor.getString(endDatePos), type1);

                    if(String.valueOf(cursor.getInt(internalId)).equals("null")) transaction.setInternalId(cursor.getInt(internalId));
                    //Get connection
                    URL url;

                    if(transaction.getId()!= null && transaction.getId()!=0) url = new URL( url1+"/"+transaction.getId().toString());
                    else url = new URL(url1);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    urlConnection.setRequestMethod("POST");

                    postData = convertTransactionToJSON(transaction);

                    if(postData != null){
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        writer.write(postData.toString());
                        writer.flush();


                        int statusCode = urlConnection.getResponseCode();

                        if (statusCode ==  200) {
                            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                            String response = convertStreamToString(inputStream);
                            Log.e("Response", response);

                            ContentResolver cr2 = getApplicationContext().getContentResolver();
                            Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");

                            String where2 = "id = ?";
                            String whereArgs2[] = {String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID)))};

                            cr2.delete(transactionsURI, where2, whereArgs2);

                        } else {
                            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                            String response = convertStreamToString(inputStream);
                            Log.e("Response", response);
                        }

                    }
                    urlConnection.disconnect();

                    continueCur = cursor.moveToNext();
                }


                //Delete transactions


                //Get local delete
                //ContentResolver cr = context.getApplicationContext().getContentResolver();
                //String[] kolone = null;
                adresa = Uri.parse("content://rma.provider.deletes/elements");
                //String where = null;
                //String whereArgs[] = null;
                //String order = null;
                cursor = cr.query(adresa,kolone,where,whereArgs,order);


                continueCur = cursor.moveToFirst();

                while (continueCur){
                    try{
                        URL url = new URL( url1+"/"+String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id"))));
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("DELETE");

                        int statusCode = urlConnection.getResponseCode();

                        if (statusCode ==  200) {
                            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                            String response = convertStreamToString(inputStream);

                            ContentResolver cr3 = getApplicationContext().getContentResolver();
                            Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");

                            String where2 = "_id = ?";
                            String whereArgs2[] = {String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.DELETE_ID)))};

                            cr3.delete(transactionsURI, where2, whereArgs2);

                        } else {
                            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                            String response = convertStreamToString(inputStream);
                        }

                        urlConnection.disconnect();
                        continueCur = cursor.moveToNext();
                    }
                    catch (CursorIndexOutOfBoundsException e){
                        Log.e("Out of bounds",e.toString());
                        break;
                    }

                }

                bundle.putString("type", "UPLOAD");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
            return;
        }

        receiver.send(STATUS_FINISHED, bundle);

    }

    /*
    @Override
    protected Void doInBackground(String... strings) {
        query = strings[0];
        String url1 = context.getString(R.string.root)+"/account/"+context.getString(R.string.api_id).trim()+query.trim();
        try{
            if(type.trim().equals("GET")){
                transactions =new ArrayList<Transaction>();
                int j = 0;
                while (true){
                    URL url = new URL( url1+"?page="+j);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String object = convertStreamToString(in);
                    JSONObject jo = new JSONObject(object);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject transaction = results.getJSONObject(i);
                        Integer id = transaction.getInt("id");
                        String date = transaction.getString("date");
                        String title = transaction.getString("title");
                        Double amount = transaction.getDouble("amount");
                        Transaction.Type transactionType = convertIntToType(transaction.getInt("TransactionTypeId"));
                        Integer transactionInterval = null;
                        String endDate = null;
                        String itemDescription = null;
                        if (transactionType != Transaction.Type.INDIVIDUALINCOME && transactionType != Transaction.Type.REGULARINCOME) {
                            itemDescription = transaction.getString("itemDescription");
                        }
                        if (transactionType == Transaction.Type.REGULARINCOME || transactionType == Transaction.Type.REGULARPAYMENT) {
                            transactionInterval = transaction.getInt("transactionInterval");
                            endDate = transaction.getString("endDate");
                        }

                        transactions.add(new Transaction(id, date, title, amount, itemDescription, transactionInterval, endDate, transactionType));
                    }
                    urlConnection.disconnect();
                    j++;
                }
                //URL url = new URL( url1);
            }
            else if (type.equals("POST")){
                Log.e("POST", postData.toString());
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod(type);

                //OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
                if(postData != null){
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();


                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode ==  200) {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertStreamToString(inputStream);

                    } else {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertStreamToString(inputStream);
                    }

                }
            }
            else if (type.equals("DELETE")){
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(type);

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(inputStream);

                } else {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(inputStream);
                }
            }
            else{

            }
            return  null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(type.equals("GET")){
            caller.onDone(transactions);
        }
    }
     */

    //@Override
    //public ArrayList<Transaction> get() { return TransactionsModel.transactions; }

    @Override
    public void update(Transaction transaction, Transaction newTransaction){
        TransactionsModel.transactions.remove(transaction);
        TransactionsModel.transactions.add(newTransaction);
    }

    @Override
    public void delete(Transaction transaction){
        TransactionsModel.transactions.remove(transaction);
    }

    @Override
    public void add(Transaction transaction) {
        TransactionsModel.transactions.add(transaction);
    }

    @Override
    public Transaction getDatabaseTransaction(int id, Context context) {
        Transaction transaction = new Transaction();
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"),id);
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);

        if (cursor != null){
            cursor.moveToFirst();
            int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
            int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID);
            int datePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
            int titlePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
            int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
            int itemDescPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION);
            int intervalPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL);
            int endDatePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ENDDATE);
            int typePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE_ID);
            Transaction.Type type1 = convertIntToType(cursor.getInt(typePos));
            Log.e("Date:", cursor.getString(datePos));
            transaction = new Transaction(cursor.getInt(idPos), cursor.getString(datePos), cursor.getString(titlePos), cursor.getDouble(amountPos),
                        cursor.getString(itemDescPos), cursor.getInt(intervalPos), cursor.getString(endDatePos), type1);
            transaction.setInternalId(cursor.getInt(internalId));
        }
        cursor.close();
        return transaction;
    }

    @Override
    public Cursor getMonthTransactionsCursor() {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    @Override
    public Date getEarliestDate() {
        Date earliestDate = new Date(Long.MAX_VALUE);

        for (int i = 0; i < TransactionsModel.transactions.size(); i++){
            if(TransactionsModel.transactions.get(i).getDate().before(earliestDate)) earliestDate = TransactionsModel.transactions.get(i).getDate();
        }
        return earliestDate;
    }

    @Override
    public Date getLatestDate() {
        Date latestDate = new Date(0L);

        for (int i = 0; i < TransactionsModel.transactions.size(); i++){
            if(TransactionsModel.transactions.get(i).getDate().after(latestDate)) latestDate = TransactionsModel.transactions.get(i).getDate();
        }
        return latestDate;
    }

    @Override
    public Double getTotalIncome() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += TransactionsModel.transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    @Override
    public Double getTotalExpenditure() {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if(!(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME) && !(TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                sum += TransactionsModel.transactions.get(i).getAmount();
            }
        }
        return sum;
    }

    @Override
    public Double getMonthExpenditure(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());

                if((cal.get(Calendar.MONTH))<= (monthCal.get(Calendar.MONTH)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH))>= (monthCal.get(Calendar.MONTH)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.MONTH))== (monthCal.get(Calendar.MONTH)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }

            }
        }
        return sum;
    }

    @Override
    public Double getMonthIncome(Month month) {
        Double sum = 0.0;
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(String.valueOf(cal.get(Calendar.MONTH)+1).equals(month.getMonthNumberString()) && String.valueOf(cal.get(Calendar.YEAR)).equals(month.getYearNumberString())){
                    sum += TransactionsModel.transactions.get(i).getAmount();

                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(month.getDate());

                if((cal.get(Calendar.MONTH))<= (monthCal.get(Calendar.MONTH)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.MONTH))>= (monthCal.get(Calendar.MONTH)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.MONTH)) == (monthCal.get(Calendar.MONTH)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public Double getWeeklyIncome(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(week);

                if((cal.get(Calendar.WEEK_OF_YEAR))<= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.WEEK_OF_YEAR))>= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.WEEK_OF_YEAR)) == (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public Double getWeeklyExpenditure(Date week) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(week);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(cal.get(Calendar.WEEK_OF_YEAR) == calCompare.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(week);

                if((cal.get(Calendar.WEEK_OF_YEAR))<= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.WEEK_OF_YEAR))>= (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.WEEK_OF_YEAR)) == (monthCal.get(Calendar.WEEK_OF_YEAR)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public Double getDailyIncome(Date day) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(day);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALINCOME)){
                if(cal.get(Calendar.DATE) == calCompare.get(Calendar.DATE) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARINCOME){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(day);

                if((cal.get(Calendar.DATE))<= (monthCal.get(Calendar.DATE)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.DATE))>= (monthCal.get(Calendar.DATE)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.DATE)) == (monthCal.get(Calendar.DATE)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public Double getDailyExpenditure(Date day) {
        Double sum = 0.0;
        Calendar calCompare = Calendar.getInstance();
        calCompare.setTime(day);
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < TransactionsModel.transactions.size(); i++){
            cal.setTime(TransactionsModel.transactions.get(i).getDate());
            if((TransactionsModel.transactions.get(i).getType() == Transaction.Type.INDIVIDUALPAYMENT) || (TransactionsModel.transactions.get(i).getType() == Transaction.Type.PURCHASE)){
                if(cal.get(Calendar.DATE) == calCompare.get(Calendar.DATE) && cal.get(Calendar.YEAR) == calCompare.get(Calendar.YEAR)){
                    sum += TransactionsModel.transactions.get(i).getAmount();
                }
            }
            else if(TransactionsModel.transactions.get(i).getType() == Transaction.Type.REGULARPAYMENT){
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(TransactionsModel.transactions.get(i).getEndDate());
                Calendar monthCal = Calendar.getInstance();
                monthCal.setTime(day);

                if((cal.get(Calendar.DATE))<= (monthCal.get(Calendar.DATE)) &&
                        (cal.get(Calendar.YEAR))<= (monthCal.get(Calendar.YEAR)) &&
                        (endCal.get(Calendar.DATE))>= (monthCal.get(Calendar.DATE)) &&
                        (endCal.get(Calendar.YEAR))>= (monthCal.get(Calendar.YEAR))){
                    while (cal.getTime().before(endCal.getTime())){
                        if((cal.get(Calendar.DATE)) == (monthCal.get(Calendar.DATE)) &&
                                (cal.get(Calendar.YEAR))== (monthCal.get(Calendar.YEAR))){
                            sum += TransactionsModel.transactions.get(i).getAmount();
                        }
                        cal.add(Calendar.DATE, TransactionsModel.transactions.get(i).getTransactionInterval());
                    }
                }
            }
        }
        return sum;
    }

}
