package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.constants.AppConstants;
import signup.constants.StatusConstants;
import signup.model.MLecture;

public class SaveDAO {
    
    private DAO dao;
    private static final Logger logger = Logger.getLogger(SaveDAO.class.getName());
    
    public SaveDAO() {
        this.dao = new DAO();
    }

    /**
     * 상태별 강의 목록 조회 (수강신청/미리담기)
     * @param userid 사용자 ID
     * @param status 조회할 상태 (StatusConstants.REGISTER 또는 PRE_REGISTER)
     * @return 강의 목록 (오류 시 빈 리스트 반환)
     * 
     * [오류 처리]
     * - SQL 오류: 로그 출력 후 빈 리스트 반환
     * - DB 연결 실패: 빈 리스트 반환
     */
    public List<MLecture> getLecturesByStatus(String userid, String status) {
        List<MLecture> lectures = new ArrayList<>();
        
        String sql = "SELECT l.id, l.name, l.professor, l.credit, l.time " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userid);
            pstmt.setString(2, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
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
        }
        catch (SQLException e) { 
            // 디버깅: save 테이블 구조, lecture_id 외래키, status 값 확인
            logger.log(Level.SEVERE, "상태별 강의 조회 SQL 오류", e); 
        } 
        return lectures;
    }
    
    /**
     * 강의 추가 (수강신청/미리담기)
     * @param userid 사용자 ID
     * @param lectureid 강의 ID
     * @param status 저장 상태 (StatusConstants.REGISTER 또는 PRE_REGISTER)
     * @param newCredits 추가할 강의 학점
     * @return 결과 코드 (AppConstants.DB_SUCCESS/DB_ERROR_* 참조)
     * 
     * [반환값]
     * - DB_SUCCESS(0): 정상 추가 완료
     * - DB_ERROR_CREDIT_EXCEEDED(1): 학점 초과 (MAX_CREDITS 확인)
     * - DB_ERROR_DUPLICATE(2): 이미 추가된 강의
     * - DB_ERROR_GENERAL(-1): DB 연결 또는 쿼리 오류
     * 
     * [주의사항]
     * - 수강신청(REGISTER)만 학점 검사 수행
     * - INSERT IGNORE 사용으로 중복 시 오류 없이 무시됨
     */
    public int addLecture(String userid, int lectureid, String status, int newCredits) {
        // 수강신청인 경우만 학점 검사
        if (StatusConstants.REGISTER.equals(status)) {
            int currentCredits = getTotalCredits(userid, StatusConstants.REGISTER);
            if (currentCredits + newCredits > AppConstants.MAX_CREDITS) {
                return AppConstants.DB_ERROR_CREDIT_EXCEEDED;
            }
        }
        
        String sql = "INSERT IGNORE INTO save (userid, lecture_id, status) VALUES (?, ?, ?)";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userid);
            pstmt.setInt(2, lectureid);
            pstmt.setString(3, status);
            
            int insertedRows = pstmt.executeUpdate();
            // insertedRows = 0: 중복으로 인한 무시, 1: 정상 삽입
            return insertedRows > 0 ? AppConstants.DB_SUCCESS : AppConstants.DB_ERROR_DUPLICATE;
        } catch (SQLException e) {
            // 디버깅: save 테이블 PK 제약, lecture_id 유효성 확인
            logger.log(Level.WARNING, "강의 저장(save) SQL 오류", e);
            return AppConstants.DB_ERROR_GENERAL;
        }
    }

    /**
     * 강의 삭제 (수강신청/미리담기 취소)
     * @param userid 사용자 ID
     * @param lectureid 강의 ID
     * @param status 삭제할 상태 (StatusConstants.REGISTER 또는 PRE_REGISTER)
     * @return 성공 여부 (true: 삭제 성공, false: 삭제 실패 또는 해당 데이터 없음)
     * 
     * [오류 처리]
     * - DB 연결 실패: false 반환, 로그 출력
     * - SQL 오류: false 반환, 로그 출력
     * - 삭제할 데이터 없음: false 반환 (정상)
     */
    public boolean removeLecture(String userid, int lectureid, String status) {
        String sql = "DELETE FROM save WHERE userid = ? AND lecture_id = ? AND status = ?";
        
        Connection conn = dao.getConnection();
        if (conn == null) {
            // DB 연결 문제: config.properties, MySQL 서버 상태 확인
            logger.log(Level.SEVERE, "removeLecture: DB 연결 실패");
            return false;
        }
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            pstmt.setInt(2, lectureid);
            pstmt.setString(3, status);
            
            // 삭제된 행 수 > 0: 성공, 0: 해당 데이터 없음
            return pstmt.executeUpdate() > 0; 
        } catch (SQLException e) {
            // 디버깅: save 테이블 구조, 파라미터 값 확인
            logger.log(Level.WARNING, "강의 삭제(remove) SQL 오류", e);
            return false;
        }
    }
    
    /**
     * 총 학점 계산
     * @param userid 사용자 ID
     * @param status 계산할 상태 (StatusConstants.REGISTER 또는 PRE_REGISTER)
     * @return 총 학점 (오류 시 0 반환)
     * 
     * [반환값]
     * - 정상: 해당 상태의 모든 강의 학점 합계
     * - 강의 없음: 0 (정상)
     * - DB 오류: 0 (로그 확인 필요)
     * 
     * [주의사항]
     * - SUM이 NULL인 경우 0으로 처리됨
     * - 학점 초과 검사 전에 반드시 호출되어야 함
     */
    public int getTotalCredits(String userid, String status) {
        int totalCredits = 0;

        String sql = "SELECT SUM(l.credit) AS total " +
                     "FROM lecture l " +
                     "JOIN save s ON l.id = s.lecture_id " +
                     "WHERE s.userid = ? AND s.status = ?";
        
        Connection conn = dao.getConnection();
        if (conn == null) {
            // DB 연결 문제: config.properties, MySQL 서버 상태 확인
            logger.log(Level.SEVERE, "getTotalCredits: DB 연결 실패");
            return 0;
        }
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            pstmt.setString(2, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // SUM 결과가 NULL이면 getInt는 0 반환
                    totalCredits = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            // 디버깅: lecture.credit 컬럼 타입, JOIN 조건 확인
            logger.log(Level.WARNING, "총 학점 계산 SQL 오류", e);
        }
        return totalCredits;
    }
}