import { NativeModules, Platform } from 'react-native';

const DEFAULT_OPTIONS: commonOptions = {
  mediaType: 'photo',
  quality: 1,
  forcejpg: false,
};
const DEFAULT_ANDROID_OPTIONS = {
  videoQuality: 'high',
  maxWidth: 0,
  maxHeight: 0,
  includeBase64: false,
  saveToPhotos: false,
  durationLimit: 0,
  cameraType: 'back',
}
export interface commonOptions {
  mediaType: 'photo'
  cropping?: boolean
  cropperCircleOverlay?: boolean,
  quality: 0 | 0.1 | 0.2 | 0.3 | 0.4 | 0.5 | 0.6 | 0.7 | 0.8 | 0.9 | 1
  outputFormat?: 'png' | 'jpg',
  forcejpg?: boolean,
  width?: number
  height?: number
  ratioX?: number
  ratioY?: number,
  path?: any
  cropperStatusBarColor?: string
  freeStyleCropEnabled?: boolean
}
export interface commonResponse {
  didCancel?: boolean,
  errorCode?: any,
  errorMessage?: string,
  uri?: string,
  fileName?: string
  fileSize?: number,
  width?: number,
  height?: number
}

export const openCropper = async (options: commonOptions, callback: (value: commonResponse) => void) => {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }
  if (Platform.OS === 'android') {
    NativeModules.ImagePickerManager.openCropper(
      {
        ...DEFAULT_ANDROID_OPTIONS, ...DEFAULT_OPTIONS, ...options
      },
      callback,
    );
    return;
  } else if (Platform.OS === 'ios') {
    try {
      const result = await NativeModules.ImageCropPicker.openCropper({
        ...options,
        ...(options.outputFormat === 'png' ? { forcejpg: false } : { forcejpg: true })
      })
      callback({
        ...result,
        fileName: result?.filename, fileSize: result?.size, height: result?.height, width: result?.width,
        uri: result?.path,
      })
    } catch (err) {
      callback({ didCancel: true, errorCode: true, errorMessage: (err as Error)?.message })
    }

  }
}

export const launchImageLibrary = async (
  options: commonOptions,
  callback: (value: commonResponse) => void,
) => {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }
  if (Platform.OS === 'android') {
    NativeModules.ImagePickerManager.launchImageLibrary(
      {
        ...DEFAULT_ANDROID_OPTIONS,
        ...DEFAULT_OPTIONS, ...options
      },
      callback,
    );
    return;
  } else if (Platform.OS === 'ios') {
    try {
      const result = await NativeModules.ImageCropPicker.openPicker({
        ...options,
        ...(options.outputFormat === 'png' ? { forcejpg: false } : { forcejpg: true })

      })
      callback({
        ...result,
        fileName: result?.filename, fileSize: result?.size, height: result?.height, width: result?.width,
        uri: result?.path,
      })
    } catch (err) {
      callback({ didCancel: true, errorCode: true, errorMessage: (err as Error).message })
    }
  }
}
