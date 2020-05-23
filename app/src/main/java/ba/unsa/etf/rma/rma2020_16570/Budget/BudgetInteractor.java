package ba.unsa.etf.rma.rma2020_16570.Budget;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import ba.unsa.etf.rma.rma2020_16570.Model.Account;
import ba.unsa.etf.rma.rma2020_16570.R;

public class BudgetInteractor extends AsyncTask<String, Void, Void> {
    private String type;
    private JSONObject postData;
    private Context context;
    private Account account;
    private OnAccountFetched caller;

    public interface  OnAccountFetched{
        public void onDone(Account account);
    }

    public BudgetInteractor(Context context, String type, OnAccountFetched caller, Account postData) {
        this.context = context;
        this.type = type;
        this.caller = caller;
        if(postData != null){
            this.postData = convertAccountToJSON(postData);
        }
    }


    @Override
    protected Void doInBackground(String... strings) {
        String url1 = context.getString(R.string.root)+"/account/"+context.getString(R.string.api_id);
        if(type.equals("POST")){
            try {
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod(type);

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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else{
            account = new Account(0.0,0.0,0.0);
            try {
                URL url = new URL( url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String object = convertStreamToString(in);
                JSONObject jo = new JSONObject(object);

                Double budget = jo.getDouble("budget");
                Double monthLimit = jo.getDouble("monthLimit");
                Double totalLimit = jo.getDouble("totalLimit");

                account.setBudget(budget);
                account.setMonthLimit(monthLimit);
                account.setTotalLimit(totalLimit);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(type.equals("GET")){
            caller.onDone(account);
        }
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

    public JSONObject convertAccountToJSON(Account account) {
        JSONObject object = new JSONObject();

        try {
            object.put("budget", account.getBudget());
            object.put("totalLimit", account.getTotalLimit());
            object.put("monthLimit", account.getMonthLimit());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
