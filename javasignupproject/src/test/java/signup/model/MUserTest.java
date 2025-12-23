package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MUser 클래스에 대한 기본 단위 테스트입니다.
 */
public class MUserTest {
    
    @Test
    public void testUserCreation() {
        MUser user = new MUser();
        assertNotNull(user, "User object should be created");
    }
    
    @Test
    public void testUserIdGetterAndSetter() {
        MUser user = new MUser();
        String testUserId = "testuser123";
        
        user.setUserid(testUserId);
        
        assertEquals(testUserId, user.getUserid(), 
                    "User ID should match the set value");
    }
    
    @Test
    public void testNameGetterAndSetter() {
        MUser user = new MUser();
        String testName = "홍길동";
        
        user.setName(testName);
        
        assertEquals(testName, user.getName(), 
                    "Name should match the set value");
    }
    
    @Test
    public void testCodeGetterAndSetter() {
        MUser user = new MUser();
        int testCode = 20241234;
        
        user.setCode(testCode);
        
        assertEquals(testCode, user.getCode(), 
                    "Code should match the set value");
    }
    
    @Test
    public void testEmailGetterAndSetter() {
        MUser user = new MUser();
        String testEmail = "test@example.com";
        
        user.setEmail(testEmail);
        
        assertEquals(testEmail, user.getEmail(), 
                    "Email should match the set value");
    }
    
    @Test
    public void testRoleGetterAndSetter() {
        MUser user = new MUser();
        String testRole = "student";
        
        user.setRole(testRole);
        
        assertEquals(testRole, user.getRole(), 
                    "Role should match the set value");
    }
    
    @Test
    public void testCampusIdGetterAndSetter() {
        MUser user = new MUser();
        int testCampusId = 1;
        
        user.setCampusId(testCampusId);
        
        assertEquals(testCampusId, user.getCampusId(), 
                    "Campus ID should match the set value");
    }
    
    @Test
    public void testCollegeIdGetterAndSetter() {
        MUser user = new MUser();
        int testCollegeId = 2;
        
        user.setCollegeId(testCollegeId);
        
        assertEquals(testCollegeId, user.getCollegeId(), 
                    "College ID should match the set value");
    }
    
    @Test
    public void testDepartmentIdGetterAndSetter() {
        MUser user = new MUser();
        int testDepartmentId = 3;
        
        user.setDepartmentId(testDepartmentId);
        
        assertEquals(testDepartmentId, user.getDepartmentId(), 
                    "Department ID should match the set value");
    }
    
    @Test
    public void testCampusGetterAndSetter() {
        MUser user = new MUser();
        String testCampus = "서울캠퍼스";
        
        user.setCampus(testCampus);
        
        assertEquals(testCampus, user.getCampus(), 
                    "Campus should match the set value");
    }
    
    @Test
    public void testCollegeGetterAndSetter() {
        MUser user = new MUser();
        String testCollege = "공과대학";
        
        user.setCollege(testCollege);
        
        assertEquals(testCollege, user.getCollege(), 
                    "College should match the set value");
    }
    
    @Test
    public void testDepartmentGetterAndSetter() {
        MUser user = new MUser();
        String testDepartment = "컴퓨터공학과";
        
        user.setDepartment(testDepartment);
        
        assertEquals(testDepartment, user.getDepartment(), 
                    "Department should match the set value");
    }
    
    @Test
    public void testUserWithAllFields() {
        MUser user = new MUser();
        
        user.setUserid("student001");
        user.setName("김철수");
        user.setCode(20241001);
        user.setEmail("student001@university.ac.kr");
        user.setRole("student");
        user.setCampusId(1);
        user.setCollegeId(2);
        user.setDepartmentId(5);
        user.setCampus("서울캠퍼스");
        user.setCollege("공과대학");
        user.setDepartment("컴퓨터공학과");
        
        assertEquals("student001", user.getUserid());
        assertEquals("김철수", user.getName());
        assertEquals(20241001, user.getCode());
        assertEquals("student001@university.ac.kr", user.getEmail());
        assertEquals("student", user.getRole());
        assertEquals(1, user.getCampusId());
        assertEquals(2, user.getCollegeId());
        assertEquals(5, user.getDepartmentId());
        assertEquals("서울캠퍼스", user.getCampus());
        assertEquals("공과대학", user.getCollege());
        assertEquals("컴퓨터공학과", user.getDepartment());
    }
}
