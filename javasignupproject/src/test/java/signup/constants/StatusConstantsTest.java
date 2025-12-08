package signup.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StatusConstants class
 */
class StatusConstantsTest {

    @Test
    void testRegisterStatus() {
        assertEquals("reg", StatusConstants.REGISTER);
    }

    @Test
    void testPreRegisterStatus() {
        assertEquals("pre", StatusConstants.PRE_REGISTER);
    }

    @Test
    void testStatusValuesAreNotNull() {
        assertNotNull(StatusConstants.REGISTER);
        assertNotNull(StatusConstants.PRE_REGISTER);
    }

    @Test
    void testStatusValuesAreDifferent() {
        assertNotEquals(StatusConstants.REGISTER, StatusConstants.PRE_REGISTER);
    }

    @Test
    void testStatusValuesAreShortStrings() {
        assertTrue(StatusConstants.REGISTER.length() <= 3);
        assertTrue(StatusConstants.PRE_REGISTER.length() <= 3);
    }
}
