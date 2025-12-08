package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MUser model class
 */
class MUserTest {

    @Test
    void testUserIdGetterSetter() {
        MUser user = new MUser();
        user.setUserid("testuser");
        assertEquals("testuser", user.getUserid());
    }

    @Test
    void testNameGetterSetter() {
        MUser user = new MUser();
        user.setName("Test Name");
        assertEquals("Test Name", user.getName());
    }

    @Test
    void testCodeGetterSetter() {
        MUser user = new MUser();
        user.setCode(12345678);
        assertEquals(12345678, user.getCode());
    }

    @Test
    void testEmailGetterSetter() {
        MUser user = new MUser();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testRoleGetterSetter() {
        MUser user = new MUser();
        user.setRole("student");
        assertEquals("student", user.getRole());
    }

    @Test
    void testCampusIdGetterSetter() {
        MUser user = new MUser();
        user.setCampusId(1);
        assertEquals(1, user.getCampusId());
    }

    @Test
    void testCollegeIdGetterSetter() {
        MUser user = new MUser();
        user.setCollegeId(2);
        assertEquals(2, user.getCollegeId());
    }

    @Test
    void testDepartmentIdGetterSetter() {
        MUser user = new MUser();
        user.setDepartmentId(3);
        assertEquals(3, user.getDepartmentId());
    }

    @Test
    void testCampusGetterSetter() {
        MUser user = new MUser();
        user.setCampus("Seoul Campus");
        assertEquals("Seoul Campus", user.getCampus());
    }

    @Test
    void testCollegeGetterSetter() {
        MUser user = new MUser();
        user.setCollege("Engineering");
        assertEquals("Engineering", user.getCollege());
    }

    @Test
    void testDepartmentGetterSetter() {
        MUser user = new MUser();
        user.setDepartment("Computer Science");
        assertEquals("Computer Science", user.getDepartment());
    }

    @Test
    void testUserCreation() {
        MUser user = new MUser();
        assertNotNull(user);
    }

    @Test
    void testMultipleFieldsSetAndGet() {
        MUser user = new MUser();
        user.setUserid("student123");
        user.setName("John Doe");
        user.setCode(20240001);
        user.setEmail("john@example.com");
        user.setRole("student");
        user.setCampusId(1);
        user.setCollegeId(2);
        user.setDepartmentId(3);
        user.setCampus("Main Campus");
        user.setCollege("Science");
        user.setDepartment("Physics");

        assertEquals("student123", user.getUserid());
        assertEquals("John Doe", user.getName());
        assertEquals(20240001, user.getCode());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("student", user.getRole());
        assertEquals(1, user.getCampusId());
        assertEquals(2, user.getCollegeId());
        assertEquals(3, user.getDepartmentId());
        assertEquals("Main Campus", user.getCampus());
        assertEquals("Science", user.getCollege());
        assertEquals("Physics", user.getDepartment());
    }
}
