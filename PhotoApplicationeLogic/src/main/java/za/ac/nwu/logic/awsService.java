package za.ac.nwu.logic;

import org.springframework.web.multipart.MultipartFile;

public interface awsService
{
    void uploadToS3(String email, MultipartFile file) throws RuntimeException;
    byte[] downloadImage(String email, String imageName) throws Exception;
    String deleteImage(String fileName, String email);
    String deleteUser(String email);
    String shareImage(String bucketName, String toBucketName, String key);
}
