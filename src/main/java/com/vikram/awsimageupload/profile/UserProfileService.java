package com.vikram.awsimageupload.profile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.vikram.awsimageupload.bucket.BucketName;
import com.vikram.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private UserProfileDataAccessService userProfileDataAccessService;

    private FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {

        // 1. Check if the image is empty
        isFileEmpty(file);

        // 2. Check if the file is an image
        isImage(file);

        // 3. Check if the user exists in database
        UserProfile user = getUserProfileOrThrow(userProfileId);

        // 4. Grab some metadata from file if any
        Map<String, String> metadata = extractMetaData(file);

        // 5. Store the image into s3 and update database with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), userProfileId);
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path, fileName, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), userProfileId);
        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + " ]");
        }
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> {
                    return userProfile.getUserProfileId().equals(userProfileId);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

}
