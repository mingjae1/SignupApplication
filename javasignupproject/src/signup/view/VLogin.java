package signup.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

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
        // 1. GridBagLayout 사용 (setLayout(null) 대체)
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- (디자인) 수직 중앙 정렬을 위한 '상단' 더미 라벨 ---
        // 이 라벨이 남는 세로 공간의 절반(weighty=1.0)을 차지합니다.
        JLabel dummyTop = new JLabel("");
        GridBagConstraints gbc_dummyTop = new GridBagConstraints();
        gbc_dummyTop.gridy = 0;
        gbc_dummyTop.weighty = 1.0; // 세로 중앙 정렬 (1)
        add(dummyTop, gbc_dummyTop);

        // --- "수강신청" 타이틀 (y=1) ---
        JLabel titleLabel = new JLabel("수강신청");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        GridBagConstraints gbc_titleLabel = new GridBagConstraints();
        gbc_titleLabel.gridwidth = GridBagConstraints.REMAINDER;
        gbc_titleLabel.anchor = GridBagConstraints.CENTER;
        gbc_titleLabel.insets = new Insets(5, 5, 20, 5); 
        gbc_titleLabel.gridx = 0;
        gbc_titleLabel.gridy = 1; // y=0 -> y=1
        add(titleLabel, gbc_titleLabel);

        // --- "ID/PW/로그인"을 묶는 내부 폼 패널 (y=2) ---
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        
        // ID 레이블
        JLabel idLabel = new JLabel("ID:");
        GridBagConstraints gbc_idLabel = new GridBagConstraints();
        gbc_idLabel.insets = new Insets(5, 5, 5, 5);
        gbc_idLabel.anchor = GridBagConstraints.EAST; 
        gbc_idLabel.gridx = 0; gbc_idLabel.gridy = 0;
        loginFormPanel.add(idLabel, gbc_idLabel);

        // ID 텍스트 필드
        idField = new JTextField(20); // (20은 이제 '기본 크기'일 뿐, GBC가 크기를 조절함)
        GridBagConstraints gbc_idField = new GridBagConstraints();
        gbc_idField.insets = new Insets(5, 5, 5, 5);
        gbc_idField.gridx = 1; gbc_idField.gridy = 0;
        gbc_idField.fill = GridBagConstraints.HORIZONTAL; 
        gbc_idField.weightx = 1.0; 
        loginFormPanel.add(idField, gbc_idField);

        // PW 레이블
        JLabel pwLabel = new JLabel("PW:");
        GridBagConstraints gbc_pwLabel = new GridBagConstraints();
        gbc_pwLabel.insets = new Insets(5, 5, 5, 5);
        gbc_pwLabel.anchor = GridBagConstraints.EAST;
        gbc_pwLabel.gridx = 0; gbc_pwLabel.gridy = 1;
        loginFormPanel.add(pwLabel, gbc_pwLabel);

        // PW 텍스트 필드
        passwordField = new JPasswordField(20);
        GridBagConstraints gbc_pwField = new GridBagConstraints();
        gbc_pwField.insets = new Insets(5, 5, 5, 5);
        gbc_pwField.gridx = 1; gbc_pwField.gridy = 1;
        gbc_pwField.fill = GridBagConstraints.HORIZONTAL; // [해결 1]
        gbc_pwField.weightx = 1.0; // [해결 2]
        loginFormPanel.add(passwordField, gbc_pwField);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        GridBagConstraints gbc_loginButton = new GridBagConstraints();
        gbc_loginButton.insets = new Insets(5, 5, 5, 5);
        gbc_loginButton.gridx = 2; gbc_loginButton.gridy = 0;
        gbc_loginButton.gridheight = 2; 
        gbc_loginButton.fill = GridBagConstraints.VERTICAL; 
        loginFormPanel.add(loginButton, gbc_loginButton);
        
        // 폼 패널(loginFormPanel)을 메인 패널(VLogin)에 추가
        GridBagConstraints gbc_loginFormPanel = new GridBagConstraints();
        gbc_loginFormPanel.gridy = 2; // y=1 -> y=2
        add(loginFormPanel, gbc_loginFormPanel);

        // --- "회원가입/비번찾기" 하단 옵션 패널 (y=3) ---
        JPanel optionPanel = new JPanel(); 
        signupButton = new JButton("회원가입");
        this.resetpwbutton = new JButton("비밀번호 초기화"); 
        
        optionPanel.add(signupButton);
        optionPanel.add(this.resetpwbutton);
        
        GridBagConstraints gbc_optionPanel = new GridBagConstraints();
        gbc_optionPanel.gridy = 3; // y=2 -> y=3
        gbc_optionPanel.insets = new Insets(10, 0, 0, 0); 
        add(optionPanel, gbc_optionPanel);
        
        // --- (디자인) 수직 중앙 정렬을 위한 '하단' 더미 라벨 (y=4) ---
        JLabel dummyBottom = new JLabel("");
        GridBagConstraints gbc_dummyBottom = new GridBagConstraints();
        gbc_dummyBottom.gridy = 4; // y=3 -> y=4
        gbc_dummyBottom.weighty = 1.0; // 세로 중앙 정렬 (2)
        add(dummyBottom, gbc_dummyBottom);
    }

    // --- Getters for CLogin (컨트롤러 접근용) ---
    
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getSignupButton() { return signupButton; }
    public JButton getResetPwButton() { return resetpwbutton; }
    
    /**
     * CLogin 컨트롤러가 호출하여 폼의 모든 입력 필드를 비웁니다.
     */
    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
    }
}