package com.vikram.awsimageupload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private AmazonS3 s3;

    @Autowired
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void save(String path, String fileName, Optional<Map<String, String>> optionalMetaData,
                     InputStream inputStream) {

        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if(!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });

        try {
            PutObjectResult putObjectResult = s3.putObject(path, fileName, inputStream, metadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to store the file to s3", e);
        }

    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = this.s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file from s3");
        }
    }
}
