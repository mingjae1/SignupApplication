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
            // 사용자 정보 표시와 함께 비밀번호 변경 다이얼로그 제공
            javax.swing.JPanel panel = new javax.swing.JPanel();
            panel.setLayout(new java.awt.BorderLayout());
            
            String infoHtml = "<html><body style='width: 280px'>" +
                              "<h2>내 정보</h2><hr>" +
                              "<b>이름:</b> " + user.getName() + "<br>" +
                              "<b>학번:</b> " + user.getCode() + "<br>" +
                              "<b>ID:</b> " + user.getUserid() + "<br>" +
                              "<b>이메일:</b> " + user.getEmail() + "<br><br>" +
                              "<b>소속:</b><br>" + user.getCampus() + " / " + user.getCollege() + "<br>" +
                              user.getDepartment() + "</body></html>";
            
            javax.swing.JLabel infoLabel = new javax.swing.JLabel(infoHtml);
            panel.add(infoLabel, java.awt.BorderLayout.NORTH);
            
            // 비밀번호 변경 버튼
            javax.swing.JButton changePwBtn = new javax.swing.JButton("비밀번호 변경");
            changePwBtn.addActionListener(ae -> {
                // 작은 폼으로 현재비밀번호, 새비밀번호, 확인 입력
                javax.swing.JPasswordField currentPf = new javax.swing.JPasswordField();
                javax.swing.JPasswordField newPf = new javax.swing.JPasswordField();
                javax.swing.JPasswordField confirmPf = new javax.swing.JPasswordField();
                
                javax.swing.JPanel pwPanel = new javax.swing.JPanel(new java.awt.GridLayout(0,1,5,5));
                pwPanel.add(new javax.swing.JLabel("현재 비밀번호:")); pwPanel.add(currentPf);
                pwPanel.add(new javax.swing.JLabel("새 비밀번호:")); pwPanel.add(newPf);
                pwPanel.add(new javax.swing.JLabel("새 비밀번호 확인:")); pwPanel.add(confirmPf);
                
                int option = JOptionPane.showConfirmDialog(vMain, pwPanel, "비밀번호 변경", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option != JOptionPane.OK_OPTION) return;
                
                String oldPw = new String(currentPf.getPassword());
                String newPw = new String(newPf.getPassword());
                String confirmPw = new String(confirmPf.getPassword());
                
                // 간단한 검증
                if (newPw == null || newPw.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(vMain, "새 비밀번호를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(vMain, "새 비밀번호와 확인이 일치하지 않습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 비밀번호 정책: 영문자 1개 이상, 숫자 1개 이상, 특수문자 1개 이상, 길이 8자 이상
                if (!isValidPassword(newPw)) {
                    String policyMsg = "비밀번호는 영어(영문자), 숫자, 특수문자 각각 1개 이상 포함하고 최소 8자여야 합니다.";
                    JOptionPane.showMessageDialog(vMain, policyMsg, "보안 정책 위반", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    boolean changed = userDAO.changePassword(user.getUserid(), oldPw, newPw);
                    if (changed) {
                        JOptionPane.showMessageDialog(vMain, "비밀번호가 변경되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vMain, "현재 비밀번호가 올바르지 않습니다.", "변경 실패", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(vMain, "비밀번호 변경 중 DB 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    java.util.logging.Logger.getLogger(CMain.class.getName()).log(java.util.logging.Level.SEVERE, "비밀번호 변경 오류", ex);
                } finally {
                    // 민감정보 메모리 정리
                    java.util.Arrays.fill(currentPf.getPassword(), '0');
                    java.util.Arrays.fill(newPf.getPassword(), '0');
                    java.util.Arrays.fill(confirmPf.getPassword(), '0');
                }
            });
            
            // 비밀번호 유효성 검사 헬퍼
            // 영문자 1개 이상, 숫자 1개 이상, 특수문자 1개 이상, 최소 8자
            
            javax.swing.JPanel btnPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            btnPanel.add(changePwBtn);
            panel.add(btnPanel, java.awt.BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(vMain, panel, "학적 사항 / 비밀번호", JOptionPane.INFORMATION_MESSAGE);
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
    
    private boolean isValidPassword(String pw) {
        if (pw == null) return false;
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
        return pw.matches(pattern);
    }
}
