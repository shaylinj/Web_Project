package za.ac.nwu.translator;

import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Component
public interface AWSTranslator
{
    void save(String path, String fileName, Optional<Map<String, String>> optionalMetaData, InputStream inputStream);
    byte[] download(String path, String key);
    boolean deleteImageFolder(String fileName);
    boolean deleteUserFolder(String path);
    boolean shareImage(String bucketName, String toBucketName, String key);
}
