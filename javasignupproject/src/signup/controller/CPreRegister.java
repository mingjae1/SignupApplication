package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import signup.constants.StatusConstants;
import signup.model.MLecture;
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VPreRegister;

/**
 * '미리담기 내역' 뷰(VPreRegister)를 제어하는 자식 컨트롤러입니다.
 * CListController를 상속받아 공통 기능(삭제)을 사용하고,
 * 고유 기능(수강신청)을 추가로 구현합니다.
 */
public class CPreRegister extends CListController {

    private VPreRegister vPreRegister; // 이 컨트롤러가 직접 제어할 뷰

    /**
     * CPreRegister 컨트롤러를 생성합니다.
     * 부모(CListController) 생성자에 뷰, 모델, DAO 및 "pre" 상태를 전달합니다.
     *
     * @param vPreRegister 제어할 미리담기 뷰 (VPreRegister)
     * @param mMain 전역 모델 (MMain)
     * @param saveDAO DB 접근 객체 (SaveDAO)
     */
    public CPreRegister(VPreRegister vPreRegister, MMain mMain, SaveDAO saveDAO) {
        // 부모 생성자(CListController) 호출
        super(
            vPreRegister,         // 1. 제어할 뷰(JPanel)
            vPreRegister.getTable(), // 2. 제어할 테이블(JTable)
            mMain,               // 3. MMain
            saveDAO,             // 4. SaveDAO
            StatusConstants.PRE_REGISTER // 5. 이 컨트롤러는 "pre" (미리담기) 상태를 담당
        );
        
        this.vPreRegister = vPreRegister;

        // 1. "수강신청" 버튼 -> 이 클래스의 'handleApply' 메서드 연결 (고유 기능)
        this.vPreRegister.getApplyButton().addActionListener(this::handleApply);
        
        // 2. "목록 삭제" 버튼 -> 부모(CListController)의 'handleDelete' 메서드 연결 (공통 기능)
        this.vPreRegister.getDeleteButton().addActionListener(this::handleDelete);
    }

    /**
     * [구현] CListController(부모)의 추상 메서드를 구현합니다.
     * 부모의 'refreshTable' 메서드가 호출할 때, VPreRegister 뷰의
     * 테이블을 실제로 업데이트하는 역할을 합니다.
     *
     * @param data DB에서 가져온 최신 강의 목록
     */
    @Override
    protected void updateViewTable(List<MLecture> data) {
        this.vPreRegister.updateTable(data);
    }

    /**
     * "수강신청" 버튼 클릭 이벤트를 처리합니다. (CPreRegister의 고유 기능)
     * 선택된 강의를 "pre" 상태에서 "reg" 상태로 변경 (add + remove)합니다.
     */
    private void handleApply(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        JTable table = vPreRegister.getTable();
        int selectedRow = table.getSelectedRow();

        if (userId == null) {
            JOptionPane.showMessageDialog(vPreRegister, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vPreRegister, "수강신청할 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // 1. 테이블에서 정보 가져오기
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1);
            int newCredits = (int) table.getModel().getValueAt(selectedRow, 3);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            // 2. SaveDAO에 "reg" 상태로 신청 요청 (학점 검사 포함)
            int resultCode = this.saveDAO.addLecture(userId, lectureId, StatusConstants.REGISTER, newCredits);
    
            // 3. DAO의 결과 코드에 따라 피드백
            switch (resultCode) {
                case 0: // 수강신청 성공
                    // 수강신청에 성공했으므로, "pre"(미리담기) 목록에서는 삭제
                    this.saveDAO.removeLecture(userId, lectureId, StatusConstants.PRE_REGISTER); 
                    
                    JOptionPane.showMessageDialog(vPreRegister, "[" + lectureName + "] 수강신청 되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(); // 미리담기 목록 새로고침 (항목이 사라짐)
                    break;
                case 1: // 학점 초과
                    JOptionPane.showMessageDialog(vPreRegister, 
                        "최대 수강신청 학점을 초과하여 신청할 수 없습니다.",
                        "학점 초과", JOptionPane.ERROR_MESSAGE);
                    break;
                case 2: // 중복
                    JOptionPane.showMessageDialog(vPreRegister, "이미 수강신청 내역에 존재하는 과목입니다.", "알림", JOptionPane.WARNING_MESSAGE);
                    break;
                default: // -1 (DB 오류 등)
                    JOptionPane.showMessageDialog(vPreRegister, "수강신청 중 알 수 없는 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vPreRegister, "처리 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}