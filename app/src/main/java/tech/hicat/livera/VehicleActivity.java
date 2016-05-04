package tech.hicat.livera;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.aaronhan.rtspclient.RtspClient;
import com.codebutler.android_websockets.WebSocketClient;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VehicleActivity extends Activity {

    public final static String TAG = "Vehicle Activity";

    private SurfaceView mSurfaceView;
    private RtspClient mRtspClient;
    private WebSocket mWebsocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle);
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String ip = preferences.getString("ip", "192.168.1.1");
        String rtspUrl = preferences.getString("rtsp_url", "rtsp://192.168.1.1/hicat.264");
        String websocketUrl = preferences.getString("rtsp_url", "ws://192.168.1.1:7681");
        boolean hardware_decoding = preferences.getBoolean("hardware_decoding", true);

        mRtspClient = new RtspClient(rtspUrl);
        mRtspClient.setSurfaceView(mSurfaceView);


        try {
            mWebsocket = new WebSocketFactory().createSocket(websocketUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Register a listener to receive web socket events.
        mWebsocket.addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                Log.d(TAG, "connected to the websocket");
            }

            public void onDisconnected(WebSocket websocket,
                                       WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame,
                                       boolean closedByServer) {

            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) {
                Log.d(TAG, "received: " + message);
            }
        });

        mWebsocket.connectAsynchronously();
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

        mWebsocket.disconnect();
    }

    public void onButtonClick(View view) {
        if (mWebsocket.isOpen()) {
            mWebsocket.sendText("ping\n");
        }
    }

}
