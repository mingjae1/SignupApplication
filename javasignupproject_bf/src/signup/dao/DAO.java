package signup.dao; // 또는 signup.dao

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DAO {
    
    // 1. "핑 테스트"에 성공한 정보와 똑같이 입력합니다.
    private static final String DBURL = "jdbc:mysql://localhost:3306/lms_data";
    private static final String DBID = "root";
    private String dbpassword;
    
    private static final Logger logger = Logger.getLogger(DAO.class.getName());

    // 2. 드라이버 로드 (프로그램 시작 시 한 번)
    public DAO() {
    	
    	// DAO.java는 이 파일을 읽어들여서 변수에 저장
    	Properties props = new Properties();
    	String password = null;
    	
    	try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            password = props.getProperty("db.password"); 
            dbpassword = password;} 
    	catch (FileNotFoundException e) { logger.log(Level.SEVERE, "config.properties 파일을 찾을 수 없습니다.", e); }
    	catch (IOException e) { logger.log(Level.SEVERE, "config.properties 파일을 읽는 중 오류가 발생했습니다.", e); }
    
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }  catch (ClassNotFoundException e) { logger.log(Level.SEVERE, "MySQL JDBC 드라이버 로드 실패", e); } }

    // 3. 다른 DAO 클래스(예: LectureDAO)가 호출할 연결 메서드
    public Connection getConnection() { try { return DriverManager.getConnection(DBURL, DBID, dbpassword); }  catch (SQLException e) { logger.log(Level.SEVERE, "DB 연결 실패", e); return null; } }

    /**
     * (공용) 사용한 DB 자원(Connection, PreparedStatement, ResultSet)을 
     * 한 번에 안전하게 닫습니다.
     * * @param rs 닫을 ResultSet (null이어도 안전)
     * @param pstmt 닫을 PreparedStatement (null이어도 안전)
     * @param conn 닫을 Connection (null이어도 안전)
     */
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        
        // 1. ResultSet 닫기
        try { if (rs != null && !rs.isClosed()) { rs.close(); } } catch (SQLException e) { logger.log(Level.WARNING, "ResultSet 닫기 실패", e); }

        // 2. PreparedStatement 닫기 (ResultSet 다음에 닫는 것이 좋음)
        try { if (pstmt != null && !pstmt.isClosed()) { pstmt.close(); } } catch (SQLException e) { logger.log(Level.WARNING, "PreparedStatement 닫기 실패", e); }

        // 3. Connection 닫기 (가장 마지막에 닫음)
        try { if (conn != null && !conn.isClosed()) { conn.close(); } } catch (SQLException e) { logger.log(Level.WARNING, "Connection 닫기 실패", e); }
    }
}