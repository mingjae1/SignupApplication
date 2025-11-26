package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.model.ComboboxItem;
import signup.model.MLecture; 

/**
 * 'lecture', 'root', 'college', 'department' 테이블 관련
 * 모든 DB 조회 작업을 전담하는 DAO 클래스입니다.
 */
public class LectureDAO {
    
    private DAO dao; 
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    private static final Logger logger = Logger.getLogger(LectureDAO.class.getName());

    public LectureDAO() {
        this.dao = new DAO();
    }

    /**
     * VSignup의 '캠퍼스' 콤보박스를 채우기 위해 DB의 'root' 테이블에서 모든 캠퍼스 이름을 가져옵니다.
     * @return 캠퍼스 이름과 ID가 담긴 ComboboxItem 리스트
     */
    public List<ComboboxItem> getAllCampuses() {
        List<ComboboxItem> campuses = new ArrayList<>();
        conn = dao.getConnection();
        String sql = "SELECT id, name FROM root ORDER BY id"; 

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                campuses.add(new ComboboxItem(
                    rs.getString("name"),
                    rs.getInt("id") 
                ));
            } } 
        catch (SQLException e) { logger.log(Level.SEVERE, "모든 캠퍼스 조회 중 SQL 오류", e); } 
        catch (NullPointerException e) { logger.log(Level.SEVERE, "DB 연결 실패. DAO의 getConnection()을 확인하세요.", e); }
        finally { DAO.close(rs, pstmt, conn); }
        return campuses;
    }

    /**
     * VSignup에서 선택한 '캠퍼스 ID'에 소속된 '단과대학' 목록을 가져옵니다.
     * @param campusId 사용자가 선택한 캠퍼스 ID (예: 1)
     * @return 해당 캠퍼스의 단과대학 ComboboxItem 리스트
     */
    public List<ComboboxItem> getCollegesByCampus(int campusId) { // [수정] String -> int
        List<ComboboxItem> colleges = new ArrayList<>();
        conn = dao.getConnection();
        
        // [수정] WHERE r.name = ? -> WHERE c.root_id = ?
        String sql = "SELECT c.id, c.name FROM college c WHERE c.root_id = ? " +
                     "AND c.name != '교양' ORDER BY c.id"; // 회원가입 시 '교양' 제외
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, campusId); // [수정]
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                colleges.add(new ComboboxItem(
                    rs.getString("name"),
                    rs.getInt("id")
                ));
            }
        } 
        catch (SQLException e) { logger.log(Level.WARNING, "캠퍼스별 단과대학 조회 SQL 오류", e); }
        finally { DAO.close(rs, pstmt, conn); }
        return colleges;
    }

    /**
     * VSignup에서 선택한 '단과대학 ID'에 소속된 '학과' 목록을 가져옵니다.
     * @param collegeId 사용자가 선택한 단과대학 ID (예: 11)
     * @return 해당 단과대학의 학과 ComboboxItem 리스트
     */
    public List<ComboboxItem> getDepartmentsByCollege(int collegeId) { // [수정] String -> int
        List<ComboboxItem> departments = new ArrayList<>();
        conn = dao.getConnection();
        
        // [수정] WHERE c.name = ? -> WHERE d.college_id = ?
        String sql = "SELECT d.id, d.name FROM department d WHERE d.college_id = ? ORDER BY d.id";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, collegeId); // [수정]
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                departments.add(new ComboboxItem(
                    rs.getString("name"),
                    rs.getInt("id")
                ));
            } } 
        catch (SQLException e) { logger.log(Level.WARNING, "단과대학별 학과 조회 SQL 오류", e); }
        finally { DAO.close(rs, pstmt, conn); }
        return departments;
    }
    
    /**
     * VSearch 패널의 검색 조건에 맞는 강좌 목록을 DB에서 조회합니다.
     * (이하 코드는 동일)
     */
    public List<MLecture> searchLectures(String userId, String collegeName, String deptName, String keyword) {
        List<MLecture> lectures = new ArrayList<>();
        conn = dao.getConnection();

        StringBuilder sql = new StringBuilder(
            "SELECT l.id, l.name, l.professor, l.credit, l.time " +
            "FROM lecture l " +
            "JOIN department d ON l.department_id = d.id " +
            "JOIN college c ON d.college_id = c.id " +
            "JOIN root r ON c.root_id = r.id " +
            "JOIN user u ON r.id = u.campus_id " + 
            "WHERE u.userid = ? " 
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append(" AND c.name = ?");
            params.add(collegeName);
        }
        
        if (deptName != null && !deptName.isEmpty()) {
            sql.append(" AND d.name = ?");
            params.add(deptName);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (REPLACE(l.name, ' ', '') LIKE ? OR REPLACE(l.professor, ' ', '') LIKE ?)");
            String keywordNoSpace = "%" + keyword.replace(" ", "") + "%";
            params.add(keywordNoSpace); 
            params.add(keywordNoSpace);
        }

        try {
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MLecture lecture = new MLecture(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getInt("credit"),
                    rs.getString("time")
                );
                lectures.add(lecture);
            } } 
        catch (SQLException e) { logger.log(Level.WARNING, "강의 검색 SQL 오류", e); } 
        catch (NullPointerException e) { logger.log(Level.SEVERE, "DB 연결 실패. DAO의 getConnection()을 확인하세요.", e); } 
        finally { DAO.close(rs, pstmt, conn); }
        return lectures;
    }
    
 // ==========================================
    //          [관리자 모드 전용 메서드]
    // ==========================================

    /**
     * [관리자] 모든 강의 목록을 조회합니다.
     * @return 전체 강의 리스트
     */
    public List<MLecture> getAllLectures() {
        List<MLecture> lectures = new ArrayList<>();
        conn = dao.getConnection();
        if (conn == null) return lectures;

        String sql = "SELECT * FROM lecture ORDER BY id";

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MLecture lecture = new MLecture(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getInt("credit"),
                    rs.getString("time"),
                    rs.getInt("department_id") // [추가] DB에서 학과 ID 가져오기
                );
                lectures.add(lecture);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "전체 강의 조회 SQL 오류", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return lectures;
    }
   
    /**
     * 모든 학과 목록을 조회합니다. (학과 코드 확인용)
     * @return "112: 컴퓨터공학과 (공과대학)" 형태의 문자열 리스트
     */
    public List<String> getAllDepartments() {
        List<String> list = new ArrayList<>();
        conn = dao.getConnection();
        // 학과 이름과 단과대학 이름을 같이 가져오는 조인 쿼리
        String sql = "SELECT d.id, d.name, c.name AS college_name " +
                     "FROM department d " +
                     "JOIN college c ON d.college_id = c.id " +
                     "ORDER BY d.id";
        
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String info = String.format("%d: %s (%s)", 
                    rs.getInt("id"), rs.getString("name"), rs.getString("college_name"));
                list.add(info);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "학과 목록 조회 오류", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return list;
    }
    /**
     * [관리자] 새 강의를 추가합니다.
     * @param id 과목코드 (PK)
     * @param name 과목명
     * @param prof 교수명
     * @param credit 학점
     * @param time 시간표
     * @param deptId 소속 학과 ID
     * @return 성공 여부
     */
    public boolean insertLecture(int id, String name, String prof, int credit, String time, int deptId) {
        conn = dao.getConnection();
        if (conn == null) return false;
        
        String sql = "INSERT INTO lecture (id, name, professor, credit, time, department_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, prof);
            pstmt.setInt(4, credit);
            pstmt.setString(5, time);
            pstmt.setInt(6, deptId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 추가 SQL 오류", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }

    /**
     * [관리자] 강의 정보를 수정합니다.
     */
    public boolean updateLecture(int id, String name, String prof, int credit, String time, int deptId) {
        conn = dao.getConnection();
        if (conn == null) return false;
        
        String sql = "UPDATE lecture SET name=?, professor=?, credit=?, time=?, department_id=? WHERE id=?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, prof);
            pstmt.setInt(3, credit);
            pstmt.setString(4, time);
            pstmt.setInt(5, deptId);
            pstmt.setInt(6, id);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 수정 SQL 오류", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }

    /**
     * [관리자] 강의를 삭제합니다.
     */
    public boolean deleteLecture(int id) {
        conn = dao.getConnection();
        if (conn == null) return false;
        
        String sql = "DELETE FROM lecture WHERE id = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 삭제 SQL 오류", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }

}