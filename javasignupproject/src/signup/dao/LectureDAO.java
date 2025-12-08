package signup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.model.ComboboxItem;
import signup.model.MLecture; 

public class LectureDAO {
    
    private DAO dao; 
    private static final Logger logger = Logger.getLogger(LectureDAO.class.getName());

    public LectureDAO() {
        this.dao = new DAO();
    }

    public List<ComboboxItem> getAllCampuses() {
        List<ComboboxItem> campuses = new ArrayList<>();
        String sql = "SELECT id, name FROM root ORDER BY id"; 

        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                campuses.add(new ComboboxItem(rs.getString("name"), rs.getInt("id")));
            } 
        } 
        catch (SQLException e) { logger.log(Level.SEVERE, "모든 캠퍼스 조회 중 SQL 오류", e); } 
        return campuses;
    }

    public List<ComboboxItem> getCollegesByCampus(int campusId) {
        List<ComboboxItem> colleges = new ArrayList<>();
        
        String sql = "SELECT c.id, c.name FROM college c WHERE c.root_id = ? " +
                     "AND c.name != '교양' ORDER BY c.id";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, campusId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    colleges.add(new ComboboxItem(rs.getString("name"), rs.getInt("id")));
                }
            }
        } 
        catch (SQLException e) { logger.log(Level.WARNING, "캠퍼스별 단과대학 조회 SQL 오류", e); }
        return colleges;
    }

    public List<ComboboxItem> getDepartmentsByCollege(int collegeId) {
        List<ComboboxItem> departments = new ArrayList<>();
        
        String sql = "SELECT d.id, d.name FROM department d WHERE d.college_id = ? ORDER BY d.id";

        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collegeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    departments.add(new ComboboxItem(rs.getString("name"), rs.getInt("id")));
                }
            } 
        } 
        catch (SQLException e) { logger.log(Level.WARNING, "단과대학별 학과 조회 SQL 오류", e); }
        return departments;
    }
    
    public List<MLecture> searchLectures(String userId, String collegeName, String deptName, String keyword) {
        List<MLecture> lectures = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT l.id, l.name, l.professor, l.credit, l.time " +
            "FROM lecture l " +
            "JOIN department d ON l.department_id = d.id " +
            "JOIN college c ON d.college_id = c.id " +
            "JOIN root r ON c.root_id = r.id " +
            "JOIN user u ON r.id = u.campus_id " + 
            "WHERE u.userid = ? " 
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append(" AND c.name = ?");
            params.add(collegeName);
        }
        
        if (deptName != null && !deptName.isEmpty()) {
            sql.append(" AND d.name = ?");
            params.add(deptName);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (REPLACE(l.name, ' ', '') LIKE ? OR REPLACE(l.professor, ' ', '') LIKE ?)");
            String keywordNoSpace = "%" + keyword.replace(" ", "") + "%";
            params.add(keywordNoSpace); 
            params.add(keywordNoSpace);
        }

        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
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
        catch (SQLException e) { logger.log(Level.WARNING, "강의 검색 SQL 오류", e); } 
        return lectures;
    }
    
    public List<MLecture> getAllLectures() {
        List<MLecture> lectures = new ArrayList<>();

        String sql = "SELECT * FROM lecture ORDER BY id";

        Connection conn = dao.getConnection();
        if (conn == null) return lectures;
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                lectures.add(new MLecture(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getInt("credit"),
                    rs.getString("time"),
                    rs.getInt("department_id")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "전체 강의 조회 SQL 오류", e);
        }
        return lectures;
    }
   
    public List<String> getAllDepartments() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT d.id, d.name, c.name AS college_name " +
                     "FROM department d " +
                     "JOIN college c ON d.college_id = c.id " +
                     "ORDER BY d.id";
        
        try (Connection conn = dao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(String.format("%d: %s (%s)", 
                    rs.getInt("id"), rs.getString("name"), rs.getString("college_name")));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "학과 목록 조회 오류", e);
        }
        return list;
    }

    public boolean insertLecture(int id, String name, String prof, int credit, String time, int deptId) {
        String sql = "INSERT INTO lecture (id, name, professor, credit, time, department_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = dao.getConnection();
        if (conn == null) return false;
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, prof);
            pstmt.setInt(4, credit);
            pstmt.setString(5, time);
            pstmt.setInt(6, deptId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 추가 SQL 오류", e);
            return false;
        }
    }

    public boolean updateLecture(int id, String name, String prof, int credit, String time, int deptId) {
        String sql = "UPDATE lecture SET name=?, professor=?, credit=?, time=?, department_id=? WHERE id=?";
        
        Connection conn = dao.getConnection();
        if (conn == null) return false;
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, prof);
            pstmt.setInt(3, credit);
            pstmt.setString(4, time);
            pstmt.setInt(5, deptId);
            pstmt.setInt(6, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 수정 SQL 오류", e);
            return false;
        }
    }

    public boolean deleteLecture(int id) {
        String sql = "DELETE FROM lecture WHERE id = ?";
        
        Connection conn = dao.getConnection();
        if (conn == null) return false;
        
        try (Connection connection = conn;
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "강의 삭제 SQL 오류", e);
            return false;
        }
    }
}