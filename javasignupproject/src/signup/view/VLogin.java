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
import javax.swing.SwingConstants;
import signup.constants.ViewConstants;

/**
 * 로그인 화면(View)을 구성하는 JPanel입니다.
 * 접근성 개선 사항:
 * - 라벨과 입력 필드의 연계(setLabelFor)
 * - 키보드 빠른 접근용 Mnemonic 지정
 * - 화면 읽기 도구를 위한 AccessibleName/Description 강화
 * - 버튼에 툴팁 제공
 */
public class VLogin extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int FORM_WIDTH = 380;
    private static final int FORM_HEIGHT = 240;
    
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton resetpwbutton;

    public VLogin() {
        setLayout(new GridBagLayout());
        setBorder(ViewConstants.createEmptyBorder(10, 10, 10, 10));
        
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(null);
        loginFormPanel.setPreferredSize(new Dimension(FORM_WIDTH, FORM_HEIGHT));
        
        // 타이틀
        JLabel titleLabel = new JLabel("수강신청");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(ViewConstants.FONT_SANS_SERIF, Font.BOLD, 24));
        titleLabel.setBounds(0, 28, FORM_WIDTH, 40);
        loginFormPanel.add(titleLabel);
        
        // ID 입력부
        JLabel idLabel = new JLabel("ID:");
        idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        idLabel.setDisplayedMnemonic('I'); // Alt+I 포커스 이동
        idLabel.setBounds(27, 84, 32, 25);
        loginFormPanel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(72, 84, 180, 25);
        idField.getAccessibleContext().setAccessibleName("아이디 입력");
        idField.getAccessibleContext().setAccessibleDescription("로그인 아이디를 입력하세요");
        idField.setToolTipText("아이디를 입력하세요");
        idLabel.setLabelFor(idField);
        loginFormPanel.add(idField);

        // PW 입력부
        JLabel pwLabel = new JLabel("PW:");
        pwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pwLabel.setDisplayedMnemonic('P'); // Alt+P 포커스 이동
        pwLabel.setBounds(27, 119, 32, 25);
        loginFormPanel.add(pwLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(71, 119, 180, 25);
        passwordField.getAccessibleContext().setAccessibleName("비밀번호 입력");
        passwordField.getAccessibleContext().setAccessibleDescription("로그인 비밀번호를 입력하세요");
        passwordField.setToolTipText("비밀번호를 입력하세요");
        pwLabel.setLabelFor(passwordField);
        loginFormPanel.add(passwordField);

        // 로그인 버튼
        loginButton = ViewConstants.createHeaderButton("로그인");
        loginButton.setBounds(264, 84, 86, 60);
        loginButton.setMnemonic('L'); // Alt+L
        loginButton.setToolTipText("로그인합니다");
        loginButton.getAccessibleContext().setAccessibleName("로그인 버튼");
        loginButton.getAccessibleContext().setAccessibleDescription("입력한 아이디와 비밀번호로 로그인합니다");
        loginFormPanel.add(loginButton);

        // 하단 버튼
        signupButton = ViewConstants.createHeaderButton("회원가입");
        signupButton.setBounds(72, 166, 97, 30);
        signupButton.setMnemonic('S'); // Alt+S
        signupButton.setToolTipText("회원가입 화면으로 이동합니다");
        signupButton.getAccessibleContext().setAccessibleName("회원가입 버튼");
        signupButton.getAccessibleContext().setAccessibleDescription("회원가입 화면으로 이동합니다");
        loginFormPanel.add(signupButton);

        resetpwbutton = ViewConstants.createHeaderButton("비밀번호 초기화");
        resetpwbutton.setBounds(181, 166, 140, 30);
        resetpwbutton.setMnemonic('R'); // Alt+R
        resetpwbutton.setToolTipText("비밀번호를 초기화합니다");
        resetpwbutton.getAccessibleContext().setAccessibleName("비밀번호 초기화 버튼");
        resetpwbutton.getAccessibleContext().setAccessibleDescription("아이디, 이름, 학번 확인 후 비밀번호를 초기화합니다");
        loginFormPanel.add(resetpwbutton);
        
        add(loginFormPanel, new GridBagConstraints());
    }
    
    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
    }
    
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
    public JButton getResetPwButton() { return resetpwbutton; }
}