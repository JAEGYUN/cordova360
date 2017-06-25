package kr.co.anylogic.gigaeyes360;

import android.app.Activity;

// import android.content.pm.ActivityInfo;
// import android.content.res.Configuration;
// import android.graphics.Bitmap;
// import android.media.AudioManager;
// import android.media.MediaMetadataRetriever;
// import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.DisplayMetrics;
// import android.view.SurfaceHolder;
// import android.view.SurfaceView;
// import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
// import android.view.Window;
// import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
// import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class gigaeyesActivity extends Activity {

    private VRVideoView mVideoView = null;
    private RelativeLayout mRelativeLayout = null;
    private String videoSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            return;
        }

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            videoSrc = extras.getString("VIDEO_URL");
        } else {
            finishWithError();
        }

        Log.d("FLP","gigaeyesActivity videoSrc"+videoSrc);
        Toast.makeText(getApplicationContext(),"gigaeyesActivity videoSrc:"+videoSrc,Toast.LENGTH_SHORT).show();

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // // create the linear layout to hold our video
        // layout = new LinearLayout(this);
        // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // layout.setLayoutParams(layoutParams);

        // // add the surfaceView with the current video
        // createVideoView();

        // // add to the view
        // setContentView(layout);


        setContentView(R.layout.activity_main);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.main_relative_layout);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width, height);

        params.leftMargin = 0;
        params.topMargin = 0;

        // Uri uri = Uri.parse("rtsp://211.54.3.138:1935/client_test/dahua360-1st-half-stream2nd.stream");
        Uri uri = Uri.parse(videoSrc);
        mVideoView = new VRVideoView(this, uri);

        mRelativeLayout.addView(mVideoView, params);
    }


    public boolean onTouchEvent(MotionEvent event) {
        if(mVideoView != null){
            mVideoView.setTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//     private void createVideoView() {
//         surfaceView = new SurfaceView(getApplicationContext());
//         surfaceView.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Toast.makeText(getApplicationContext(),"not supported function",Toast.LENGTH_SHORT).show();
//             }
//         });
//         surfaceHolder = surfaceView.getHolder();
//         surfaceHolder.addCallback(this);

//         layout.addView(surfaceView);
//     }

//     @Override
//     public void surfaceCreated(SurfaceHolder holder) {
//         try {
//             // Surface ready, add the mediaPlayer to it
//             mediaPlayer = new MediaPlayer();

//             // Setting up media player
//             mediaPlayer.setDisplay(surfaceHolder);
//             mediaPlayer.setDataSource(videoSrc);
//             mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//             mediaPlayer.setVolume(0f,0f);
//             mediaPlayer.setScreenOnWhilePlaying(true);
//             mediaPlayer.setOnPreparedListener(this);
//             mediaPlayer.setOnErrorListener(this);

//             mediaPlayer.prepareAsync();
//         } catch (IOException e) {
//             Toast.makeText(getApplicationContext(), "can not load 360 video",Toast.LENGTH_SHORT).show();
//             e.printStackTrace();
//             finishWithError();
//         }

//     }

//     @Override
//     public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
// //        if (mediaPlayer.isPlaying()) {
// //            mediaPlayer.stop();
// //            mediaPlayer.release();
// //        } else {
// //            mediaPlayer.start();
// //        }
//     }

//     @Override
//     public void surfaceDestroyed(SurfaceHolder holder) {
//         if (mediaPlayer.isPlaying()) {
//             mediaPlayer.stop();
//             mediaPlayer.release();
//         }
//     }



//     @Override
//     public void onPrepared(MediaPlayer mp) {
//         Log.d("FLP", "onPrepared fired");
//         mediaPlayer.start();
//     }


//     @Override
//     public boolean onError(MediaPlayer mp, int what, int extra) {
//         Log.d("FLP", "onError fired");
//         Toast.makeText(getApplicationContext(), "can not play 360 video", Toast.LENGTH_SHORT).show();
//         finishWithError();
//         return false;
//     }

//     @Override
//     public void onBackPressed() {
// //        super.onBackPressed();
//         Log.d("FLP", "DO NOTHING");
//         setResult(Activity.RESULT_OK);
//         finish();
//     }

    private void finishWithError() {
        setResult(100);
        finish();
    }


}
