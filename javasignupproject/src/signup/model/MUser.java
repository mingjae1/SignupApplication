package signup.model;

/**
 * '사용자' 1명의 데이터를 담는 DTO (Data Transfer Object) 클래스입니다.
 * (DB의 'user' 테이블과 매칭됩니다.)
 */
public class MUser {
    
    // DB user 테이블의 컬럼들과 일치시킵니다.
    private String userid;
    private String name;
    private int code; // 학번
    private String email;
    private int campusId;
    private int collegeId;
    private int departmentId;
    private String role;
    private String campus;      // 캠퍼스 이름
    private String college;     // 단과대학 이름
    private String department;  // 학과 이름
    
    public MUser() {}

    // --- 각 필드에 대한 Getter와 Setter ---
    
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public int getCampusId() { return campusId; }
    public void setCampusId(int campusId) { this.campusId = campusId; }

    public int getCollegeId() { return collegeId; }
    public void setCollegeId(int collegeId) { this.collegeId = collegeId; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

}