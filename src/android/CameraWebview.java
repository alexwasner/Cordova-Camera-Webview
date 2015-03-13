package com.wasner.cordova.camerawebview;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class CameraWebview extends CordovaPlugin {
    private static final String ACTION_START_RECORDING = "start";
    private static final String TAG = "CAMERA_WEBVIEW";
    private String FILE_PATH = "";
    private String FILE_NAME = "";
    private CameraWebview self;
    private CameraView cameraView;
    private WebView webViewWrapper;
    private RelativeLayout relativeLayout;
    private boolean hasFlash = false;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        FILE_PATH = cordova.getActivity().getFilesDir().toString() + "/";
    }

    @Override
    public boolean execute(String action, JSONArray args,
            final CallbackContext callbackContext) throws JSONException {
        try {
            Log.d(TAG, "ACTION: " + action);
            cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (ACTION_START_RECORDING.equals(action)) {
                FILE_NAME = args.getString(0);
                final String CAMERA_FACE = args.getString(1);

                if (cameraView == null) {
                    cameraView = new CameraView(cordova.getActivity(),
                            getFilePath());
                    cameraView.setCameraFacing(CAMERA_FACE);
                    self = this;
                    relativeLayout = new RelativeLayout(cordova.getActivity());
                    relativeLayout.setBackgroundColor(Color.BLACK);
                    hasFlash = cameraView.hasFlash();
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            webView.setKeepScreenOn(true);
                            cordova.getActivity().addContentView(
                                    relativeLayout,
                                    new ViewGroup.LayoutParams(webView
                                            .getWidth(), webView
                                            .getHeight()));
                            try {
                                webViewWrapper = new WebView((Context) cordova
                                        .getActivity());
                                webViewWrapper.getSettings()
                                        .setJavaScriptEnabled(true);
                                webViewWrapper
                                        .setBackgroundColor(Color.TRANSPARENT);
                                webViewWrapper.setLayerType(
                                        View.LAYER_TYPE_SOFTWARE, null);
                                if(hasFlash){
                                    webViewWrapper.setWebViewClient(new WebViewClient(){
                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            String javascript="javascript: document.getElementById('view-root').setAttribute('class', 'has-flash');";
                                            view.loadUrl(javascript);
                                        }
                                    });
                                }
                                
                                else{
                                    webViewWrapper.setWebViewClient(new WebViewClient());
                                }
                                

                                webViewWrapper.loadUrl("file:///android_asset/www/webview.html");
                                webViewWrapper.requestFocus();
                                webViewWrapper.requestFocusFromTouch();
                                webViewWrapper.getSettings()
                                        .setSupportMultipleWindows(false);
                                webViewWrapper.addJavascriptInterface(
                                        new JSInterface(self), "androidbridge");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    WebView.setWebContentsDebuggingEnabled(true);
                                }

                                relativeLayout.setY(webView.getHeight());
                                relativeLayout.addView(
                                            cameraView,
                                            new ViewGroup.LayoutParams(webView
                                                    .getWidth(), webView
                                                    .getHeight()));
                                    
                                relativeLayout.addView(
                                        webViewWrapper,
                                        new ViewGroup.LayoutParams(webView
                                                .getWidth(), webView
                                                .getHeight()));
                                
                                relativeLayout.animate().y(0).setDuration(400);
                            } catch (Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": "
                                        + e.getMessage());
                            }
                            
                              
                        }
                    });
                } else {
                    cordova.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cameraView.setCameraFacing(CAMERA_FACE);
                                cameraView.setFilePath(getFilePath());
                                cameraView.startPreview(true);

                                webViewWrapper.loadUrl("file:///android_asset/www/webview.html");
                                webViewWrapper.requestFocus();
                                webViewWrapper.requestFocusFromTouch();

                                relativeLayout.animate().y(0).setDuration(400).setListener(new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if(webViewWrapper.getParent() == null){
                                            relativeLayout.addView(
                                                    webViewWrapper,
                                                    new ViewGroup.LayoutParams(webView
                                                            .getWidth(), webView
                                                            .getHeight())); 
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(TAG, "Error during preview create", e);
                                callbackContext.error(TAG + ": "
                                        + e.getMessage());
                            }
                        }
                    });
                }
                return true;
            }

            callbackContext.error(TAG + ": INVALID ACTION");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage(), e);
            callbackContext.error(TAG + ": " + e.getMessage());
            return false;
        }
    }
    
    public void toggleFlash(){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraView != null) {
                    cameraView.toggleFlash();
                }
            }
        });
    }
    
    public void close() {
        if(isTablet()){
            cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraView != null) {
                    cameraView.onPause();
                }
                relativeLayout.animate().y(webView.getHeight()).setDuration(400).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        relativeLayout.removeView(webViewWrapper);
                    }
                });
//              
            }
        });

    }
    
    public boolean isTablet(){
        WindowManager wm = (WindowManager) cordova.getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int density = cordova.getActivity().getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160;
        int width = metrics.widthPixels / density;
        int height = metrics.heightPixels / density;
        if(width > height && width > 800 || width < height && height > 800){
            return true;
        }
        return false;

    }

    private String getFilePath() {
        return FILE_PATH + getNextFileName() + ".mp4";
    }

    private String getNextFileName() {
        int i = 1;
        String tmpFileName = FILE_NAME;
        while (new File(FILE_PATH + tmpFileName + ".mp4").exists()) {
            tmpFileName = FILE_NAME + '_' + i;
            i++;
        }
        return tmpFileName;
    }

    // Plugin Method Overrides
    @Override
    public void onPause(boolean multitasking) {
        if (cameraView != null)
            cameraView.onPause();
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (cameraView != null)
            cameraView.onResume();
    }

    @Override
    public void onDestroy() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (cameraView != null) {
                    cameraView.onDestroy();
                }
                if (webViewWrapper != null) {
                    webViewWrapper.destroy();
                }
            }
        });

        super.onDestroy();
    }

}