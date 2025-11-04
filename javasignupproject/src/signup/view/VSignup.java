package signup.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.FlowLayout;    // FlowLayout 임포트
import javax.swing.BorderFactory; // BorderFactory 임포트
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * 회원가입 화면(View)을 구성하는 JPanel 클래스입니다.
 * 2열(Left/Right) 레이아웃으로 디자인이 수정되었습니다.
 */
public class VSignup extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // --- GUI 컴포넌트 필드 ---
    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;
    private JTextField emailField;
    private JComboBox<Object> comboCampus;
    private JComboBox<Object> comboCollege;
    private JComboBox<Object> comboDepartment;
    private JButton signupButton;
    private JButton cancelButton;

    /**
     * VSignup 패널의 GUI 컴포넌트들을 생성하고 2열로 배치합니다.
     */
    public VSignup() {
    	// 메인 패널은 GridBagLayout 사용
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // --- 제목 (y=0, 2열에 걸쳐 배치) ---
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24)); 
        GridBagConstraints sTitleLabel = new GridBagConstraints();
        sTitleLabel.anchor = GridBagConstraints.CENTER;
        sTitleLabel.gridwidth = 2; // 2열(left/right)에 걸쳐 중앙 배치
        sTitleLabel.insets = new Insets(5, 5, 10, 5);
        sTitleLabel.gridx = 0;
        sTitleLabel.gridy = 0;
        add(titleLabel, sTitleLabel);

        // --- 1. 왼쪽 패널 (소속 및 개인정보) ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("소속 및 개인정보"));
        
        // 왼쪽 패널 (y=1, x=0) 메인 GBC
        GridBagConstraints sLPanel = new GridBagConstraints();
        sLPanel.fill = GridBagConstraints.BOTH; // 패널이 세로로 같이 늘어나도록
        sLPanel.insets = new Insets(0, 5, 5, 5);
        sLPanel.gridx = 0;
        sLPanel.gridy = 1;
        sLPanel.weightx = 1.0; // 왼쪽 패널 너비 비율
        sLPanel.weighty = 1.0; // 높이 비율
        add(leftPanel, sLPanel);

        // --- 왼쪽 패널 내부 컴포넌트 (GridBagLayout) ---

        // 왼쪽: 캠퍼스 (y=0)
        JLabel campusLabel = new JLabel("캠퍼스:");
        GridBagConstraints sCampusLabel = new GridBagConstraints();
        sCampusLabel.anchor = GridBagConstraints.WEST;
        sCampusLabel.insets = new Insets(5, 5, 5, 5);
        sCampusLabel.gridx = 0; sCampusLabel.gridy = 0;
        leftPanel.add(campusLabel, sCampusLabel);

        comboCampus = new JComboBox<>(new String[]{"- 캠퍼스 선택 -"});
        GridBagConstraints sCampusCombo = new GridBagConstraints();
        sCampusCombo.fill = GridBagConstraints.HORIZONTAL;
        sCampusCombo.insets = new Insets(5, 5, 5, 5);
        sCampusCombo.gridx = 1; sCampusCombo.gridy = 0;
        sCampusCombo.weightx = 1.0; // 필드가 가로 공간을 채움
        leftPanel.add(comboCampus, sCampusCombo);

        // 왼쪽: 단과대학 (y=1)
        JLabel collegeLabel = new JLabel("단과대학:");
        GridBagConstraints sCollegeLabel = new GridBagConstraints();
        sCollegeLabel.anchor = GridBagConstraints.WEST;
        sCollegeLabel.insets = new Insets(5, 5, 5, 5);
        sCollegeLabel.gridx = 0; sCollegeLabel.gridy = 1;
        leftPanel.add(collegeLabel, sCollegeLabel);

        comboCollege = new JComboBox<>(new String[]{"- 대학 선택 -"});
        comboCollege.setEnabled(false);
        GridBagConstraints sCollegeCombo = new GridBagConstraints();
        sCollegeCombo.fill = GridBagConstraints.HORIZONTAL;
        sCollegeCombo.insets = new Insets(5, 5, 5, 5);
        sCollegeCombo.gridx = 1; sCollegeCombo.gridy = 1;
        leftPanel.add(comboCollege, sCollegeCombo);

        // 왼쪽: 학과 (y=2)
        JLabel departmentLabel = new JLabel("학과:");
        GridBagConstraints sDepartmentLabel = new GridBagConstraints();
        sDepartmentLabel.anchor = GridBagConstraints.WEST;
        sDepartmentLabel.insets = new Insets(5, 5, 5, 5);
        sDepartmentLabel.gridx = 0; sDepartmentLabel.gridy = 2;
        leftPanel.add(departmentLabel, sDepartmentLabel);

        comboDepartment = new JComboBox<>(new String[]{"- 학과 선택 -"});
        comboDepartment.setEnabled(false);
        GridBagConstraints sDepartmentCombo = new GridBagConstraints();
        sDepartmentCombo.fill = GridBagConstraints.HORIZONTAL;
        sDepartmentCombo.insets = new Insets(5, 5, 5, 5);
        sDepartmentCombo.gridx = 1; sDepartmentCombo.gridy = 2;
        leftPanel.add(comboDepartment, sDepartmentCombo);

        // 왼쪽: 이름 (y=3)
        JLabel nameLabel = new JLabel("이름:");
        GridBagConstraints sNameLabel = new GridBagConstraints();
        sNameLabel.anchor = GridBagConstraints.WEST;
        sNameLabel.insets = new Insets(5, 5, 5, 5);
        sNameLabel.gridx = 0; sNameLabel.gridy = 3;
        leftPanel.add(nameLabel, sNameLabel);

        nameField = new JTextField(20);
        GridBagConstraints sNameField = new GridBagConstraints();
        sNameField.fill = GridBagConstraints.HORIZONTAL;
        sNameField.insets = new Insets(5, 5, 5, 5);
        sNameField.gridx = 1; sNameField.gridy = 3;
        leftPanel.add(nameField, sNameField);

        // 왼쪽: 학번 (y=4)
        JLabel studentIdLabel = new JLabel("학번:");
        GridBagConstraints sStudentIdLabel = new GridBagConstraints();
        sStudentIdLabel.anchor = GridBagConstraints.WEST;
        sStudentIdLabel.insets = new Insets(5, 5, 5, 5);
        sStudentIdLabel.gridx = 0; sStudentIdLabel.gridy = 4;
        leftPanel.add(studentIdLabel, sStudentIdLabel);

        studentIdField = new JTextField(20);
        GridBagConstraints sStudentIdField = new GridBagConstraints();
        sStudentIdField.fill = GridBagConstraints.HORIZONTAL;
        sStudentIdField.insets = new Insets(5, 5, 5, 5);
        sStudentIdField.gridx = 1; sStudentIdField.gridy = 4;
        leftPanel.add(studentIdField, sStudentIdField);

        // 왼쪽: 이메일 (y=5)
        JLabel emailLabel = new JLabel("이메일:");
        GridBagConstraints sEmailLabel = new GridBagConstraints();
        sEmailLabel.anchor = GridBagConstraints.WEST;
        sEmailLabel.insets = new Insets(5, 5, 5, 5);
        sEmailLabel.gridx = 0; sEmailLabel.gridy = 5;
        leftPanel.add(emailLabel, sEmailLabel);

        emailField = new JTextField(20);
        GridBagConstraints sEmailField = new GridBagConstraints();
        sEmailField.fill = GridBagConstraints.HORIZONTAL;
        sEmailField.insets = new Insets(5, 5, 5, 5);
        sEmailField.gridx = 1; sEmailField.gridy = 5;
        leftPanel.add(emailField, sEmailField);
        
        // (디자인) 왼쪽 패널의 빈 공간을 채울 더미 라벨 (y=6)
        GridBagConstraints sDummyL = new GridBagConstraints();
        sDummyL.weighty = 1.0; // 이 컴포넌트가 남은 세로 공간을 모두 차지
        sDummyL.gridy = 6;
        leftPanel.add(new JLabel(""), sDummyL);


        // --- 2. 오른쪽 패널 (계정 정보) ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("계정 정보"));
        
        // 오른쪽 패널 (y=1, x=1) 메인 GBC
        GridBagConstraints sRPanel = new GridBagConstraints();
        sRPanel.fill = GridBagConstraints.BOTH;
        sRPanel.insets = new Insets(0, 5, 5, 5);
        sRPanel.gridx = 1;
        sRPanel.gridy = 1;
        sRPanel.weightx = 1.0; // 오른쪽 패널 너비 비율
        sRPanel.weighty = 1.0;
        add(rightPanel, sRPanel);
        
        // --- 오른쪽 패널 내부 컴포넌트 ---
        
        // 오른쪽: 아이디 (y=0)
        JLabel idLabel = new JLabel("아이디:");
        GridBagConstraints sIdLabel = new GridBagConstraints();
        sIdLabel.anchor = GridBagConstraints.WEST;
        sIdLabel.insets = new Insets(5, 5, 5, 5);
        sIdLabel.gridx = 0; sIdLabel.gridy = 0;
        rightPanel.add(idLabel, sIdLabel);

        idField = new JTextField(20);
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new EnglishOnlyFilter());
        GridBagConstraints sIdField = new GridBagConstraints();
        sIdField.fill = GridBagConstraints.HORIZONTAL;
        sIdField.insets = new Insets(5, 5, 5, 5);
        sIdField.gridx = 1; sIdField.gridy = 0;
        sIdField.weightx = 1.0; // 필드가 가로 공간을 채움
        rightPanel.add(idField, sIdField);

        // 오른쪽: 비밀번호 (y=1)
        JLabel passwordLabel = new JLabel("비밀번호:");
        GridBagConstraints sPWLabel = new GridBagConstraints();
        sPWLabel.anchor = GridBagConstraints.WEST;
        sPWLabel.insets = new Insets(5, 5, 5, 5);
        sPWLabel.gridx = 0; sPWLabel.gridy = 1;
        rightPanel.add(passwordLabel, sPWLabel);

        passwordField = new JPasswordField(20);
        GridBagConstraints sPasswordField = new GridBagConstraints();
        sPasswordField.fill = GridBagConstraints.HORIZONTAL;
        sPasswordField.insets = new Insets(5, 5, 5, 5);
        sPasswordField.gridx = 1; sPasswordField.gridy = 1;
        rightPanel.add(passwordField, sPasswordField);

        // 오른쪽: 비밀번호 확인 (y=2)
        JLabel passwordConfirmLabel = new JLabel("비밀번호 확인:");
        GridBagConstraints sPWConfirmLabel = new GridBagConstraints();
        sPWConfirmLabel.anchor = GridBagConstraints.WEST;
        sPWConfirmLabel.insets = new Insets(5, 5, 5, 5);
        sPWConfirmLabel.gridx = 0; sPWConfirmLabel.gridy = 2;
        rightPanel.add(passwordConfirmLabel, sPWConfirmLabel);

        passwordConfirmField = new JPasswordField(20);
        GridBagConstraints sPWConfirmField = new GridBagConstraints();
        sPWConfirmField.fill = GridBagConstraints.HORIZONTAL;
        sPWConfirmField.insets = new Insets(5, 5, 5, 5);
        sPWConfirmField.gridx = 1; sPWConfirmField.gridy = 2;
        rightPanel.add(passwordConfirmField, sPWConfirmField);
        
        // (디자인) 오른쪽 패널의 빈 공간을 채울 더미 라벨 (y=3)
        GridBagConstraints sDummyR = new GridBagConstraints();
        sDummyR.weighty = 1.0; // 이 컴포넌트가 남은 세로 공간을 모두 차지
        sDummyR.gridy = 3;
        rightPanel.add(new JLabel(""), sDummyR);
        

        // --- 3. 버튼 패널 (y=2, 2열에 걸쳐 배치) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 중앙 정렬
        signupButton = new JButton("가입하기");
        cancelButton = new JButton("취소");
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        GridBagConstraints sBTPanel = new GridBagConstraints();
        sBTPanel.gridwidth = 2; // 2열에 걸쳐 중앙 배치
        sBTPanel.insets = new Insets(10, 5, 5, 5); // 상단 여백
        sBTPanel.gridx = 0;
        sBTPanel.gridy = 2;
        add(buttonPanel, sBTPanel);
    }
    
    /**
     * ID 필드에 영어(대소문자)와 숫자만 입력되도록 강제하는 DocumentFilter입니다.
     */
    public class EnglishOnlyFilter extends DocumentFilter {
        private final static String REGEX = "^[a-zA-Z0-9]*$";

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches(REGEX)) {
                super.insertString(fb, offset, string, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches(REGEX)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // --- Getters for CSignup (컨트롤러가 컴포넌트에 접근하기 위한 메서드) ---
    
    public JTextField getNameField() { return nameField; }
    public JTextField getStudentIdField() { return studentIdField; }
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JPasswordField getPasswordConfirmField() { return passwordConfirmField; }
    public JTextField getEmailField() { return emailField; }
    
    public JComboBox<Object> getComboCampus() { return comboCampus; }
    public JComboBox<Object> getComboCollege() { return comboCollege; }
    public JComboBox<Object> getComboDepartment() { return comboDepartment; }
    
    public JButton getSignupButton() { return signupButton; }
    public JButton getCancelButton() { return cancelButton; }
    
    /**
     * 회원가입 폼의 모든 입력 필드와 콤보박스를 초기 상태로 되돌립니다.
     */
    public void clearFields() {
        nameField.setText("");
        studentIdField.setText("");
        idField.setText("");
        passwordField.setText("");
        passwordConfirmField.setText("");
        emailField.setText("");
        
        comboCampus.setSelectedIndex(0); 
        
        comboCollege.removeAllItems();
        comboCollege.addItem("- 대학 선택 -");
        comboCollege.setEnabled(false);
        
        comboDepartment.removeAllItems();
        comboDepartment.addItem("- 학과 선택 -");
        comboDepartment.setEnabled(false);
    }
}