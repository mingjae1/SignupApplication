package signup.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 기본 DAO 클래스: DB 연결 및 자원 관리 기능을 제공합니다.
 * 
 * <p>이 클래스는 MySQL 데이터베이스에 대한 기본 연결 기능을 제공합니다.
 * 다른 DAO 클래스들(UserDAO, LectureDAO, SaveDAO 등)은 이 클래스를 사용하여
 * 데이터베이스 연결을 얻고, 공용 자원 해제 메서드를 활용합니다.</p>
 * 
 * <h3>설정 방법:</h3>
 * <ul>
 *   <li>config.properties 파일에 db.password 속성을 설정해야 합니다.</li>
 *   <li>MySQL 서버가 localhost:3306에서 실행 중이어야 합니다.</li>
 *   <li>lms_data 데이터베이스가 생성되어 있어야 합니다.</li>
 * </ul>
 * 
 * <h3>사용 패턴:</h3>
 * <pre>
 * Connection conn = dao.getConnection();
 * try {
 *     // DB 작업 수행
 * } finally {
 *     DAO.close(rs, pstmt, conn);
 * }
 * </pre>
 */
public class DAO {
    
    // DB 연결 정보
    private static final String DBURL = "jdbc:mysql://localhost:3306/lms_data";
    private static final String DBID = "root";
    private String dbpassword;
    
    private static final Logger logger = Logger.getLogger(DAO.class.getName());

    /**
     * DAO 생성자: config.properties 파일에서 DB 비밀번호를 읽어오고
     * MySQL JDBC 드라이버를 로드합니다.
     */
    public DAO() {
        loadDbPassword();
        loadJdbcDriver();
    }
    
    /**
     * config.properties 파일에서 DB 비밀번호를 읽어옵니다.
     */
    private void loadDbPassword() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            dbpassword = props.getProperty("db.password");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "config.properties 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * MySQL JDBC 드라이버를 로드합니다.
     */
    private void loadJdbcDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC 드라이버 로드 실패", e);
        }
    }

    /**
     * DB 연결을 생성하여 반환합니다.
     * @return Connection 객체, 연결 실패 시 null
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DBURL, DBID, dbpassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB 연결 실패", e);
            return null;
        }
    }

    /**
     * 사용한 DB 자원(Connection, PreparedStatement, ResultSet)을 
     * 안전하게 닫습니다.
     * @param rs 닫을 ResultSet (null이어도 안전)
     * @param pstmt 닫을 PreparedStatement (null이어도 안전)
     * @param conn 닫을 Connection (null이어도 안전)
     */
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        // 1. ResultSet 닫기
        if (rs != null) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "ResultSet 닫기 실패", e);
            }
        }

        // 2. PreparedStatement 닫기
        if (pstmt != null) {
            try {
                if (!pstmt.isClosed()) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "PreparedStatement 닫기 실패", e);
            }
        }

        // 3. Connection 닫기
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Connection 닫기 실패", e);
            }
        }
    }
}