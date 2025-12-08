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

public class DAO {
    
    private static final String DBURL = "jdbc:mysql://localhost:3306/lms_data";
    private static final String DBID = "root";
    private String dbpassword;
    private static final Logger logger = Logger.getLogger(DAO.class.getName());

    public DAO() {
        loadDbPassword();
        loadJdbcDriver();
    }
    
    private void loadDbPassword() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            dbpassword = props.getProperty("db.password");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "config.properties 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }
    
    private void loadJdbcDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC 드라이버 로드 실패", e);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DBURL, DBID, dbpassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB 연결 실패", e);
            return null;
        }
    }

    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        if (rs != null) {
            try {
                if (!rs.isClosed()) rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "ResultSet 닫기 실패", e);
            }
        }
        if (pstmt != null) {
            try {
                if (!pstmt.isClosed()) pstmt.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "PreparedStatement 닫기 실패", e);
            }
        }
        if (conn != null) {
            try {
                if (!conn.isClosed()) conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Connection 닫기 실패", e);
            }
        }
    }
}