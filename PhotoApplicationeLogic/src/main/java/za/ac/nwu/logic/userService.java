package za.ac.nwu.logic;

import za.ac.nwu.Domain.DTO.UserDto;
import java.sql.SQLException;

public interface userService
{
    UserDto createNewUser(UserDto userDto) throws SQLException, Exception;
    Integer deleteUser(Integer id) throws SQLException;
    UserDto updateUserDto(Integer userId, String firstName, String lastName, String email, String contactNumber) throws SQLException;
    UserDto getUserDtoId(Integer userId);
    UserDto getUserDtoByEmail(String email);
    boolean userExists(Integer userId);
    boolean userEmailExists(String email);
    boolean loginUser(String password, String email) throws Exception;
    boolean verifyUserContactNumberAndEmail (String contactNumber, String email) throws Exception;
}
