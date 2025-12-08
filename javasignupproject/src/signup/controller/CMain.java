package signup.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import signup.constants.AppConstants;
import signup.constants.PanelNames;
import signup.dao.UserDAO;
import signup.model.MMain;
import signup.model.MUser;
import signup.view.VMain;

import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;

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
    
    public CMain(VMain vMain, MMain mMain, UserDAO userDAO, 
                 CSearch cSearch, CRegister cRegister, CPreRegister cPreRegister, CSchedule cSchedule, CAdmin cAdmin) {
        this.vMain = vMain;
        this.mMain = mMain;
        this.userDAO = userDAO;
        this.cSearch = cSearch;
        this.cRegister = cRegister;
        this.cPreRegister = cPreRegister;
        this.cSchedule = cSchedule;
        this.cAdmin = cAdmin;

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
        vMain.setSize(AppConstants.LOGIN_WINDOW_WIDTH, AppConstants.LOGIN_WINDOW_HEIGHT);
        vMain.setLocationRelativeTo(null);
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
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(vMain, "테마 변경 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMyInfo(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        
        MUser user = userDAO.getUserInfo(userId);
        
        if (user != null) {
            String message = "<html><body style='width: 200px'>" +
                             "<h2>내 정보</h2><hr>" +
                             "<b>이름:</b> " + user.getName() + "<br>" +
                             "<b>학번:</b> " + user.getCode() + "<br>" +
                             "<b>ID:</b> " + user.getUserid() + "<br>" +
                             "<b>이메일:</b> " + user.getEmail() + "<br><br>" +
                             "<b>소속:</b><br>" +
                             user.getCampus() + " / " + user.getCollege() + "<br>" +
                             user.getDepartment() +
                             "</body></html>";
            JOptionPane.showMessageDialog(vMain, message, "학적 사항", JOptionPane.INFORMATION_MESSAGE);
        }
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