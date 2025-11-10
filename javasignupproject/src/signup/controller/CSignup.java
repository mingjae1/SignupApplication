package signup.controller;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import signup.dao.UserDAO;     // UserDAO 임포트
import signup.dao.LectureDAO; // LectureDAO 임포트
import signup.model.MUser;
import signup.model.ComboboxItem;
import signup.view.VMain;
import signup.view.VSignup;

/**
 * VSignup(회원가입 뷰)의 이벤트를 처리하고
 * UserDAO(DB) 및 LectureDAO(DB)와 상호작용하는 컨트롤러입니다.
 */
public class CSignup {

    private VMain vMain;
    private VSignup vSignup;
    
    private UserDAO userDAO;     // [수정됨] DAO를 필드로 선언
    private LectureDAO lectureDAO; // [수정됨] DAO를 필드로 선언
    
    private static final String DEFAULT_CAMPUS = "- 캠퍼스 선택 -";
    private static final String DEFAULT_COLLEGE = "- 대학 선택 -";
    private static final String DEFAULT_DEPT = "- 학과 선택 -";
    
 // 미리 컴파일된 정규식 패턴 (Pattern)입니다.
    private static final Pattern HAS_LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern HAS_DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]");
    
    
    /**
     * CSignup 컨트롤러를 생성합니다.
     * RMain으로부터 뷰와 DAO 객체들을 주입받습니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param vSignup 제어할 회원가입 뷰 (VSignup)
     * @param lectureDAO 콤보박스 데이터를 로드할 LectureDAO
     * @param userDAO 사용자 생성/중복검사를 처리할 UserDAO
     */
    public CSignup(VMain vMain, VSignup vSignup, LectureDAO lectureDAO, UserDAO userDAO) {
        this.vMain = vMain;
        this.vSignup = vSignup;
        
        // [수정됨] RMain으로부터 DAO를 주입받음
        this.userDAO = userDAO;
        this.lectureDAO = lectureDAO; 

        // 1. 버튼 리스너 연결
        this.vSignup.getSignupButton().addActionListener(this::handleSignup);
        this.vSignup.getCancelButton().addActionListener(this::handleCancel);
        
        // 2. 콤보박스 리스너 연결
        this.vSignup.getComboCampus().addActionListener(this::handleCampusSelect);
        this.vSignup.getComboCollege().addActionListener(this::handleCollegeSelect);

        // 3. 컨트롤러가 생성될 때 뷰에 초기 캠퍼스 목록을 로드
        loadInitialCampusData();
    }
    
    /**
     * 컨트롤러 생성 시, DB에서 '캠퍼스' 목록을 가져와 콤보박스에 채웁니다.
     */
    private void loadInitialCampusData() {
        List<ComboboxItem> campuses = this.lectureDAO.getAllCampuses(); 
        
        JComboBox<Object> comboCampus = vSignup.getComboCampus();
        comboCampus.removeAllItems(); 
        comboCampus.addItem(DEFAULT_CAMPUS); 
        for (ComboboxItem item : campuses) {
            comboCampus.addItem(item);
        }
    }
    
    /**
     * '캠퍼스' 콤보박스 선택 시 '단과대학' 목록을 로드합니다.
     */
    private void handleCampusSelect(ActionEvent e) {
        JComboBox<Object> comboCampus = vSignup.getComboCampus();
        JComboBox<Object> comboCollege = vSignup.getComboCollege();
        JComboBox<Object> comboDepartment = vSignup.getComboDepartment();

        if (!(comboCampus.getSelectedItem() instanceof ComboboxItem)) {
            if (comboCampus.getSelectedIndex() <= 0) { 
                resetSubCombos(comboCollege, comboDepartment);
            }
            return;
        }

        ComboboxItem selectedCampus = (ComboboxItem) comboCampus.getSelectedItem();
        
        // [수정!] DAO에 String 이름 대신 int ID를 전달합니다.
        List<ComboboxItem> colleges = this.lectureDAO.getCollegesByCampus(selectedCampus.getId()); 
        
        // --- (이하 콤보박스 채우는 로직은 동일) ---
        comboCollege.removeAllItems();
        comboCollege.addItem(DEFAULT_COLLEGE);
        for (ComboboxItem item : colleges) {
            comboCollege.addItem(item);
        }
        comboCollege.setEnabled(true);
        
        comboDepartment.removeAllItems();
        comboDepartment.addItem(DEFAULT_DEPT);
        comboDepartment.setEnabled(false);
    }

    /**
     * '단과대학' 콤보박스 선택 시 '학과' 목록을 로드합니다.
     */
    private void handleCollegeSelect(ActionEvent e) {
        JComboBox<Object> comboCollege = vSignup.getComboCollege();
        JComboBox<Object> comboDepartment = vSignup.getComboDepartment();

        if (!(comboCollege.getSelectedItem() instanceof ComboboxItem)) {
            comboDepartment.removeAllItems();
            comboDepartment.addItem(DEFAULT_DEPT);
            comboDepartment.setEnabled(false);
            return;
        }

        ComboboxItem selectedCollege = (ComboboxItem) comboCollege.getSelectedItem();
        
        // [수정!] DAO에 String 이름 대신 int ID를 전달합니다.
        List<ComboboxItem> departments = this.lectureDAO.getDepartmentsByCollege(selectedCollege.getId());
        
        // --- (이하 콤보박스 채우는 로직은 동일) ---
        comboDepartment.removeAllItems();
        comboDepartment.addItem(DEFAULT_DEPT);
        for (ComboboxItem item : departments) {
            comboDepartment.addItem(item);
        }
        comboDepartment.setEnabled(true);
    }
    
    /**
     * 하위 콤보박스들을 초기 상태로 되돌립니다.
     */
    private void resetSubCombos(JComboBox<Object> comboCollege, JComboBox<Object> comboDepartment) {
        comboCollege.removeAllItems();
        comboCollege.addItem(DEFAULT_COLLEGE);
        comboCollege.setEnabled(false);
        comboDepartment.removeAllItems();
        comboDepartment.addItem(DEFAULT_DEPT);
        comboDepartment.setEnabled(false);
    }

    /**
     * "가입하기" 버튼 클릭 이벤트를 처리합니다.
     */
    private void handleSignup(ActionEvent e) {
        
        // 1. 뷰(VSignup)에서 모든 입력 값을 가져옵니다.
        String name = vSignup.getNameField().getText().trim();
        String studentIdStr = vSignup.getStudentIdField().getText().trim();
        String id = vSignup.getIdField().getText().trim();
        String email = vSignup.getEmailField().getText().trim();
        
        char[] passwordChars = vSignup.getPasswordField().getPassword();
        String password = new String(passwordChars);
        char[] passwordConfirmChars = vSignup.getPasswordConfirmField().getPassword();
        String passwordConfirm = new String(passwordConfirmChars);
        
        // 콤보박스에서 선택된 ComboboxItem 객체를 가져옵니다.
        Object campusObj = vSignup.getComboCampus().getSelectedItem();
        Object collegeObj = vSignup.getComboCollege().getSelectedItem();
        Object departmentObj = vSignup.getComboDepartment().getSelectedItem();
        
        MUser user = new MUser();
        user.setUserid(id);
        user.setName(name);
        user.setEmail(email);
        
     // 콤보박스 값(ID와 이름)을 DTO에 저장
        if (campusObj instanceof ComboboxItem item) {
            user.setCampusId(item.getId());
            user.setCampus(item.getName()); // 유효성 검사용 String 이름
        }
        if (collegeObj instanceof ComboboxItem item) {
            user.setCollegeId(item.getId());
            user.setCollege(item.getName()); // 유효성 검사용 String 이름
        }
        if (departmentObj instanceof ComboboxItem item) {
            user.setDepartmentId(item.getId());
            user.setDepartment(item.getName()); // 유효성 검사용 String 이름
        }
        
        // 2. 유효성 검사 (여전히 String 이름을 사용)
        if (!validateInput(user, studentIdStr, password, passwordConfirm)) {
            return; 
        }

        // 3. DTO 생성 및 ID 값 추출
        int studentId = Integer.parseInt(studentIdStr);
        user.setCode(studentId);// 유효성 검사를 통과했으므로 안전함
        
        if (userDAO.isUserIdDuplicate(user.getUserid())) {
            JOptionPane.showMessageDialog(vSignup, "이미 사용 중인 아이디입니다.", "가입 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userDAO.isStudentIdDuplicate(user.getCode())) { 
            JOptionPane.showMessageDialog(vSignup, "이미 가입된 학번입니다.", "가입 오류", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        // 6. DAO에게 MUser DTO와 비밀번호 전달 (매개변수 2개)
        boolean isSuccess = userDAO.addUser(user, password);

        if (isSuccess) {
            JOptionPane.showMessageDialog(vSignup, name + "님, 회원가입이 성공적으로 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            vSignup.clearFields(); 
            vMain.contentPanel("loginPanel"); 
        } else {
            JOptionPane.showMessageDialog(vSignup, "회원가입 중 오류가 발생했습니다. (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
        }
        
        // 6. (보안) 메모리 초기화
        Arrays.fill(passwordChars, '0');
        Arrays.fill(passwordConfirmChars, '0');
    }

    /**
     * "취소" 버튼 클릭 이벤트를 처리합니다.
     */
    private void handleCancel(ActionEvent e) {
        vSignup.clearFields();
        vMain.contentPanel("loginPanel");
    }
    
    /**
     * 입력 필드와 콤보박스의 유효성을 단계별로 검사하는 헬퍼(Helper) 메서드입니다.
     * (validation은 이름(String)을 기반으로 수행합니다.)
     */
    private boolean validateInput(MUser user,String studentId, String password, String passwordConfirm) {
        
        // 빈 필드 검사 (콤보박스에서 추출된 이름이 비어있으면 선택 안 한 것임)
    	if (user.getName().isEmpty() || studentId.isEmpty() || user.getUserid().isEmpty() || 
                password.isEmpty() || user.getEmail().isEmpty() ||
                user.getCampus() == null || user.getCollege() == null || user.getDepartment() == null) {
                JOptionPane.showMessageDialog(vSignup, "모든 정보를 입력/선택해주세요.", "정보누락", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 2. 이메일 형식 검사
            if (!user.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                JOptionPane.showMessageDialog(vSignup, "올바른 이메일 형식이 아닙니다.", "이메일 오류", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 3. 학번 검사 (숫자 8자리)
            if (!studentId.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(vSignup, "올바르지 않은 학번입니다. (숫자 8자리)", "학번오류", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 4. 아이디 길이 검사
            if (user.getUserid().length() < 3 || user.getUserid().length() > 15) {
                JOptionPane.showMessageDialog(vSignup, "아이디는 3~15자 이내여야 합니다.", "아이디수제한", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 5. 비밀번호 일치 검사
            if (!password.equals(passwordConfirm)) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호가 일치하지 않습니다.", "비번확인불일치", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 6. 비밀번호 길이 검사 (8~20자)
            if (password.length() < 8 || password.length() > 20) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호는 8~20자 이내여야 합니다.", "비번수제한", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 7. 비밀번호 내용 (한글) 검사
            if (password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호에 한글을 포함할 수 없습니다.", "비번한글", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 8. 비밀번호 내용 (공백) 검사
            if (password.contains(" ")) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호에 공백을 포함할 수 없습니다.", "비번공백", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 9. 비밀번호 필수 포함 요소 검사
            boolean hasLetter = HAS_LETTER_PATTERN.matcher(password).find();
            boolean hasDigit = HAS_DIGIT_PATTERN.matcher(password).find();
            boolean hasSpecial = HAS_SPECIAL_PATTERN.matcher(password).find();

            if (!hasLetter || !hasDigit || !hasSpecial) {
                JOptionPane.showMessageDialog(vSignup, 
                    "비밀번호는 영어, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.", 
                    "조건불충족", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true; // 모든 검사 통과
        }
    
    }