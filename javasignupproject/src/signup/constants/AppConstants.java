package signup.constants;

public final class AppConstants {
    
    private AppConstants() {
    }
    
    // 학점 관련 상수
    public static final int MAX_CREDITS = 18;
    
    // 입력 유효성 검사 상수
    public static final int MIN_USER_ID_LENGTH = 3;
    public static final int MAX_USER_ID_LENGTH = 15;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final int STUDENT_CODE_LENGTH = 8;
    
    // 창 크기 상수
    public static final int LOGIN_WINDOW_WIDTH = 420;
    public static final int LOGIN_WINDOW_HEIGHT = 320;
    public static final int SIGNUP_WINDOW_WIDTH = 800;
    public static final int SIGNUP_WINDOW_HEIGHT = 600;
    public static final int MAIN_WINDOW_WIDTH = 1280;
    public static final int MAIN_WINDOW_HEIGHT = 800;
    
    // UI 스케일
    public static final String UI_SCALE = "1.2";
    
    // 데이터베이스 오류 코드
    public static final int DB_SUCCESS = 0;
    public static final int DB_ERROR_CREDIT_EXCEEDED = 1;
    public static final int DB_ERROR_DUPLICATE = 2;
    public static final int DB_ERROR_GENERAL = -1;
    
    // 파일 확장자
    public static final String PNG_EXTENSION = ".png";
    public static final String DEFAULT_SCHEDULE_FILENAME = "내시간표.png";
}
