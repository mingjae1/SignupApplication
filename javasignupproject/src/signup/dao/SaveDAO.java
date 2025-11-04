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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "상태별 강의 조회 SQL 오류", e);
        } finally {
        	DAO.close(rs, pstmt, conn);
        }
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
    public boolean addLecture(String userId, int lectureId, String status) {
        conn = dao.getConnection();
        
        // INSERT IGNORE: PK(userid, lecture_id, status)가 중복되면 오류 대신 무시
        String sql = "INSERT IGNORE INTO save (userid, lecture_id, status) VALUES (?, ?, ?)";
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, lectureId);
            pstmt.setString(3, status);
            
            // executeUpdate()는 실제로 삽입된 행(row)의 수를 반환합니다.
            int insertedRows = pstmt.executeUpdate();
            
            // 1개 이상의 행이 삽입되었다면 성공
            return insertedRows > 0; 
            
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 저장(save) SQL 오류", e);
            return false;
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "DB 연결 실패. DAO의 getConnection()을 확인하세요.", e);
            return false;
        } finally {
            DAO.close(null, pstmt, conn); // rs가 없으므로 null 전달
        }
    }
    
    
}