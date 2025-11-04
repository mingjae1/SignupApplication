package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import signup.model.MLogin;
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;

/**
 * VLogin(로그인 뷰)의 이벤트를 처리하고
 * MLogin(로그인 모델) 및 MMain(메인 모델)과 상호작용하는 컨트롤러입니다.
 */
public class CLogin {

    private VMain vMain;     // 메인 프레임 (화면 전환용)
    private VLogin vLogin;   // 로그인 패널 (입력 필드 접근용)
    private MLogin mLogin;   // 로그인 모델 (사용자 인증용)
    private MMain mMain;     // 메인 모델 (로그인 ID 저장용)

    /**
     * CLogin 컨트롤러를 생성합니다.
     * VLogin의 버튼 및 엔터 키에 대한 리스너를 설정합니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param vLogin 제어할 로그인 뷰 (VLogin)
     * @param mMain 로그인 성공 시 ID를 전달할 메인 모델 (MMain)
     */
    public CLogin(VMain vMain, VLogin vLogin, MMain mMain) {
        this.vMain = vMain;
        this.vLogin = vLogin;
        this.mMain = mMain; 
        this.mLogin = new MLogin(); // CLogin이 MLogin을 직접 생성하여 사용

        // 버튼 리스너 연결
        this.vLogin.getLoginButton().addActionListener(new LoginActionListener());
        this.vLogin.getSignupButton().addActionListener(e -> vMain.contentPanel("signupPanel"));
        	
        // ID/PW 필드에서 엔터 키를 누르면 로그인을 시도하는 리스너
        ActionListener enterKeyListener = e -> vLogin.getLoginButton().doClick();
        this.vLogin.getIdField().addActionListener(enterKeyListener);
        this.vLogin.getPasswordField().addActionListener(enterKeyListener);
    }

    /**
     * "로그인" 버튼 클릭 또는 엔터 키 입력을 처리하는 내부 클래스입니다.
     */
    class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = vLogin.getIdField().getText();
            String password = new String(vLogin.getPasswordField().getPassword());

            // MLogin에 사용자 인증 요청 (성공 시 "이름", 실패 시 null 반환)
            String userName = mLogin.validateUser(id, password);

            if (userName != null) {
                // 1. 로그인 성공
                mMain.setCurrentUserId(id); // MMain에 로그인한 ID 저장

                JOptionPane.showMessageDialog(vLogin, userName + "님, 환영합니다!", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                
                // 2. (보안) 로그인 폼 필드 초기화
                vLogin.clearFields();

                // 3. (UI 변경) 메인 창 크기 변경 및 중앙 정렬
                vMain.setSize(1600, 900);
                vMain.setLocationRelativeTo(null);

                // 4. (화면 전환) 메인 컨텐츠 패널로 전환
                vMain.contentPanel("mainContentPanel");

            } else {
                // 5. 로그인 실패
                JOptionPane.showMessageDialog(vLogin, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                vLogin.getPasswordField().setText(""); // 비밀번호 필드만 초기화
            }
        }
    }
}