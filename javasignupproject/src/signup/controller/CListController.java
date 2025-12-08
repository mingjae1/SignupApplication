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

public abstract class CListController {

    protected MMain mMain;
    protected SaveDAO saveDAO;
    protected JPanel viewPanel;
    protected JTable listTable;
    protected String status;

    protected CListController(JPanel viewPanel, JTable table, MMain mMain, SaveDAO saveDAO, String status) {
        this.viewPanel = viewPanel;
        this.listTable = table;
        this.mMain = mMain;
        this.saveDAO = saveDAO;
        this.status = status;
    }

    public void refreshTable() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) {
            this.updateViewTable(null); 
            return; 
        }
        
        List<MLecture> data = this.saveDAO.getLecturesByStatus(userId, status); 
        this.updateViewTable(data);
    }

    protected void handleDelete(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        int selectedRow = listTable.getSelectedRow();

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
            String lectureIdStr = (String) listTable.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) listTable.getModel().getValueAt(selectedRow, 1);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            boolean success = this.saveDAO.removeLecture(userId, lectureId, this.status);
    
            if (success) {
                String action = StatusConstants.REGISTER.equals(status) ? "신청이 취소" : "목록에서 삭제";
                JOptionPane.showMessageDialog(viewPanel, "[" + lectureName + "] " + action + "되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(); 
            } else {
                String action = StatusConstants.REGISTER.equals(status) ? "신청 취소" : "삭제";
                JOptionPane.showMessageDialog(viewPanel, action + " 실패 (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(viewPanel, "강의 코드를 숫자로 변환하는 데 실패했습니다.", "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected abstract void updateViewTable(List<MLecture> lectureList);
}