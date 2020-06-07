package ba.unsa.etf.rma.rma2020_16570.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import ba.unsa.etf.rma.rma2020_16570.View.MainActivity;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    public ConnectionChange mainActivity;

    public void setMainActivity(ConnectionChange mainActivity) {
        this.mainActivity = mainActivity;
    }

    public interface ConnectionChange{
        void changeConnectionStatus(Boolean connected);
        void changeWorkMode(Boolean connected);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            Toast toast = Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT);
            toast.show();
            mainActivity.changeConnectionStatus(false);

        }
        else {
            Toast toast = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
            toast.show();
            mainActivity.changeConnectionStatus(true);
        }
    }
}
