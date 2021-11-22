package za.ac.nwu.translator.impl;

import za.ac.nwu.domain.persistence.Shared;
import za.ac.nwu.repo.persistence.imageShareRepo;
import za.ac.nwu.translator.imageSharedTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.SQLException;

@Component
public class imageshareTranslatorImpl implements imageSharedTranslator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(imageshareTranslatorImpl.class);
    private final ImageShareRepo imageShareRepo;

    //Dependency Injection: sharedRepository is injected and singleton pattern is followed
    @Autowired
    public imageshareTranslatorImpl (ImageShareRepo imageShareRepo) {
        this.imageShareRepo = imageShareRepo;
    }

    //Inserts a shared record in the database
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public Share addShare(Share share) throws RuntimeException, SQLException
    {
        try
        {
            LOGGER.info("Input object's date {}", shared.getShareDate());
            return imageShareRepo.save(share);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not add the new shared record {}", error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Deletes a shared record from the database by given sharedWith id and photo id
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public Integer deleteShareRecord(Integer sharedWith, Integer imageId) throws SQLException
    {
        try
        {
            LOGGER.info("Delete image {} for user {} ", sharedWith, imageId);
            return imageShareRepo.deleteShareWithAndImageId_ImageId(sharedWith, imageId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not delete the shared record with sharedWith id {} and image id {}, with error {}", sharedWith, photoId, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Checks if a shared record exists given te set of parameters
    @Override
    public boolean existsShareWithAndImageId(Integer sharedWith, Integer imageId)
    {
        try
        {
            LOGGER.info("Shared with id {} and imageId {} ", sharedWith, imageId);
            return imageShareRepo.existsShareWithAndImageId_ImageId(sharedWith, imageId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the shared record with shareWith id {} and image id {}, error {}", sharedWith, imageId, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Checks to see if a shred record exists with the given parameters
    @Override
    public boolean existsShareWithAndUserIdAndImageId(Integer sharedWith, Integer userId, Integer imageId)
    {
        try
        {
            LOGGER.info("Shared with id {} by user id {} and image id {}", sharedWith, userId, imageId);
            return imageShareRepo.existsShareWithAndUserId_UserIdAndImageId_ImageId(sharedWith, userId, ImageId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Shared record with sharedWith id {}, user id {}, and image id {} does not exist, error {}", sharedWith, userId, imageId, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Inserts a photo in the database
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class})
    @Override
    public Shared shareImage(Shared shareImage) throws SQLException, RuntimeException
    {
        try
        {
            LOGGER.info("Input object's date: {}", shareImage.getSharedDate());
            return imageShareRepo.save(shareImage);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not share the share with error {}", error.getMessage());
            throw new RuntimeException("Failed to execute ", error.getCause());
        }
    }

    //Returns a shared object from the database with given sharedWith and photo id
    @Override
    public Shared findShareWithAndImageId(Integer sharedWith, Integer imageId)
    {
        try
        {
            LOGGER.info("Shared with id {} and image id {}", sharedWith, imageId);
            return imageShareRepo.findShareWithAndPhotoId(sharedWith, imageId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the shared record by id {} and image id {}, error {}", sharedWith, imageId, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }
}
