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
import signup.constants.ViewConstants;

/**
 * 로그인 화면(View)을 구성하는 JPanel입니다.
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
        idLabel.setBounds(27, 84, 32, 25);
        loginFormPanel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(72, 84, 180, 25);
        idField.getAccessibleContext().setAccessibleName("ID");
        idField.getAccessibleContext().setAccessibleDescription("ID 입력");
        loginFormPanel.add(idField);

        // PW 입력부
        JLabel pwLabel = new JLabel("PW:");
        pwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pwLabel.setBounds(27, 119, 32, 25);
        loginFormPanel.add(pwLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(71, 119, 180, 25);
        passwordField.getAccessibleContext().setAccessibleName("PW");
        passwordField.getAccessibleContext().setAccessibleDescription("비밀번호 입력");
        loginFormPanel.add(passwordField);

        // 로그인 버튼
        loginButton = ViewConstants.createHeaderButton("로그인");
        loginButton.setBounds(264, 84, 86, 60);
        loginButton.getAccessibleContext().setAccessibleDescription("로그인");
        loginFormPanel.add(loginButton);

        // 하단 버튼
        signupButton = ViewConstants.createHeaderButton("회원가입");
        signupButton.setBounds(72, 166, 97, 30);
        signupButton.getAccessibleContext().setAccessibleDescription("회원가입");
        loginFormPanel.add(signupButton);

        resetpwbutton = ViewConstants.createHeaderButton("비밀번호 초기화");
        resetpwbutton.setBounds(181, 166, 140, 30);
        resetpwbutton.getAccessibleContext().setAccessibleDescription("비밀번호 초기화");
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