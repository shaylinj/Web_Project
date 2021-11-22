package za.ac.nwu.translator;

import za.ac.nwu.domain.persistence.Shared;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

@Component
public interface imageSharedTranslator
{
    Share addShare (Share share) throws Exception;
    Integer deleteSharedRecord(Integer shareWith, Integer imageId) throws SQLException;
    boolean existsSharedWithAndImageId(Integer shareWith, Integer imageId);
    boolean existsSharedWithAndUserIdAndImageId(Integer shareWith, Integer userId, Integer imageId);
    Shared shareImage (Shared shareImage) throws Exception;
    Shared findBySharedWithAndImageId(Integer shareWith, Integer imageId);
}
