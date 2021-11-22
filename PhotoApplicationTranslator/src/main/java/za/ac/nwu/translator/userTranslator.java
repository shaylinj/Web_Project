package za.ac.nwu.translator;

import za.ac.nwu.domain.persistence.User;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

@Component
public interface userTranslator
{
    User newUser(User user) throws SQLException;
    Integer deleteUser(Integer userId) throws SQLException;
    Integer updateUser(String firstName, String lastName, String email, String contactNumber, Integer userId) throws SQLException;
    User getUserId(Integer userId);
    User getUserEmail(String email);
    boolean userEmailExists(String email);
    boolean userExists(Integer userId);
    boolean loginUser(String password, String email) throws Exception;
    boolean registerCheck(String contactNumber, String email) throws Exception;
}
