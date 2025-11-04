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
import signup.model.MLecture; // 님의 DTO 클래스 이름 (MLecture)

/**
 * 'lecture', 'root', 'college', 'department' 테이블 관련
 * 모든 DB 조회 작업을 전담하는 DAO 클래스입니다.
 */
public class LectureDAO {
    
    private DAO dao; // DB 연결을 위한 기본 DAO
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    private static final Logger logger = Logger.getLogger(LectureDAO.class.getName());

    public LectureDAO() {
        this.dao = new DAO();
    }

    /**
     * VSignup의 '캠퍼스' 콤보박스를 채우기 위해 DB의 'root' 테이블에서 모든 캠퍼스 이름을 가져옵니다.
     * @return 캠퍼스 이름의 리스트 (List<String>)
     */
    public List<ComboboxItem> getAllCampuses() {
        List<ComboboxItem> campuses = new ArrayList<>();
        conn = dao.getConnection();
        // 쿼리 수정: ID도 함께 가져옴
        String sql = "SELECT id, name FROM root ORDER BY id"; 

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // ComboboxItem 객체 생성
                campuses.add(new ComboboxItem(
                    rs.getString("name"),
                    rs.getInt("id") // ID도 가져와서 DTO에 저장
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "모든 캠퍼스 조회 중 SQL 오류", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return campuses;
    }

    /**
     * VSignup에서 선택한 '캠퍼스'에 소속된 '단과대학' 목록을 가져옵니다.
     * @param campusName 사용자가 선택한 캠퍼스 이름 (예: "용인", "서울")
     * @return 해당 캠퍼스의 단과대학 이름 리스트
     */
    public List<ComboboxItem> getCollegesByCampus(String campusName) {
        List<ComboboxItem> colleges = new ArrayList<>();
        conn = dao.getConnection();
        // root(r) 테이블과 college(c) 테이블을 JOIN
        String sql = "SELECT c.name FROM college c " +
                     "JOIN root r ON c.root_id = r.id " +
                     "WHERE r.name = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, campusName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rs.getString("name");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "캠퍼스별 단과대학 조회 SQL 오류", e);
        } finally {
        	DAO.close(rs, pstmt, conn);
        }
        return colleges;
    }

    /**
     * VSignup에서 선택한 '단과대학'에 소속된 '학과' 목록을 가져옵니다.
     * @param collegeName 사용자가 선택한 단과대학 이름 (예: "공과대학")
     * @return 해당 단과대학의 학과 이름 리스트
     */
    public List<ComboboxItem> getDepartmentsByCollege(String collegeName) {
        List<ComboboxItem> departments = new ArrayList<>();
        conn = dao.getConnection();
        // college(c) 테이블과 department(d) 테이블을 JOIN
        String sql = "SELECT d.name FROM department d " +
                     "JOIN college c ON d.college_id = c.id " +
                     "WHERE c.name = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, collegeName);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rs.getString("name");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "단과대학별 학과 조회 SQL 오류", e);
        } finally {
        	DAO.close(rs, pstmt, conn);
        }
        return departments;
    }
    
    /**
     * VSearch 패널의 검색 조건에 맞는 강좌 목록을 DB에서 조회합니다.
     * 사용자의 캠퍼스에 맞는 강의만 자동으로 필터링합니다.
     *
     * @param userId      현재 로그인한 사용자 ID (캠퍼스 필터링용)
     * @param collegeName 사용자가 선택한 단과대학 (선택 안 했으면 "모두")
     * @param deptName    사용자가 선택한 학과 (선택 안 했으면 "모두")
     * @param keyword     사용자가 입력한 검색어 (과목명/교수명)
     * @return 검색 조건에 맞는 MLecture 객체 리스트
     */
    public List<MLecture> searchLectures(String userId, String collegeName, String deptName, String keyword) {
        List<MLecture> lectures = new ArrayList<>();
        conn = dao.getConnection();

        // 1. 기본 SQL 쿼리: 5개 테이블을 JOIN하고 사용자의 캠퍼스로 자동 필터링
        // (l=lecture, d=department, c=college, r=root, u=user)
        StringBuilder sql = new StringBuilder(
            "SELECT l.id, l.name, l.professor, l.credit, l.time " +
            "FROM lecture l " +
            "JOIN department d ON l.department_id = d.id " +
            "JOIN college c ON d.college_id = c.id " +
            "JOIN root r ON c.root_id = r.id " +
            "JOIN user u ON r.name = u.campus " + // 사용자의 캠퍼스와 강의의 캠퍼스를 조인
            "WHERE u.userid = ? " // 1. 사용자의 ID로 필터
        );

        // SQL 인젝션 공격을 방지하기 위해, PreparedStatement에 들어갈 파라미터 리스트를 만듭니다.
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 2. 동적 쿼리 생성: 사용자가 선택한 조건들을 SQL에 추가
        
        // "모두"가 아닌 특정 단과대학을 선택한 경우
        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append(" AND c.name = ?");
            params.add(collegeName);
        }
        
        // "모두"가 아닌 특정 학과를 선택한 경우
        if (deptName != null && !deptName.isEmpty()) {
            sql.append(" AND d.name = ?");
            params.add(deptName);
        }
        
        // 검색어를 입력한 경우 (띄어쓰기 무시)
        if (keyword != null && !keyword.trim().isEmpty()) {
            // REPLACE()로 공백을 제거하고, LIKE로 부분 일치 검색
            sql.append(" AND (REPLACE(l.name, ' ', '') LIKE ? OR REPLACE(l.professor, ' ', '') LIKE ?)");
            String keywordNoSpace = "%" + keyword.replace(" ", "") + "%";
            params.add(keywordNoSpace); // 과목명 검색
            params.add(keywordNoSpace); // 교수명 검색
        }

        try {
            // 3. 완성된 SQL로 PreparedStatement 생성
            pstmt = conn.prepareStatement(sql.toString());

            // 4. 파라미터 리스트에 담긴 값들을 순서대로 SQL에 바인딩
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
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 검색 SQL 오류", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "DB 연결 실패. DAO의 getConnection()을 확인하세요.", e);
        } finally {
            DAO.close(rs, pstmt, conn); // 공용 close 메서드 사용
        }
        return lectures;
    }
    
    
    
    

}