package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import signup.constants.AppConstants;
import signup.constants.PanelNames;
import signup.dao.UserDAO; 
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;
import signup.model.MUser;

public class CLogin {
	
    private VMain vMain;
    private VLogin vLogin;
    private MMain mMain;
    private UserDAO userDAO;
    private CSearch cSearch; 
    private CMain cMain;
    private CAdmin cAdmin;
    private static final Logger logger = Logger.getLogger(CLogin.class.getName());
    
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain, UserDAO userDAO, CSearch cSearch, CAdmin cAdmin) {
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.userDAO = userDAO;
        this.cSearch = cSearch; 
        this.cAdmin = cAdmin;
        
        this.vLogin.getLoginButton().addActionListener(this::handleLogin);
        this.vLogin.getSignupButton().addActionListener(e -> {
            vMain.setSize(AppConstants.SIGNUP_WINDOW_WIDTH, AppConstants.SIGNUP_WINDOW_HEIGHT);
            vMain.setLocationRelativeTo(null);
            vMain.contentPanel(PanelNames.SIGNUP_PANEL);
        });
        
        ActionListener enterKeyListener = e -> vLogin.getLoginButton().doClick();
        this.vLogin.getIdField().addActionListener(enterKeyListener);
        this.vLogin.getPasswordField().addActionListener(enterKeyListener);
        // 비밀번호 초기화 버튼 리스너 추가
        this.vLogin.getResetPwButton().addActionListener(this::handleResetPassword);
    }

    private void handleLogin(ActionEvent e) {
        String id = vLogin.getIdField().getText();
        char[] passwordChars = vLogin.getPasswordField().getPassword();
        String password = new String(passwordChars);
        
        try {
        	MUser loginUser = this.userDAO.validateUser(id, password);
        	
            if (loginUser != null) {
                mMain.setCurrentUserId(id);
                
                if ("admin".equals(loginUser.getRole())) {
                    JOptionPane.showMessageDialog(vLogin, "관리자 모드로 로그인합니다.", "관리자 로그인", JOptionPane.INFORMATION_MESSAGE);
                    cMain.setAdminMode(true);
                } else {
                    JOptionPane.showMessageDialog(vLogin, loginUser.getName() + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    cMain.setAdminMode(false);
                }
                vLogin.clearFields();
                this.cMain.resetNavigation("searchPanel");
                this.cSearch.loadInitialCollegeData();
                this.cMain.refreshUserInfo();
                vMain.setSize(AppConstants.MAIN_WINDOW_WIDTH, AppConstants.MAIN_WINDOW_HEIGHT);
                vMain.setLocationRelativeTo(null);
            } else {
                JOptionPane.showMessageDialog(vLogin, 
                    "아이디 혹은 비밀번호가 틀렸습니다.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);
                vLogin.getPasswordField().setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vLogin, 
                "알 수 없는 오류입니다. 나중에 다시 시도해주세요. (DB 오류)", 
                "로그인 오류", 
                JOptionPane.ERROR_MESSAGE);
            vLogin.getPasswordField().setText("");
            logger.log(Level.SEVERE, "로그인 DB 오류", ex);
        } finally {
            // Clear password from memory for security
            java.util.Arrays.fill(passwordChars, '0');
        }
    }
    
    /**
     * 비밀번호 초기화 처리: 사용자로부터 이름, 아이디, 학번을 입력받아 일치하면 초기 비밀번호(1234)로 변경
     */
    private void handleResetPassword(ActionEvent e) {
        String id = javax.swing.JOptionPane.showInputDialog(vLogin, "아이디를 입력하세요:", "비밀번호 초기화", javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty()) return; // 취소 또는 빈값

        String name = javax.swing.JOptionPane.showInputDialog(vLogin, "이름을 입력하세요:", "비밀번호 초기화", javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String codeStr = javax.swing.JOptionPane.showInputDialog(vLogin, "학번(숫자)을 입력하세요:", "비밀번호 초기화", javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (codeStr == null || codeStr.trim().isEmpty()) return;

        int code;
        try {
            code = Integer.parseInt(codeStr.trim());
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(vLogin, "학번은 숫자여야 합니다.", "입력 오류", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean ok = userDAO.resetPasswordIfMatch(id.trim(), name.trim(), code, "1234");
            if (ok) {
                javax.swing.JOptionPane.showMessageDialog(vLogin, "비밀번호가 초기화되었습니다. 초기 비밀번호: 1234", "초기화 완료", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(vLogin, "입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.", "초기화 실패", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            javax.swing.JOptionPane.showMessageDialog(vLogin, "비밀번호 초기화 중 DB 오류가 발생했습니다.", "오류", javax.swing.JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "비밀번호 초기화 DB 오류", ex);
        }
    }
    
    public void setCMain(CMain cMain) {
        this.cMain = cMain;
    }
}