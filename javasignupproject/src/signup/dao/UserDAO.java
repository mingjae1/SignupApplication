package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.model.MUser;

public class UserDAO {

    private DAO dao;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public UserDAO() {
        this.dao = new DAO();
    }

    public MUser validateUser(String id, String password) throws SQLException {
        conn = dao.getConnection();
        
        if (conn == null) {
            throw new SQLException("데이터베이스 연결에 실패했습니다.");
        }
        
        String sql = "SELECT u.name, u.role FROM login l " +
                     "JOIN user u ON l.userId = u.userid " +
                     "WHERE l.userId = ? AND l.password = ?";

        try (PreparedStatement validpstmt = conn.prepareStatement(sql)) {
            validpstmt.setString(1, id);
            validpstmt.setString(2, password);

            try (ResultSet result = validpstmt.executeQuery()) {
                if (result.next()) {
                   MUser mUser = new MUser();
                   mUser.setUserid(id);
                   mUser.setName(result.getString("name"));
                   mUser.setRole(result.getString("role"));
                   return mUser;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("로그인 인증 중 데이터베이스 오류가 발생했습니다.", e);
        } finally {
            DAO.close(null, null, conn);
        }
    }
    
    public boolean addUser(MUser mUser, String password) { 
        String sqlLogin = "INSERT INTO login (userId, password) VALUES (?, ?)";
        String sqlUser = "INSERT INTO user (userid, name, code, email, campus_id, college_id, department_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        conn = dao.getConnection();
        if (conn == null) { 
            logger.log(Level.SEVERE, "addUser: DB 연결 실패");
            return false; 
        }

        PreparedStatement addprsmt = null;

        try {
            conn.setAutoCommit(false);
            
            addprsmt = conn.prepareStatement(sqlUser);
            addprsmt.setString(1, mUser.getUserid());
            addprsmt.setString(2, mUser.getName());
            addprsmt.setInt(3, mUser.getCode());
            addprsmt.setString(4, mUser.getEmail());
            addprsmt.setInt(5, mUser.getCampusId());
            addprsmt.setInt(6, mUser.getCollegeId());
            addprsmt.setInt(7, mUser.getDepartmentId());
            int userResult = addprsmt.executeUpdate();
            
            addprsmt = conn.prepareStatement(sqlLogin);
            addprsmt.setString(1, mUser.getUserid());
            addprsmt.setString(2, password);
            int loginResult = addprsmt.executeUpdate();
            
            DAO.close(null, addprsmt, null);

            if (loginResult > 0 && userResult > 0) { 
                conn.commit(); 
                return true; 
            } else { 
                conn.rollback(); 
                return false; 
            }
        }
        catch (SQLException e) { 
            logger.log(Level.WARNING, "회원가입 트랜잭션 오류", e);
            try { conn.rollback(); } 
            catch (SQLException ex) { logger.log(Level.SEVERE, "롤백 실패", ex); } 
            return false; 
        } 
        finally {
            try { conn.setAutoCommit(true); } 
            catch (SQLException e) { logger.log(Level.WARNING, "AutoCommit 원상복구 실패", e); }
            DAO.close(null, addprsmt, conn); 
        }
    }
    
    public boolean isUserIdDuplicate(String id) {
        conn = dao.getConnection();
        String sql = "SELECT COUNT(*) FROM user WHERE userid = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ID 중복 검사 SQL 오류", e);
        } finally {
            DAO.close(rs, pstmt, conn);	
        }
        return false;
    }
    
    public boolean isStudentIdDuplicate(int studentCode) { 
        conn = dao.getConnection();
        String sql = "SELECT COUNT(*) FROM user WHERE code = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentCode);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "학번 중복 검사 SQL 오류", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "DB 연결 실패", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return false;
    }
    
    public int getCampusIdByUserId(String userId) {
        conn = dao.getConnection();
        String sql = "SELECT campus_id FROM user WHERE userid = ?";
        int campusId = -1;
        
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
            logger.log(Level.SEVERE, "DB 연결 실패", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return campusId;
    }
    
    public MUser getUserInfo(String userId) {
        conn = dao.getConnection();
        MUser user = null;
        
        if (conn == null) return null;
        
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
