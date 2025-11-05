package signup.dao; // 또는 signup.dao

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.model.MUser;

public class UserDAO {

    private DAO dao; // 1. 방금 만든 기본 DAO 객체
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public UserDAO() {
        this.dao = new DAO(); // 2. 기본 DAO를 생성
    }

    /**
     * 로그인을 시도하는 메서드.
     * @param id 사용자가 입력한 ID
     * @param password 사용자가 입력한 Password
     * @return 로그인 성공 시 사용자의 이름(name), 실패 시 null
     */
    public String validateUser(String id, String password) {
        // 3. DAO로부터 DB 연결(Connection)을 받아옵니다.
        conn = dao.getConnection(); 
        
        // (참고) SQL 파일에 login 테이블과 user 테이블이 둘 다 있습니다.
        // 이 쿼리는 login 테이블에서 비번을 확인하고, user 테이블에서 이름을 가져옵니다.
        String sql = "SELECT u.name FROM login l " +
                     "JOIN user u ON l.userId = u.userid " +
                     "WHERE l.userId = ? AND l.password = ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 4. 로그인 성공: user 테이블의 'name' 컬럼 값을 반환
                return rs.getString("name"); 
            } else {
                // 5. 로그인 실패: 일치하는 사용자가 없음
                return null; 
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "로그인 인증 중 SQL 오류 발생", e);
            return null;
        } finally {
            // 6. (중요) 사용한 자원(ResultSet, PreparedStatement, Connection)을 닫습니다.
        	DAO.close(rs, pstmt, conn);
        }
    }
    
    /**
     * MUser DTO와 password를 받아 새 사용자를 등록합니다.
     * (매개변수 초과, NullPointerException, 롤백 버그 수정됨)
     * @param user      모든 사용자 정보가 담긴 MUser DTO
     * @param password  login 테이블에 저장할 비밀번호
     * @return 회원가입 성공 시 true, 실패(DB 오류) 시 false
     */
    public boolean addUser(MUser user, String password) { 
        
        String sqlLogin = "INSERT INTO login (userId, password) VALUES (?, ?)";
        
        // SQL 쿼리: user 테이블에 *_id 컬럼들을 사용
        String sqlUser = "INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        conn = dao.getConnection();
        
        if (conn == null) {
            logger.log(Level.SEVERE, "addUser: DB 연결 실패. Connection이 null입니다.");
            return false;
        }

        PreparedStatement pstmt = null;

        try {
            conn.setAutoCommit(false); 

            // 1. login 테이블
            pstmt = conn.prepareStatement(sqlLogin);
            pstmt.setString(1, user.getUserid());
            pstmt.setString(2, password);
            int loginResult = pstmt.executeUpdate();
            
            DAO.close(null, pstmt, null);
            
            // 2. user 테이블 (DTO에서 ID를 추출)
            pstmt = conn.prepareStatement(sqlUser);
            pstmt.setString(1, user.getUserid());
            pstmt.setString(2, user.getName());
            pstmt.setInt(3, user.getCode());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getCampusId()); // [수정됨] DTO에서 ID 추출
            pstmt.setInt(6, user.getCollegeId()); // [수정됨] DTO에서 ID 추출
            pstmt.setInt(7, user.getDepartmentId()); // [수정됨] DTO에서 ID 추출
            int userResult = pstmt.executeUpdate();

            // 3. 커밋/롤백 로직
            if (loginResult > 0 && userResult > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) { 
            logger.log(Level.WARNING, "회원가입 트랜잭션 오류 (ID/학번 중복 등)", e);
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "롤백 실패", ex);
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "AutoCommit 원상복구 실패", e);
            }
            DAO.close(null, pstmt, conn); 
        }
    }
    
    /**
     * DB의 'login' 테이블에서 userID가 이미 존재하는지 확인합니다.
     * @param id 중복 검사할 사용자 ID
     * @return 중복이면 true, 아니면 false
     */
    public boolean isUserIdDuplicate(String id) {
        conn = dao.getConnection();
        // login 테이블의 PK인 userId를 count해서 0보다 크면 중복임
        String sql = "SELECT COUNT(*) FROM login WHERE userId = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // count가 0보다 크면 true (중복) 반환
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ID 중복 검사 SQL 오류", e);
        } finally {
        	DAO.close(rs, pstmt, conn);	
        	}
        return false; // 오류가 발생하거나 결과가 없으면 false
    }
    
    /**
     * DB의 'user' 테이블에서 학번(code)이 이미 존재하는지 확인합니다.
     * @param studentId 중복 검사할 학번
     * @return 중복이면 true, 아니면 false
     */
    public boolean isStudentIdDuplicate(int studentCode) { 
        conn = dao.getConnection();
        String sql = "SELECT COUNT(*) FROM user WHERE code = ?";
        
        try {
            // [수정됨] NumberFormatException try-catch가 필요 없음 (컨트롤러가 보장)
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentCode); // int 값을 바로 설정
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // "always true" 경고 해결
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "학번 중복 검사 SQL 오류", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "DB 연결 실패.", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return false;
    }
    
    /**
     * CSearch 컨트롤러가 '단과대학' 콤보박스를 초기화하기 위해
     * 현재 로그인한 사용자의 캠퍼스 이름을 조회합니다.
     * @param userId 현재 로그인한 사용자 ID
     * @return 사용자의 캠퍼스 이름 (예: "용인", "서울"), 실패 시 null
     */
    public int getCampusIdByUserId(String userId) {
        conn = dao.getConnection();
        String sql = "SELECT campus_id FROM user WHERE userid = ?";
        int campusId = -1; // 실패 시 -1 반환
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                campusId = rs.getInt("campus_id");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "사용자 캠퍼스 ID 조회 SQL 오류", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "DB 연결 실패. DAO의 getConnection()을 확인하세요.", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return campusId;
    }
    
}
