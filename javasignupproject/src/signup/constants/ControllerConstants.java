package signup.constants;

import java.util.regex.Pattern;

/**
 * Controller 관련 공통 상수 및 유틸리티 메서드를 관리하는 클래스입니다.
 * 데이터 검증, 에러 메시지, 비지니스 로직 상수를 중앙에서 관리합니다.
 */
public final class ControllerConstants {
    
    private ControllerConstants() {
    }
    
    // ==================== 유효성 검사 패턴 ====================
    /** 비밀번호 정책: 영문자, 숫자, 특수문자 각 1개 이상, 최소 8자 */
    public static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    
    /** 이메일 유효성 검사 패턴 */
    public static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /** 숫자만 검사 */
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");
    
    // ==================== 에러 메시지 ====================
    public static final String ERROR_LOGIN_FAILED = "아이디 혹은 비밀번호가 틀렸습니다.";
    public static final String ERROR_LOGIN_UNKNOWN = "알 수 없는 오류입니다. 나중에 다시 시도해주세요. (DB 오류)";
    public static final String ERROR_DB_CONNECTION = "데이터베이스 연결에 실패했습니다.";
    public static final String ERROR_SECRET_CHANGE_DB = "비밀번호 변경 중 DB 오류가 발생했습니다.";
    public static final String ERROR_SECRET_MISMATCH = "현재 비밀번호가 올바르지 않습니다.";
    public static final String ERROR_SECRET_EMPTY = "새 비밀번호를 입력하세요.";
    public static final String ERROR_SECRET_CONFIRM_MISMATCH = "새 비밀번호와 확인이 일치하지 않습니다.";
    public static final String ERROR_SECRET_POLICY = "비밀번호는 영어(영문자), 숫자, 특수문자 각각 1개 이상 포함하고 최소 8자여야 합니다.";
    public static final String ERROR_INPUT_NUMBER = "학번은 숫자여야 합니다.";
    public static final String ERROR_RESET_SECRET_NOT_FOUND = "입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.";
    public static final String ERROR_RESET_SECRET_DB = "비밀번호 초기화 중 DB 오류가 발생했습니다.";
    public static final String ERROR_CREDIT_EXCEEDED = "신청 가능한 학점을 초과했습니다.";
    public static final String ERROR_DUPLICATE_LECTURE = "이미 신청한 강의입니다.";
    
    // ==================== 성공 메시지 ====================
    public static final String SUCCESS_LOGIN = "로그인 성공";
    public static final String TITLE_LOGIN_COMPLETE_ADMIN = "관리자 로그인";
    public static final String SUCCESS_ADMIN_LOGIN = "관리자 로그인";
    public static final String SUCCESS_SECRET_CHANGED = "비밀번호가 변경되었습니다.";
    public static final String SUCCESS_SECRET_RESET = "비밀번호가 초기화되었습니다. 초기 비밀번호가 재발급되었습니다.";
    public static final String SUCCESS_LECTURE_SIGNUP = "수강신청이 완료되었습니다.";
    public static final String SUCCESS_LECTURE_CANCEL = "수강신청이 취소되었습니다.";
    public static final String SUCCESS_LECTURE_SAVE = "강의가 저장되었습니다.";
    
    // ==================== 다이얼로그 타이틀 ====================
    public static final String TITLE_LOGIN_FAILED = "로그인 실패";
    public static final String TITLE_LOGIN_ERROR = "로그인 오류";
    public static final String TITLE_SECRET_CHANGE = "비밀번호 변경";
    public static final String TITLE_SECRET_RESET = "비밀번호 초기화";
    public static final String TITLE_INPUT_ERROR = "입력 오류";
    public static final String TITLE_RESET_FAILED = "초기화 실패";
    public static final String TITLE_RESET_COMPLETE = "초기화 완료";
    public static final String TITLE_ERROR = "오류";
    public static final String TITLE_COMPLETE = "완료";
    public static final String TITLE_CONFIRMATION = "확인";
    public static final String TITLE_SECURITY_ERROR = "보안 정책 위반";
    
    // ==================== 초기값 ====================
    public static final String INITIAL_SECRET_KEY = "controller.initial.secret";
    
    // ==================== 검증 메서드 ====================
    
    /**
     * 비밀번호의 유효성을 검사합니다.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 이메일의 유효성을 검사합니다.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 입력값이 숫자인지 검사합니다.
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        return NUMERIC_PATTERN.matcher(value).matches();
    }
    
    /**
     * 입력값이 비어있는지 검사합니다.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * 두 값이 같은지 검사합니다.
     */
    public static boolean matches(String value1, String value2) {
        if (value1 == null || value2 == null) return false;
        return value1.equals(value2);
    }
    
    /**
     * 정수로 파싱을 시도합니다.
     */
    public static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}