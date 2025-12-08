package signup.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MLecture model class
 */
class MLectureTest {

    @Test
    void testDefaultConstructor() {
        MLecture lecture = new MLecture();
        assertNotNull(lecture);
    }

    @Test
    void testConstructorWithFiveParameters() {
        MLecture lecture = new MLecture("CS101", "Computer Science", "Dr. Smith", 3, "Mon 9:00-10:30");
        
        assertEquals("CS101", lecture.getId());
        assertEquals("Computer Science", lecture.getName());
        assertEquals("Dr. Smith", lecture.getProfessor());
        assertEquals(3, lecture.getCredits());
        assertEquals("Mon 9:00-10:30", lecture.getSchedule());
        assertEquals(0, lecture.getDeptId());
    }

    @Test
    void testConstructorWithSixParameters() {
        MLecture lecture = new MLecture("CS201", "Data Structures", "Dr. Johnson", 4, "Tue 13:00-15:00", 5);
        
        assertEquals("CS201", lecture.getId());
        assertEquals("Data Structures", lecture.getName());
        assertEquals("Dr. Johnson", lecture.getProfessor());
        assertEquals(4, lecture.getCredits());
        assertEquals("Tue 13:00-15:00", lecture.getSchedule());
        assertEquals(5, lecture.getDeptId());
    }

    @Test
    void testGetters() {
        MLecture lecture = new MLecture("MATH101", "Calculus", "Prof. Lee", 3, "Wed 10:00-12:00", 10);
        
        assertEquals("MATH101", lecture.getId());
        assertEquals("Calculus", lecture.getName());
        assertEquals("Prof. Lee", lecture.getProfessor());
        assertEquals(3, lecture.getCredits());
        assertEquals("Wed 10:00-12:00", lecture.getSchedule());
        assertEquals(10, lecture.getDeptId());
    }

    @Test
    void testWithKoreanCharacters() {
        MLecture lecture = new MLecture("한국어101", "한국어 기초", "김교수", 2, "월 14:00-16:00", 3);
        
        assertEquals("한국어101", lecture.getId());
        assertEquals("한국어 기초", lecture.getName());
        assertEquals("김교수", lecture.getProfessor());
        assertEquals(2, lecture.getCredits());
        assertEquals("월 14:00-16:00", lecture.getSchedule());
        assertEquals(3, lecture.getDeptId());
    }

    @Test
    void testWithZeroCredits() {
        MLecture lecture = new MLecture("SEM100", "Seminar", "Various", 0, "Fri 15:00-17:00");
        assertEquals(0, lecture.getCredits());
    }
}
