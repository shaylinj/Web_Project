package za.ac.nwu.repo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.ac.nwu.domain.DTO.ImageDTO;
import za.ac.nwu.domain.persistence.Image;
import za.ac.nwu.domain.persistence.User;

@Repository
public interface imageRepo extends JpaRepository <image, integer>
{
    void deleteLink(String imageLink);
    @Query(value = "SELECT i.name FROM Image i WHERE i.imageID = :imageID")
    String getImageName(Integer imageID);
    List<ImageDTO> findUserID(User userID);
}
