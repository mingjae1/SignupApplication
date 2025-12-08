package signup.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PanelNames constants class
 */
class PanelNamesTest {

    @Test
    void testLoginPanel() {
        assertEquals("loginPanel", PanelNames.LOGIN_PANEL);
    }

    @Test
    void testSignupPanel() {
        assertEquals("signupPanel", PanelNames.SIGNUP_PANEL);
    }

    @Test
    void testMainContentPanel() {
        assertEquals("mainContentPanel", PanelNames.MAIN_CONTENT_PANEL);
    }

    @Test
    void testSearchPanel() {
        assertEquals("searchPanel", PanelNames.SEARCH_PANEL);
    }

    @Test
    void testRegisterPanel() {
        assertEquals("registerPanel", PanelNames.REGISTER_PANEL);
    }

    @Test
    void testPreRegisterPanel() {
        assertEquals("preRegisterPanel", PanelNames.PREREGISTER_PANEL);
    }

    @Test
    void testSchedulePanel() {
        assertEquals("schedulePanel", PanelNames.SCHEDULE_PANEL);
    }

    @Test
    void testAdminPanel() {
        assertEquals("adminPanel", PanelNames.ADMIN_PANEL);
    }

    @Test
    void testAllPanelNamesAreUnique() {
        String[] panelNames = {
            PanelNames.LOGIN_PANEL,
            PanelNames.SIGNUP_PANEL,
            PanelNames.MAIN_CONTENT_PANEL,
            PanelNames.SEARCH_PANEL,
            PanelNames.REGISTER_PANEL,
            PanelNames.PREREGISTER_PANEL,
            PanelNames.SCHEDULE_PANEL,
            PanelNames.ADMIN_PANEL
        };
        
        for (int i = 0; i < panelNames.length; i++) {
            for (int j = i + 1; j < panelNames.length; j++) {
                assertNotEquals(panelNames[i], panelNames[j], 
                    "Panel names should be unique");
            }
        }
    }
}
