package ru.kaelth4s.tests.api.dto;

public record ApiTestData(
        String uploadTextFileDiskPath,
        String changedFileExtension,
        String uploadImageFileResourcePath
) {
    public static ApiTestData from(ApiTestData json) {
        return new ApiTestData(json.uploadTextFileDiskPath, json.changedFileExtension, json.uploadImageFileResourcePath);
    }
}