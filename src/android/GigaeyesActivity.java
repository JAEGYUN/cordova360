package kr.co.anylogic.gigaeyes360;

import android.app.Activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.DisplayMetrics;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.videolan.libvlc.MediaPlayer;

import java.io.IOException;
import java.lang.ref.WeakReference;

import kr.co.anylogic.joystick.JoystickEvents;

public class GigaeyesActivity extends Activity {

    private VRVideoView mVideoView = null;
    private RelativeLayout mRelativeLayout = null;
	private String videoSrc;
    private String cctvName;
    private String packageName;
    private Resources res;
    private static ImageView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.packageName = getApplication().getPackageName();
        this.res = getApplication().getResources();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            return;
        }

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            this.videoSrc = extras.getString("VIDEO_URL");
            this.cctvName = extras.getString("TITLE");
        } else {
            finishWithError();
        }

        Log.d("FLP","gigaeyesActivity videoSrc"+this.videoSrc);
       
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mRelativeLayout = new RelativeLayout(this);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( width, height);

        Uri uri = Uri.parse(videoSrc);
        mVideoView = new VRVideoView(this, uri);

        this.setContentView(mRelativeLayout);
        mRelativeLayout.addView(mVideoView, params);

        loadingView = new ImageView(this);

        RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(100,100);

        vParams.setMargins(width/2-50,height/2-50,0,0);


        mRelativeLayout.addView(loadingView);
        loadingView.setLayoutParams(vParams);
        loadingView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        loadingView.setMaxHeight(100);
        loadingView.setMaxWidth(100);

        int loading_gif = res.getIdentifier("loading", "raw", this.packageName);


        Glide.with(this).load(loading_gif).into(loadingView);
    }


    public boolean onTouchEvent(MotionEvent event) {
        if(mVideoView != null){
            mVideoView.setTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    private void finishWithError() {
        setResult(100);
        finish();
    }

    public static void hideLoading(){
        GigaeyesActivity.loadingView.setVisibility(View.INVISIBLE);
    }



}
