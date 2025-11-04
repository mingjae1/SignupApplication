package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import signup.dao.UserDAO; // UserDAO 임포트
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;

/**
 * VLogin(로그인 뷰)의 이벤트를 처리하고
 * UserDAO(DB) 및 MMain(메인 모델)과 상호작용하는 컨트롤러입니다.
 */
public class CLogin {

    private VMain vMain;
    private VLogin vLogin;
    private MMain mMain;
    
    private UserDAO userDAO; // [수정됨] DAO를 필드로 선언

    /**
     * CLogin 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO 객체를 주입받습니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param vLogin 제어할 로그인 뷰 (VLogin)
     * @param mMain 로그인 성공 시 ID를 전달할 메인 모델 (MMain)
     * @param userDAO 사용자 인증을 처리할 UserDAO
     */
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain, UserDAO userDAO) {
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.userDAO = userDAO; // [수정됨] RMain으로부터 DAO를 주입받음

        // 버튼 리스너 연결
        this.vLogin.getLoginButton().addActionListener(this::handleLogin);
        this.vLogin.getSignupButton().addActionListener(e -> vMain.contentPanel("signupPanel"));
        	
        // ID/PW 필드에서 엔터 키를 누르면 로그인을 시도하는 리스너
        ActionListener enterKeyListener = e -> vLogin.getLoginButton().doClick();
        this.vLogin.getIdField().addActionListener(enterKeyListener);
        this.vLogin.getPasswordField().addActionListener(enterKeyListener);
    }

    /**
     * "로그인" 버튼 클릭 또는 엔터 키 입력을 처리하는 private 메서드입니다.
     */
    private void handleLogin(ActionEvent e) {
        String id = vLogin.getIdField().getText();
        String password = new String(vLogin.getPasswordField().getPassword());

        // [수정됨] new UserDAO() 대신, 필드에 주입된 userDAO를 사용합니다.
        String userName = this.userDAO.validateUser(id, password);

        if (userName != null) {
            // 1. 로그인 성공
            mMain.setCurrentUserId(id); 

            JOptionPane.showMessageDialog(vLogin, userName + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            
            vLogin.clearFields();

            vMain.setSize(1600, 900);
            vMain.setLocationRelativeTo(null);

            // 4. (수정) "searchPanel"로 바로 이동하도록 수정 (이전: "mainContentPanel")
            // CMain의 navigateTo를 호출하는 대신, VMain의 contentPanel을 직접 호출
            // 또는 CMain의 초기 패널을 searchPanel로 설정
            vMain.contentPanel("searchPanel"); // VSearch 패널의 이름

        } else {
            // 5. 로그인 실패
            JOptionPane.showMessageDialog(vLogin, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            vLogin.getPasswordField().setText("");
        }
    }
}