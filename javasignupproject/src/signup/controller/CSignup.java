package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays; 
import javax.swing.JOptionPane;

import signup.model.MSignup;
import signup.view.VMain;
import signup.view.VSignup;

/**
 * VSignup(회원가입 뷰)의 이벤트를 처리하고
 * MSignup(회원가입 모델)과 상호작용하는 컨트롤러입니다.
 */
public class CSignup {

    private VMain vMain;     // 메인 프레임 (화면 전환용)
    private VSignup vSignup; // 회원가입 패널 (입력 필드 접근용)
    private MSignup mSignup; // 회원가입 모델 (중복 검사, 사용자 등록용)

    /**
     * CSignup 컨트롤러를 생성합니다.
     * VSignup의 "가입하기", "취소" 버튼에 대한 리스너를 설정합니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param vSignup 제어할 회원가입 뷰 (VSignup)
     */
    public CSignup(VMain vMain, VSignup vSignup) {
        this.vMain = vMain;
        this.vSignup = vSignup;
        this.mSignup = new MSignup(); // CSignup이 MSignup을 직접 생성하여 사용

        this.vSignup.getSignupButton().addActionListener(new SignupActionListener());
        this.vSignup.getCancelButton().addActionListener(new CancelActionListener());
    }

    /**
     * "가입하기" 버튼 클릭을 처리하는 내부 클래스입니다.
     * 모든 입력값에 대한 유효성 검사를 수행한 후 모델에 등록을 요청합니다.
     */
    class SignupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. 뷰(VSignup)에서 모든 입력 값을 가져와 공백을 제거합니다.
            String name = vSignup.getNameField().getText().trim();
            String studentId = vSignup.getStudentIdField().getText().trim();
            String id = vSignup.getIdField().getText().trim(); 
            
            char[] passwordChars = vSignup.getPasswordField().getPassword();
            String password = new String(passwordChars);
            char[] passwordConfirmChars = vSignup.getPasswordConfirmField().getPassword();
            String passwordConfirm = new String(passwordConfirmChars);
            
            // 2. 입력값 유효성 검사 (Validation)
            if (!validateInput(name, studentId, id, password, passwordConfirm)) {
                // 유효성 검사에 실패하면(오류 메시지는 validateInput 내부에서 처리) 즉시 종료
                return; 
            }

            // 3. 학번 중복 검사 (모델에게 요청)
            if (mSignup.isStudentIdDuplicate(studentId)) {
                JOptionPane.showMessageDialog(vSignup, "이미 가입된 학번입니다.", "가입 오류", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            // 4. 모든 검사 통과: 모델에게 사용자 등록 요청
            boolean isSuccess = mSignup.registerUser(studentId, id, password, name);

            if (isSuccess) {
                JOptionPane.showMessageDialog(vSignup, name + "님, 회원가입이 성공적으로 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                vSignup.clearFields(); // 성공 시 폼 초기화
                vMain.contentPanel("loginPanel"); // 로그인 화면으로 전환
            } else {
                JOptionPane.showMessageDialog(vSignup, "회원가입 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
            
            // 5. (보안) 메모리에 남아있는 비밀번호 배열을 즉시 초기화
            Arrays.fill(passwordChars, '0');
            Arrays.fill(passwordConfirmChars, '0');
        }

        /**
         * SignupActionListener 내부 헬퍼 메소드.
         * 입력 필드의 유효성을 단계별로 검사합니다.
         * @return 모든 검사를 통과하면 true, 하나라도 실패하면 false
         */
        private boolean validateInput(String name, String studentId, String id, String password, String passwordConfirm) {
            // 빈 필드 검사
            if (name.isEmpty() || studentId.isEmpty() || id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(vSignup, "모든 정보를 입력해주세요.", "정보누락", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 학번 길이 검사
            if (studentId.length() != 8) {
                JOptionPane.showMessageDialog(vSignup, "올바르지 않은 학번입니다. (8자리)", "학번오류", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 비밀번호 일치 검사
            if (!password.equals(passwordConfirm)) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호가 일치하지 않습니다.", "비번확인불일치", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 아이디 길이 검사 (3~15자)
            if (id.length() < 3 || id.length() > 15) {
                JOptionPane.showMessageDialog(vSignup, "아이디는 3~15자 이내여야 합니다.", "아이디수제한", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 비밀번호 길이 검사 (8~20자)
            if (password.length() < 8 || password.length() > 20) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호는 8~20자 이내여야 합니다.", "비번수제한", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 비밀번호 내용 (한글) 검사
            if (password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호에 한글을 포함할 수 없습니다.", "비번한글", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // 비밀번호 내용 (공백) 검사
            if (password.contains(" ")) {
                JOptionPane.showMessageDialog(vSignup, "비밀번호에 공백을 포함할 수 없습니다.", "비번공백", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // 비밀번호 필수 포함 요소 검사 (영어, 숫자, 특수문자)
            boolean hasLetter = password.matches(".*[a-zA-Z]+.*");
            boolean hasDigit = password.matches(".*[\\d]+.*");
            boolean hasSpecial = password.matches(".*[^a-zA-Z0-9]+.*"); 

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

    /**
     * "취소" 버튼 클릭을 처리하는 내부 클래스입니다.
     */
    class CancelActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            vSignup.clearFields(); // 폼 초기화
            vMain.contentPanel("loginPanel"); // 로그인 화면으로 돌아가기
        }
    }
}