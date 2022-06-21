package com.rootscare.data.model.api.response.addmedicallrecords

import com.theartofdev.edmodo.cropper.CropImage
import java.io.File

class AddlabTestImageSelectionModel {
    var fileName: String? = null
    var fileNameAsOriginal: String? = null
    var filePath: String? = null
    var type: String? = null
    var file: File? = null
    var rawFileName: String? = null
    var imageNameGivenFromApiAfterImageUpload: String? = null
    var imageDataFromCropLibrary: CropImage.ActivityResult? = null

}
