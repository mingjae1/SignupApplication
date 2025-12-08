package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MMain model class
 */
class MMainTest {

    @Test
    void testDefaultConstructor() {
        MMain mMain = new MMain();
        assertNull(mMain.getCurrentUserId());
    }

    @Test
    void testSetAndGetCurrentUserId() {
        MMain mMain = new MMain();
        mMain.setCurrentUserId("testuser");
        assertEquals("testuser", mMain.getCurrentUserId());
    }

    @Test
    void testSetCurrentUserIdToNull() {
        MMain mMain = new MMain();
        mMain.setCurrentUserId("testuser");
        assertEquals("testuser", mMain.getCurrentUserId());
        
        mMain.setCurrentUserId(null);
        assertNull(mMain.getCurrentUserId());
    }

    @Test
    void testMultipleUserIdChanges() {
        MMain mMain = new MMain();
        
        mMain.setCurrentUserId("user1");
        assertEquals("user1", mMain.getCurrentUserId());
        
        mMain.setCurrentUserId("user2");
        assertEquals("user2", mMain.getCurrentUserId());
        
        mMain.setCurrentUserId("admin");
        assertEquals("admin", mMain.getCurrentUserId());
    }

    @Test
    void testSetEmptyStringUserId() {
        MMain mMain = new MMain();
        mMain.setCurrentUserId("");
        assertEquals("", mMain.getCurrentUserId());
    }
}
