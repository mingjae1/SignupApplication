package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.constants.StatusConstants;
import signup.model.MLecture;

public class SaveDAO {
    
    private DAO dao;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private static final Logger logger = Logger.getLogger(SaveDAO.class.getName());
    private static final int MAX_CREDITS = 18;
    
    public SaveDAO() {
        this.dao = new DAO();
    }

    public List<MLecture> getLecturesByStatus(String userid, String status) {
        List<MLecture> lectures = new ArrayList<>();
        conn = dao.getConnection();
        
        String sql = "SELECT l.id, l.name, l.professor, l.credit, l.time " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setString(2, status);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                lectures.add(new MLecture(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getInt("credit"),
                    rs.getString("time")
                ));
            }
        }
        catch (SQLException e) { logger.log(Level.SEVERE, "상태별 강의 조회 SQL 오류", e); } 
        finally { DAO.close(rs, pstmt, conn); }
        return lectures;
    }
    
    public int addLecture(String userid, int lectureid, String status, int newCredits) {
        if (StatusConstants.REGISTER.equals(status)) {
            int currentCredits = getTotalCredits(userid, StatusConstants.REGISTER);
            if (currentCredits + newCredits > MAX_CREDITS) {
                return 1;
            }
        }
        
        conn = dao.getConnection();
        String sql = "INSERT IGNORE INTO save (userid, lecture_id, status) VALUES (?, ?, ?)";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setInt(2, lectureid);
            pstmt.setString(3, status);
            
            int insertedRows = pstmt.executeUpdate();
            return insertedRows > 0 ? 0 : 2;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 저장(save) SQL 오류", e);
            return -1;
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }

    public boolean removeLecture(String userid, int lectureid, String status) {
        conn = dao.getConnection();
        
        if (conn == null) {
            logger.log(Level.SEVERE, "removeLecture: DB 연결 실패");
            return false;
        }
        
        String sql = "DELETE FROM save WHERE userid = ? AND lecture_id = ? AND status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setInt(2, lectureid);
            pstmt.setString(3, status);
            
            return pstmt.executeUpdate() > 0; 
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 삭제(remove) SQL 오류", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }
    
    public int getTotalCredits(String userid, String status) {
        conn = dao.getConnection();
        int totalCredits = 0;
        
        if (conn == null) {
            logger.log(Level.SEVERE, "getTotalCredits: DB 연결 실패");
            return 0;
        }

        String sql = "SELECT SUM(l.credit) AS total " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userid);
            pstmt.setString(2, status);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                totalCredits = rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "총 학점 계산 SQL 오류", e);
        } finally {
            DAO.close(rs, pstmt, conn);
        }
        return totalCredits;
    }
}