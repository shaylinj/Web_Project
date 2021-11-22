package za.ac.nwu.logic;

import za.ac.nwu.Domain.DTO.ImageDto;
import org.springframework.web.multipart.MultipartFile;
import java.sql.SQLException;
import java.util.List;

public interface imageService
{
    ImageDto createPhotoDto(ImageDto imageDto, String email, MultipartFile imageFile) throws Exception;
    ImageDto getImageDtoId(Integer id);
    String sendimage(String sharingEmail, String receivingEmail, boolean accessRights, Integer imageId);
    List<ImageDto> getEmail(String email);
    List<ImageDto> getImage();
    boolean imageExists(Integer id, String imageLink);
    Integer deleteImageDto(Integer id, String imageLink, String email) throws Exception;
    ImageDto updatePhotoDto(String imageName, String imageLocation, String CapturedBy, Integer imageId, String email) throws SQLException;
}
