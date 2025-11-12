package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JOptionPane;

import signup.model.MLecture; // 님의 DTO 클래스
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VRegister;

/**
 * VRegister(수강신청 내역 뷰)의 내부 이벤트를 전담하는 컨트롤러입니다.
 * (예: '신청 취소' 버튼 클릭)
 */
public class CRegister {

    private VRegister vRegister; // 제어할 뷰
    private MMain mMain;         // 유저 ID를 가져올 모델
    private SaveDAO saveDAO;     // DB 작업을 위한 DAO

    /**
     * CRegister 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO 객체를 주입받습니다.
     * @param vRegister 제어할 수강신청 뷰 (VRegister)
     * @param mMain 전역 모델 (MMain)
     * @param saveDAO DB 접근 객체 (SaveDAO)
     */
    public CRegister(VRegister vRegister, MMain mMain, SaveDAO saveDAO) {
        this.vRegister = vRegister;
        this.mMain = mMain;
        this.saveDAO = saveDAO;

        // 1. VRegister 뷰 내부의 "신청 취소" 버튼에 리스너를 연결
        this.vRegister.getCancelButton().addActionListener(this::handleCancel);
    }

    /**
     * "신청 취소" 버튼 클릭 이벤트를 처리합니다.
     * 테이블에서 선택된 강의를 DB에서 삭제합니다.
     */
    private void handleCancel(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        JTable table = vRegister.getTable();
        int selectedRow = table.getSelectedRow();

        // 1. 유효성 검사 (로그인 상태, 항목 선택)
        if (userId == null) {
            JOptionPane.showMessageDialog(vRegister, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vRegister, "취소할 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // 2. 테이블에서 과목 ID와 이름 가져오기
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            // 3. SaveDAO에 "reg" 상태의 강의 삭제 요청
            boolean success = this.saveDAO.removeLecture(userId, lectureId, "reg");
    
            if (success) {
                JOptionPane.showMessageDialog(vRegister, "[" + lectureName + "] 신청이 취소되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                // 4. 삭제 성공 후, 테이블을 즉시 새로고침
                refreshTable(); 
            } else {
                JOptionPane.showMessageDialog(vRegister, "신청 취소 실패 (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vRegister, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * CMain(툴바 컨트롤러)이 이 패널을 보여줄 때 호출할
     * "공개(public)" 새로고침 메서드입니다.
     */
    public void refreshTable() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            // 로그인이 풀렸을 경우를 대비해 테이블을 비움
            this.vRegister.updateTable(null); 
            return; 
        }
        
        // "reg" (수강신청) 상태의 목록을 DB에서 가져옴
        List<MLecture> registeredData = this.saveDAO.getLecturesByStatus(userId, "reg"); 
        
        // VRegister 뷰의 테이블을 업데이트
        this.vRegister.updateTable(registeredData);
    }
}