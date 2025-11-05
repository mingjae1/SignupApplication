package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import signup.dao.UserDAO; 
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;
import signup.controller.CSearch; // [추가] CSearch 임포트

/**
 * VLogin(로그인 뷰)의 이벤트를 처리하는 컨트롤러입니다.
 */
public class CLogin {

    private VMain vMain;
    private VLogin vLogin;
    private MMain mMain;
    private UserDAO userDAO;
    
    private CSearch cSearch; // [추가] CSearch 컨트롤러 필드

    /**
     * CLogin 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO, CSearch 객체를 주입받습니다.
     * @param cSearch [추가] 로그인 성공 시 데이터를 로드할 CSearch 컨트롤러
     */
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain, UserDAO userDAO, CSearch cSearch) { // [수정]
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.userDAO = userDAO;
        this.cSearch = cSearch; // [추가]

        // 리스너 연결 (동일)
        this.vLogin.getLoginButton().addActionListener(this::handleLogin);
        this.vLogin.getSignupButton().addActionListener(e -> vMain.contentPanel("signupPanel"));
        	
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

        String userName = this.userDAO.validateUser(id, password);

        if (userName != null) {
            // 1. 로그인 성공 (ID 저장)
            mMain.setCurrentUserId(id); 

            JOptionPane.showMessageDialog(vLogin, userName + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            
            vLogin.clearFields();
            vMain.setSize(1600, 900);
            vMain.setLocationRelativeTo(null);
            
            // 2. [핵심 수정] VSearch 패널로 전환하기 *전에* 데이터 로드를 명령합니다.
            this.cSearch.loadInitialCollegeData(); 

            // 3. VSearch 패널로 전환
            vMain.contentPanel("searchPanel"); 

        } else {
            // ... (로그인 실패 로직 - 동일)
        }
    }
}