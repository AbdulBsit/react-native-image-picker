import { NativeModules, Platform } from 'react-native';

const DEFAULT_OPTIONS: commonOptions = {
  mediaType: 'photo',
  quality: 1,
  forcejpg: false,
};
interface commonOptions {
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
interface commonResponse {
  didCancel?: boolean,
  errorCode?: any,
  errorMessage?: string,
  uri?: string,
  fileName?: string
  fileSize?: number,
  width?: number,
  height?: number
}

export async function openCropper(options: commonOptions, callback: (value: commonResponse) => void) {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }
  if (Platform.OS === 'android') {
    NativeModules.ImagePickerManager.openCropper(
      { ...DEFAULT_OPTIONS, ...options },
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
      callback({ didCancel: true, errorCode: true, errorMessage: err?.message })
    }

  }
}

export async function launchImageLibrary(
  options: commonOptions,
  callback: (value: commonResponse) => void,
) {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }
  if (Platform.OS === 'android') {
    NativeModules.ImagePickerManager.launchImageLibrary(
      { ...DEFAULT_OPTIONS, ...options },
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
      callback({ didCancel: true, errorCode: true, errorMessage: err?.message })
    }
  }
}
