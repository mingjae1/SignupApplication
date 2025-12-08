package signup.controller;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import signup.constants.AppConstants;
import signup.constants.PanelNames;
import signup.dao.UserDAO;
import signup.dao.LectureDAO;
import signup.model.MUser;
import signup.model.ComboboxItem;
import signup.view.VMain;
import signup.view.VSignup;

public class CSignup {

    private VMain vMain;
    private VSignup vSignup;
    private UserDAO userDAO;     
    private LectureDAO lectureDAO; 
    
    private static final String DEFAULT_CAMPUS = "- 캠퍼스 선택 -";
    private static final String DEFAULT_COLLEGE = "- 대학 선택 -";
    private static final String DEFAULT_DEPT = "- 학과 선택 -";
    
    private static final Pattern HAS_LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern HAS_DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]");
    private static final Pattern HAS_KOREAN_PATTERN = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern STUDENT_CODE_PATTERN = Pattern.compile("\\d{" + AppConstants.STUDENT_CODE_LENGTH + "}");
    
    public CSignup(VMain vMain, VSignup vSignup, LectureDAO lectureDAO, UserDAO userDAO) {
        this.vMain = vMain;
        this.vSignup = vSignup;
        this.userDAO = userDAO;
        this.lectureDAO = lectureDAO; 
        
        this.vSignup.getSignupButton().addActionListener(this::handleSignup);
        this.vSignup.getCancelButton().addActionListener(this::handleCancel);
        this.vSignup.getComboCampus().addActionListener(this::handleCampusSelect);
        this.vSignup.getComboCollege().addActionListener(this::handleCollegeSelect);

        new Thread(this::loadInitialCampusData).start();
    }
    
    private void loadInitialCampusData() {
        List<ComboboxItem> campuses = this.lectureDAO.getAllCampuses(); 
        JComboBox<Object> comboCampus = vSignup.getComboCampus();
        comboCampus.removeAllItems(); 
        comboCampus.addItem(DEFAULT_CAMPUS); 
        for (ComboboxItem item : campuses) {
            comboCampus.addItem(item);
        }
    }
    
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
        List<ComboboxItem> colleges = this.lectureDAO.getCollegesByCampus(selectedCampus.getId()); 
        
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
        List<ComboboxItem> departments = this.lectureDAO.getDepartmentsByCollege(selectedCollege.getId());
        
        comboDepartment.removeAllItems();
        comboDepartment.addItem(DEFAULT_DEPT);
        for (ComboboxItem item : departments) {
            comboDepartment.addItem(item);
        }
        comboDepartment.setEnabled(true);
    }
    
    private void resetSubCombos(JComboBox<Object> comboCollege, JComboBox<Object> comboDepartment) {
        comboCollege.removeAllItems();
        comboCollege.addItem(DEFAULT_COLLEGE);
        comboCollege.setEnabled(false);
        comboDepartment.removeAllItems();
        comboDepartment.addItem(DEFAULT_DEPT);
        comboDepartment.setEnabled(false);
    }

    private void handleSignup(ActionEvent e) {
        String name = vSignup.getNameField().getText().trim();
        String studentIdStr = vSignup.getStudentIdField().getText().trim();
        String id = vSignup.getIdField().getText().trim();
        String email = vSignup.getEmailField().getText().trim();
        
        char[] passwordChars = vSignup.getPasswordField().getPassword();
        String password = new String(passwordChars);
        char[] passwordConfirmChars = vSignup.getPasswordConfirmField().getPassword();
        String passwordConfirm = new String(passwordConfirmChars);
        
        Object campusObj = vSignup.getComboCampus().getSelectedItem();
        Object collegeObj = vSignup.getComboCollege().getSelectedItem();
        Object departmentObj = vSignup.getComboDepartment().getSelectedItem();
        
        MUser user = new MUser();
        user.setUserid(id);
        user.setName(name);
        user.setEmail(email);
        
        if (campusObj instanceof ComboboxItem item) {
            user.setCampusId(item.getId());
            user.setCampus(item.getName());
        }
        if (collegeObj instanceof ComboboxItem item) {
            user.setCollegeId(item.getId());
            user.setCollege(item.getName());
        }
        if (departmentObj instanceof ComboboxItem item) {
            user.setDepartmentId(item.getId());
            user.setDepartment(item.getName());
        }
        
        if (!validateInput(user, studentIdStr, password, passwordConfirm)) {
            return; 
        }

        int studentId = Integer.parseInt(studentIdStr);
        user.setCode(studentId);
        
        if (userDAO.isUserIdDuplicate(user.getUserid())) {
            JOptionPane.showMessageDialog(vSignup, "이미 사용 중인 아이디입니다.", "가입 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userDAO.isStudentIdDuplicate(user.getCode())) { 
            JOptionPane.showMessageDialog(vSignup, "이미 가입된 학번입니다.", "가입 오류", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        boolean isSuccess = userDAO.addUser(user, password);

        if (isSuccess) {
            JOptionPane.showMessageDialog(vSignup, name + "님, 회원가입이 성공적으로 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            vSignup.clearFields(); 
            vMain.contentPanel(PanelNames.LOGIN_PANEL); 
        } else {
            JOptionPane.showMessageDialog(vSignup, "회원가입 중 오류가 발생했습니다. (DB 오류)", "오류", JOptionPane.ERROR_MESSAGE);
        }
        
        Arrays.fill(passwordChars, '0');
        Arrays.fill(passwordConfirmChars, '0');
    }

    private void handleCancel(ActionEvent e) {
        vSignup.clearFields();
        vMain.setSize(380, 280);
        vMain.setLocationRelativeTo(null);
        vMain.contentPanel(PanelNames.LOGIN_PANEL);
    }
    
    private boolean validateInput(MUser user, String studentId, String password, String passwordConfirm) {
        if (!isAllFieldsFilled(user, studentId, password)) {
            JOptionPane.showMessageDialog(vSignup, "모든 정보를 입력/선택해주세요.", "정보누락", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            JOptionPane.showMessageDialog(vSignup, "올바른 이메일 형식이 아닙니다.", "이메일 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!STUDENT_CODE_PATTERN.matcher(studentId).matches()) {
            JOptionPane.showMessageDialog(vSignup, 
                "올바르지 않은 학번입니다. (숫자 " + AppConstants.STUDENT_CODE_LENGTH + "자리)", 
                "학번오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isUserIdLengthValid(user.getUserid())) {
            JOptionPane.showMessageDialog(vSignup, 
                "아이디는 " + AppConstants.MIN_USER_ID_LENGTH + "~" + AppConstants.MAX_USER_ID_LENGTH + "자 이내여야 합니다.", 
                "아이디수제한", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(vSignup, "비밀번호가 일치하지 않습니다.", "비번확인불일치", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isPasswordLengthValid(password)) {
            JOptionPane.showMessageDialog(vSignup, 
                "비밀번호는 " + AppConstants.MIN_PASSWORD_LENGTH + "~" + AppConstants.MAX_PASSWORD_LENGTH + "자 이내여야 합니다.", 
                "비번수제한", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (HAS_KOREAN_PATTERN.matcher(password).find()) {
            JOptionPane.showMessageDialog(vSignup, "비밀번호에 한글을 포함할 수 없습니다.", "비번한글", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (password.contains(" ")) {
            JOptionPane.showMessageDialog(vSignup, "비밀번호에 공백을 포함할 수 없습니다.", "비번공백", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!hasRequiredPasswordElements(password)) {
            JOptionPane.showMessageDialog(vSignup, 
                "비밀번호는 영어, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.", 
                "조건불충족", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean isAllFieldsFilled(MUser user, String studentId, String password) {
        return !user.getName().isEmpty() && !studentId.isEmpty() && 
               !user.getUserid().isEmpty() && !password.isEmpty() && 
               !user.getEmail().isEmpty() && user.getCampus() != null && 
               user.getCollege() != null && user.getDepartment() != null;
    }
    
    private boolean isUserIdLengthValid(String userId) {
        return userId.length() >= AppConstants.MIN_USER_ID_LENGTH && 
               userId.length() <= AppConstants.MAX_USER_ID_LENGTH;
    }
    
    private boolean isPasswordLengthValid(String password) {
        return password.length() >= AppConstants.MIN_PASSWORD_LENGTH && 
               password.length() <= AppConstants.MAX_PASSWORD_LENGTH;
    }
    
    private boolean hasRequiredPasswordElements(String password) {
        return HAS_LETTER_PATTERN.matcher(password).find() && 
               HAS_DIGIT_PATTERN.matcher(password).find() && 
               HAS_SPECIAL_PATTERN.matcher(password).find();
    }
}