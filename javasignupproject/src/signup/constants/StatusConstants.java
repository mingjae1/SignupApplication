package signup.constants;

/**
 * 수강신청 및 미리담기 상태를 나타내는 상수를 정의합니다.
 * save 테이블의 status 컬럼에 사용됩니다.
 */
public final class StatusConstants {
    
    private StatusConstants() {
        // 인스턴스화 방지
    }
    
    /**
     * 수강신청 상태
     */
    public static final String REGISTER = "reg";
    
    /**
     * 미리담기 상태
     */
    public static final String PRE_REGISTER = "pre";
}
