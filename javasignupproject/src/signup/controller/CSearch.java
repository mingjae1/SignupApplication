package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import signup.dao.LectureDAO;
import signup.dao.SaveDAO;
import signup.dao.UserDAO;
import signup.model.MLecture; // 강의 DTO
import signup.model.MMain;
import signup.view.VSearch;
import signup.model.ComboboxItem; // ComboboxItem DTO 임포트

/**
 * VSearch(강좌 검색 뷰)의 모든 이벤트를 처리하고
 * DAO와 통신하여 강좌를 조회하거나 수강신청/미리담기를 처리하는 컨트롤러입니다.
 */
public class CSearch {
    
	// 로거 설정
	private static final Logger logger = Logger.getLogger(CSearch.class.getName());
	
    // MVC 컴포넌트
    private VSearch vSearch;
    private MMain mMain;
    
    // DAO
    private LectureDAO lectureDAO;
    private SaveDAO saveDAO;
    private UserDAO userDAO;

    /**
     * CSearch 컨트롤러를 생성하고 DAO를 주입받아 뷰에 리스너를 연결합니다.
     */
    public CSearch(VSearch vSearch, MMain mMain, LectureDAO lectureDAO, SaveDAO saveDAO, UserDAO userDAO) {
        this.vSearch = vSearch;
        this.mMain = mMain;
        this.lectureDAO = lectureDAO;
        this.saveDAO = saveDAO;
        this.userDAO = userDAO;
        
        // 1. 이벤트 리스너 연결
        this.vSearch.getSearchButton().addActionListener(this::handleSearch);
        this.vSearch.getRegisterButton().addActionListener(e -> handleSave("reg")); 
        this.vSearch.getPreRegisterButton().addActionListener(e -> handleSave("pre")); 
        
        // 콤보박스 연쇄 동작 리스너 연결
        this.vSearch.getComboCollege().addActionListener(this::handleCollegeSelect);

    }

    /**
     * 현재 로그인한 사용자의 캠퍼스에 맞는 '단과대학' 목록을 뷰에 채웁니다.
     * (LectureDAO는 List<ComboboxItem>을 반환해야 함)
     */
    public void loadInitialCollegeData() {
        String userId = mMain.getCurrentUserId();
        // 1. 로그인이 안 되어 있으면(userId=null) 중단
        if (userId == null) { 
            logger.log(Level.WARNING, "loadInitialCollegeData: userId가 null입니다. (로그인 전)");
            return; 
        }

        // 2. UserDAO로부터 'String 이름' 대신 'int ID'를 가져옵니다.
        int campusId = userDAO.getCampusIdByUserId(userId);
        
        // 3. 유효한 ID인지 확인합니다. (DAO가 실패 시 -1을 반환한다고 가정)
        if (campusId == -1) {
            logger.log(Level.SEVERE, "loadInitialCollegeData: 유효한 campusId를 찾지 못했습니다.");
            return; 
        }

        // 4. LectureDAO에 'String 이름' 대신 'int ID'를 넘깁니다.
        List<ComboboxItem> colleges = lectureDAO.getCollegesByCampus(campusId);
        
        // JComboBox<Object>를 사용하여 ComboboxItem 객체를 채웁니다.
        JComboBox<Object> comboCollege = vSearch.getComboCollege(); 
        comboCollege.removeAllItems();
        comboCollege.addItem("- 대학 전체 -"); // "모두" 옵션
        for (ComboboxItem item : colleges) {
            comboCollege.addItem(item); 
        }
    }
    
 // CSearch.java의 handleCollegeSelect 메서드

    /**
     * '단과대학' 콤보박스 선택 시 '학과' 목록을 DB에서 로드합니다.
     */
    private void handleCollegeSelect(ActionEvent e) {
        JComboBox<Object> comboCollege = vSearch.getComboCollege();
        JComboBox<Object> comboDept = vSearch.getComboDept();

        // 선택된 항목이 ComboboxItem 객체인지 확인 ("- 대학 전체 -" 등 프롬프트 제외)
        if (!(comboCollege.getSelectedItem() instanceof ComboboxItem)) {
            // "대학 전체"를 선택하거나 초기 상태일 때 학과 콤보박스 비활성화
            comboDept.removeAllItems();
            comboDept.addItem("- 학과 선택 -"); 
            comboDept.setEnabled(false);
            return;
        }

        // ComboboxItem에서 대학 ID 추출 (DB 쿼리용)
        ComboboxItem selectedCollege = (ComboboxItem) comboCollege.getSelectedItem();
        
        // [수정!] LectureDAO에 String 이름 대신 int ID를 전달합니다.
        List<ComboboxItem> departments = lectureDAO.getDepartmentsByCollege(selectedCollege.getId());
        
        comboDept.removeAllItems();
        comboDept.addItem("- 학과 전체 -");
        for (ComboboxItem department : departments) {
            comboDept.addItem(department); // ComboboxItem 객체 추가
        }
        comboDept.setEnabled(true);
    }

    /**
     * "조회" 버튼 클릭 시, 검색 조건에 맞는 강좌 목록을 DB에서 가져와 테이블을 업데이트합니다.
     */
    private void handleSearch(ActionEvent e) {
        // 1. 현재 로그인한 사용자 ID 확인 (DB 검색 필터링 기준)
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(vSearch, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. 뷰(View)에서 검색 조건 가져오기
        String collegeName = "";
        if (vSearch.getComboCollege().getSelectedIndex() > 0) {
            collegeName = ((ComboboxItem) vSearch.getComboCollege().getSelectedItem()).getName();
        }
        
        String deptName = "";
        if (vSearch.getComboDept().isEnabled() && vSearch.getComboDept().getSelectedIndex() > 0) {
            deptName = ((ComboboxItem) vSearch.getComboDept().getSelectedItem()).getName();
        }
        
        String keyword = vSearch.getSearchField().getText();
        
        // 3. DAO로 검색 요청
        List<MLecture> results = lectureDAO.searchLectures(userId, collegeName, deptName, keyword);
        
        // 4. JTable 모델 업데이트
        DefaultTableModel model = vSearch.getTableModel();
        model.setRowCount(0); // 테이블 비우기
        
        for (MLecture lecture : results) {
            Object[] row = {
                lecture.getId(),
                lecture.getName(),
                lecture.getProfessor(),
                lecture.getCredits(),
                lecture.getSchedule()
            };
            model.addRow(row);
        }
    }
    
    /**
     * "수강신청" 또는 "미리담기" 버튼 클릭을 처리하고, 내역을 DB에 저장합니다.
     * @param status "reg"(수강신청) 또는 "pre"(미리담기)
     */
    private void handleSave(String status) {
        String userId = mMain.getCurrentUserId();
        JTable table = vSearch.getResultTable();
        int selectedRow = table.getSelectedRow();
        
        // 1. 유효성 검사
        if (userId == null) {
            JOptionPane.showMessageDialog(vSearch, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vSearch, "먼저 테이블에서 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // 2. 테이블에서 과목코드(ID)와 '과목명'을 가져옵니다.
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1); 
            int lectureId = Integer.parseInt(lectureIdStr);
            
            // 3. SaveDAO로 저장 요청
            boolean success = saveDAO.addLecture(userId, lectureId, status);
            
            // 4. 결과 피드백
            if (success) {
                String message = status.equals("reg") ? "수강신청" : "미리담기";
                JOptionPane.showMessageDialog(vSearch, "[" + lectureName + "]\n" + message + " 되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vSearch, "이미 신청(또는 미리담기)한 과목입니다.", "알림", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vSearch, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vSearch, "처리 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
        /**
         * CMain(툴바)의 '새로고침' 버튼에 의해 호출됩니다.
         * VSearch(뷰)의 '조회' 버튼을 프로그래밍 방식으로 클릭하여
         * 현재 검색 조건으로 테이블을 새로고침합니다.
         */
        public void refreshSearch() {
            // CSearch는 vSearch(뷰)를 알고 있으므로, 
            // 뷰의 '조회' 버튼을 대신 클릭해 줍니다.
            this.vSearch.getSearchButton().doClick();
        }
    }
