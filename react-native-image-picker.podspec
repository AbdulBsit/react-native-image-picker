require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.platform     = :ios, "9.0"

  s.source       = { :git => "https://github.com/AbdulBsit/react-native-image-picker.git", :tag => "v#{s.version}" }
  s.source_files  = "ios/*.{h,m}"

  s.dependency 'React-Core'
  s.dependency 'React-RCTImage'
  s.dependency 'TOCropViewController'

  s.subspec 'QBImagePickerController' do |qb|
    qb.name             = "QBImagePickerController"
    qb.source_files     = "ios/QBImagePicker/QBImagePicker/*.{h,m}"
    qb.exclude_files    = "ios/QBImagePicker/QBImagePicker/QBImagePicker.h"
    qb.resource_bundles = { "QBImagePicker" => "ios/QBImagePicker/QBImagePicker/*.{lproj,storyboard}" }
    qb.requires_arc     = true
    qb.frameworks       = "Photos"
  end
end
