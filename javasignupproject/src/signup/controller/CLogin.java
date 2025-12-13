package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import signup.constants.AppConstants;
import signup.constants.ControllerConstants;
import signup.constants.PanelNames;
import signup.constants.ViewConstants;
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
                
                String message = "admin".equals(loginUser.getRole()) 
                    ? loginUser.getName() + "님, 관리자 모드로 로그인합니다!" 
                    : loginUser.getName() + "님, 환영합니다!";
                String title = "admin".equals(loginUser.getRole()) 
                    ? ControllerConstants.TITLE_LOGIN_COMPLETE_ADMIN 
                    : ControllerConstants.SUCCESS_LOGIN;
                
                ViewConstants.showInfoMessage(vLogin, message, title);
                cMain.setAdminMode("admin".equals(loginUser.getRole()));
                
                vLogin.clearFields();
                this.cMain.resetNavigation("searchPanel");
                this.cSearch.loadInitialCollegeData();
                this.cMain.refreshUserInfo();
                ViewConstants.resizeFrame(vMain, AppConstants.MAIN_WINDOW_WIDTH, AppConstants.MAIN_WINDOW_HEIGHT);
            } else {
                ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_LOGIN_FAILED, 
                    ControllerConstants.TITLE_LOGIN_FAILED);
                vLogin.getPasswordField().setText("");
            }
        } catch (SQLException ex) {
            ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_LOGIN_UNKNOWN, 
                ControllerConstants.TITLE_LOGIN_ERROR);
            vLogin.getPasswordField().setText("");
            logger.log(Level.SEVERE, "로그인 DB 오류", ex);
        } finally {
            java.util.Arrays.fill(passwordChars, '0');
        }
    }
    
    /**
     * 비밀번호 초기화 처리
     */
    private void handleResetPassword(ActionEvent e) {
        String id = ViewConstants.showInputDialog(vLogin, "아이디를 입력하세요:", ControllerConstants.TITLE_PASSWORD_RESET);
        if (ControllerConstants.isEmpty(id)) return;

        String name = ViewConstants.showInputDialog(vLogin, "이름을 입력하세요:", ControllerConstants.TITLE_PASSWORD_RESET);
        if (ControllerConstants.isEmpty(name)) return;

        String codeStr = ViewConstants.showInputDialog(vLogin, "학번(숫자)을 입력하세요:", ControllerConstants.TITLE_PASSWORD_RESET);
        if (ControllerConstants.isEmpty(codeStr)) return;

        if (!ControllerConstants.isNumeric(codeStr)) {
            ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_INPUT_NUMBER, ControllerConstants.TITLE_INPUT_ERROR);
            return;
        }

        Integer code = ControllerConstants.tryParseInt(codeStr);
        if (code == null) {
            ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_INPUT_NUMBER, ControllerConstants.TITLE_INPUT_ERROR);
            return;
        }

        try {
            boolean ok = userDAO.resetPasswordIfMatch(id.trim(), name.trim(), code, ControllerConstants.INITIAL_PASSWORD);
            if (ok) {
                ViewConstants.showInfoMessage(vLogin, ControllerConstants.SUCCESS_PASSWORD_RESET, ControllerConstants.TITLE_RESET_COMPLETE);
            } else {
                ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_RESET_PASSWORD_NOT_FOUND, ControllerConstants.TITLE_RESET_FAILED);
            }
        } catch (SQLException ex) {
            ViewConstants.showErrorMessage(vLogin, ControllerConstants.ERROR_RESET_PASSWORD_DB, ControllerConstants.TITLE_ERROR);
            logger.log(Level.SEVERE, "비밀번호 초기화 DB 오류", ex);
        }
    }
    
    public void setCMain(CMain cMain) {
        this.cMain = cMain;
    }
}