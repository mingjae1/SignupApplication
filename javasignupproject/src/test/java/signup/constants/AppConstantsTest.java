package signup.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppConstants class
 */
class AppConstantsTest {

    @Test
    void testMaxCredits() {
        assertEquals(18, AppConstants.MAX_CREDITS);
    }

    @Test
    void testUserIdLengthConstants() {
        assertEquals(3, AppConstants.MIN_USER_ID_LENGTH);
        assertEquals(15, AppConstants.MAX_USER_ID_LENGTH);
        assertTrue(AppConstants.MIN_USER_ID_LENGTH < AppConstants.MAX_USER_ID_LENGTH);
    }

    @Test
    void testPasswordLengthConstants() {
        assertEquals(8, AppConstants.MIN_PASSWORD_LENGTH);
        assertEquals(20, AppConstants.MAX_PASSWORD_LENGTH);
        assertTrue(AppConstants.MIN_PASSWORD_LENGTH < AppConstants.MAX_PASSWORD_LENGTH);
    }

    @Test
    void testStudentCodeLength() {
        assertEquals(8, AppConstants.STUDENT_CODE_LENGTH);
    }

    @Test
    void testLoginWindowDimensions() {
        assertEquals(420, AppConstants.LOGIN_WINDOW_WIDTH);
        assertEquals(320, AppConstants.LOGIN_WINDOW_HEIGHT);
    }

    @Test
    void testSignupWindowDimensions() {
        assertEquals(800, AppConstants.SIGNUP_WINDOW_WIDTH);
        assertEquals(600, AppConstants.SIGNUP_WINDOW_HEIGHT);
    }

    @Test
    void testMainWindowDimensions() {
        assertEquals(1280, AppConstants.MAIN_WINDOW_WIDTH);
        assertEquals(800, AppConstants.MAIN_WINDOW_HEIGHT);
    }

    @Test
    void testUiScale() {
        assertEquals("1.2", AppConstants.UI_SCALE);
        assertNotNull(AppConstants.UI_SCALE);
    }

    @Test
    void testDatabaseErrorCodes() {
        assertEquals(0, AppConstants.DB_SUCCESS);
        assertEquals(1, AppConstants.DB_ERROR_CREDIT_EXCEEDED);
        assertEquals(2, AppConstants.DB_ERROR_DUPLICATE);
        assertEquals(-1, AppConstants.DB_ERROR_GENERAL);
    }

    @Test
    void testFileConstants() {
        assertEquals(".png", AppConstants.PNG_EXTENSION);
        assertEquals("내시간표.png", AppConstants.DEFAULT_SCHEDULE_FILENAME);
        assertTrue(AppConstants.DEFAULT_SCHEDULE_FILENAME.endsWith(AppConstants.PNG_EXTENSION));
    }
}
