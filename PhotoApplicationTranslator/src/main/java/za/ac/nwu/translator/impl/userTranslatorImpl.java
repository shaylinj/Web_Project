package za.ac.nwu.translator.impl;

import com.amazonaws.services.connect.model.UserNotFoundException;
import za.ac.nwu.domain.persistence.User;
import za.ac.nwu.repo.persistence.userReposerRepository;
import za.ac.nwu.translator.userTranslatorserTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.SQLException;

@Component
public class userTranslatorImpl implements userTranslator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(userTranslatorImpl.class);
    private final UserRepo userRepo;

    //Dependency Injection: userRepository is injected and singleton pattern is followed
    @Autowired
    public userTranslatorImpl(UserRepo userRepo)
    {
        this.userRepo = userRepo;
    }

    //Inserts a user in the database
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public User newUser(User user) throws SQLException
    {
        try
        {
            LOGGER.info("Input object's email: {}", user.getEmail());
            return userRepo.save(user);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not add the new user, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Deletes a user from the database with given id
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public Integer deleteUser(Integer id) throws SQLException
    {
        try
        {
            LOGGER.info("Input id: {}", id);
            int deleteValue = userRepo.deleteUserId(id);
            LOGGER.info("Exists: {}", deleteValue);
            return deleteValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not delete the user with id {}, with error {}", id, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Updates a user in the database with given id and information
    @Transactional(rollbackOn = {SQLException.class, Exception.class, RuntimeException.class})
    @Override
    public Integer updateUser(String firstName, String lastName, String email, String contactNumber, Integer userId) throws SQLException
    {
        try
        {
            return userRepo.updateUser(firstName, lastName, email, contactNumber, userId);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not update the user, error {}", error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Returns a user object from the database with given id
    @Override
    public User getUserId(Integer userId)
    {
        try
        {
            LOGGER.info("[User Translator log] getUserById method, input id: {}", userId);
            return userRepo.findUserId(userId).orElseThrow(() -> new UserNotFoundException("User with id " + userId + " was not found"));
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not get the user by id {}, with error {}", userId, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Returns a user object from the database with given email
    @Override
    public User getUserEmail(String email) throws UserNotFoundException
    {
        try
        {
            if (!userEmailExists(email))
            {
                LOGGER.warn("User with email {} does not exist", email);
                throw new UserNotFoundException("User with email " + email + " does not exist");
            }
            return userRepo.findEmail(email);
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not find the user with email {}, error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute", error.getCause());
        }
    }

    //Confirms that the user exists with email or not
    @Override
    public boolean userEmailExists(String email)
    {
        try
        {
            boolean returnValue = userRepo.existsEmail(email);
            LOGGER.info("Email exists: {}", returnValue);
            return returnValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not confirm if the user with email {} exist,error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Confirms that the user exists by id or not
    @Override
    public boolean userExists(Integer userId)
    {
        try
        {
            boolean returnValue = userRepo.existsUserId(userId);
            LOGGER.info("Result: {}", returnValue);
            return returnValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not confirm if the user with id {} exists, with error {}", userId, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Confirms that the login of a user
    @Override
    public boolean loginUser(String password, String email) throws Exception
    {
        try
        {
            LOGGER.info("Input email: {}", email);
            boolean loginValue = userRepo.existsUserHashPasswordAndEmail(password, email);
            LOGGER.info("Exists: {}", loginValue);
            return loginValue;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not confirm the login of the user with email {},error {}", email, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }

    //Confirms that the user is not already registered
    @Override
    public boolean registerCheck(String contactNumber, String email) throws Exception
    {
        try
        {
            LOGGER.info("Input contact number: {} and email: {}", contactNumber, email);
            if (userRepo.existsContactNumberAndEmail(contactNumber, email))
            {
                LOGGER.error("User with contact number: {} and email: {} already exists", contactNumber, email);
                throw new RuntimeException("User already exists");
            }
            LOGGER.info("User status: {}", false);
            return false;
        }
        catch (RuntimeException error)
        {
            LOGGER.error("Could not verify that the user is unique with email {} and contact number {},error {}", email, contactNumber, error.getMessage());
            throw new RuntimeException("Failed to execute the request ", error.getCause());
        }
    }
}
