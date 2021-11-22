package za.ac.nwu.logic.impl;

import com.amazonaws.services.connect.model.UserNotFoundException;
import za.ac.nwu.Domain.dto.UserDto;
import za.ac.nwu.Domain.persistence.User;
import za.ac.nwu.logic.userService;
import za.ac.nwu.Translator.UserTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Component("userServiceImpl")
public class userServiceImpl implements userService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(imageServiceImpl.class);
    private final PasswordEncrypter passwordEncrypter;
    private final UserTranslator userTranslator;

    //Dependency Injection: userTranslator and passwordEncoder are injected and singleton pattern is followed
    @Autowired
    public userServiceImpl(UserTranslator userTranslator, PasswordEncrypter passwordEncrypter)
    {
        this.userTranslator = userTranslator;
        this.passwordEncrypter = passwordEncrypter;
    }

    //Inserts a userDto in the database
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public UserDto createNewUser(UserDto userDto) throws SQLException, Exception
    {
        try
        {
            LOGGER.info("Input Dto object is {}", userDto);
            isUniqueUser(userDto);
            User user = userDto.buildUser();
            LOGGER.info("Dto Object converted to persistence is {}", user);
            user.setUserHashPassword(passwordEncrypter.encrypter(userDto.getUserHashPassword()));
            user.setDate(LocalDate.now());
            User addedUser = userTranslator.newUser(user);
            LOGGER.info("Object saved to database {}", addedUser);
            UserDto returnUser = new UserDto(userTranslator.newUser(addedUser));
            LOGGER.info("Dto returned {}", returnUser);
            return returnUser;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not create the new user Dto, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Deletes a userDto from the database with given id
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class})
    @Override
    public Integer deleteUser(Integer userId) throws SQLException
    {
        try
        {
            LOGGER.info("Queried id: {}", userId);
            boolean beforeDelete = userTranslator.userExists(userId);
            LOGGER.info("[User Logic log] deleteUser method: {}", beforeDelete);
            if (!userExists(userId))
            {
                LOGGER.warn("User with id {} does not exist", userId);
                throw new RuntimeException("Delete user error.");
            }
            int userDelete = userTranslator.deleteUser(userId);
            boolean afterDelete = userTranslator.userExists(userId);
            LOGGER.info("[User Logic log] deleteUser method: {}", afterDelete);
            return userDelete;
        } catch (RuntimeException error) {
            LOGGER.error("Could not delete the user, with error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Updates a userDto in the database with given id and information
    @Transactional(rollbackOn = {SQLException.class, RuntimeException.class})
    @Override
    public UserDto updateUserDto(String firstName, String lastName, String email, String contactNumber, Integer userId) throws SQLException
    {
        try
        {
            int returnValue = userTranslator.updateUser(firstName, lastName, email, contactNumber, userId);
            if (returnValue == 0) {
                LOGGER.error("Could not update account: {}", false);
                throw new RuntimeException("Could not update account");
            }
            return new UserDto(userTranslator.getUserId(userId));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not update the user,error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Returns a userDto object from the database with given id
    @Override
    public UserDto getUserDtoById(Integer userId)
    {
        try
        {
            LOGGER.info("Input id {}", userId);
            return new UserDto(userTranslator.getUserId(userId));
        } catch (RuntimeException error) {
            LOGGER.error("Could not get the user with id {},error {}", userId, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Returns a userDto object from the database with given email
    @Override
    public UserDto getUserDtoByEmail(String email) {
        try {
            LOGGER.info("Input email {}", email);
            if (!userTranslator.userEmailExists(email))
            {
                LOGGER.error("User with email {} does not exist", email);
                throw new UserNotFoundException("User with email " + email + " does not exist");
            }
            return new UserDto(userTranslator.getUserEmail(email));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not get the userDto with email {}, with error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Private method that verifies that the userDto is unique
    private void isUniqueUser(UserDto userDto) throws Exception
    {
        if (userTranslator.registerCheck(userDto.getContactNumber(), userDto.getEmail()))
        {
            LOGGER.warn("User with contact number: {} and email: {} already exists", userDto.getContactNumber(), userDto.getEmail());
            throw new RuntimeException("User already exists");
        }
    }

    //Confirms that the userDto exists with email or not
    @Override
    public boolean userExistsEmail(String email)
    {
        try
        {
            LOGGER.info("Queried email {}", email);
            boolean returnLogicValue = userTranslator.userEmailExists(email);
            LOGGER.info("Result {}", returnLogicValue);
            return returnLogicValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not verify the user with email {}, error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Confirms that the user exists by id or not
    @Override
    public boolean userExists(Integer userId)
    {
        try
        {
            LOGGER.info("Queried user id: {}", userId);
            boolean returnLogicValue = userTranslator.userExists(userId);
            LOGGER.info("Result: {}", returnLogicValue);
            return returnLogicValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not verify the user with id {}, error {}", userId, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Confirms that the login of a user
    @Override
    public boolean loginUser(String password, String email) throws Exception
    {
        try
        {
            LOGGER.info("password: {} and email: {}", password, email);
            boolean userValid = userTranslator.loginUser(password, email);
            LOGGER.info("Valid: {}", userValid);
            return userValid;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not log the user in, with error {}", error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Verifies that the userDto is unique according to given parameters
    @Override
    public boolean verifyUserByPhoneNumberAndEmail(String contactNumber, String email) throws Exception {
        try
        {
            return userTranslator.registerCheck(contactNumber, email);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not verify the user with contact number {} and email {}, error {}", contactNumber, email, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }

    //Method that loads the user by given email, implemented for spring security
    @Override
    public UserDetails loadUserUsername(String email) throws UsernameNotFoundException
    {
        try
        {
            UserDto userDto = new UserDto(userTranslator.getUserEmail(email));
            if (userDto == null)
            {
                LOGGER.error("User not present in database");
                throw new UsernameNotFoundException("User not present in database");
            }
            else
            {
                LOGGER.info("User present in database: {}", email);
            }
            Collection<SimpleGrantedAuthority> auths = new ArrayList<>();
            auths.add(new SimpleGrantedAuthority("USER_ROLE"));
            return new org.springframework.security.core.userdetails.User(userDto.getEmail(), userDto.getUserHashPassword(), auths);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not load the user with email {} by email, error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute the request", error.getCause());
        }
    }
}
