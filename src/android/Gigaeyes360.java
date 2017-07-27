package kr.co.anylogic.gigaeyes360;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class Gigaeyes360 extends CordovaPlugin {

    private CallbackContext callbackContext;
    private static String TAG = "Gigaeyes360";
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            this.coolMethod(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("watchPanorama")) {
            this.callbackContext = callbackContext;
            String videoUrl = args.getString(0);
            String title = "";
            if(args.length()>1){
                title = args.getString(1);
            }
            Context context = cordova.getActivity().getApplicationContext();
            Intent intent = new Intent(context, GigaeyesActivity.class);
            intent.putExtra("VIDEO_URL", videoUrl);
            intent.putExtra("TITLE", title);
            Log.d(TAG,"Adicionaod extra: "+videoUrl);
            cordova.startActivityForResult(this, intent, 0);
            return true;
        }

        return false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG,"Result: "+resultCode);

        if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_OK)  {
            Log.d(TAG, "OK");
            callbackContext.success();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "ok");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } else {
            Log.d(TAG, "error");
            callbackContext.error("Failed");
        }
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
