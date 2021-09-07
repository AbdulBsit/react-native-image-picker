export interface commonOptions {
    mediaType: 'photo';
    cropping?: boolean;
    cropperCircleOverlay?: boolean;
    quality: 0 | 0.1 | 0.2 | 0.3 | 0.4 | 0.5 | 0.6 | 0.7 | 0.8 | 0.9 | 1;
    outputFormat?: 'png' | 'jpg';
    forcejpg?: boolean;
    width?: number;
    height?: number;
    ratioX?: number;
    ratioY?: number;
    path?: any;
    cropperStatusBarColor?: string;
    freeStyleCropEnabled?: boolean;
}
export interface commonResponse {
    didCancel?: boolean;
    errorCode?: any;
    errorMessage?: string;
    uri?: string;
    fileName?: string;
    fileSize?: number;
    width?: number;
    height?: number;
}
export declare function openCropper(options: commonOptions, callback: (value: commonResponse) => void): Promise<void>;
export declare const launchImageLibrary: (options: commonOptions, callback: (value: commonResponse) => void) => Promise<void>;
