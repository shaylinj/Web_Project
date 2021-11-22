package za.ac.nwu.repo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.ac.nwu.Domain.DTO.UserDTO;
import za.ac.nwu.Domain.persistence.User;
import javax.transaction.Transactional;
import java.util.List;

public interface userRepo extends JpaRepository <user, integer>
{
    UserDTO findEmail(String email);
    @Transactional
    @Modifying
    @Query(value = "" + "UPDATE User u SET u.password = :newPassword" + " WHERE u.email = :email")
    void changePassword(String newPassword, String email);
    @Query(value = "SELECT u.userID from User u")
    List<Integer> getAllUserID();
    @Query(value = "select u.userID from User u where u.email = :email")
    Integer getUserID(String email);
    @Query(value = "select u.password from User u where u.email = :email")
    String getPassword(String email);
}
