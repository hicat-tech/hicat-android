package tech.hicat.livera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    public final static String TAG = "Livera Main Activity";

    private boolean use_default_player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        PreferenceManager.setDefaultValues(this, R.xml.settings, true);

        if (!isNetworkConnected()) {
            Log.v(TAG, "network is not available.");

            Toast.makeText(this, ("network is not available."), Toast.LENGTH_SHORT).show();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        use_default_player = preferences.getBoolean("player", true);
    }

    public void onRobotButtonClick(View view) {
        Intent intent;
        if (use_default_player) {
            intent = new Intent(this, RobotActivity.class);
        } else {
            intent = new Intent(this, VehicleActivity.class);
        }
        startActivity(intent);
    }

    public void onSettingsButtonClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onCamButtonClick(View view) {
        Intent intent = new Intent(this, CamActivity.class);
        startActivity(intent);
    }

    public void onDashboardButtonClick(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return ((networkInfo != null && networkInfo.isConnected()) ? true : false);
    }
}