package kr.co.anylogic.gigaeyes360;

import android.app.Activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.DisplayMetrics;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

public class GigaeyesActivity extends Activity {

    private VRVideoView mVideoView = null;
    private RelativeLayout mRelativeLayout = null;
	private String videoSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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

//        setContentView(R.layout.activity_main);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mRelativeLayout = new RelativeLayout(this);

//                (RelativeLayout) findViewById(R.id.main_relative_layout);

//        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

//        params.leftMargin = 0;
//        params.topMargin = 0;

        mRelativeLayout.setLayoutParams(params);

        Uri uri = Uri.parse(videoSrc);
        mVideoView = new VRVideoView(this, uri);

        this.setContentView(mRelativeLayout);
//        mRelativeLayout.addView(mVideoView, params);
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
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    private void finishWithError() {
        setResult(100);
        finish();
    }


}
