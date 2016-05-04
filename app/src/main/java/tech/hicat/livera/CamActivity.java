package tech.hicat.livera;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceView;
import com.aaronhan.rtspclient.RtspClient;

public class CamActivity extends Activity {

    public final static String TAG = "Cam Activity";

    private SurfaceView mSurfaceView;
    private RtspClient mRtspClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cam);
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String ip = preferences.getString("ip", "192.168.1.1");
        String rtspUrl = preferences.getString("rtsp_url", "rtsp://192.168.1.1/hicat.264");
        boolean hardware_decoding = preferences.getBoolean("hardware_decoding", true);

        mRtspClient = new RtspClient(rtspUrl);
        mRtspClient.setSurfaceView(mSurfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRtspClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRtspClient.shutdown();
    }

}
