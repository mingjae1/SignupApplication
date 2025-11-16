package signup.view;


import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;



/**
 * 로그인 화면을 구성하는 JPanel입니다.
 * ID, PW 입력 필드와 버튼들을 포함합니다.
 */
public class VLogin extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton resetpwbutton;

    /**
     * VLogin 패널의 GUI 컴포넌트들을 생성하고 배치합니다.
     */
    public VLogin() {
        setLayout(null);
        setSize(380,280);

        // 타이틀 라벨
        JLabel titleLabel = new JLabel("수강신청");
        titleLabel.setBounds(136, 27, 96, 34);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel);
              
        // 아이디 라벨
        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(57, 93, 15, 15);
        add(idLabel);
                                        
        // 아이디 입력 필드
        idField = new JTextField(20);
        idField.setBounds(84, 90, 181, 21);
        add(idField);
        
        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setBounds(277, 90, 69, 53);
        add(loginButton);
        
        // 비밀번호 라벨
        JLabel passwordLabel = new JLabel("PW:");
        passwordLabel.setBounds(50, 125, 22, 15);
        add(passwordLabel);
        
        // 비밀번호 입력 필드
        passwordField = new JPasswordField(20);
        passwordField.setBounds(84, 122, 181, 21);
        add(passwordField);
        
       // 회원가입 버튼
        signupButton = new JButton("회원가입");
        signupButton.setBounds(84, 153, 81, 23);
        add(signupButton);
        
        JButton resetpwButton = new JButton("비밀번호 초기화");
        resetpwButton.setBounds(177, 153, 133, 23);
        add(resetpwButton);
    }	
        
    // --- Getters for CLogin (컨트롤러 접근용) ---
    
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
    public JButton getResetPwButton() { return resetpwbutton; }
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    
    
    /**
     * CLogin 컨트롤러가 로그인 성공 시 호출하여 입력 필드를 비웁니다.
     */
    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
    }
}