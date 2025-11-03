package signup.model;

/**
 * 강의(Lecture) 하나의 정보를 담는 데이터 모델 클래스 (DTO)입니다.
 * 이 객체는 MMain이 파일에서 읽어온 데이터를 CMain/VMain 등
 * 다른 MVC 컴포넌트로 전달할 때 사용됩니다.
 */
public class Lecture {
    
    private String id;        // 과목 코드
    private String name;      // 과목명
    private String professor; // 교수명
    private int credits;      // 학점
    private String schedule;  // 시간표

    /**
     * 새로운 Lecture 객체를 생성합니다.
     *
     * @param id        과목 코드 (e.g., "1")
     * @param name      과목명 (e.g., "행정학의이해")
     * @param professor 교수명 (e.g., "이현정")
     * @param credits   학점 (e.g., 3)
     * @param schedule  시간표 (e.g., "목12:00-14:45")
     */
    public Lecture(String id, String name, String professor, int credits, String schedule) {
        this.id = id;
        this.name = name;
        this.professor = professor;
        this.credits = credits;
        this.schedule = schedule;
    }

    /**
     * @return 과목 코드를 반환합니다.
     */
    public String getId() {
        return id;
    }

    /**
     * @return 과목명을 반환합니다.
     */
    public String getName() {
        return name;
    }

    /**
     * @return 교수명을 반환합니다.
     */
    public String getProfessor() {
        return professor;
    }

    /**
     * @return 학점을 반환합니다.
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @return 시간표를 반환합니다.
     */
    public String getSchedule() {
        return schedule;
    }
}