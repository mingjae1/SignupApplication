package signup.controller;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JTable;

import signup.constants.AppConstants;
import signup.constants.ControllerConstants;
import signup.constants.StatusConstants;
import signup.constants.ViewConstants;
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
            ViewConstants.showErrorMessage(vPreRegister, "로그인이 필요합니다.", ControllerConstants.TITLE_ERROR);
            return;
        }
        if (selectedRow == -1) {
            ViewConstants.showInfoMessage(vPreRegister, "수강신청할 강의를 선택하세요.", ControllerConstants.TITLE_CONFIRMATION);
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
            ViewConstants.showErrorMessage(vPreRegister, "처리 중 오류 발생: " + ex.getMessage(), ControllerConstants.TITLE_ERROR);
        }
    }
    
    private void handleApplyResult(int resultCode, String userId, int lectureId, String lectureName) {
        switch (resultCode) {
            case AppConstants.DB_SUCCESS:
                this.saveDAO.removeLecture(userId, lectureId, StatusConstants.PRE_REGISTER);
                ViewConstants.showInfoMessage(vPreRegister, 
                    "[" + lectureName + "] " + ControllerConstants.SUCCESS_LECTURE_SIGNUP, 
                    ControllerConstants.TITLE_COMPLETE);
                refreshTable();
                break;
            case AppConstants.DB_ERROR_CREDIT_EXCEEDED:
                ViewConstants.showErrorMessage(vPreRegister, 
                    ControllerConstants.ERROR_CREDIT_EXCEEDED,
                    ControllerConstants.TITLE_ERROR);
                break;
            case AppConstants.DB_ERROR_DUPLICATE:
                ViewConstants.showErrorMessage(vPreRegister, 
                    ControllerConstants.ERROR_DUPLICATE_LECTURE, 
                    ControllerConstants.TITLE_CONFIRMATION);
                break;
            default:
                ViewConstants.showErrorMessage(vPreRegister, 
                    "수강신청 중 알 수 없는 오류가 발생했습니다.", 
                    ControllerConstants.TITLE_ERROR);
                break;
        }
    }
}