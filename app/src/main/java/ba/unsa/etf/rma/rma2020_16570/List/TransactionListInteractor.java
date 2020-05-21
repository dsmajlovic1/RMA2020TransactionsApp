package ba.unsa.etf.rma.rma2020_16570.List;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.TranslateAnimation;

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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Model.Month;
import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;
import ba.unsa.etf.rma.rma2020_16570.Model.TransactionsModel;
import ba.unsa.etf.rma.rma2020_16570.R;

public class TransactionListInteractor extends AsyncTask<String, Void, Void> implements ITransactionListInteractor {
    private String type;
    private String query;
    private JSONObject postData;
    private ArrayList<Transaction> transactions;
    private OnTransactionsFetched caller;
    private Context context;

    public interface  OnTransactionsFetched{
    public void onDone(ArrayList<Transaction> transactions);
    }

    public TransactionListInteractor(OnTransactionsFetched caller) { this.caller = caller;}
    public TransactionListInteractor(Context context, OnTransactionsFetched caller, String type, Transaction postData) {
        this.context = context;
        this.caller = caller;
        this.type = type;
        if (postData != null) {
            this.postData = convertTransactionToJSON(postData);
        }
    }

    public JSONObject convertTransactionToJSON(Transaction transaction){
        JSONObject object = new JSONObject();
        try {
            object.put("id", transaction.getId());
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
    protected Void doInBackground(String... strings) {
        /*try{
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
         */
        query = strings[0];
        try{
            URL url = new URL( context.getString(R.string.root)+"/account/"+context.getString(R.string.api_id).trim()+query.trim());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if(type.trim().equals("GET")){
                transactions =new ArrayList<Transaction>();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String object = convertStreamToString(in);
                JSONObject jo = new JSONObject(object);
                Log.e("Object",jo.toString());
                JSONArray results = jo.getJSONArray("transactions");
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
                    if(transactionType != Transaction.Type.INDIVIDUALINCOME && transactionType != Transaction.Type.REGULARINCOME){
                        itemDescription = transaction.getString("itemDescription");
                    }
                    if(transactionType == Transaction.Type.REGULARINCOME || transactionType == Transaction.Type.REGULARPAYMENT){
                        transactionInterval = transaction.getInt("transactionInterval");
                        endDate = transaction.getString("endDate");
                    }

                    transactions.add(new Transaction(id, date, title, amount, itemDescription, transactionInterval, endDate, transactionType));
                }
            }
            else if (type.equals("POST")){
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
                }
            }
            else if (type.equals("DELETE")){
                urlConnection.setRequestMethod(type);
            }
            else{

            }
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
