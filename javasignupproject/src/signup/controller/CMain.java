package signup.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import signup.constants.AppConstants;
import signup.constants.ControllerConstants;
import signup.constants.PanelNames;
import signup.constants.ViewConstants;
import signup.dao.UserDAO;
import signup.model.MMain;
import signup.model.MUser;
import signup.view.VMain;

import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

public class CMain {

    private VMain vMain;
    private MMain mMain;
    private CSearch cSearch;
    private CRegister cRegister;
    private CPreRegister cPreRegister;
    private CSchedule cSchedule;
    private CAdmin cAdmin;
    private UserDAO userDAO;
    private Deque<String> previousStack;
    private Deque<String> forwardStack; 
    private String currentPanel;
    private static final Logger logger = Logger.getLogger(CMain.class.getName());
    
    public CMain(VMain vMain, MMain mMain, UserDAO userDAO, ControllerBundle controllers) {
        this.vMain = vMain;
        this.mMain = mMain;
        this.userDAO = userDAO;
        this.cSearch = controllers.search;
        this.cRegister = controllers.register;
        this.cPreRegister = controllers.preRegister;
        this.cSchedule = controllers.schedule;
        this.cAdmin = controllers.admin;

        this.previousStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        this.currentPanel = PanelNames.LOGIN_PANEL; 

        this.vMain.getMenuToggleButton().addActionListener(e -> vMain.toggleSidebar());
        this.vMain.getBeforeButton().addActionListener(this::handlePrevious);
        this.vMain.getAfterButton().addActionListener(this::handleNext);
        this.vMain.getRefreshButton().addActionListener(this::handleRefresh);
        this.vMain.getLogoutButton().addActionListener(this::handleLogout);
        
        this.vMain.getBtnSideSearch().addActionListener(e -> { 
            cSearch.setMode("REGISTER"); 
            navigateTo(PanelNames.SEARCH_PANEL); 
        });
        
        this.vMain.getBtnSideRegister().addActionListener(e -> {
        	cSearch.setMode("REGISTER");
        	navigateTo(PanelNames.REGISTER_PANEL);
        });
        
        this.vMain.getBtnSidePreRegister().addActionListener(e -> { 
        	cSearch.setMode("PREREGISTER");
        	navigateTo(PanelNames.PREREGISTER_PANEL);
        });
        
        this.vMain.getBtnSideAdmin().addActionListener(e -> this.cAdmin.showAdminDialog());
        this.vMain.getBtnSideTimeTable().addActionListener(e -> this.cSchedule.showSchedule());
        this.vMain.getBtnSideMyInfo().addActionListener(this::handleMyInfo);
        this.vMain.getBtnSideClock().addActionListener(this::handleClockToggle);
        this.vMain.getBtnSideTheme().addActionListener(this::handleThemeChange);

        updateNavigationButtons();
    }

    public void resetNavigation(String panelName) {
        previousStack.clear();
        forwardStack.clear();
        currentPanel = panelName;
        vMain.contentPanel(panelName);
        updateNavigationButtons();
    }

    public void setAdminMode(boolean isAdmin) {
        vMain.getBtnSideAdmin().setVisible(isAdmin);
    }
    
    private void navigateTo(String panelName) {
        if (panelName.equals(PanelNames.LOGIN_PANEL)) return;
        
        if (!panelName.equals(currentPanel)) {
        	if(!PanelNames.LOGIN_PANEL.equals(currentPanel)) {
        		previousStack.push(currentPanel);
        	}
            currentPanel = panelName;
            vMain.contentPanel(panelName);
            forwardStack.clear();
            updateNavigationButtons();
        }
        
        switch (panelName) {
            case PanelNames.REGISTER_PANEL:
                cRegister.refreshTable();
                break;
            case PanelNames.PREREGISTER_PANEL:
                cPreRegister.refreshTable();
                break;
            case PanelNames.ADMIN_PANEL:
				cAdmin.loadAllLectures();
				break;
            default: break;
        }
    }

    private void handlePrevious(ActionEvent e) {
        if (!previousStack.isEmpty()) {
            forwardStack.push(currentPanel);
            currentPanel = previousStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    private void handleNext(ActionEvent e) {
        if (!forwardStack.isEmpty()) {
            previousStack.push(currentPanel);
            currentPanel = forwardStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    private void handleRefresh(ActionEvent e) {
        switch (currentPanel) {
            case PanelNames.REGISTER_PANEL:
                cRegister.refreshTable();
                break;
            case PanelNames.PREREGISTER_PANEL:
                cPreRegister.refreshTable();
                break;
            case PanelNames.SEARCH_PANEL:
                this.cSearch.refreshSearch();
                break;
            default: break;
        }
    }

    public void handleLogout(ActionEvent e) {
        mMain.setCurrentUserId(null);
        vMain.setMyNameLabel("");
        setAdminMode(false);
        ViewConstants.resizeFrame(vMain, AppConstants.LOGIN_WINDOW_WIDTH, AppConstants.LOGIN_WINDOW_HEIGHT);
        vMain.contentPanel(PanelNames.LOGIN_PANEL);
        previousStack.clear();
        forwardStack.clear();
        updateNavigationButtons();
        currentPanel = PanelNames.LOGIN_PANEL;
    }
    
    private void handleThemeChange(ActionEvent e) {
        try {
            if (FlatLaf.isLafDark()) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            FlatLaf.updateUI();
            SwingUtilities.updateComponentTreeUI(vMain);
            vMain.refreshClockTheme(); // 시계 팝업도 테마 변경 적용
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(vMain, "테마 변경 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClockToggle(ActionEvent e) {
        vMain.toggleClockPopup();
    }

    private void handleMyInfo(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        
        MUser user = userDAO.getUserInfo(userId);
        
        if (user != null) {
            // VMain에서 사용자 정보 표시
            vMain.showUserInfoDialog(
                user.getName(),
                user.getCode(),
                user.getUserid(),
                user.getEmail(),
                user.getCampus(),
                user.getCollege(),
                user.getDepartment()
            );
        }
    }
    
    /**
     * 비밀번호 변경 처리 (VMain의 비밀번호 변경 버튼 클릭 시 호출)
     */
    public void handlePasswordChange() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        
        if (!vMain.showPasswordChangeDialog()) {
            return;
        }
        
        String oldPw = vMain.getCurrentPasswordInput();
        String newPw = vMain.getNewPasswordInput();
        String confirmPw = vMain.getConfirmPasswordInput();
        
        try {
            // Validate password input
            if (!validatePasswordInput(newPw, confirmPw)) {
                return;
            }
            
            boolean changed = userDAO.changePassword(userId, oldPw, newPw);
            
            if (changed) {
                vMain.showInfoMessage(ControllerConstants.SUCCESS_SECRET_CHANGED, ControllerConstants.TITLE_COMPLETE);
            } else {
                vMain.showErrorMessage(ControllerConstants.ERROR_SECRET_MISMATCH, ControllerConstants.TITLE_LOGIN_FAILED);
            }
        } catch (java.sql.SQLException ex) {
            vMain.showErrorMessage(ControllerConstants.ERROR_SECRET_CHANGE_DB, ControllerConstants.TITLE_ERROR);
            logger.log(java.util.logging.Level.SEVERE, "비밀번호 변경 오류", ex);
        }
    }
    
    /**
     * 비밀번호 입력값을 유효성 검사합니다.
     */
    private boolean validatePasswordInput(String newPw, String confirmPw) {
        // Check if password is empty
        if (ControllerConstants.isEmpty(newPw)) {
            vMain.showErrorMessage(ControllerConstants.ERROR_SECRET_EMPTY, ControllerConstants.TITLE_INPUT_ERROR);
            return false;
        }
        
        // Check if new password and confirmation password match
        if (!ControllerConstants.matches(newPw, confirmPw)) {
            vMain.showErrorMessage(ControllerConstants.ERROR_SECRET_CONFIRM_MISMATCH, ControllerConstants.TITLE_INPUT_ERROR);
            return false;
        }
        
        // Check if password meets security policy
        if (!ControllerConstants.isValidPassword(newPw)) {
            vMain.showErrorMessage(ControllerConstants.ERROR_SECRET_POLICY, ControllerConstants.TITLE_SECURITY_ERROR);
            return false;
        }
        
        return true;
    }
    
    public void refreshUserInfo() {
        String userId = mMain.getCurrentUserId();
        if (userId != null) {
            MUser user = userDAO.getUserInfo(userId);
            if (user != null) {
                vMain.setMyNameLabel(user.getName());
            }
        } else {
            vMain.setMyNameLabel(null);
        }
    }

    private void updateNavigationButtons() {
        vMain.getBeforeButton().setEnabled(!previousStack.isEmpty());
        vMain.getAfterButton().setEnabled(!forwardStack.isEmpty());
    }
}
