package signup.view;

// (수정) java.awt.*, javax.swing.* 대신 사용하는 클래스들을 명시
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
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
 * 회원가입 화면을 구성하는 JPanel입니다.
 * 이름, 학번, ID, PW 입력 필드와 버튼들을 포함합니다.
 */
public class VSignup extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;
    private JTextField nameField;
    private JTextField studentIdField;
    private JButton signupButton;
    private JButton cancelButton;

    /**
     * VSignup 패널의 GUI 컴포넌트들을 생성하고 배치합니다.
     * idField에 영어/숫자만 입력되도록 DocumentFilter를 적용합니다.
     */
    public VSignup() {
    	setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        // 제목
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        GridBagConstraints gbctitleLabel = new GridBagConstraints();
        gbctitleLabel.anchor = GridBagConstraints.CENTER;
        gbctitleLabel.gridwidth = 2;
        gbctitleLabel.insets = new Insets(5, 5, 5, 5);
        gbctitleLabel.gridx = 0;
        gbctitleLabel.gridy = 0;
        add(titleLabel, gbctitleLabel);

        // 이름
        JLabel nameLabel = new JLabel("이름:");
        GridBagConstraints gbcnameLabel = new GridBagConstraints();
        gbcnameLabel.anchor = GridBagConstraints.WEST;
        gbcnameLabel.insets = new Insets(5, 5, 5, 5);
        gbcnameLabel.gridx = 0;
        gbcnameLabel.gridy = 1;
        add(nameLabel, gbcnameLabel);
        
        nameField = new JTextField(20);
        GridBagConstraints gbcnameField = new GridBagConstraints();
        gbcnameField.fill = GridBagConstraints.HORIZONTAL;
        gbcnameField.insets = new Insets(5, 5, 5, 5);
        gbcnameField.gridx = 1;
        gbcnameField.gridy = 1;
        add(nameField, gbcnameField);

        // 학번
        JLabel studentIdLabel = new JLabel("학번:");
        GridBagConstraints gbcstudentIdLabel = new GridBagConstraints();
        gbcstudentIdLabel.anchor = GridBagConstraints.WEST;
        gbcstudentIdLabel.insets = new Insets(5, 5, 5, 5);
        gbcstudentIdLabel.gridx = 0;
        gbcstudentIdLabel.gridy = 2;
        add(studentIdLabel, gbcstudentIdLabel);
        
        studentIdField = new JTextField(20);
        GridBagConstraints gbcstudentIdField = new GridBagConstraints();
        gbcstudentIdField.fill = GridBagConstraints.HORIZONTAL;
        gbcstudentIdField.insets = new Insets(5, 5, 5, 5);
        gbcstudentIdField.gridx = 1;
        gbcstudentIdField.gridy = 2;
        add(studentIdField, gbcstudentIdField);
 
        // 아이디
        JLabel idLabel = new JLabel("아이디:");
        GridBagConstraints gbcidLabel = new GridBagConstraints();
        gbcidLabel.anchor = GridBagConstraints.WEST;
        gbcidLabel.insets = new Insets(5, 5, 5, 5);
        gbcidLabel.gridx = 0;
        gbcidLabel.gridy = 3;
        add(idLabel, gbcidLabel);
        
        idField = new JTextField(20);
        
        // idField의 Document에 EnglishOnlyFilter 적용
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new EnglishOnlyFilter());

        GridBagConstraints gbcidField = new GridBagConstraints();
        gbcidField.fill = GridBagConstraints.HORIZONTAL;
        gbcidField.insets = new Insets(5, 5, 5, 5);
        gbcidField.gridx = 1;
        gbcidField.gridy = 3;
        add(idField, gbcidField);

        // 비밀번호
        JLabel passwordLabel = new JLabel("비밀번호:");
        GridBagConstraints gbcpwLabel = new GridBagConstraints();
        gbcpwLabel.anchor = GridBagConstraints.WEST;
        gbcpwLabel.insets = new Insets(5, 5, 5, 5);
        gbcpwLabel.gridx = 0;
        gbcpwLabel.gridy = 4;
        add(passwordLabel, gbcpwLabel);
        
        passwordField = new JPasswordField(20);
        GridBagConstraints gbcpwField = new GridBagConstraints();
        gbcpwField.fill = GridBagConstraints.HORIZONTAL;
        gbcpwField.insets = new Insets(5, 5, 5, 5);
        gbcpwField.gridx = 1;
        gbcpwField.gridy = 4;
        add(passwordField, gbcpwField);

        // 비밀번호 확인
        JLabel passwordConfirmLabel = new JLabel("비밀번호 확인:");
        GridBagConstraints gbcpwcLabel = new GridBagConstraints();
        gbcpwcLabel.anchor = GridBagConstraints.WEST;
        gbcpwcLabel.insets = new Insets(5, 5, 5, 5);
        gbcpwcLabel.gridx = 0;
        gbcpwcLabel.gridy = 5;
        add(passwordConfirmLabel, gbcpwcLabel);
        
        passwordConfirmField = new JPasswordField(20);
        GridBagConstraints gbcpwcField = new GridBagConstraints();
        gbcpwcField.fill = GridBagConstraints.HORIZONTAL;
        gbcpwcField.insets = new Insets(5, 5, 5, 5);
        gbcpwcField.gridx = 1;
        gbcpwcField.gridy = 5;
        add(passwordConfirmField, gbcpwcField);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        signupButton = new JButton("가입하기");
        cancelButton = new JButton("취소");
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        GridBagConstraints gbcbtPanel = new GridBagConstraints();
        gbcbtPanel.gridwidth = 2;
        gbcbtPanel.insets = new Insets(5, 5, 5, 5);
        gbcbtPanel.gridx = 0;
        gbcbtPanel.gridy = 6;
        add(buttonPanel, gbcbtPanel);
    }
    
    /**
     * VSignup 클래스 내부에 정의된 내부 클래스(Inner Class)입니다.
     * idField에 영어(대소문자)와 숫자만 입력되도록 강제하는 DocumentFilter입니다.
     */
    public class EnglishOnlyFilter extends DocumentFilter {

        private final String regex = "^[a-zA-Z0-9]*$"; // 영어(a-z, A-Z)와 숫자(0-9)만 허용

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) {
                return;
            }
            if (string.matches(regex)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }
            if (text.matches(regex)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // --- Getters for CSignup (컨트롤러 접근용) ---
    
    public JTextField getIdField() { return idField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JPasswordField getPasswordConfirmField() { return passwordConfirmField; }
    public JTextField getNameField() { return nameField; }
    public JTextField getStudentIdField() { return studentIdField; }
    public JButton getSignupButton() { return signupButton; }
    public JButton getCancelButton() { return cancelButton; }
    
    /**
     * CSignup 컨트롤러가 호출하여 회원가입 폼의 모든 입력 필드를 비웁니다.
     */
    public void clearFields() {
        nameField.setText("");
        studentIdField.setText("");
        idField.setText("");
        passwordField.setText("");
        passwordConfirmField.setText("");
    }
}