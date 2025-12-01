package signup.constants;

/**
 * 애플리케이션 전체에서 사용되는 패널 이름 상수를 정의합니다.
 * CardLayout 전환 시 사용되는 패널 식별자들을 중앙에서 관리합니다.
 */
public final class PanelNames {
    
    private PanelNames() {
        // 인스턴스화 방지
    }
    
    // 메인 화면 패널들
    public static final String LOGIN_PANEL = "loginPanel";
    public static final String SIGNUP_PANEL = "signupPanel";
    public static final String MAIN_CONTENT_PANEL = "mainContentPanel";
    
    // 컨텐츠 패널들
    public static final String SEARCH_PANEL = "searchPanel";
    public static final String REGISTER_PANEL = "registerPanel";
    public static final String PREREGISTER_PANEL = "preRegisterPanel";
    public static final String SCHEDULE_PANEL = "schedulePanel";
    public static final String ADMIN_PANEL = "adminPanel";
}
