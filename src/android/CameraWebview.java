package com.wasner.cordova.camerawebview;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class CameraWebview extends CordovaPlugin{
    private static final String ACTION_START_RECORDING = "start";
    private static final String ACTION_STOP_RECORDING = "stop";
    private static final String TAG = "CAMERA_WEBVIEW";
    private String FILE_PATH = "";
    private String FILE_NAME = "";
    private CameraView cameraView;
    
    private RelativeLayout relativeLayout;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        FILE_PATH = cordova.getActivity().getFilesDir().toString() + "/";
    }
    
    
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        try {
            Log.d(TAG, "ACTION: " + action);
            
            if(ACTION_START_RECORDING.equals(action)) {
                FILE_NAME = args.getString(0);
                String CAMERA_FACE = args.getString(1);
                
                if(cameraView == null) {
                    cameraView = new CameraView(cordova.getActivity(), getFilePath());
                    cameraView.setCameraFacing(CAMERA_FACE);
                    
                    //NOTE: Now wrapping view in relative layout because GT-I9300 testing
                    //      the overlay required wrapping for setAlpha to work.
                    relativeLayout = new RelativeLayout(cordova.getActivity());
                    
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.setKeepScreenOn(true);
                            try {
                              relativeLayout.addView(cameraView, new ViewGroup.LayoutParams(webView.getWidth(), webView.getHeight()));
                              cordova.getActivity().addContentView(relativeLayout, new ViewGroup.LayoutParams(webView.getWidth(), webView.getHeight()));
                            } catch(Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": " + e.getMessage());
                            }
                        }
                    });
                } else {
                    cameraView.setCameraFacing(CAMERA_FACE);
                    cameraView.setFilePath(getFilePath());
                    
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cameraView.startPreview(true);
                            } catch(Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": " + e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }
            
            if(ACTION_STOP_RECORDING.equals(action)) {
                if(cameraView != null) {
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(cameraView != null)
                                cameraView.onPause();
                        }
                    });
                }
                return true;
            }
            
            callbackContext.error(TAG + ": INVALID ACTION");
            return false;
        } catch(Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage(), e);
            callbackContext.error(TAG + ": " + e.getMessage());
            return false;
        }
    }
    
    private String getFilePath(){
        return  FILE_PATH + getNextFileName() + ".mp4";
    }
    
    private String getNextFileName(){
        int i=1;
        String tmpFileName = FILE_NAME;
        while(new File(FILE_PATH + tmpFileName + ".mp4").exists()) {
            tmpFileName = FILE_NAME + '_' + i;
            i++;
        }
        return tmpFileName;
    }
    
    //Plugin Method Overrides
    @Override
    public void onPause(boolean multitasking) {
        if(cameraView != null)
            cameraView.onPause();
        super.onPause(multitasking);
    }
    
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if(cameraView != null)
            cameraView.onResume();
    }
    
    @Override
    public void onDestroy() {
        if(cameraView != null)
            cameraView.onDestroy();
        super.onDestroy();
    }
    
}