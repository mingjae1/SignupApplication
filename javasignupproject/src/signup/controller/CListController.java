package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JPanel;

import signup.constants.StatusConstants;
import signup.model.MLecture;
import signup.model.MMain;
import signup.dao.SaveDAO;

/**
 * [신규] 수강신청/미리담기 목록 컨트롤러의 공통 기능을 담는 '부모' 추상 클래스입니다.
 * CRegister와 CPreRegister가 이 클래스를 상속받아 코드 중복을 제거합니다.
 */
public abstract class CListController {

    // 1. 자식 클래스들이 공통으로 사용할 필드 (protected)
    protected MMain mMain;
    protected SaveDAO saveDAO;
    
    /**
     * 이 컨트롤러가 제어할 뷰(View) 패널입니다.
     * (JOptionPane을 띄울 때 부모 창으로 사용됩니다.)
     */
    protected JPanel viewPanel;
    
    /**
     * 이 컨트롤러가 제어할 뷰(View)의 테이블입니다.
     * (선택된 행을 찾을 때 사용됩니다.)
     */
    protected JTable listTable;
    
    /**
     * 이 컨트롤러의 상태 ("reg" 또는 "pre")입니다.
     */
    protected String status;

    /**
     * 공통 컨트롤러의 생성자입니다.
     * 자식 클래스(CRegister, CPreRegister)가 이 생성자를 호출해야 합니다.
     *
     * @param viewPanel 컨트롤러가 제어할 뷰(JPanel)
     * @param table 컨트롤러가 제어할 테이블(JTable)
     * @param mMain 전역 모델 (MMain)
     * @param saveDAO DB 접근 객체 (SaveDAO)
     * @param status 이 컨트롤러가 처리할 상태 ("reg" 또는 "pre")
     */
    protected CListController(JPanel viewPanel, JTable table, MMain mMain, SaveDAO saveDAO, String status) {
        this.viewPanel = viewPanel;
        this.listTable = table;
        this.mMain = mMain;
        this.saveDAO = saveDAO;
        this.status = status;
    }

    /**
     * CMain(툴바 컨트롤러)이 이 패널을 보여줄 때 호출할 "공개(public)" 새로고침 메서드입니다.
     * DB에서 최신 목록을 가져와 뷰(View)의 테이블을 업데이트합니다.
     */
    public void refreshTable() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            // 로그인이 풀렸을 경우를 대비해 테이블을 비움
            this.updateViewTable(null); 
            return; 
        }
        
        // DAO에 "reg" 또는 "pre" 상태의 목록을 요청
        List<MLecture> data = this.saveDAO.getLecturesByStatus(userId, status); 
        
        // 뷰(View)의 테이블을 업데이트 (자식이 구현한 메서드 호출)
        this.updateViewTable(data);
    }

    /**
     * "신청 취소" 또는 "목록 삭제" 버튼 클릭 이벤트를 공통으로 처리합니다.
     * 이 메서드는 자식 클래스의 버튼 리스너에 의해 호출됩니다.
     */
    protected void handleDelete(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        int selectedRow = listTable.getSelectedRow();

        // 1. 유효성 검사
        if (userId == null) {
            JOptionPane.showMessageDialog(viewPanel, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            String action = StatusConstants.REGISTER.equals(status) ? "취소할" : "삭제할";
            JOptionPane.showMessageDialog(viewPanel, action + " 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // 2. 테이블에서 과목 ID와 이름 가져오기
            String lectureIdStr = (String) listTable.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) listTable.getModel().getValueAt(selectedRow, 1);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            // 3. SaveDAO에 "reg" 또는 "pre" 상태의 강의 삭제 요청
            boolean success = this.saveDAO.removeLecture(userId, lectureId, this.status);
    
            if (success) {
                String action = StatusConstants.REGISTER.equals(status) ? "신청이 취소" : "목록에서 삭제";
                JOptionPane.showMessageDialog(viewPanel, "[" + lectureName + "] " + action + "되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                // 4. 삭제 성공 후, 테이블을 즉시 새로고침
                refreshTable(); 
            } else {
                String action = StatusConstants.REGISTER.equals(status) ? "신청 취소" : "삭제";
                JOptionPane.showMessageDialog(viewPanel, action + " 실패 (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(viewPanel, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * [추상 메서드] 자식 컨트롤러(CRegister, CPreRegister)가 
     * 각자의 뷰(View) 테이블을 어떻게 업데이트할지 *반드시* 구현해야 하는 메서드입니다.
     * @param lectureList DB에서 가져온 최신 강의 목록
     */
    protected abstract void updateViewTable(List<MLecture> lectureList);
}