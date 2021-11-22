package za.ac.nwu.translator.impl;

import com.amazonaws.services.account.model.AWSAccountException;
import za.ac.nwu.aws.AWSServices;
import za.ac.nwu.domain.persistence.AwsBucket;
import za.ac.nwu.translator.AWSTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Component
public class AWSTranslatorImpl implements AWSTranslator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AWSTranslatorImpl.class);
    private final AWSServices awsServices;

    //Dependency Injection: awsFileServices is injected and singleton pattern is followed
    @Autowired
    public AWSTranslatorImpl(AWSServices awsServices) {
        this.awsServices = awsServices;
    }

    //Saving to the S3 bucket on AWS
    @Override
    public void save(String path, String fileName, Optional<Map<String, String>> optionalMetaData, InputStream inputStream)
    {
        try
        {
            awsServices.save(path, fileName, optionalMetaData, inputStream);
            LOGGER.info("Successful save");
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not execute the save request with error {}", error.getMessage());
            throw new RuntimeException("Could not execute save", error.getCause());
        }
    }

    //Downloading from the S3 bucket on AWS
    @Override
    public byte[] download(String path, String key)
    {
        try
        {
            LOGGER.info("Location {}{} ", path, key);
            byte[] downloadResult = awsServices.download(path, key);
            LOGGER.info("Successful download");
            return downloadResult;
        }
        catch (AWSAccountException e)
        {
            LOGGER.error("Could not execute the download request with error {}", e.getMessage());
            throw new RuntimeException("Failed to execute", e.getCause());
        }
    }

    //Deleting a photo from the S3 bucket on AWS
    @Override
    public boolean deleteImageFolder(String fileName)
    {
        try
        {
            awsServices.deleteImageFolder(AwsBucket.PROFILE_IMAGE.getAwsBucket(), fileName);
            LOGGER.info("Successfully deleted image from {}", fileName);
            return true;
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Could not delete the image error {}", e.getMessage());
            throw new RuntimeException("Failed to execute", e.getCause());
        }
    }

    //Deleting a folder from the S3 bucket on AWS
    @Override
    public boolean deleteUserFolder(String path)
    {
        try
        {
            awsServices.deleteUserFolder(AwsBucket.PROFILE_IMAGE.getAwsBucket(), path);
            LOGGER.info("Successfully deleted folder from {}", path);
            return true;
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Could not delete the folder with error {}", e.getMessage());
            throw new RuntimeException("Failed to execute ", e.getCause());
        }
    }

    //Sharing a photo to another folder in the S3 bucket on AWS
    @Override
    public boolean shareImage(String fromBucketName, String toBucketName, String key)
    {
        try
        {
            awsServices.shareImage(fromBucketName, toBucketName, key);
            LOGGER.info("Successfully shared photo from {}", key);
            return true;
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Could not share the photo with error {}", e.getMessage());
            throw new RuntimeException("Failed to execute the request ", e.getCause());
        }
    }
}
