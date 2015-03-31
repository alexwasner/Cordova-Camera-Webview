package com.wasner.cordova.camerawebview;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

public class CustomWebview extends WebView {

    private OnKeyDownListener listener;
    
  public CustomWebview(Context context) {
    super(context);
  }
  
    public void setOnKeyDownListener(OnKeyDownListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(listener != null) {
            listener.onKeyDown(keyCode, event);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public interface OnKeyDownListener {
        public void onKeyDown(int keyCode, KeyEvent event);
    }
}