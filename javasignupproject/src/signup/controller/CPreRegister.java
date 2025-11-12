package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JOptionPane;

import signup.model.MLecture; // 님의 DTO 클래스
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VPreRegister;

/**
 * VPreRegister(미리담기 내역 뷰)의 내부 이벤트를 전담하는 컨트롤러입니다.
 * (예: '수강신청' 버튼, '목록 삭제' 버튼 클릭)
 */
public class CPreRegister {

    private VPreRegister vPreRegister; // 제어할 뷰
    private MMain mMain;               // 유저 ID를 가져올 모델
    private SaveDAO saveDAO;           // DB 작업을 위한 DAO

    /**
     * CPreRegister 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO 객체를 주입받습니다.
     * @param vPreRegister 제어할 미리담기 뷰 (VPreRegister)
     * @param mMain 전역 모델 (MMain)
     * @param saveDAO DB 접근 객체 (SaveDAO)
     */
    public CPreRegister(VPreRegister vPreRegister, MMain mMain, SaveDAO saveDAO) {
        this.vPreRegister = vPreRegister;
        this.mMain = mMain;
        this.saveDAO = saveDAO;

        // 1. VPreRegister 뷰 내부의 버튼들에 리스너를 연결
        this.vPreRegister.getApplyButton().addActionListener(this::handleApply);
        this.vPreRegister.getDeleteButton().addActionListener(this::handleDelete);
    }

    /**
     * "목록 삭제" 버튼 클릭 이벤트를 처리합니다.
     * 테이블에서 선택된 강의를 DB에서 삭제("pre" 상태)합니다.
     */
    private void handleDelete(ActionEvent e) {
    	 
        String userId = mMain.getCurrentUserId();
        JTable table = vPreRegister.getTable();
        int selectedRow = table.getSelectedRow();

        if (userId == null) {
            JOptionPane.showMessageDialog(vPreRegister, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vPreRegister, "삭제할 강의를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            // SaveDAO에 "pre" (미리담기) 상태의 강의 삭제 요청
            boolean success = this.saveDAO.removeLecture(userId, lectureId, "pre");
    
            if (success) {
                JOptionPane.showMessageDialog(vPreRegister, "[" + lectureName + "] 미리담기 목록에서 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(); // 테이블 즉시 새로고침
            } else {
                JOptionPane.showMessageDialog(vPreRegister, "삭제 실패 (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vPreRegister, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * "수강신청" 버튼 클릭 이벤트를 처리합니다.
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
            String lectureIdStr = (String) table.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) table.getModel().getValueAt(selectedRow, 1);
            int newCredits = (int) table.getModel().getValueAt(selectedRow, 3);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            // 1. "reg" (수강신청) 상태로 먼저 추가
            int resultCode = this.saveDAO.addLecture(userId, lectureId, "reg", newCredits);
            
            // 3. [수정] DAO의 결과 코드에 따라 피드백
            switch (resultCode) {
                case 0: // 수강신청 성공
                    this.saveDAO.removeLecture(userId, lectureId, "pre"); // 미리담기 목록에서 삭제
                    JOptionPane.showMessageDialog(vPreRegister, "[" + lectureName + "] 수강신청 되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(); // 미리담기 목록 새로고침
                    break;
                case 1: // 학점 초과
                    JOptionPane.showMessageDialog(vPreRegister, "최대 수강신청 학점을 초과할 수 없습니다.", "학점 초과", JOptionPane.ERROR_MESSAGE);
                    break;
                case 2: // 중복
                    JOptionPane.showMessageDialog(vPreRegister, "이미 수강신청 내역에 존재하는 과목입니다.", "알림", JOptionPane.WARNING_MESSAGE);
                    break;
                case -1: // DB 오류
                    JOptionPane.showMessageDialog(vPreRegister, "수강신청 중 DB 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    break;
                default: // 기타 오류
                    JOptionPane.showMessageDialog(vPreRegister, "수강신청 중 알 수 없는 오류가 발생했습니다. (Code: " + resultCode + ")", "오류", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vPreRegister, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * CMain(툴바 컨트롤러)이 이 패널을 보여줄 때 호출할 "공개(public)" 새로고침 메서드입니다.
     */
    public void refreshTable() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            this.vPreRegister.updateTable(null); // 로그아웃 시 테이블 비우기
            return; 
        }
        
        // "pre" (미리담기) 상태의 목록을 DB에서 가져옴
        List<MLecture> preRegisterData = this.saveDAO.getLecturesByStatus(userId, "pre"); 
        
        // VPreRegister 뷰의 테이블을 업데이트
        this.vPreRegister.updateTable(preRegisterData);
    }
}