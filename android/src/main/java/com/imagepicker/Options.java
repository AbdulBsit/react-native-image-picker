package com.imagepicker;

import com.facebook.react.bridge.ReadableMap;
import android.text.TextUtils;

public class Options {
    Boolean pickVideo = false;
    Boolean pickAudio = false;
    Boolean includeBase64;
    int videoQuality = 1;
    //
    int ratioX = 1;
    int ratioY = 1;
    int width =0;
    int height=0;
    Boolean forceJPG = false;
    String outputFormat = null;
    Boolean cropping = false;
    String cropperActiveWidgetColor = null;
    String cropperStatusBarColor = null;
    String cropperToolbarColor = null;
    String cropperToolbarTitle = null;
    String path = null;
    String cropperToolbarWidgetColor = null;
    Boolean cropperCircleOverlay = false;
    Boolean freeStyleCropEnabled = false;
    Boolean showCropGuidelines = true;
    Boolean showCropFrame = true;
    Boolean hideBottomControls = false;
    Boolean enableRotationGesture = false;
    Boolean disableCropperColorSetters = false;
    //
    int quality=1;
    int maxWidth=0;
    int maxHeight=0;
    Boolean saveToPhotos;
    int durationLimit;
    Boolean useFrontCamera = false;


    Options(ReadableMap options) {
        if (options.getString("mediaType").equals("video")) {
            pickVideo = true;
        }else if (options.getString("mediaType").equals("audio")) {
            pickAudio = true;
        }
        includeBase64 = options.getBoolean("includeBase64");
        
        String videoQualityString = options.getString("videoQuality");
        if(!TextUtils.isEmpty(videoQualityString) && !videoQualityString.toLowerCase().equals("high")) {
            videoQuality = 0;
        }

        if (options.getString("cameraType").equals("front")) {
            useFrontCamera = true;
        }

        quality = (int) (options.getDouble("quality") * 100);
        maxHeight = options.getInt("maxHeight");
        maxWidth = options.getInt("maxWidth");
        saveToPhotos = options.getBoolean("saveToPhotos");
        durationLimit = options.getInt("durationLimit");

        //cropping options
        width = options.hasKey("width") ? options.getInt("width") : 0;
        height = options.hasKey("height") ? options.getInt("height") : 0;
        ratioX = options.hasKey("ratioX") ? options.getInt("ratioX") : 0;
        ratioY = options.hasKey("ratioY") ? options.getInt("ratioY") : 0;
        cropping = options.hasKey("cropping") && options.getBoolean("cropping");
        forceJPG = options.hasKey("forceJPG") && options.getBoolean("forceJPG");
        cropperActiveWidgetColor = options.hasKey("cropperActiveWidgetColor") ? options.getString("cropperActiveWidgetColor") : null;
        cropperStatusBarColor = options.hasKey("cropperStatusBarColor") ? options.getString("cropperStatusBarColor") : null;
        cropperToolbarColor = options.hasKey("cropperToolbarColor") ? options.getString("cropperToolbarColor") : null;
        cropperToolbarTitle = options.hasKey("cropperToolbarTitle") ? options.getString("cropperToolbarTitle") : null;
        path = options.hasKey("path") ? options.getString("path") : null;
        outputFormat = options.hasKey("outputFormat") ? options.getString("outputFormat") : null;
        cropperToolbarWidgetColor = options.hasKey("cropperToolbarWidgetColor") ? options.getString("cropperToolbarWidgetColor") : null;
        cropperCircleOverlay = options.hasKey("cropperCircleOverlay") && options.getBoolean("cropperCircleOverlay");
        freeStyleCropEnabled = options.hasKey("freeStyleCropEnabled") && options.getBoolean("freeStyleCropEnabled");
        showCropGuidelines = !options.hasKey("showCropGuidelines") || options.getBoolean("showCropGuidelines");
        showCropFrame = !options.hasKey("showCropFrame") || options.getBoolean("showCropFrame");
        hideBottomControls = options.hasKey("hideBottomControls") && options.getBoolean("hideBottomControls");
        enableRotationGesture = options.hasKey("enableRotationGesture") && options.getBoolean("enableRotationGesture");
        disableCropperColorSetters = options.hasKey("disableCropperColorSetters") && options.getBoolean("disableCropperColorSetters");
        
    }
}
