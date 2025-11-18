/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package commons;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileTest {

    /**
     * Tests the setter method for the ID of the File object.
     * Verifies that the ID is set correctly.
     */
    @Test
    public void testSetId() {
        File file = new File("fileName", "contentType", null);
        file.setId(1);
        assertEquals(1, file.getId());
    }

    /**
     * Tests the setter method for the content type of the File object.
     * Verifies that the content type is set correctly.
     */
    @Test
    public void testSetContentType() {
        File file = new File("fileName", "contentType", null);
        file.setContentType("contentType2");
        assertEquals("contentType2", file.getContentType());
    }

    /**
     * Tests the setter method for the file name of the File object.
     * Verifies that the file name is set correctly.
     */
    @Test
    public void testSetFileName() {
        File file = new File("fileName", "contentType", null);
        file.setFileName("fileName2");
        assertEquals("fileName2", file.getFileName());
    }

    /**
     * Tests the `equals` method of the File object for inequality.
     * Verifies that two File objects with different properties are not considered equal.
     */
    @Test
    public void testNotEquals() {
        File file = new File("fileName", "contentType", null);
        File file2 = new File("fileName2", "contentType2", null);
        assertNotEquals(file, file2);
    }

    /**
     * Tests the `equals` method of the File object for equality.
     * Verifies that two File objects with the same properties are considered equal.
     */
    @Test
    public void testEquals() {
        File file = new File("fileName", "contentType", null);
        File file2 = new File("fileName", "contentType", null);
        assertEquals(file, file2);
    }

    /**
     * Tests the `hashCode` method of the File object when objects are not equal.
     * Verifies that the hash codes of two different File objects are not the same.
     */
    @Test
    public void testHashCodeNotEqual() {
        File file = new File("fileName", "contentType", null);
        File file2 = new File("fileName2", "contentType2", null);
        assertNotEquals(file.hashCode(), file2.hashCode());
    }

    /**
     * Tests the `hashCode` method of the File object when objects are equal.
     * Verifies that the hash codes of two equal File objects are the same.
     */
    @Test
    public void testHashCodeEqual() {
        File file = new File("fileName", "contentType", null);
        File file2 = new File("fileName", "contentType", null);
        assertEquals(file.hashCode(), file2.hashCode());
    }


    /**
     * Tests both the getter and setter of the addedTime at the same time.
     */
    @Test
    public void setGetDateAdded() {
        File file = new File("test", "cType", null);
        ZonedDateTime time = ZonedDateTime.now();
        file.setDateAdded(time);
        assertEquals(file.getDateAdded(), time);
    }

    /**
     * Test both the getter and setter of the size at the same time
     */
    @Test
    public void setGetSize() {
        File file = new File("test", "cType", null);
        long size = 500;
        file.setSize(size);
        assertEquals(file.getSize(), size);
    }
}
