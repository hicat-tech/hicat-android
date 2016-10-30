package tech.hicat.livera;


import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.VideoView;

import com.aaronhan.rtspclient.RtspClient;

public class CamActivity extends Activity {

    public final static String TAG = "Cam Activity";

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cam);
        mVideoView = (VideoView) findViewById(R.id.videoView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String streamUrl = preferences.getString("stream_url", "rtsp://192.168.1.1/hicat.264");

        mVideoView.setVideoPath(streamUrl);
        mVideoView.setKeepScreenOn(true);
//        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//
//            @Override
//            public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
//                switch (arg1) {
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        //Begin buffer, pause playing
//                        if (arg0.isPlaying()) {
//                            arg0.stop();
//                            needResume = true;
//                        }
//                        break;
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                        //The buffering is done, resume playing
//                        if (needResume)
//                            arg0.start();
//                        break;
//                }
//                return true;
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "onResume()");
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean needResume;



}
