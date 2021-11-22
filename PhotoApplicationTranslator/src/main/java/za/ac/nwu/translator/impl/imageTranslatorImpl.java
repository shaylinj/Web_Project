package za.ac.nwu.translator.impl;

import za.ac.nwu.domain.persistence.Photo;
import za.ac.nwu.repo.persistence.imageRepo;
import za.ac.nwu.translator.imageTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.List;

@Component
public class imageTranslatorImpl extends imageTranslator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(imageTranslatorImpl.class);
    private final imageRepo imageRepo;

    //Dependency Injection: photoRepository is injected and singleton pattern is followed
    @Autowired
    public imageTranslatorImpl (imageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    //Inserts a photo in the database
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public Image addImage(Image image) throws SQLException
    {
        try
        {
            LOGGER.info("Input object's name: {}", image.getPhotoName());
            return imageRepo.save(image);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not add the new image record {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Deletes a photo in the database by given id and photoLink
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public Integer deleteImage(Integer imageId, String imageLink) throws Exception
    {
        try
        {
            LOGGER.info("Input image id: {}", imageId);
            int deleteValue = imageRepo.deleteImageIdAndImageLink(imageId, imageLink);
            LOGGER.info("Exists: {}", deleteValue);
            return deleteValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not delete the image {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Updates the metadata of a photo in the database with given information
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public Integer updateImage(String imageName, String Location, String CapturedBy, Integer imageId)
    {
        try
        {
            return imageRepo.updateImage(imageName, Location, CapturedBy, imageId);
        } catch (RuntimeException error)
        {
            LOGGER.error("Could not update the image{}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Returns a photo object from the database with given photo id
    @Override
    public Image getImageId(Integer imageId)
    {
        try
        {
            LOGGER.info("Input id: {}", imageId);
            return imageRepo.findImageId(imageId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not get the image by id {} error {} ", imageId, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Filters the photos by name and format in the database
    @Override
    public Image findImageNameAndImageFormat(String name, String format)
    {
        try
        {
            LOGGER.info("Input name {} and format {}", name, format);
            return imageRepo.findImageNameAndImageFormat(name, format);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the image with name {} and format {},error {}", name, format, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Loads all the photos specifically for a shared by id from the database [Shared Entity]
    @Override
    public List<Image> findUserEmail(Integer sharedWith)
    {
        try
        {
            LOGGER.info("User shared with {}", sharedWith);
            return imageRepo.findUserImageSharedWith(sharedWith);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the user of the image: id {} not found, error {} ", sharedWith, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Checks if a photo exists with specified id and photoLink in the database
    @Override
    public boolean imageExists(Integer imageId, String imageLink)
    {
        try
        {
            LOGGER.info("Queried id: {}", imageId);
            boolean returnValue = imageRepo.existsImageIdAndImageLink(imageId, imageLink);
            LOGGER.info("Result: {}", returnValue);
            return returnValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not confirm if the image exists with id {} and link {}, error {} ", imageId, imageLink, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Loads all the photos from the database
    @Override
    public List<Image> getImage()
    {
        try
        {
            return imageRepo.findAll();
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not get all the image error {}", error.getMessage());
            throw new RuntimeException("[Photo Translator Error] getAllPhotos method, failed to execute the request ", error.getCause());
        }
    }

    //Loads all the photos specifically for a user by id from the database [Photo Entity]
    @Override
    public List<Image> getImageOfUser(Integer userId)
    {
        try
        {
            LOGGER.info("User id {}", userId);
            return imageRepo.findSharedWith(userId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not get all the image for user id {}, error {}", userId, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }
}
