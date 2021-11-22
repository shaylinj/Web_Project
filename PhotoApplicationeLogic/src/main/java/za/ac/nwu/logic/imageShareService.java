package za.ac.nwu.logic;

import za.ac.nwu.Domain.DTO.ImageShareDto;
import org.springframework.web.multipart.MultipartFile;
import java.sql.SQLException;

public interface imageShareService
{
    ImageShareDto createSharedDto(ImageShareDto imageShareDto) throws SQLException, Exception;
    Integer deleteShareRecord(Integer shareWith, Integer imageId, String imageLink) throws SQLException;
    ImageShareDto findBySharedWithAndPhotoId(Integer shareWith, Integer imageId);
    String shareImage(String sharingEmail, String receivingEmail, boolean accessRights, Integer imageId, MultipartFile image) throws Exception;
    boolean checkShareWithAndImageId(String email, Integer imageId) throws Exception;
    boolean existsShareWithAndUserIdAndImageId(String email, Integer imageId) throws Exception;
}
