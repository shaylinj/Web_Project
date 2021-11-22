package za.ac.nwu.aws;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface AWSServices
{
    void save(String path, String fileName, Optional<Map<String,String>> optionalMetaData, InputStream inputStream);
    byte[] download(String path, String key);
    void deletePhotoFromFolder(String bucketName, String fileName);
    void deleteUserFolder(String bucketName, String path);
    void sharePhoto(String bucketName, String toBucketName, String key);
}

