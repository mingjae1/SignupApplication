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

/**
 * 로그인 관련 사용자 입력을 처리하는 컨트롤러.
 * <ul>
 *   <li>로그인 버튼/엔터 키로 로그인 시도</li>
 *   <li>회원가입 화면 전환</li>
 *   <li>비밀번호 초기화</li>
 * </ul>
 * [계약]
 * - 입력: VLogin의 ID, PW 필드 값
 * - 처리: UserDAO.validateUser로 인증, 성공 시 메인 화면로 이동
 * - 실패: 에러 메시지 출력 및 PW 필드 초기화
 * - 예외: DB 오류 발생 시 사용자에게 안내 후 로그 기록
 */
public class CLogin {
    private static final String ROLE_ADMIN = "admin";
    
    private VMain vMain;
    private VLogin vLogin;
    private MMain mMain;
    private UserDAO userDAO;
    private CSearch cSearch; 
    private CMain cMain;
    private static final Logger logger = Logger.getLogger(CLogin.class.getName());
    
    /**
     * 생성자: 화면/모델/DAO/다른 컨트롤러를 주입하고 리스너를 연결합니다.
     * @param vMain 메인 프레임 뷰
     * @param vLogin 로그인 뷰 패널
     * @param mMain 메인 모델
     * @param userDAO 사용자 DAO (BCrypt 포함)
     * @param cSearch 검색 컨트롤러 (초기 데이터 로딩)
     */
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain, UserDAO userDAO, CSearch cSearch) {
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.userDAO = userDAO;
        this.cSearch = cSearch; 
        
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

    /**
     * 로그인 버튼/엔터 입력 처리.
     * - ID/PW를 읽어 DAO에 인증 요청
     * - 성공: 사용자 환영/관리자 안내, 화면 리셋 및 데이터 로딩
     * - 실패: 에러 메시지 및 PW 초기화
     * - 항상: PW char 배열을 0으로 덮어 보안 강화
     */
    private void handleLogin(ActionEvent e) {
        String id = vLogin.getIdField().getText();
        char[] passwordChars = vLogin.getPasswordField().getPassword();
        String password = new String(passwordChars);
        
        try {
            MUser loginUser = this.userDAO.validateUser(id, password);
            
            if (loginUser != null) {
                mMain.setCurrentUserId(id);
                
                boolean isAdmin = ROLE_ADMIN.equals(loginUser.getRole());
                String message = isAdmin 
                    ? loginUser.getName() + "님, 관리자 모드로 로그인합니다!" 
                    : loginUser.getName() + "님, 환영합니다!";
                String title = isAdmin 
                    ? ControllerConstants.TITLE_LOGIN_COMPLETE_ADMIN 
                    : ControllerConstants.SUCCESS_LOGIN;
                
                ViewConstants.showInfoMessage(vLogin, message, title);
                cMain.setAdminMode(isAdmin);
                
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
     * - ID/이름/학번 입력값 검증 후, 일치 시 초기 비밀번호(ControllerConstants.INITIAL_PASSWORD)로 재설정.
     * - 실패 또는 DB 오류 시 사용자에게 에러 메시지 출력.
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
    
    /**
     * 외부에서 메인 컨트롤러를 연결합니다.
     */
    public void setCMain(CMain cMain) {
        this.cMain = cMain;
    }
}