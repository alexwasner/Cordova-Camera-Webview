package com.wasner.cordova.camerawebview;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.View;

import java.io.IOException;

interface Preview {

    void startRecordingWhenAvailable(boolean startOnCreate);

    void attach(Camera camera) throws IOException;

    void attach(MediaRecorder recorder);

    View getView();
}

