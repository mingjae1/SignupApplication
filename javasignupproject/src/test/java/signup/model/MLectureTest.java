package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MLecture 클래스에 대한 기본 단위 테스트입니다.
 */
public class MLectureTest {
    
    @Test
    public void testDefaultConstructor() {
        MLecture lecture = new MLecture();
        assertNotNull(lecture, "Lecture object should be created");
    }
    
    @Test
    public void testConstructorWithFiveParameters() {
        MLecture lecture = new MLecture("CS101", "자료구조", "김교수", 3, "월수 10:00-11:30");
        
        assertEquals("CS101", lecture.getId());
        assertEquals("자료구조", lecture.getName());
        assertEquals("김교수", lecture.getProfessor());
        assertEquals(3, lecture.getCredits());
        assertEquals("월수 10:00-11:30", lecture.getSchedule());
        assertEquals(0, lecture.getDeptId());
    }
    
    @Test
    public void testConstructorWithSixParameters() {
        MLecture lecture = new MLecture("CS102", "알고리즘", "박교수", 3, "화목 14:00-15:30", 5);
        
        assertEquals("CS102", lecture.getId());
        assertEquals("알고리즘", lecture.getName());
        assertEquals("박교수", lecture.getProfessor());
        assertEquals(3, lecture.getCredits());
        assertEquals("화목 14:00-15:30", lecture.getSchedule());
        assertEquals(5, lecture.getDeptId());
    }
    
    @Test
    public void testGetId() {
        MLecture lecture = new MLecture("CS201", "운영체제", "이교수", 3, "월수 13:00-14:30");
        assertEquals("CS201", lecture.getId());
    }
    
    @Test
    public void testGetName() {
        MLecture lecture = new MLecture("CS202", "데이터베이스", "최교수", 3, "화목 10:00-11:30");
        assertEquals("데이터베이스", lecture.getName());
    }
    
    @Test
    public void testGetProfessor() {
        MLecture lecture = new MLecture("CS301", "컴퓨터구조", "정교수", 3, "월수 15:00-16:30");
        assertEquals("정교수", lecture.getProfessor());
    }
    
    @Test
    public void testGetCredits() {
        MLecture lecture = new MLecture("CS302", "소프트웨어공학", "강교수", 4, "화목 13:00-15:00");
        assertEquals(4, lecture.getCredits());
    }
    
    @Test
    public void testGetSchedule() {
        MLecture lecture = new MLecture("CS401", "인공지능", "윤교수", 3, "금 10:00-13:00");
        assertEquals("금 10:00-13:00", lecture.getSchedule());
    }
    
    @Test
    public void testGetDeptId() {
        MLecture lecture = new MLecture("CS402", "머신러닝", "한교수", 3, "수 14:00-17:00", 10);
        assertEquals(10, lecture.getDeptId());
    }
    
    @Test
    public void testLectureWithKoreanCharacters() {
        MLecture lecture = new MLecture(
            "한국어101", 
            "한국어와 문화", 
            "김한국", 
            3, 
            "월수금 09:00-09:50",
            15
        );
        
        assertEquals("한국어101", lecture.getId());
        assertEquals("한국어와 문화", lecture.getName());
        assertEquals("김한국", lecture.getProfessor());
        assertEquals(3, lecture.getCredits());
        assertEquals("월수금 09:00-09:50", lecture.getSchedule());
        assertEquals(15, lecture.getDeptId());
    }
}
