package signup.constants;

/**
 * 애플리케이션 전역 상수 관리
 * 수정 시 영향 범위를 확인하고 관련 테스트 필요
 */
public final class AppConstants {
    
    private AppConstants() {
    }
    
    // ==================== 학점 관련 상수 ====================
    /** 최대 신청 가능 학점 (수강신청/미리담기 모두 적용) */
    public static final int MAX_CREDITS = 18;
    
    // ==================== 입력 유효성 검사 상수 ====================
    /** 사용자 ID 최소 길이 - 변경 시 DB 제약 조건 확인 필요 */
    public static final int MIN_USER_ID_LENGTH = 3;
    /** 사용자 ID 최대 길이 - 변경 시 DB 제약 조건 확인 필요 */
    public static final int MAX_USER_ID_LENGTH = 15;
    /** 비밀번호 최소 길이 - 보안 정책에 따라 조정 */
    public static final int MIN_PASSWORD_LENGTH = 8;
    /** 비밀번호 최대 길이 - 보안 정책에 따라 조정 */
    public static final int MAX_PASSWORD_LENGTH = 20;
    /** 학번 자릿수 - DB 스키마와 일치 필수 */
    public static final int STUDENT_CODE_LENGTH = 8;
    
    // ==================== 창 크기 상수 ====================
    /** 로그인 화면 너비 */
    public static final int LOGIN_WINDOW_WIDTH = 420;
    /** 로그인 화면 높이 */
    public static final int LOGIN_WINDOW_HEIGHT = 320;
    /** 회원가입 화면 너비 */
    public static final int SIGNUP_WINDOW_WIDTH = 800;
    /** 회원가입 화면 높이 */
    public static final int SIGNUP_WINDOW_HEIGHT = 600;
    /** 메인 화면 너비 */
    public static final int MAIN_WINDOW_WIDTH = 1280;
    /** 메인 화면 높이 */
    public static final int MAIN_WINDOW_HEIGHT = 800;
    
    // ==================== UI 설정 ====================
    /** UI 스케일 배율 - FlatLaf 설정용 */
    public static final String UI_SCALE = "1.2";
    
    // ==================== 데이터베이스 오류 코드 ====================
    /** 성공 - 정상 처리 완료 */
    public static final int DB_SUCCESS = 0;
    /** 오류 - 학점 초과 (MAX_CREDITS 확인) */
    public static final int DB_ERROR_CREDIT_EXCEEDED = 1;
    /** 오류 - 중복 데이터 (PK 제약 위반) */
    public static final int DB_ERROR_DUPLICATE = 2;
    /** 오류 - 일반 DB 오류 (로그 확인 필요) */
    public static final int DB_ERROR_GENERAL = -1;
    
    // ==================== 파일 관련 상수 ====================
    /** PNG 파일 확장자 */
    public static final String PNG_EXTENSION = ".png";
    /** 시간표 이미지 기본 파일명 */
    public static final String DEFAULT_SCHEDULE_FILENAME = "내시간표.png";
}
