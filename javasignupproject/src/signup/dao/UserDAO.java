package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import signup.model.MUser;

public class UserDAO {

    private DAO dao;
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    private static final int MIN_PASSWORD_HASH_LENGTH = 60;
    private static final int TARGET_PASSWORD_COLUMN_LENGTH = 100;
    private static volatile boolean passwordColumnChecked = false;

    public UserDAO() {
        this.dao = new DAO();
    }

    /**
     * 로그인 인증 처리
     * @param id 사용자 ID
     * @param password 비밀번호 (평문)
     * @return 인증 성공 시 사용자 정보, 실패 시 null
     * @throws SQLException DB 연결 또는 쿼리 실행 오류
     * 
     * [반환값]
     * - MUser 객체: 인증 성공 (userid, name, role 포함)
     * - null: 아이디 또는 비밀번호 불일치
     * - SQLException: DB 연결 실패 또는 SQL 오류
     * 
     * [주의사항]
     * - 비밀번호는 평문 저장 (추후 암호화 권장)
     * - login 테이블과 user 테이블 JOIN 필요
     */
    public MUser validateUser(String id, String password) throws SQLException {
        Connection conn = dao.getConnection();
        if (conn == null) {
            // DB 연결 문제: config.properties 파일, MySQL 서버 확인
            throw new SQLException("데이터베이스 연결에 실패했습니다.");
        }
        ensurePasswordColumnLength(conn);
        
        String sql = "SELECT l.password AS hash, u.name, u.role FROM login l " +
                     "JOIN user u ON l.userId = u.userid " +
                     "WHERE l.userId = ?";

        try (Connection connection = conn;
             PreparedStatement validpstmt = connection.prepareStatement(sql)) {
            validpstmt.setString(1, id);

            try (ResultSet result = validpstmt.executeQuery()) {
                if (result.next()) {
                   String hash = result.getString("hash");
                   if (hash != null) {
                       // 기존에 평문으로 저장된 비밀번호가 있을 수 있으므로 마이그레이션 처리
                       boolean passwordMatches = false;
                       boolean wasPlain = false;

                       if (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
                           passwordMatches = BCrypt.checkpw(password, hash);
                       } else {
                           // 평문 저장된 케이스: 직접 비교 후 해시로 교체
                           if (password.equals(hash)) {
                               passwordMatches = true;
                               wasPlain = true;
                           }
                       }

                       if (passwordMatches) {
                           MUser mUser = new MUser();
                           mUser.setUserid(id);
                           mUser.setName(result.getString("name"));
                           mUser.setRole(result.getString("role"));

                           if (wasPlain) {
                               // 평문에서 해시로 마이그레이션: 즉시 DB 업데이트
                               String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
                               String updSql = "UPDATE login SET password = ? WHERE userId = ?";
                               try (PreparedStatement updStmt = connection.prepareStatement(updSql)) {
                                   updStmt.setString(1, newHash);
                                   updStmt.setString(2, id);
                                   updStmt.executeUpdate();
                               } catch (SQLException ex) {
                                   logger.log(Level.WARNING, "비밀번호 해시 마이그레이션 실패", ex);
                               }
                           }

                           return mUser;
                       }
                   }
                }
                // 인증 실패: 아이디 또는 비밀번호 불일치
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("로그인 인증 중 데이터베이스 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 회원 가입 처리 (트랜잭션)
     * @param mUser 사용자 정보 (userid, name, code, email, campus_id, college_id, department_id)
     * @param password 비밀번호 (평문)
     * @return 성공 여부 (true: 가입 성공, false: 가입 실패)
     * 
     * [트랜잭션 처리]
     * 1. user 테이블 INSERT
     * 2. login 테이블 INSERT
     * 3. 둘 다 성공 시 commit, 하나라도 실패 시 rollback
     * 
     * [실패 원인]
     * - userid 중복: PK 제약 위반
     * - code(학번) 중복: UNIQUE 제약 위반
     * - 외래키 오류: campus_id, college_id, department_id 유효성
     * - DB 연결 실패: config.properties 확인
     * 
     * [주의사항]
     * - 반드시 중복 검사 후 호출 권장 (isUserIdDuplicate, isStudentIdDuplicate)
     * - 비밀번호는 평문 저장 (추후 암호화 권장)
     */
    public boolean addUser(MUser mUser, String password) { 
        String sqlLogin = "INSERT INTO login (userId, password) VALUES (?, ?)";
        String sqlUser = "INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dao.getConnection()) {
            if (conn == null) { 
                // DB 연결 문제: config.properties, MySQL 서버 상태 확인
                logger.log(Level.SEVERE, "addUser: DB 연결 실패");
                return false; 
            }
            ensurePasswordColumnLength(conn);

            try {
                // 수동 트랜잭션 시작
                conn.setAutoCommit(false);
                
                // 1단계: user 테이블 INSERT
                try (PreparedStatement pstmt1 = conn.prepareStatement(sqlUser)) {
                    pstmt1.setString(1, mUser.getUserid());
                    pstmt1.setString(2, mUser.getName());
                    pstmt1.setInt(3, mUser.getCode());
                    pstmt1.setString(4, mUser.getEmail());
                    pstmt1.setInt(5, mUser.getCampusId());
                    pstmt1.setInt(6, mUser.getCollegeId());
                    pstmt1.setInt(7, mUser.getDepartmentId());
                    int userResult = pstmt1.executeUpdate();
                    
                    // 2단계: login 테이블 INSERT
                    try (PreparedStatement pstmt2 = conn.prepareStatement(sqlLogin)) {
                        pstmt2.setString(1, mUser.getUserid());
                        // 비밀번호 해싱
                        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                        pstmt2.setString(2, hashed);
                        int loginResult = pstmt2.executeUpdate();
                        
                        // 둘 다 성공 시 commit, 실패 시 rollback
                        if (loginResult > 0 && userResult > 0) { 
                            conn.commit(); 
                            return true; 
                        } else { 
                            conn.rollback(); 
                            return false; 
                        }
                    }
                }
            }
            catch (SQLException e) { 
                // 디버깅: 중복 키, 외래키 제약, NOT NULL 제약 확인
                logger.log(Level.WARNING, "회원가입 트랜잭션 오류", e);
                try { conn.rollback(); }
                catch (SQLException ex) { logger.log(Level.SEVERE, "롤백 실패", ex); } 
                return false; 
            } 
            finally {
                try { conn.setAutoCommit(true); } 
                catch (SQLException e) { logger.log(Level.WARNING, "AutoCommit 원상복구 실패", e); }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB 연결 오류", e);
            return false;
        }
    }
    
    /**
     * 사용자 ID 중복 검사
     * @param id 검사할 사용자 ID
     * @return true: 이미 존재하는 ID, false: 사용 가능한 ID
     * 
     * [사용 시점]
     * - 회원가입 전 필수 검사
     * - addUser 호출 전에 실행 권장
     * 
     * [주의사항]
     * - DB 오류 시 false 반환 (안전하게 처리됨)
     * - user 테이블의 userid(PK) 기준
     */
    public boolean isUserIdDuplicate(String id) {
        String sql = "SELECT COUNT(*) FROM user WHERE userid = ?";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // COUNT > 0: 중복, 0: 사용 가능
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ID 중복 검사 SQL 오류", e);
        }
        return false;
    }
    
    public boolean isStudentIdDuplicate(int studentCode) { 
        String sql = "SELECT COUNT(*) FROM user WHERE code = ?";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentCode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "학번 중복 검사 SQL 오류", e);
        }
        return false;
    }
    
    public int getCampusIdByUserId(String userId) {
        String sql = "SELECT campus_id FROM user WHERE userid = ?";
        int campusId = -1;
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    campusId = rs.getInt("campus_id");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "사용자 캠퍼스 ID 조회 SQL 오류", e);
        }
        return campusId;
    }
    
    public MUser getUserInfo(String userId) {
        MUser user = null;
        
        String sql = "SELECT u.userid, u.name, u.code, u.email, " +
                     "r.name AS campus_name, " +
                     "c.name AS college_name, " +
                     "d.name AS dept_name " +
                     "FROM user u " +
                     "JOIN root r ON u.campus_id = r.id " +
                     "JOIN college c ON u.college_id = c.id " +
                     "JOIN department d ON u.department_id = d.id " +
                     "WHERE u.userid = ?";
        
        Connection conn = dao.getConnection();
        if (conn == null) return null;
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new MUser();
                    user.setUserid(rs.getString("userid"));
                    user.setName(rs.getString("name"));
                    user.setCode(rs.getInt("code"));
                    user.setEmail(rs.getString("email"));
                    user.setCampus(rs.getString("campus_name"));
                    user.setCollege(rs.getString("college_name"));
                    user.setDepartment(rs.getString("dept_name"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "사용자 상세 정보 조회 실패", e);
        }
        return user;
    }
    
    /**
     * 주어진 id, name, code가 user 테이블에 일치하는지 확인하고,
     * 일치하면 login 테이블의 password를 newPassword로 업데이트합니다.
     * @return true: 업데이트(초기화) 성공, false: 일치하는 사용자 없음
     * @throws SQLException DB 오류 발생 시
     */
    public boolean resetPasswordIfMatch(String id, String name, int code, String newPassword) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM user WHERE userid = ? AND name = ? AND code = ?";
        String updateSql = "UPDATE login SET password = ? WHERE userId = ?";

        Connection conn = dao.getConnection();
        if (conn == null) throw new SQLException("데이터베이스 연결에 실패했습니다.");
        ensurePasswordColumnLength(conn);

        try (Connection connection = conn) {
            // 먼저 사용자 존재 및 정보 일치 여부 확인
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, id);
                checkStmt.setString(2, name);
                checkStmt.setInt(3, code);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // 정보가 일치하면 비밀번호 업데이트
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            // 해시 저장
                            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                            updateStmt.setString(1, hashed);
                            updateStmt.setString(2, id);
                            int updated = updateStmt.executeUpdate();
                            return updated > 0;
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "비밀번호 초기화 중 DB 오류", e);
            throw e;
        }
    }
    
    /**
     * 현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다.
     * @return true: 변경 성공, false: 현재 비밀번호 불일치 또는 업데이트 실패
     * @throws SQLException DB 오류 발생 시
     */
    public boolean changePassword(String userId, String oldPassword, String newPassword) throws SQLException {
        String fetchSql = "SELECT password FROM login WHERE userId = ?";
        String updateSql = "UPDATE login SET password = ? WHERE userId = ?";

        Connection conn = dao.getConnection();
        if (conn == null) throw new SQLException("데이터베이스 연결에 실패했습니다.");
        ensurePasswordColumnLength(conn);

        try (Connection connection = conn) {
            try (PreparedStatement fetchStmt = connection.prepareStatement(fetchSql)) {
                fetchStmt.setString(1, userId);
                try (ResultSet rs = fetchStmt.executeQuery()) {
                    if (rs.next()) {
                        String hash = rs.getString("password");
                        if (hash != null && BCrypt.checkpw(oldPassword, hash)) {
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                                String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                                updateStmt.setString(1, hashed);
                                updateStmt.setString(2, userId);
                                int updated = updateStmt.executeUpdate();
                                return updated > 0;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "비밀번호 변경 중 DB 오류", e);
            throw e;
        }
    }

    private void ensurePasswordColumnLength(Connection connection) {
        if (connection == null || passwordColumnChecked) {
            return;
        }

        synchronized (UserDAO.class) {
            if (passwordColumnChecked) {
                return;
            }

            boolean verified = false;
            final String showSql = "SHOW COLUMNS FROM login LIKE 'password'";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(showSql)) {
                if (rs.next()) {
                    String type = rs.getString("Type");
                    int definedLength = parseVarcharLength(type);
                    if (definedLength > 0 && definedLength < MIN_PASSWORD_HASH_LENGTH) {
                        try (Statement alterStmt = connection.createStatement()) {
                            alterStmt.executeUpdate(
                                "ALTER TABLE login MODIFY password VARCHAR(" + TARGET_PASSWORD_COLUMN_LENGTH + ")"
                            );
                            logger.info("비밀번호 해시화에 성공했습니다.");
                        }
                    }
                }
                verified = true;
            } catch (SQLException ex) {
                logger.log(Level.FINE, "비밀번호 컬럼 길이 확인 실패 - 수동 점검 필요", ex);
            } finally {
                if (verified) {
                    passwordColumnChecked = true;
                }
            }
        }
    }

    private int parseVarcharLength(String typeDefinition) {
        if (typeDefinition == null) {
            return -1;
        }

        int start = typeDefinition.indexOf('(');
        int end = typeDefinition.indexOf(')');
        if (start >= 0 && end > start) {
            try {
                return Integer.parseInt(typeDefinition.substring(start + 1, end));
            } catch (NumberFormatException ignored) {
                // no-op
            }
        }
        return -1;
    }
}