package za.ac.nwu.logic.impl;

import za.ac.nwu.Domain.dto.UserDto;
import za.ac.nwu.Domain.persistence.AwsBucket;
import za.ac.nwu.logic.awsService;
import za.ac.nwu.Translator.AwsTranslator;
import za.ac.nwu.Translator.UserTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Component("awsServiceFlow")
public class awsServiceImpl implements awsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(awsServiceImpl.class);
    private final UserTranslator userTranslator;
    private final AWSTranslator awsTranslator;

    @Autowired
    public awsServiceImpl(UserTranslator userTranslator, AWSTranslator awsTranslator) {
        this.userTranslator = userTranslator;
        this.awsTranslator = awsTranslator;
    }

    @Transactional(rollbackOn = {IllegalStateException.class, RuntimeException.class})
    @Override
    public void uploadToS3(String email, MultipartFile image) throws RuntimeException
    {
        try
        {
            isImageEmpty(image);
            isImage(image);
            Map<String, String> extraData = getMetadata(photo);
            UserDto userDto = new UserDto(userTranslator.getUserEmail(email));
            LOGGER.info("File name: {}", image.getFilename());
            String path = String.format("%s/%s", AWSBucket.PROFILE_IMAGE.getAwsBucket(), userDto.getUserId());
            String filename = image.getFilename();
            awsTranslator.save(path, filename, Optional.of(extraData), image.getInputStream());
            LOGGER.info("AWS uploadToS3 method save success");
        }
        catch (IOException error)
        {
            LOGGER.error("AWS uploadToS3 method error {} ", error.getMessage());
            throw new IllegalStateException("AWS uploadToS3 method could not execut", error.getCause());
        }
    }
}
