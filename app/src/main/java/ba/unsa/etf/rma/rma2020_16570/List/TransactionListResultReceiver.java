package ba.unsa.etf.rma.rma2020_16570.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class TransactionListResultReceiver extends ResultReceiver {
    private Receiver transactionReceiver;
    public void setTransactionReceiver(Receiver receiver){
        transactionReceiver = receiver;
    }
    public interface Receiver{
        void onResultsReceived(int resultCode, Bundle resultData);
    }
    public TransactionListResultReceiver(Handler handler) {
        super(handler);
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (transactionReceiver != null) {
            transactionReceiver.onResultsReceived(resultCode, resultData);
        }
    }
}
