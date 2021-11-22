package za.ac.nwu.translator;

import za.ac.nwu.domain.persistence.Photo;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.util.List;

@Component
public interface imageTranslator
{
    Image addPhoto(Image image) throws SQLException;
    Integer updateImage(String imageName, String Location, String CapturedBy, Integer imageId);
    Integer deleteImage(Integer imageId, String imageLink) throws Exception;
    Image getImageId(Integer imageId);
    Image findImageNameAndImageFormat(String name, String format);
    List<Image> findByUserEmail(Integer shareWith);
    boolean photoExists(Integer imageId, String imageLink);
    List<Image> getAllImage();
    List<Image> getImageOfUser(Integer userId);
}
