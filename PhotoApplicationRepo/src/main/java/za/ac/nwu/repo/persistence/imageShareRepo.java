package za.ac.nwu.repo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.ac.nwu.domain.persistence.ImageShare;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface imageShareRepo extends JpaRepository <share, integer>
{
    @Query(value = "SELECT s.imageShareID FROM ImageShare s WHERE s.imageID = :imageID AND s.userIDShare.userID = :shareID AND s.userIDSharer.userID = :sharerID")
    Integer getShareID(Integer imageID, Integer sharerID, Integer shareID);
}
