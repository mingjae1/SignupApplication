package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import signup.constants.AppConstants;
import signup.constants.StatusConstants;
import signup.dao.LectureDAO;
import signup.dao.SaveDAO;
import signup.dao.UserDAO;
import signup.model.MLecture;
import signup.model.MMain;
import signup.view.VSearch;
import signup.model.ComboboxItem;

public class CSearch {
    
    private static final Logger logger = Logger.getLogger(CSearch.class.getName());
    
    private VSearch vSearch;
    private MMain mMain;
    private LectureDAO lectureDAO;
    private SaveDAO saveDAO;
    private UserDAO userDAO;

    public CSearch(VSearch vSearch, MMain mMain, LectureDAO lectureDAO, SaveDAO saveDAO, UserDAO userDAO) {
        this.vSearch = vSearch;
        this.mMain = mMain;
        this.lectureDAO = lectureDAO;
        this.saveDAO = saveDAO;
        this.userDAO = userDAO;
        
        this.vSearch.getSearchButton().addActionListener(this::handleSearch);
        this.vSearch.getRegisterButton().addActionListener(e -> handleSave(StatusConstants.REGISTER)); 
        this.vSearch.getPreRegisterButton().addActionListener(e -> handleSave(StatusConstants.PRE_REGISTER));
        this.vSearch.getComboCollege().addActionListener(this::handleCollegeSelect);
    }

    public void loadInitialCollegeData() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) { 
            logger.log(Level.WARNING, "loadInitialCollegeData: userId가 null입니다.");
            return; 
        }

        int campusId = userDAO.getCampusIdByUserId(userId);
        if (campusId == -1) {
            logger.log(Level.SEVERE, "loadInitialCollegeData: 유효한 campusId를 찾지 못했습니다.");
            return; 
        }

        List<ComboboxItem> colleges = lectureDAO.getCollegesByCampus(campusId);
        
        JComboBox<Object> comboCollege = vSearch.getComboCollege(); 
        comboCollege.removeAllItems();
        comboCollege.addItem("- 대학 전체 -");
        for (ComboboxItem item : colleges) {
            comboCollege.addItem(item); 
        }
    }
    
    private void handleCollegeSelect(ActionEvent e) {
        JComboBox<Object> comboCollege = vSearch.getComboCollege();
        JComboBox<Object> comboDept = vSearch.getComboDept();

        if (!(comboCollege.getSelectedItem() instanceof ComboboxItem)) {
            comboDept.removeAllItems();
            comboDept.addItem("- 학과 선택 -"); 
            comboDept.setEnabled(false);
            return;
        }

        ComboboxItem selectedCollege = (ComboboxItem) comboCollege.getSelectedItem();
        List<ComboboxItem> departments = lectureDAO.getDepartmentsByCollege(selectedCollege.getId());
        
        comboDept.removeAllItems();
        comboDept.addItem("- 학과 전체 -");
        for (ComboboxItem department : departments) {
            comboDept.addItem(department);
        }
        comboDept.setEnabled(true);
    }

    private void handleSearch(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(vSearch, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String collegeName = "";
        if (vSearch.getComboCollege().getSelectedIndex() > 0) {
            collegeName = ((ComboboxItem) vSearch.getComboCollege().getSelectedItem()).getName();
        }
        
        String deptName = "";
        if (vSearch.getComboDept().isEnabled() && vSearch.getComboDept().getSelectedIndex() > 0) {
            deptName = ((ComboboxItem) vSearch.getComboDept().getSelectedItem()).getName();
        }
        
        String keyword = vSearch.getSearchField().getText();
        List<MLecture> results = lectureDAO.searchLectures(userId, collegeName, deptName, keyword);
        
        DefaultTableModel model = vSearch.getTableModel();
        model.setRowCount(0);
        
        for (MLecture lecture : results) {
            model.addRow(new Object[]{
                lecture.getId(),
                lecture.getName(),
                lecture.getProfessor(),
                lecture.getCredits(),
                lecture.getSchedule()
            });
        }
    }
    
    private void handleSave(String status) {
        String userId = mMain.getCurrentUserId();
        JTable table = vSearch.getResultTable();
        int selectedRow = table.getSelectedRow();
        
        if (userId == null) {
            JOptionPane.showMessageDialog(vSearch, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vSearch, "먼저 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1);
            int newCredits = (int) table.getModel().getValueAt(selectedRow, 3);
            int lectureId = Integer.parseInt(lectureIdStr);
            
            int resultCode = saveDAO.addLecture(userId, lectureId, status, newCredits);
            handleSaveResult(resultCode, lectureName, status);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vSearch, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshSearch() {
        this.vSearch.getSearchButton().doClick();
    }
        
    public void setMode(String mode) {
        this.vSearch.setMode(mode);
    }
    
    private void handleSaveResult(int resultCode, String lectureName, String status) {
        String messageType = StatusConstants.REGISTER.equals(status) ? "수강신청" : "미리담기";
        
        switch (resultCode) {
        case AppConstants.DB_SUCCESS:
            JOptionPane.showMessageDialog(vSearch, 
                "[" + lectureName + "]\n" + messageType + " 되었습니다.", 
                "성공", JOptionPane.INFORMATION_MESSAGE);
            break;
        case AppConstants.DB_ERROR_CREDIT_EXCEEDED:
            JOptionPane.showMessageDialog(vSearch, 
                "최대 " + messageType + " 학점(" + AppConstants.MAX_CREDITS + "학점)을 초과할 수 없습니다.",
                "학점 초과", JOptionPane.ERROR_MESSAGE);
            break;
        case AppConstants.DB_ERROR_DUPLICATE:
            JOptionPane.showMessageDialog(vSearch, 
                "이미 " + messageType + " 내역에 존재하는 과목입니다.", 
                "알림", JOptionPane.WARNING_MESSAGE);
            break;
        case AppConstants.DB_ERROR_GENERAL:
            JOptionPane.showMessageDialog(vSearch, 
                messageType + " 중 DB 오류가 발생했습니다.", 
                "오류", JOptionPane.ERROR_MESSAGE);
            break;
        default:
            JOptionPane.showMessageDialog(vSearch, 
                messageType + " 중 알 수 없는 오류가 발생했습니다. (Code: " + resultCode + ")", 
                "오류", JOptionPane.ERROR_MESSAGE);
            break;
        }
    }
}
