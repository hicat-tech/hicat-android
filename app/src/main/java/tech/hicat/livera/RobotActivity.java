package tech.hicat.livera;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.VideoView;

import com.aaronhan.rtspclient.RtspClient;
import com.erz.joysticklibrary.JoyStick;
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

public class RobotActivity extends Activity {

    public final static String TAG = "Robot Activity";

    private VideoView mVideoView;
    private WebSocket mWebsocket;
    private long mLeftPower;
    private long mRightPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLeftPower = 0;
        mRightPower = 0;

        setContentView(R.layout.activity_robot);
        mVideoView = (VideoView)findViewById(R.id.videoView);
        JoyStick joyStick = (JoyStick) findViewById(R.id.joyStick);
        joyStick.setListener(new JoyStick.JoyStickListener() {
            @Override
            public void onMove(JoyStick joyStick, double angle, double power) {
                long left = 0;
                long right = 0;

                power = power * 255 / 100;

                if (angle > Math.PI / 2.0) {
                    right = Math.round(power);
                    left = Math.round(power - power * 2 * Math.sin(angle - Math.PI / 2.0));
                } else if (angle > 0) {
                    left = Math.round(power);
                    right = Math.round(power - power * 2 * Math.cos(angle));
                } else if (angle > -Math.PI / 2.0) {
                    right = -Math.round(power);
                    left = Math.round(power + power * 2 * Math.sin(angle));
                } else {
                    left = -Math.round(power);
                    right = Math.round(power - power * 2 * Math.cos(angle + Math.PI / 2.0));
                }

                if ((left != 0) && (right != 0)) {
                    if (Math.abs(left - mLeftPower) < 10 && Math.abs(right - mRightPower) < 10) {
                        return;
                    }
                }

                Log.v(TAG, "power:" + power + ", angle:" + angle);

                mLeftPower = left;
                mRightPower = right;

                String msg = "motor " + right + " " + left + "\n";
                if (mWebsocket.isOpen()) {
                    mWebsocket.sendText(msg);
                } else {
//                    mWebsocket.connectAsynchronously();
                }

                Log.v(TAG, msg);
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String streamUrl = preferences.getString("stream_url", "rtsp://192.168.1.1/hicat.264");
        String websocketUrl = preferences.getString("websocket_url", "ws://192.168.1.1:7681");
        boolean manual = preferences.getBoolean("manual", false);
        
        mVideoView.setVideoPath(streamUrl);

        try {
            mWebsocket = new WebSocketFactory().createSocket(websocketUrl);

            // Register a listener to receive web socket events.
            mWebsocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                    Log.d(TAG, "connected to the websocket");
                    websocket.sendText("motor 0 0");
                }

                public void onDisconnected(WebSocket websocket,
                                           WebSocketFrame serverCloseFrame,
                                           WebSocketFrame clientCloseFrame,
                                           boolean closedByServer) {
                    Log.d(TAG, "disconnected with the websocket");
                    mWebsocket.connectAsynchronously();
                }

                @Override
                public void onTextMessage(WebSocket websocket, String message) {
                    Log.d(TAG, "received: " + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mWebsocket.isOpen()) {
            mWebsocket.connectAsynchronously();
        }

        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        mWebsocket.disconnect();
    }

    public void onButtonClick(View view) {
        String msg = "unknown\n";
        switch (view.getId()) {
            case R.id.aButton:
                msg = "camera down\n";
                break;
            case R.id.bButton:
                msg = "camera up\n";
                break;
            case R.id.cButton:
                msg = "laser\n";
                break;
            default:
                Log.v(TAG, "Unknown button is clicked");
        }

        if (mWebsocket.isOpen()) {
            mWebsocket.sendText(msg);
        } else {
//            mWebsocket.connectAsynchronously();
        }
    }

}
