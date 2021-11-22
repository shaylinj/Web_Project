package za.ac.nwu.logic.impl;

import com.amazonaws.services.connect.model.UserNotFoundException;
import za.ac.nwu.Domain.DTO.ImageDto;
import za.ac.nwu.Domain.DTO.ImageShareDto;
import za.ac.nwu.Domain.DTO.UserDto;
import za.ac.nwu.Domain.persistence.AwsBucket;
import za.ac.nwu.Domain.persistence.Photo;
import za.ac.nwu.Domain.persistence.Shared;
import za.ac.nwu.logic.awsService;
import za.ac.nwu.logic.imageService;
import za.ac.nwu.logic.imageShareService;
import za.ac.nwu.logic.userService;
import za.ac.nwu.Translator.ImageTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Dependency Injection: photoTranslator, userCRUDService, sharedCRUDService, and awsCRUDService are injected to ensure singleton pattern is followed
@Component("imageServiceImpl")
public class imageServiceImpl implements imageService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoCRUDServiceImpl.class);
    private final ImageTranslator imageTranslator;
    private final UserService userService;
    private final ImageShareService imageShareService;
    private final AWSService awsService;

    @Autowired
    public  imageServiceImpl(ImageTranslator imageTranslator, UserService userService, ImageShareService imageShareService, AWSService awsService)
    {
        this.imageTranslator = imageTranslator;
        this.userService = userService;
        this.imageShareService = imageShareService;
        this.awsService = awsService;
    }

    //Inserts a photo in the database and on AWS
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public ImageDto createImageDto(ImageDto imageDto, String email, MultipartFile imageFile) throws Exception
    {
        try
        {
            UserDto user = userService.getUserDtoEmail(email);
            Image image = imageDto.buildImage();
            LOGGER.info("Image createImageDto method, Dto Object converted to persistence: {}", image);
            image.setUploadDate(LocalDate.now());
            image.setDateModified(LocalDate.now());
            Image addedImage = imageTranslator.addImage(image);
            LOGGER.info("Image createImageoDto method, object saved to database: {}", addedImage);
            awsService.uploadToS3(email, imageFile);
            ImageShareDto imageshareDto = new ImageShareDto();
            ImageShareDto.setAccess(true);
            ImageShareDto.setImageId(addedImage.getImageId());
            ImageShareDto.setShareDate(LocalDate.now());
            ImageShareDto.setUserId(user.getUserId());
            ImageShareDto.setShareWith(user.getUserId());
            ImageShareDto dto = imageShareService.createShareDto(imageshareDto);
            ImageDto returnDto = new ImageDto(imageTranslator.addimage(addedImage));
            LOGGER.info("Image createImageDto method, Dto returned: {}", returnDto);
            return returnDto;
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Image createImageDto method error {}", e.getMessage());
            throw new RuntimeException("Image createImageDto method could not be executed ", e);
        }
    }

    //Deletes a photo in the database and from AWS by given id and photoLink
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public Integer deleteImageDto(Integer imageId, String imageLink, String email) throws SQLException, Exception
    {
        try
        {
            boolean beforeDelete = imageTranslator.imageExists(imageId, imageLink);
            LOGGER.info("Image deleteImage method: {}", beforeDelete);
            if (!imageExists(imageId, imageLink))
            {
                LOGGER.error("Image with id {} does not exists", imageId);
                throw new RuntimeException("Image with id " + imageId + " does not exist");
            }
            int imageDelete = imageTranslator.deleteimage(imageId, imageLink);
            awsService.deleteImage(imageLink, email);
            boolean afterDelete = imageTranslator.imageExists(imageId, imageLink);
            LOGGER.info("Image deleteImage method: {}", afterDelete);
            return imageDelete;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Image could not be delete {}", error.getMessage());
            throw new SQLException("Image request could not be executed ", error.getCause());
        }
    }

    //Updates the metadata of a photo in the database with given information
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public ImageDto updateImageDto(String imageName, String Location, String CapturedBy, Integer imageId, String email) throws SQLException
    {
        try
        {
            int returnValue = imageTranslator.updatePhoto(imageName, Location, CapturedBy, imageId);
            if (returnValue == 0)
            {
                LOGGER.error("Image did not update image metadata: {}", false);
                throw new RuntimeException("Image did not update metadata");
            }
            return new ImageDto(imageTranslator.getImageId(imageId));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Image could not be updated {}", error.getMessage());
            throw new SQLException("Image request could not be executed ", error.getCause());
        }
    }

    //Returns a photoDto object from the database with given photo id
    @Override
    public ImageDto getImageoDtoId(Integer imageId)
    {
        try
        {
            LOGGER.info("Image input id {}", imageId);
            return new ImageDto(imageTranslator.getImageById(imageId));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Image could not be found with id {} error {}", imageId, error.getMessage());
            throw new RuntimeException("Image request could not be executed ", error.getCause());
        }
    }

    //Shares a specified photo with another user in the database and on AWS
    @Override
    public String sendImage(String shareEmail, String receiveEmail, boolean accessRights, Integer imageId) {
        try {
            if (!userService.userEmailExists(receiveEmail)) {
                LOGGER.error("Could not share the image with email {}", receiveEmail);
                throw new UserNotFoundException("Could not share the image");
            }
            UserDto receiveUserDto = userService.getUserDtoEmail(receiveEmail);
            UserDto sendUserDto = userService.getUserDtoEmail(shareEmail);
            ImageDto imageDto = new ImageDto(imageTranslator.getImageId(imageId));
            ImageShareDto imageShareDto = new ImageShareDto(LocalDate.now(), receiveUserDto.getUserId(), accessRights, sendUserDto.getUserId(), imageId);
            Share share = imageShareDto.buildShare();
            ImageShareDto addedShareImage = imageShareService.createShareDto(imageShareDto);
            String fromBucket = AwsBucket.PROFILE_IMAGE.getAwsBucket() + "/" + sendUserDto.getUserId();
            String toBucket = AwsBucket.PROFILE_IMAGE.getAwsBucket() + "/" + receiveUserDto.getUserId();
            awservice.shareImage(fromBucket, toBucket, imageDto.getPhotoLink());

            LOGGER.info("Image shared with email {}", receivingEmail);
            return "Photo shared";
        }
        catch (Exception error)
        {
            LOGGER.error("Could not share the image with email {}, error {}", receiveEmail, error.getMessage());
            throw new RuntimeException("Image request could not be executed ", error.getCause());
        }
    }

    //Loads all the photos from the database by a give user email
    @Override
    public List<ImageDto> getUserEmail(String email)
    {
        try
        {
            LOGGER.info("User email {}", email);
            UserDto userDto = userService.getUserEmail(email);
            LOGGER.info("User id {}", userDto.getUserId());
            return imageTranslator.findUserEmail(userDto.getUserId()).stream().map(ImageDto::new).collect(Collectors.toList());
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not return images for user with email {}, error {}", email, error.getMessage());
            throw new RuntimeException("Image request could not be executed ", error.getCause());
        }
    }

    //Loads all the photos from the database
    @Override
    public List<ImageDto> getImages()
    {
        try
        {
            List<ImageDto> images = new ArrayList<>();
            for(Image image : imageTranslator.getImages())
            {
                images.add(new ImageDto(image));
            }
            return image;
        } catch (RuntimeException error) {
            LOGGER.error("Could not get images {}, error {}", error.getMessage());
            throw new RuntimeException("Image request could not be executed ", error.getCause());
        }
    }

    //Checks if a photo exists with specified id and photoLink in the database
    @Override
    public boolean imageExists(Integer iamgeId, String imageLink)
    {
        try {
            boolean returnImageLogicValue = imageTranslator.imageExists(imageId, imageLink);
            LOGGER.info("Image result {}", returnimageLogicValue);
            return returnImageLogicValue;
        } catch (RuntimeException error) {
            LOGGER.error("Image with id {}, link {} could not be found, error {}", imageId, imageLink, error.getMessage());
            throw new RuntimeException("Image request could not be executed ", error.getCause());
        }
    }
}

