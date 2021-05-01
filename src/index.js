import { NativeModules } from 'react-native';
const DEFAULT_OPTIONS = {
  mediaType: 'photo',
  videoQuality: 'high',
  quality: 1,
  maxWidth: 0,
  maxHeight: 0,
  includeBase64: false,
  saveToPhotos: false,
  durationLimit: 0,
  cameraType: 'back'
};
export function launchCamera(options, callback) {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }

  NativeModules.ImagePickerManager.launchCamera(
    { ...DEFAULT_OPTIONS, ...options },
    callback,
  );
}

export function openCropper(options, callback) {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }

  NativeModules.ImagePickerManager.openCropper(
    { ...DEFAULT_OPTIONS, ...options },
    callback,
  );
}

export function launchImageLibrary(
  options,
  callback,
) {
  if (typeof callback !== 'function') {
    console.error("Send proper callback function, check API");
    return;
  }
  NativeModules.ImagePickerManager.launchImageLibrary(
    { ...DEFAULT_OPTIONS, ...options },
    callback,
  );
}
