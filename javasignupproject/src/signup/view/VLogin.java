package signup.view;


import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

    /**
     * VLogin 패널의 GUI 컴포넌트들을 생성하고 배치합니다.
     */
    public VLogin() {
        
        setLayout(new GridBagLayout());

        // 타이틀 라벨
        JLabel titleLabel = new JLabel("수강신청");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        GridBagConstraints gbctitleLabel = new GridBagConstraints();
        gbctitleLabel.insets = new Insets(5, 5, 5, 5);
        gbctitleLabel.gridx = 2;
        gbctitleLabel.gridy = 0;
        add(titleLabel, gbctitleLabel);
              
        // 아이디 라벨
        JLabel idLabel = new JLabel("ID:");
        GridBagConstraints gbcidLabel = new GridBagConstraints();
        gbcidLabel.anchor = GridBagConstraints.EAST;
        gbcidLabel.insets = new Insets(5, 5, 5, 5);
        gbcidLabel.gridx = 1;
        gbcidLabel.gridy = 1;
        add(idLabel, gbcidLabel);
                                        
        // 아이디 입력 필드
        idField = new JTextField(20);
        GridBagConstraints gbcidField = new GridBagConstraints();
        gbcidField.anchor = GridBagConstraints.WEST;
        gbcidField.insets = new Insets(5, 5, 5, 5);
        gbcidField.gridx = 2;
        gbcidField.gridy = 1;
        add(idField, gbcidField);
                                                 
        // 로그인 버튼
        loginButton = new JButton("로그인");
        // (삭제) 뷰(VLogin)는 리스너를 직접 처리하지 않으므로 addActionListener 제거
        GridBagConstraints gbcloginbt = new GridBagConstraints();
        gbcloginbt.insets = new Insets(0, 0, 5, 0);
        gbcloginbt.gridx = 3;
        gbcloginbt.gridy = 1;
        add(loginButton, gbcloginbt);
                                                       
        // 비밀번호 라벨
        JLabel passwordLabel = new JLabel("PW:");
        GridBagConstraints gbcpwLabel = new GridBagConstraints();
        gbcpwLabel.anchor = GridBagConstraints.EAST;
        gbcpwLabel.insets = new Insets(5, 5, 5, 5);
        gbcpwLabel.gridx = 1;
        gbcpwLabel.gridy = 2;
        add(passwordLabel, gbcpwLabel);
        
        // 비밀번호 입력 필드
        passwordField = new JPasswordField(20);
        GridBagConstraints gbcpwField = new GridBagConstraints();
        gbcpwField.anchor = GridBagConstraints.WEST;
        gbcpwField.insets = new Insets(5, 5, 5, 5);
        gbcpwField.gridx = 2;
        gbcpwField.gridy = 2;
        add(passwordField, gbcpwField);
        
       // 회원가입 버튼
        signupButton = new JButton("회원가입");
        // (삭제) 뷰(VLogin)는 리스너를 직접 처리하지 않으므로 addActionListener 제거
        GridBagConstraints gbcsignupbt = new GridBagConstraints();
        gbcsignupbt.insets = new Insets(10, 5, 5, 0);
        gbcsignupbt.anchor = GridBagConstraints.WEST;
        gbcsignupbt.gridx = 3;
        gbcsignupbt.gridy = 2;
        add(signupButton, gbcsignupbt);
    }	
        
    // --- Getters for CLogin (컨트롤러 접근용) ---
    
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
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