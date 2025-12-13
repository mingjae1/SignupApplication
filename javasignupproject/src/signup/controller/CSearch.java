package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import signup.constants.AppConstants;
import signup.constants.ControllerConstants;
import signup.constants.StatusConstants;
import signup.constants.ViewConstants;
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
            ViewConstants.showErrorMessage(vSearch, "로그인이 필요합니다.", ControllerConstants.TITLE_ERROR);
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
            ViewConstants.showErrorMessage(vSearch, "로그인이 필요합니다.", ControllerConstants.TITLE_ERROR);
            return;
        }
        if (selectedRow == -1) {
            ViewConstants.showInfoMessage(vSearch, "먼저 강의를 선택하세요.", ControllerConstants.TITLE_CONFIRMATION);
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
            ViewConstants.showErrorMessage(vSearch, "강의 코드를 숫자로 변환하는 데 실패했습니다.", ControllerConstants.TITLE_ERROR);
        }
    }
    
    public void refreshSearch() {
        this.vSearch.getSearchButton().doClick();
    }
        
    public void setMode(String mode) {
        this.vSearch.setMode(mode);
    }
    
    /**
     * 강의 저장 결과 처리 (공통)
     * @param resultCode SaveDAO.addLecture 반환 코드
     * @param lectureName 강의 이름
     * @param status 저장 상태 (StatusConstants.REGISTER 또는 PRE_REGISTER)
     * 
     * [결과 코드 처리]
     * - DB_SUCCESS(0): 정상 추가
     * - DB_ERROR_CREDIT_EXCEEDED(1): 학점 초과
     * - DB_ERROR_DUPLICATE(2): 중복
     * - DB_ERROR_GENERAL(-1): DB 오류
     * 
     * [오류 대응]
     * - 학점 초과: 기존 신청 내역 확인, MAX_CREDITS 확인
     * - 중복: 이미 추가된 강의, 새로고침 필요
     * - DB 오류: 로그 확인, DB 연결 상태 확인
     */
    private void handleSaveResult(int resultCode, String lectureName, String status) {
        String messageType = StatusConstants.REGISTER.equals(status) ? "수강신청" : "미리담기";
        
        switch (resultCode) {
        case AppConstants.DB_SUCCESS:
            ViewConstants.showInfoMessage(vSearch, 
                "[" + lectureName + "]\n" + messageType + " 되었습니다.", 
                ControllerConstants.TITLE_COMPLETE);
            break;
        case AppConstants.DB_ERROR_CREDIT_EXCEEDED:
            ViewConstants.showErrorMessage(vSearch, 
                ControllerConstants.ERROR_CREDIT_EXCEEDED,
                ControllerConstants.TITLE_ERROR);
            break;
        case AppConstants.DB_ERROR_DUPLICATE:
            ViewConstants.showErrorMessage(vSearch, 
                ControllerConstants.ERROR_DUPLICATE_LECTURE, 
                ControllerConstants.TITLE_CONFIRMATION);
            break;
        case AppConstants.DB_ERROR_GENERAL:
            ViewConstants.showErrorMessage(vSearch, 
                ControllerConstants.ERROR_DB_CONNECTION, 
                ControllerConstants.TITLE_ERROR);
            break;
        default:
            ViewConstants.showErrorMessage(vSearch, 
                "알 수 없는 오류가 발생했습니다. (Code: " + resultCode + ")", 
                ControllerConstants.TITLE_ERROR);
            break;
        }
    }
}
