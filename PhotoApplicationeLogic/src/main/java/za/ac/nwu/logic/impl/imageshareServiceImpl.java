package za.ac.nwu.logic.impl;

import com.amazonaws.services.connect.model.UserNotFoundException;
import za.ac.nwu.Domain.dto.SharedDto;
import za.ac.nwu.Domain.dto.UserDto;
import za.ac.nwu.Domain.persistence.Shared;
import za.ac.nwu.logic.awsService;
import za.ac.nwu.logic.imageShareService;
import za.ac.nwu.Translator.SharedTranslator;
import za.ac.nwu.Translator.UserTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;

@Component("sharedServiceImpl")
public class imageshareServiceImpl implements imageShareService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(imageshareServiceImpl.class);
    private final ShareTranslator shareTranslator;
    private final UserTranslator userTranslator;
    private final AWSService awsService;

    //Dependency Injection: sharedTranslator, userTranslator, and awsCRUDService are injected and singleton pattern is followed
    @Autowired
    public imageshareServiceImpl (ShareTranslator shareTranslator, UserTranslator userTranslator, AWSService awsService)
    {
        this.shareTranslator = shareTranslator;
        this.userTranslator = userTranslator;
        this.awsService = awsService;
    }

    //Inserts a shared record in the database
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public ShareDto createSharedDto(ShareDto imageShareDto) throws SQLException, Exception
    {
        try
        {
            if (!userTranslator.userExists(imageShareDto.getUserId()))
            {
                LOGGER.warn("Input Dto contained invalid user id: {}", "invalid");
                imageShareDto.setUserId(0);
            }
            Share share = imageShareDto.buildShare();
            Share addedShare = shareTranslator.addShare(share);
            ShareDto returnShare = new ShareDto(shareTranslator.addShare(addedShare));
            return returnShared;
        }
        catch (SQLException error)
        {
            LOGGER.error("Image exception with error {}", error.getMessage());
            throw new RuntimeException("Image share failed to execute", error.getCause());
        }
    }

    //Deletes a sharedDto record from the database by given parameters
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public Integer deleteShareRecord(Integer shareWith, Integer imageId, String imageLink) throws SQLException
    {
        try
        {
            LOGGER.info("Delete photo {} for user {} ", shareWith, imageId);
            int response = shareTranslator.deleteShareRecord(shareWith, imageId);
            UserDto userDto = new UserDto(userTranslator.getUserId(shareWith));
            awsService.deleteImage(imageLink, userDto.getEmail());
            return response;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not delete the shared record, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Returns a sharedDto object from the database
    @Override
    public ShareDto findSharedWithAndImageId(Integer shareWith, Integer imageId)
    {
        try
        {
            LOGGER.info("Shared with id {} and photo id {}", shareWith, imageId);
            return new ShareDto(shareTranslator.findSharedWithAndImageId(shareWith, imageId));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the shared record, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Shares a photo between users in database and on AWS
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class, Exception.class})
    @Override
    public String sharePhoto(String sharingEmail, String receivingEmail, boolean accessRights, Integer id, MultipartFile photo) throws Exception {
        try {
            if (!userTranslator.userEmailExists(receiveEmail))
            {
                LOGGER.error("Could not share the photo with email {}", receiveEmail);
                throw new UserNotFoundException("Could not share the photo with email");
            }
            UserDto receiveUserDto = getUserDtoEmail(receiveEmail);
            UserDto sendUserDto = getUserDtoEmail(shareEmail);
            ShareDto shareDto = new Sharedto(LocalDate.now(), receiveUserDto.getUserId(), accessRights, sendUserDto.getUserId(), imageId);
            Share share = shareDto.buildShare();
            Shared addedShareImage = sharedTranslator.shareImage(share);
            ShareDto returnShareImage = new ShareDto(shareTranslator.shareImage(addedShareImage));
            awsService.uploadToS3(receiveEmail, image);
            LOGGER.info("Shared the photo with email {}", receiveEmail);
            return "Photo shared";
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not share the photo with email {}, error {}", receiveEmail, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Verifies that a shared record exists with the given parameters
    @Override
    public boolean checkShareWithAndImageId(String email, Integer imageId) throws Exception
    {
        try
        {
            UserDto userDto = getUserDtoEmail(email);
            LOGGER.info("Image id {} and email {}", imageId, email);
            if (shareTranslator.existsShareWithAndImageId(userDto.getUserId(),imageId))
            {
                return true;
            }
            else
            {
                LOGGER.error("User {} already has photo with id  {}", email, imageId);
                throw new SQLException("User already has this photo");
            }
        } catch (RuntimeException error) {
            LOGGER.error("Could not verify the shared record, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Verifies that a shared record exists with the given parameters
    @Override
    public boolean existsShareWithAndUserIdAndImageId(String email, Integer imageId) throws Exception
    {
        try
        {
            UserDto userDto = getUserDtoEmail(email);
            LOGGER.info("Image id {} and email {}", imageId, email);
            if (shareTranslator.existsSharedWithAndUserIdAndImageId(userDto.getUserId(), userDto.getUserId(), imageId))
            {
                LOGGER.error("User {} already has photo with id  {}", email, imageId);
                throw new SQLException("User already has this photo");
            }
            return false;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not verify the shared record, with error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Private method for getting the user by email
    private UserDto getUserDtoEmail(String email)
    {
        return new UserDto(userTranslator.getUserEmail(email));
    }
}
