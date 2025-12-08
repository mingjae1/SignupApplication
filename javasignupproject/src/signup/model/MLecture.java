package signup.model;

/**
 * 강의(Lecture) 하나의 정보를 담는 데이터 모델 클래스 (DTO)입니다.
 * 이 객체는 MMain이 파일에서 읽어온 데이터를 CMain/VMain 등
 * 다른 MVC 컴포넌트로 전달할 때 사용됩니다.
 */
public class MLecture {
    
    private String id;
    private String name;
    private String professor;
    private int credits;
    private String schedule;
    private int deptId;
    
    public MLecture() {}
    
    public MLecture(String id, String name, String professor, int credits, String schedule) {
        this(id, name, professor, credits, schedule, 0);
    }
    
    public MLecture(String id, String name, String professor, int credits, String schedule, int deptId) {
        this.id = id;
        this.name = name;
        this.professor = professor;
        this.credits = credits;
        this.schedule = schedule;
        this.deptId = deptId;
    }

    /**
     * @return 과목 코드를 반환합니다.
     */
    public String getId() { return id; }
    public String getName() { return name; }
    public String getProfessor() { return professor; }
    public int getCredits() { return credits; }
    public String getSchedule() { return schedule; }
    
    public int getDeptId() { return deptId; }
}