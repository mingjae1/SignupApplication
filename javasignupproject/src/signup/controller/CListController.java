package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JPanel;

import signup.constants.ControllerConstants;
import signup.constants.StatusConstants;
import signup.constants.ViewConstants;
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

    protected void handleDelete(ActionEvent event) {
        if (event != null && event.getSource() instanceof java.awt.Component source) {
            source.requestFocusInWindow();
        }
        String userId = mMain.getCurrentUserId();
        int selectedRow = listTable.getSelectedRow();

        if (userId == null) {
            ViewConstants.showErrorMessage(viewPanel, "로그인이 필요합니다.", ControllerConstants.TITLE_ERROR);
            return;
        }
        if (selectedRow == -1) {
            String action = StatusConstants.REGISTER.equals(status) ? "취소할" : "삭제할";
            ViewConstants.showInfoMessage(viewPanel, action + " 강의를 선택하세요.", ControllerConstants.TITLE_CONFIRMATION);
            return;
        }

        try {
            String lectureIdStr = (String) listTable.getModel().getValueAt(selectedRow, 0);
            String lectureName = (String) listTable.getModel().getValueAt(selectedRow, 1);
            int lectureId = Integer.parseInt(lectureIdStr);
    
            boolean success = this.saveDAO.removeLecture(userId, lectureId, this.status);
    
            if (success) {
                String action = StatusConstants.REGISTER.equals(status) ? ControllerConstants.SUCCESS_LECTURE_CANCEL : ControllerConstants.SUCCESS_LECTURE_SAVE;
                ViewConstants.showInfoMessage(viewPanel, "[" + lectureName + "] " + action, ControllerConstants.TITLE_COMPLETE);
                refreshTable();
            } else {
                String action = StatusConstants.REGISTER.equals(status) ? "신청 취소" : "삭제";
                ViewConstants.showErrorMessage(viewPanel, action + " 실패 (DB 오류)", ControllerConstants.TITLE_ERROR);
            }
        } catch (NumberFormatException ex) {
            ViewConstants.showErrorMessage(viewPanel, "강의 코드를 숫자로 변환하는 데 실패했습니다.", ControllerConstants.TITLE_ERROR);
        }
    }
    
    protected abstract void updateViewTable(List<MLecture> lectureList);
}