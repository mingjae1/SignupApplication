package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.model.MLecture; // model.Lecture DTO가 필요합니다.

/**
 * 'save' 테이블 (수강신청, 미리담기 내역) 관련
 * 모든 DB 작업을 전담하는 DAO 클래스입니다.
 */
public class SaveDAO {
    
    private DAO dao; // 기본 DB 연결 헬퍼
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    private static final Logger logger = Logger.getLogger(SaveDAO.class.getName());
    
    // 최대 허용 학점 상수 (18학점)
    private static final int MAX_CREDITS = 18;
    
    public SaveDAO() {
        this.dao = new DAO();
    }

    /**
     * 특정 사용자의 '수강신청' 또는 '미리담기' 목록을 DB에서 조회합니다.
     * save 테이블과 lecture 테이블을 JOIN하여 강의 상세 정보를 가져옵니다.
     * * @param userId 로그인한 사용자의 ID
     * @param status "reg" (수강신청) 또는 "pre" (미리담기)
     * @return 해당 상태의 Lecture 객체 리스트
     */
    public List<MLecture> getLecturesByStatus(String userId, String status) {
        List<MLecture> lectures = new ArrayList<>();
        conn = dao.getConnection();
        
        // save 테이블(s)과 lecture 테이블(l)을 JOIN하는 SQL
        String sql = "SELECT l.id, l.name, l.professor, l.credit, l.time " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, status); // "reg" 또는 "pre"
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // Lecture DTO 객체에 결과값을 담음
                MLecture lecture = new MLecture(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getInt("credit"), // SQL 스키마에 따라 credit으로 수정
                    rs.getString("time")
                );
                lectures.add(lecture);
            }
        } 
        catch (SQLException e) { logger.log(Level.SEVERE, "상태별 강의 조회 SQL 오류", e); } 
        finally { DAO.close(rs, pstmt, conn); }
        return lectures;
    }
    
    
    /**
     * 'save' 테이블에 수강신청("reg") 또는 미리담기("pre") 내역을 추가합니다.
     * 이미 동일한 항목이 있다면 (PRIMARY KEY 중복), 무시하고 false를 반환합니다.
     *
     * @param userId    로그인한 사용자 ID
     * @param lectureId 사용자가 선택한 강의의 ID (숫자 ID)
     * @param status    저장할 상태 ("reg" 또는 "pre")
     * @return 삽입 성공 시 true, 실패(중복 등) 시 false
     */
    public int addLecture(String userId, int lectureId, String status, int newCredits) {
        
    	// 1. [핵심] "reg" (수강신청) 상태일 때만 학점 제한을 검사합니다.
        // (미리담기("pre")는 학점 제한 없이 담을 수 있어야 합니다.)
        if (status.equals("reg")) {
            int currentCredits = getTotalCredits(userId, "reg");
            if (currentCredits + newCredits > MAX_CREDITS) {
                // 1번 오류: 학점 초과
                return 1; 
            }
        }

    	conn = dao.getConnection();
        
        // INSERT IGNORE: PK(userid, lecture_id, status)가 중복되면 오류 대신 무시
        String sql = "INSERT IGNORE INTO save (userid, lecture_id, status) VALUES (?, ?, ?)";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, lectureId);
            pstmt.setString(3, status);
            
            int insertedRows = pstmt.executeUpdate();
            
            if (insertedRows > 0) {
                return 0; // 0번: 성공
            } else {
                return 2; // 2번 오류: 이미 존재함 (중복)
            }
            
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 저장(save) SQL 오류", e);
            return -1; // -1번: DB 오류
        } finally {
            DAO.close(null, pstmt, conn);
        }
    }

    public boolean removeLecture(String userId, int lectureId, String status) {
        conn = dao.getConnection();
        
        // [방어 코드] DB 연결 실패 시 즉시 중단
        if (conn == null) {
            logger.log(Level.SEVERE, "removeLecture: DB 연결 실패 (conn=null)");
            return false;
        }

        String sql = "DELETE FROM save WHERE userid = ? AND lecture_id = ? AND status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, lectureId);
            pstmt.setString(3, status);
            
            // executeUpdate()는 삭제된 행(row)의 수를 반환합니다.
            int deletedRows = pstmt.executeUpdate();
            
            // 1개 이상의 행(row)이 삭제되었다면 성공
            return deletedRows > 0; 
            
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 삭제(remove) SQL 오류", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn); // rs가 없으므로 null 전달
        }
    }
    
    /**
     * 특정 사용자의 '수강신청' 또는 '미리담기' 목록의 총 학점을 계산합니다.
     * @param userId 로그인한 사용자 ID
     * @param status "reg" (수강신청) 또는 "pre" (미리담기)
     * @return 계산된 총 학점 (int). 오류 발생 시 0 반환.
     */
    public int getTotalCredits(String userId, String status) {
        conn = dao.getConnection();
        int totalCredits = 0;
        
        if (conn == null) {
            logger.log(Level.SEVERE, "getTotalCredits: DB 연결 실패 (conn=null)");
            return 0;
        }

        // 'save' 테이블(s)과 'lecture' 테이블(l)을 JOIN하여 학점(credit)의 합(SUM)을 계산
        String sql = "SELECT SUM(l.credit) AS total " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, status);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // SUM() 결과는 'total'이라는 이름의 컬럼으로 반환됨
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