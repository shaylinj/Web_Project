package za.ac.nwu.repo.persistence;

import za.ac.nwu.domain.persistence.User;
import za.ac.nwu.repo.config.repoTest;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {RepositoryTestConfiguration.class})
public interface userTest
{
    @Autowired
    UserRepo userRepo;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Transactional
    @Test
    @DisplayName("Update a user's information.")
    public void updateUser() {
        Optional<User> user = userRepo.findUserId(1);
        assertNotNull(user);
        int result = userRepo.updateUser("Johnson", "Cedric", "cedricjohnson@gmail.com", "0724123695", 1);
        assertEquals(result, 1);
    }

    @Transactional
    @Cascade(CascadeType.ALL)
    @Test(expected =  DataIntegrityViolationException.class)
    @DisplayName("Delete a user by id.")
    public void deleteUserId()
    {
        Optional<User> user = userRepo.findUserId(2);
        assertNotNull(user);
        int value = userRepo.deleteUserId(2);
        assertEquals(value, 2);
    }

    @Test
    @DisplayName("Verify a user exists by email.")
    public void existsEmail()
    {
        boolean value = userRepo.existsEmail("shaylinjohnson1008@gmail.com");
        assertTrue(value);
    }

    @Test
    @DisplayName("Find a user by email.")
    public void findEmail()
    {
        User user = userRepo.findEmail("cedricjohnson@gmail.com");
        assertNotNull(user);
        assertEquals("Cedric", user.getFirstName());
        assertEquals("Johnson", user.getLastName());
    }

    @Test
    @DisplayName("Find a user by id.")
    public void findUserId()
    {
        Optional<User> user = userRepo.findUserId(3);
        assertNotNull(user);
    }

    @Test
    @DisplayName("Verify a user exists with password and email.")
    public void existsUserHashPasswordAndEmail()
    {
        User user = userRepo.findEmail("shaylinjohnson1008@gmail.com");
        assertNotNull(user);
        boolean value = userRepo.existsUserHashPasswordAndEmail("shayj1008", "shaylinjohnson1008@gmail.com");
        assertTrue(value);
    }

    @Test
    @DisplayName("Verify a user exists with contact number and email.")
    public void existsContactNumberAndEmail() {
        User user = userRepo.findEmail("shaylinjohnson1008@gmail.com");
        assertNotNull(user);
        boolean value = userRepository.existsContactNumberAndEmail("0747287781", "shaylinjohnson1008@gmail.com");
        assertTrue(value);
    }

    @Test
    @DisplayName("Verify a user exists with id.")
    public void existsUserId() {
        User user = userRepo.findEmail("shaylinjohnson1008@gmail.com");
        assertNotNull(user);
        boolean value = userRepository.existsById(3);
        assertTrue(value);
    }
}
