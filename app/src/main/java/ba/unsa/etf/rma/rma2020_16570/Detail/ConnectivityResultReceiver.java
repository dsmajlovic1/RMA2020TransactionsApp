package ba.unsa.etf.rma.rma2020_16570.Detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import ba.unsa.etf.rma.rma2020_16570.List.TransactionListResultReceiver;

public class ConnectivityResultReceiver extends ResultReceiver {
    private Receiver connectivityReceiver;
    public void setTransactionReceiver(ConnectivityResultReceiver.Receiver receiver){
        connectivityReceiver = receiver;
    }
    public interface Receiver{
        void onResultsReceived(int resultCode, Bundle resultData);
    }
    public ConnectivityResultReceiver(Handler handler) {
        super(handler);
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (connectivityReceiver != null) {
            connectivityReceiver.onResultsReceived(resultCode, resultData);
        }
    }
}
