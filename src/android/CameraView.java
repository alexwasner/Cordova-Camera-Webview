package com.wasner.cordova.camerawebview;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

public class CameraView extends ViewGroup {
    private static final String TAG = "CameraView";
    private Preview preview;
    private MediaRecorder recorder = null;
    private Camera camera = null;
    private int cameraId;
    private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String filePath = "";
    private Boolean flashOn = false;

    private boolean inPreview = false;
    private boolean viewIsAttached = false;
    private Camera.Size currentSize;


    public CameraView(Context context, String filePath) {
        super(context);
        this.filePath = filePath;
        preview = new PreviewView(this);
        addView(preview.getView());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int numChildren = getChildCount();
        if (changed && numChildren > 0) {
            int itemWidth = (r - l) / numChildren;
            for (int i = 0; i < numChildren; i++) {
                View v = getChildAt(i);
                v.layout(itemWidth * i, 0, (i + 1) * itemWidth, b - t);
            }
        }
    }

    public boolean isRecording() {
        return recorder != null;
    }
    public void startRecording() {
        if (isRecording()) {
            Log.d(TAG, "Already Recording!");
            return;
        }

        initCamera();

        if (camera == null)
            throw new NullPointerException("Cannot start recording, we don't have a camera!");

        Camera.Parameters cameraParameters = camera.getParameters();

        if(currentSize == null){
            setCameraParameters(camera, cameraParameters);
        }

        camera.stopPreview(); //Apparently helps with freezing issue on some Samsung devices.
        camera.unlock();

        try {
            recorder = new MediaRecorder();
            recorder.setCamera(camera);

            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            setProfile(recorder, cameraParameters);
            recorder.setOutputFile(filePath);
            recorder.setOrientationHint(90);

            preview.attach(recorder);
            recorder.prepare();
            recorder.start();
        }
        catch (IOException e) {
            recorder.reset();
            recorder.release();
            recorder=null;
            camera.lock();
            Log.e(TAG, "Could not start recording! MediaRecorder Error", e);
        }
    }

    public void stopRecording() throws IOException {
        Log.d(TAG, "stopRecording called");

        if(recorder != null) {
            MediaRecorder tempRecorder = recorder;
            recorder = null;
            tempRecorder.stop();
            tempRecorder.reset();
            tempRecorder.release();
            camera.lock();
        }
    }

    private void initCamera(){
        if (camera == null) {
            // Find the total number of cameras available
            int mNumberOfCameras = Camera.getNumberOfCameras();

            // Find the ID of the back-facing ("default") camera
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < mNumberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == cameraFacing) {
                    cameraId = i;
                    camera = Camera.open(i);
                    return;
                }
            }
            cameraId = -1;
            camera = null;
        }
    }

    void previewAvailable(){
        viewIsAttached = true;
        initCamera();
        if(camera != null) {
            try {
                preview.attach(camera);
            } catch (IOException e) {
                Log.e(TAG, "Unable to attach preview to camera!", e);
            }
        }
    }

    public void initPreview(int height, int width) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();

            setCameraParameters(camera, parameters);

            camera.startPreview();
            inPreview = true;
        }
    }

    private void setCameraParameters(Camera camera, Camera.Parameters parameters){
        currentSize = CameraHelper.getPreviewSize(parameters);
        parameters.setPreviewSize(currentSize.width, currentSize.height);

        parameters.setRotation(90);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            parameters.setRecordingHint(true);
        }

        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
    }

    private void setProfile(MediaRecorder mediaRecorder, Camera.Parameters parameters){
        CamcorderProfile profile;

        if(CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
            profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
        } else {
            profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
        }

        if(currentSize == null){
            currentSize = CameraHelper.getLowestResolution(parameters);
        }


        profile.videoFrameWidth = currentSize.width;
        profile.videoFrameHeight = currentSize.height;

        mediaRecorder.setProfile(profile);
    }


    public void startPreview(boolean startRecording){
        if(!inPreview) {
            if(preview != null && !viewIsAttached ) {
                preview.startRecordingWhenAvailable(startRecording);
                if(preview == null){
                    addView(preview.getView()); 
                }
            } else {
                previewAvailable();
                initPreview(getHeight(), getWidth());
                if (startRecording)
                    startRecording();
                inPreview = true;
            }
        }
    }

    public void stopPreview() {
        Log.d(TAG, "stopPreview called");
        if (inPreview && camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
        }

        if(camera != null) {
            camera.lock();
            camera.release();
            camera = null;
        }

        inPreview = false;
    }

    public void setCameraFacing(String cameraFace) {
        cameraFacing = ( cameraFace.equalsIgnoreCase("FRONT") ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK );
    }

    public void onResume() {
        if(preview == null)
            addView(preview.getView());
        viewIsAttached = true;
    }
    
    public void toggleFlash(){
        if(camera != null) {
            Camera.Parameters cp = camera.getParameters();
            if(flashOn){
                cp.setFlashMode(Parameters.FLASH_MODE_OFF);
            }
            else{
                cp.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }
            flashOn = !flashOn;
            camera.setParameters(cp);
        }
    }
    
    public void onPause() {
        try {
            Log.d(TAG, "onPause called");
            stopRecording();
            stopPreview();
            preview.startRecordingWhenAvailable(false);
//            Log.d(TAG, "removing View");
//            if(preview != null)
//                removeView(preview.getView());
//            viewIsAttached = false;
        } catch (IOException e) {
            Log.e(TAG, "Error in OnPause - Could not stop camera", e);
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        onPause();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean hasFlash() {
        initCamera();
        if (camera == null) {
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }
}
