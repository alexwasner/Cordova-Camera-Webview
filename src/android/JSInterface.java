package com.wasner.cordova.camerawebview;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSInterface {
  Context mContext;
  CameraWebview mCameraWebview;

      /** Instantiate the interface and set the context */
      JSInterface(CameraWebview cameraWebview) {
          mCameraWebview = cameraWebview;
      }

      @JavascriptInterface   // must be added for API 17 or higher
      public void close() {
        mCameraWebview.close();
      }
      
      @JavascriptInterface   // must be added for API 17 or higher
      public void toggleFlash() {
        mCameraWebview.toggleFlash();
      }
  }