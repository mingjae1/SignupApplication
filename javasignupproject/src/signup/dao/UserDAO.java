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
    public String validateUser(String id, String password) throws SQLException {
        conn = dao.getConnection();
        
        // 1. DB 연결 자체를 실패하면 예외를 컨트롤러로 던짐
        if (conn == null) {
            // CLogin의 catch 블록이 이 예외를 잡을 것입니다.
            throw new SQLException("데이터베이스 연결에 실패했습니다. (conn is null)");
        }
        
        String sql = "SELECT u.name FROM login l " +
                     "JOIN user u ON l.userId = u.userid " +
                     "WHERE l.userId = ? AND l.password = ?";

        // PreparedStatement와 ResultSet은 try-with-resources로 자동 관리
        try (PreparedStatement validpstmt = conn.prepareStatement(sql)) {
            
            validpstmt.setString(1, id);
            validpstmt.setString(2, password);

            try (ResultSet result = validpstmt.executeQuery()) {
                if (result.next()) {
                    return result.getString("name"); // 1. 로그인 성공
                } else {
                    return null; // 2. 아이디 또는 비밀번호 틀림
                }
            }
        } catch (SQLException e) {
            // 3. 쿼리 실행 중 DB 오류 발생 시, 예외를 컨트롤러로 '다시 던짐'
            throw new SQLException("로그인 인증 중 데이터베이스 오류가 발생했습니다.", e);
        } finally {
            // 4. Connection은 닫아야 함
            // (try-with-resources가 pstmt, rs를 닫아주므로 conn만 닫습니다)
            DAO.close(null, null, conn);
        }
    }
    
    /**
     * MUser DTO와 password를 받아 새 사용자를 등록합니다.
     * (매개변수 초과, NullPointerException, 롤백 버그 수정됨)
     * @param mUser      모든 사용자 정보가 담긴 MUser DTO
     * @param password  login 테이블에 저장할 비밀번호
     * @return 회원가입 성공 시 true, 실패(DB 오류) 시 false
     */
    public boolean addUser(MUser mUser, String password) { 
        
        String sqlLogin = "INSERT INTO login (userId, password) VALUES (?, ?)";
        
        // SQL 쿼리: user 테이블에 *_id 컬럼들을 사용
        String sqlUser = "INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        conn = dao.getConnection();
        
        if (conn == null) { logger.log(Level.SEVERE, "addUser: DB 연결 실패. Connection이 null입니다."); return false; }

        PreparedStatement addprsmt = null;

        try {
            conn.setAutoCommit(false); // 수동 커밋 모드
            
            // 2. user 테이블 (DTO에서 ID를 추출)
            addprsmt = conn.prepareStatement(sqlUser);
            addprsmt.setString(1, mUser.getUserid());
            addprsmt.setString(2, mUser.getName());
            addprsmt.setInt(3, mUser.getCode());
            addprsmt.setString(4, mUser.getEmail());
            addprsmt.setInt(5, mUser.getCampusId()); // [수정됨] DTO에서 ID 추출
            addprsmt.setInt(6, mUser.getCollegeId()); // [수정됨] DTO에서 ID 추출
            addprsmt.setInt(7, mUser.getDepartmentId()); // [수정됨] DTO에서 ID 추출
            int userResult = addprsmt.executeUpdate();
            
            // 1. login 테이블
            addprsmt = conn.prepareStatement(sqlLogin);
            addprsmt.setString(1, mUser.getUserid());
            addprsmt.setString(2, password);
            int loginResult = addprsmt.executeUpdate();
            
            DAO.close(null, addprsmt, null);

            // 3. 커밋/롤백 로직
            if (loginResult > 0 && userResult > 0) { conn.commit(); return true; } 
            else { conn.rollback(); return false; }

        }
        catch (SQLException e) { logger.log(Level.WARNING, "회원가입 트랜잭션 오류 (ID/학번 중복 등)", e);
            try { conn.rollback();  } 
            catch (SQLException ex) { logger.log(Level.SEVERE, "롤백 실패", ex); } return false; } 
        finally {
            try { conn.setAutoCommit(true); } 
            catch (SQLException e) { logger.log(Level.WARNING, "AutoCommit 원상복구 실패", e); }
            DAO.close(null, addprsmt, conn); 
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
        String sql = "SELECT COUNT(*) FROM user WHERE userid = ?";
        
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
    
    public MUser getUserInfo(String userId) {
        conn = dao.getConnection();
        MUser user = null;
        
        if (conn == null) return null; // DB 연결 실패 시 null 반환
        
        // [핵심] 4개의 테이블을 JOIN하여 소속 '이름'을 가져오는 쿼리
        String sql = "SELECT u.userid, u.name, u.code, u.email, " +
                     "r.name AS campus_name, " +
                     "c.name AS college_name, " +
                     "d.name AS dept_name " +
                     "FROM user u " +
                     "JOIN root r ON u.campus_id = r.id " +
                     "JOIN college c ON u.college_id = c.id " +
                     "JOIN department d ON u.department_id = d.id " +
                     "WHERE u.userid = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new MUser();
                user.setUserid(rs.getString("userid"));
                user.setName(rs.getString("name"));
                user.setCode(rs.getInt("code"));
                user.setEmail(rs.getString("email"));
                
                // [수정됨] 조회한 '이름(String)'을 DTO의 String 필드에 저장합니다.
                // 이제 user.getCampus()를 호출하면 "자연캠퍼스"가 나옵니다.
                user.setCampus(rs.getString("campus_name"));
                user.setCollege(rs.getString("college_name"));
                user.setDepartment(rs.getString("dept_name"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "사용자 상세 정보 조회 실패", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return user;
    }
}
