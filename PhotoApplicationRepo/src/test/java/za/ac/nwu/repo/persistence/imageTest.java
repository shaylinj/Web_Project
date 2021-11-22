package za.ac.nwu.repo.persistence;

import za.ac.nwu.domain.persistence.Photo;
import za.ac.nwu.repo.config.RepositoryTestConfiguration;
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
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {RepositoryTestConfiguration.class})
public interface imageTest
{
    @Autowired
    ImageRepo imageRepo;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Transactional
    @Test
    @DisplayName("Update image metadata.")
    public void updateImage() {
        Image image = imageRepo.findImageId(3);
        assertNotNull(image);
        int result = imageRepo.updateImage("Wian du Toit", "Johannesburg", "Shaylin Johnson", 3);
        assertEquals(result, 3);
    }

    @Transactional
    @Cascade(CascadeType.ALL)
    @Test(expected =  DataIntegrityViolationException.class)
    @DisplayName("Delete image by id and link.")
    public void deleteImageIdAndImageLink() {
        Image image = imageRepo.findImageId(2);
        assertNotNull(image);
        int value = imageRepo.deleteImageIdAndImageLink(2, "IMG_002.jpeg");
        assertEquals(value, 2);
    }

    @Test
    @DisplayName("Find photo by id.")
    public void findImageId() {
        Image image = imageRepo.findImageId(1);
        assertNotNull(image);
        assertEquals("Cape Town", image.getPhotoLocation());
    }

    @Test
    @DisplayName("Verify photo exists")
    public void existImageIdAndImageLink() {
        boolean value = imageRepo.existImageIdAndImageLink(4, "IMG_004.jpeg");
        assertTrue(value);
    }
}

