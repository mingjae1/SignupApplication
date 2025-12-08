package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import signup.constants.AppConstants;
import signup.constants.StatusConstants;
import signup.model.MLecture;
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VPreRegister;

public class CPreRegister extends CListController {

    private VPreRegister vPreRegister;

    public CPreRegister(VPreRegister vPreRegister, MMain mMain, SaveDAO saveDAO) {
        super(vPreRegister, vPreRegister.getTable(), mMain, saveDAO, StatusConstants.PRE_REGISTER);
        this.vPreRegister = vPreRegister;
        this.vPreRegister.getApplyButton().addActionListener(this::handleApply);
        this.vPreRegister.getDeleteButton().addActionListener(this::handleDelete);
    }

    @Override
    protected void updateViewTable(List<MLecture> data) {
        this.vPreRegister.updateTable(data);
    }

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
    
            int resultCode = this.saveDAO.addLecture(userId, lectureId, StatusConstants.REGISTER, newCredits);
            handleApplyResult(resultCode, userId, lectureId, lectureName);
        } catch (NumberFormatException | ClassCastException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(vPreRegister, "처리 중 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleApplyResult(int resultCode, String userId, int lectureId, String lectureName) {
        switch (resultCode) {
            case AppConstants.DB_SUCCESS:
                this.saveDAO.removeLecture(userId, lectureId, StatusConstants.PRE_REGISTER); 
                JOptionPane.showMessageDialog(vPreRegister, 
                    "[" + lectureName + "] 수강신청 되었습니다.", 
                    "성공", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                break;
            case AppConstants.DB_ERROR_CREDIT_EXCEEDED:
                JOptionPane.showMessageDialog(vPreRegister, 
                    "최대 수강신청 학점을 초과하여 신청할 수 없습니다.",
                    "학점 초과", JOptionPane.ERROR_MESSAGE);
                break;
            case AppConstants.DB_ERROR_DUPLICATE:
                JOptionPane.showMessageDialog(vPreRegister, 
                    "이미 수강신청 내역에 존재하는 과목입니다.", 
                    "알림", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(vPreRegister, 
                    "수강신청 중 알 수 없는 오류가 발생했습니다.", 
                    "오류", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
}