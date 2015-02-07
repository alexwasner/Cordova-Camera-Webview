package com.wasner.cordova.camera;

import org.json.JSONArray;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.content.pm.ActivityInfo;

public class CameraWebview extends CordovaPlugin{
    
  @Override
  public boolean execute(String action, JSONArray arguments, CallbackContext callbackContext) {
    if (action.equals("start")) {
      this.cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      callbackContext.success();
      return true;
            
    } else if (action.equals("stop")) {
      this.cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
      callbackContext.success();
      return true;
            
    } else {
      return false;
    }
  }
    
}
