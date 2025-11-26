package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import signup.dao.UserDAO; 
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;
import signup.model.MUser;

/**
 * VLogin(로그인 뷰)의 이벤트를 처리하는 컨트롤러입니다.
 */
public class CLogin {
	
    private VMain vMain;
    private VLogin vLogin;
    private MMain mMain;
    private UserDAO userDAO;
    private CSearch cSearch; 
    private CMain cMain;
    private CAdmin cAdmin;
    
    private static final Logger logger = Logger.getLogger(CLogin.class.getName());
    
    /**
     * CLogin 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO, CSearch 객체를 주입받습니다.
     * @param cSearch [추가] 로그인 성공 시 데이터를 로드할 CSearch 컨트롤러
     * @param cMain 
     */
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain, UserDAO userDAO, CSearch cSearch, CAdmin cAdmin) {
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.userDAO = userDAO;
        this.cSearch = cSearch; 
        this.cAdmin = cAdmin;
        
        // 리스너 연결 (동일)
        this.vLogin.getLoginButton().addActionListener(this::handleLogin);
        this.vLogin.getSignupButton().addActionListener(e -> {
            vMain.setSize(800, 600);
            vMain.setLocationRelativeTo(null); // (선택사항) 크기 변경 후 중앙 정렬
            vMain.contentPanel("signupPanel");
        });
        	
        ActionListener enterKeyListener = e -> vLogin.getLoginButton().doClick();
        this.vLogin.getIdField().addActionListener(enterKeyListener);
        this.vLogin.getPasswordField().addActionListener(enterKeyListener);
    }

    /**
     * "로그인" 버튼 클릭 이벤트를 처리합니다.
     */
    private void handleLogin(ActionEvent e) {
        String id = vLogin.getIdField().getText();
        String password = new String(vLogin.getPasswordField().getPassword());
        
        try {
            // DAO 호출을 try 블록으로 감쌉니다.
        	MUser loginUser = this.userDAO.validateUser(id, password);
        	
            if (loginUser != null) {
                // 1. 로그인 성공 (로직 동일)
                mMain.setCurrentUserId(id);
                
                if ("admin".equals(loginUser.getRole())) {
                    JOptionPane.showMessageDialog(vLogin, "관리자 모드로 로그인합니다.", "관리자 로그인", JOptionPane.INFORMATION_MESSAGE);
                    cMain.setAdminMode(true);

                } else {
                    // 일반 학생 로그인
                    JOptionPane.showMessageDialog(vLogin, loginUser.getName() + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    cMain.setAdminMode(false);
                    
                }
                vLogin.clearFields();
                this.cMain.resetNavigation("searchPanel");
                this.cSearch.loadInitialCollegeData();
                this.cMain.refreshUserInfo();
                vMain.setSize(1280, 800);
                vMain.setLocationRelativeTo(null);

            } else {
                // 2. [요청사항 1] 아이디/비밀번호가 틀린 경우 (DAO가 null을 반환)
                JOptionPane.showMessageDialog(vLogin, 
                    "아이디 혹은 비밀번호가 틀렸습니다.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);
                vLogin.getPasswordField().setText(""); // 비밀번호 필드만 초기화
            }
            
        } catch (SQLException ex) {
            // 3. [요청사항 2] DB 자체에 오류가 발생한 경우 (DAO가 예외를 던짐)
            JOptionPane.showMessageDialog(vLogin, 
                "알 수 없는 오류입니다. 나중에 다시 시도해주세요. (DB 오류)", 
                "로그인 오류", 
                JOptionPane.ERROR_MESSAGE);
            vLogin.getPasswordField().setText(""); // 비밀번호 필드 초기화
            logger.log(Level.SEVERE, "로그인 DB 오류", ex);
        }
    }
    
    public void setCMain(CMain cMain) {
		this.cMain = cMain;
	}
}