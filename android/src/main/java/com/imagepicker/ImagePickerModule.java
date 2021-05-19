package com.imagepicker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.util.UUID;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import static com.imagepicker.Utils.*;

@ReactModule(name = ImagePickerModule.NAME)
public class ImagePickerModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    static final String NAME = "ImagePickerManager";

    static final int REQUEST_LAUNCH_IMAGE_CAPTURE = 13001;
    static final int REQUEST_LAUNCH_IMAGE_LIBRARY = 13002;
    static final int REQUEST_LAUNCH_VIDEO_LIBRARY = 13003;
    static final int REQUEST_LAUNCH_VIDEO_CAPTURE = 13004;
    static final int REQUEST_LAUNCH_AUDIO_LIBRARY = 13005;

    private Uri fileUri;

    final ReactApplicationContext reactContext;

    Callback callback;

    Options options;
    Uri cameraCaptureURI;
    public ImagePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void launchCamera(final ReadableMap options, final Callback callback) {
        if (!isCameraAvailable(reactContext)) {
            callback.invoke(getErrorMap(errCameraUnavailable, null));
            return;
        }

        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke(getErrorMap(errOthers, "Activity error"));
            return;
        }

        if (!isCameraPermissionFulfilled(reactContext, currentActivity)) {
            callback.invoke(getErrorMap(errOthers, cameraPermissionDescription));
            return;
        }

        this.callback = callback;
        this.options = new Options(options);

        if (this.options.saveToPhotos && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !hasPermission(currentActivity)) {
            callback.invoke(getErrorMap(errPermission, null));
            return;
        }

        int requestCode;
        File file;
        Intent cameraIntent;

        if (this.options.pickVideo) {
            requestCode = REQUEST_LAUNCH_VIDEO_CAPTURE;
            cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, this.options.videoQuality);
            if (this.options.durationLimit > 0) {
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, this.options.durationLimit);
            }
            file = createFile(reactContext, "mp4");
            cameraCaptureURI = createUri(file, reactContext);
        } else {
            requestCode = REQUEST_LAUNCH_IMAGE_CAPTURE;
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            file = createFile(reactContext, "jpg");
            cameraCaptureURI = createUri(file, reactContext);
        }

        if (this.options.useFrontCamera) {
            setFrontCamera(cameraIntent);
        }

        fileUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraCaptureURI);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            currentActivity.startActivityForResult(cameraIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            callback.invoke(getErrorMap(errOthers, e.getMessage()));
            this.callback = null;
        }
    }

    @ReactMethod
    public void openCropper(final ReadableMap options, final Callback callback) {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke(getErrorMap(errOthers, "Activity error"));
            return;
        }
        this.callback = callback;
        this.options = new Options(options);
    if(this.options.path!=null){
        final Uri uri = Uri.parse(this.options.path);
        try {
            startCropping(currentActivity, uri);
        } catch (ActivityNotFoundException e) {
            callback.invoke(getErrorMap(errOthers, e.getMessage()));
            this.callback = null;
        }
        }else{
        callback.invoke(getErrorMap(errOthers, "Path not provided"));
            this.callback = null;
        }
    }

    @ReactMethod
    public void launchImageLibrary(final ReadableMap options, final Callback callback) {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            callback.invoke(getErrorMap(errOthers, "Activity error"));
            return;
        }

        this.callback = callback;
        this.options = new Options(options);

        int requestCode;
        Intent libraryIntent;
        if (this.options.pickVideo) {
            requestCode = REQUEST_LAUNCH_VIDEO_LIBRARY;
            libraryIntent = new Intent(Intent.ACTION_PICK);
            libraryIntent.setType("video/*");
        }
        else if (this.options.pickAudio) {
            requestCode = REQUEST_LAUNCH_AUDIO_LIBRARY;
            libraryIntent = new Intent(Intent.ACTION_PICK);
            libraryIntent.setType("audio/*");
        } else {
            requestCode = REQUEST_LAUNCH_IMAGE_LIBRARY;
            libraryIntent = new Intent(Intent.ACTION_PICK);
            libraryIntent.setType("image/*");
        }
        try {
            currentActivity.startActivityForResult(Intent.createChooser(libraryIntent, null), requestCode);
        } catch (ActivityNotFoundException e) {
            callback.invoke(getErrorMap(errOthers, e.getMessage()));
            this.callback = null;
        }
    }

    void onImageObtained(Uri uri) {
        Uri newUri = resizeImage(uri, reactContext, options);
        callback.invoke(getResponseMap(newUri, options, reactContext));
        
    }

    void onVideoObtained(Uri uri) {
        callback.invoke(getVideoResponseMap(uri, reactContext));
    }

    void configureCropperColors(UCrop.Options cropOptions) {
        if (options.cropperActiveWidgetColor != null) {
            cropOptions.setActiveControlsWidgetColor(Color.parseColor(options.cropperActiveWidgetColor));
        }

        if (options.cropperToolbarColor != null) {
            cropOptions.setToolbarColor(Color.parseColor(options.cropperToolbarColor));
        }

        if (options.cropperStatusBarColor != null) {
            cropOptions.setStatusBarColor(Color.parseColor(options.cropperStatusBarColor));
        }

        if (options.cropperToolbarWidgetColor != null) {
            cropOptions.setToolbarWidgetColor(Color.parseColor(options.cropperToolbarWidgetColor));
        }
    }


    ///method crop
    void startCropping(final Activity activity, final Uri uri) {
        UCrop.Options cropOptions = new UCrop.Options();
        if(options.forceJPG){
            cropOptions.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        }else{
        cropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG);
        }
        cropOptions.setCompressionQuality(100);
        cropOptions.setCircleDimmedLayer(options.cropperCircleOverlay);
        cropOptions.setFreeStyleCropEnabled(options.freeStyleCropEnabled);
        cropOptions.setShowCropGrid(options.showCropGuidelines);
        cropOptions.setShowCropFrame(options.showCropFrame);
        cropOptions.setHideBottomControls(options.hideBottomControls);

        if (options.cropperToolbarTitle != null) {
            cropOptions.setToolbarTitle(options.cropperToolbarTitle);
        }

        if (options.enableRotationGesture) {
            // UCropActivity.ALL = enable both rotation & scaling
            cropOptions.setAllowedGestures(
                    UCropActivity.ALL, // When 'scale'-tab active
                    UCropActivity.ALL, // When 'rotate'-tab active
                    UCropActivity.ALL  // When 'aspect ratio'-tab active
            );
        }

        if (!options.disableCropperColorSetters) {
            configureCropperColors(cropOptions);
        }
        fileUri=Uri.fromFile(createFile(reactContext, options.forceJPG ? "jpg" : "png"));
        UCrop uCrop = UCrop.of(uri, fileUri).withOptions(cropOptions);

        if (options.ratioX > 0 && options.ratioY > 0) {
            uCrop.withAspectRatio(options.ratioX, options.ratioY);
        }
        
        uCrop.start(activity);
    }

//TODO
    void onCropped(Activity activity, final int requestCode, final int resultCode, final Intent data) {
        if (data != null) {
            Uri resultUri = UCrop.getOutput(data);

            if (resultUri != null) {
                try {
                     onImageObtained(resultUri);
                } catch (Exception e) {
                    // some error occured
                    callback.invoke(getErrorMap(cropFailed,"Cropping failed"+e.getMessage()));
                }
            } else {
                //no image from crop
                callback.invoke(getErrorMap(cropFailed,"Cropper not returns uri"));

            }
        } else {
            //no data
            callback.invoke(getErrorMap(cropFailed,"Cropping failed"));
        }
    }

///test zone


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        // onActivityResult is called even when ActivityNotFoundException occurs
        if (!isValidRequestCode(requestCode) || (this.callback == null)) {
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            if (requestCode == REQUEST_LAUNCH_IMAGE_CAPTURE) {
                deleteFile(fileUri);
            }
            callback.invoke(getCancelMap());
            return;
        }

        switch (requestCode) {
            case REQUEST_LAUNCH_IMAGE_CAPTURE:
                if (options.saveToPhotos) {
                    saveToPublicDirectory(cameraCaptureURI, reactContext, "photo");
                }
                if(options.cropping){
                    startCropping(activity, fileUri);
                }else{
                onImageObtained(fileUri);
                 }
                break;

            case REQUEST_LAUNCH_IMAGE_LIBRARY:
                if(options.cropping){
                    startCropping(activity, data.getData());
                }else{
                    onImageObtained(getAppSpecificStorageUri(data.getData(), reactContext));
                }
                break;

            case REQUEST_LAUNCH_VIDEO_LIBRARY:
                onVideoObtained(getAppSpecificStorageUri(data.getData(), reactContext));
                break;
            case REQUEST_LAUNCH_AUDIO_LIBRARY:
                onVideoObtained(getAppSpecificStorageUri(data.getData(), reactContext));
                break;

            case REQUEST_LAUNCH_VIDEO_CAPTURE:
                if (options.saveToPhotos) {
                    saveToPublicDirectory(cameraCaptureURI, reactContext, "video");
                }
                onVideoObtained(fileUri);
                break;
            case UCrop.REQUEST_CROP:
                onCropped(activity, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) { }
}
