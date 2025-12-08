package signup.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

/**
 * 로그인 화면(View)을 구성하는 JPanel입니다.
 * setLayout(null) 대신 GridBagLayout을 사용하여 창 크기 조절에 반응하고
 * 컴포넌트를 중앙에 고정시킵니다.
 */
public class VLogin extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // GUI 컴포넌트 필드
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton resetpwbutton; // 비밀번호 초기화 버튼

    /**
     * VLogin 패널의 GUI 컴포넌트들을 생성하고 배치합니다.
     */
    public VLogin() {
        // 1. GridBagLayout 사용
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // --- "ID/PW/로그인"을 묶는 내부 폼 패널 (y=2) ---
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(null);
        loginFormPanel.setPreferredSize(new Dimension(380, 240));
        
        // --- "수강신청" 타이틀 ---
        JLabel titleLabel = new JLabel("수강신청");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(0, 28, 380, 40);
        loginFormPanel.add(titleLabel);
        
        // 2. ID 입력부
        JLabel idLabel = new JLabel("ID:");
        idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        idLabel.setBounds(27, 84, 32, 25);
        loginFormPanel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(72, 84, 180, 25);
        loginFormPanel.add(idField);

        // 3. PW 입력부
        JLabel pwLabel = new JLabel("PW:");
        pwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pwLabel.setBounds(27, 119, 32, 25);
        loginFormPanel.add(pwLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(71, 119, 180, 25);
        loginFormPanel.add(passwordField);

        // 4. 로그인 버튼 (ID/PW 우측)
        loginButton = new JButton("로그인");
        // ID(y=70) ~ PW(y=105+25=130) 높이에 맞춤
        loginButton.setBounds(264, 84, 86, 60); 
        loginFormPanel.add(loginButton);

        // 5. 하단 버튼 (회원가입, 비번찾기)
        signupButton = new JButton("회원가입");
        signupButton.setFont(new Font("굴림", Font.PLAIN, 12));
        signupButton.setBounds(72, 166, 97, 30);
        loginFormPanel.add(signupButton);

        this.resetpwbutton = new JButton("비밀번호 초기화");
        resetpwbutton.setFont(new Font("굴림", Font.PLAIN, 12));
        this.resetpwbutton.setBounds(181, 166, 140, 30);
        loginFormPanel.add(this.resetpwbutton);
        
        add(loginFormPanel, new GridBagConstraints());

    }
    
    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
    }
    
    // --- Getters for CLogin (컨트롤러 접근용) ---
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
    public JButton getResetPwButton() { return resetpwbutton; }
    
}