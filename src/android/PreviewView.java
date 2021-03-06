package com.wasner.cordova.camerawebview;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class PreviewView implements Preview, TextureView.SurfaceTextureListener {
    private static final String TAG = "CAMERA_WEBVIEW_TEXTURE";
    private final TextureView view;
    private final CameraView cameraView;
    private SurfaceTexture surface;
    private float opacity = 0.2f;
    private boolean startRecordingOnCreate = true;

    public PreviewView (CameraView cameraView) {
        Log.d(TAG, "Creating Texture Preview");
        this.cameraView = cameraView;
        view = new TextureView(cameraView.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setClickable(false);
        view.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(TAG, "Creating Texture Created");
        try {
            this.surface = surfaceTexture;
            cameraView.previewAvailable();
            cameraView.initPreview(height, width);


            if (startRecordingOnCreate) {
                cameraView.startRecording();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error start camera", ex);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "Surface Destroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void startRecordingWhenAvailable(boolean startOnCreate) { startRecordingOnCreate = startOnCreate; }

    @Override
    public void attach(Camera camera) throws IOException {
        camera.setPreviewTexture(surface);
    }

    @Override
    public void attach(MediaRecorder recorder) { }

    @Override
    public View getView() {
        Log.d(TAG, "getView called");
        return view;
    }
}
