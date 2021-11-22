package za.ac.nwu.repo.persistence;

import za.ac.nwu.domain.persistence.Shared;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;
import java.time.LocalDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {RepositoryTestConfiguration.class})
public interface imageShareTest
{
    @Autowired
    SharedRepo sharedRepo;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Transactional
    @Cascade(CascadeType.ALL)
    @Test
    @DisplayName("Delete the shared record by sharedWith id and photo id.")
    public void deleteSharedWithAndImageId_ImageId()
    {
        int value = sharedRepo.deleteSharedWithAndImageId_ImageId(5, 5);
        assertEquals(5, value);
    }

    @Test
    @DisplayName("Find shared record by sharedWith id and photo id.")
    public void findSharedWithAndImageId() {
        Shared shared = sharedRepo.findSharedWithAndImageId(5, 5);
        assertNotNull(shared);
        assertEquals(LocalDate.parse("2021-08-10"), shared.getSharedDate());
    }
}
