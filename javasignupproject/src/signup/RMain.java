package signup;


import signup.controller.CLogin;
import signup.controller.CMain;
import signup.controller.CSignup;
import signup.model.MMain;
import signup.view.VLogin;
import signup.view.VMain;
import signup.view.VSignup;

/**
 * 프로그램을 시작하고 모든 MVC 구성 요소를 조립(Assembly)하는 메인 클래스입니다.
 * (Model, View, Controller 객체를 생성하고 서로 연결합니다.)
 */
public class RMain {

    // 1. 뷰 (프레임과 패널)
    private VMain vMain;
    private VLogin vLogin;
    private VSignup vSignup;
    
    // 2. 모델 (데이터)
    private MMain mMain;

    // 3. 컨트롤러 (로직)
    private CLogin cLogin;
    private CSignup cSignup;
    private CMain cMain;

    /**
     * RMain 생성자:
     * 모든 핵심 MVC 컴포넌트를 생성하고 의존성을 주입합니다.
     */
    public RMain() {
        // 모델과 뷰 객체 생성
        this.mMain = new MMain();
        this.vMain = new VMain();
        this.vLogin = new VLogin();
        this.vSignup = new VSignup();
        
        // VMain(메인 프레임)에 뷰 패널들 추가 (CardLayout으로 관리)
        this.vMain.addPanel(this.vLogin, "loginPanel");
        this.vMain.addPanel(this.vSignup, "signupPanel");

        // 컨트롤러 생성 및 의존성 주입 (필요한 객체 전달)
        cLogin = new CLogin(this.vMain, this.vLogin, this.mMain);
        cSignup = new CSignup(this.vMain, this.vSignup);
        cMain = new CMain(this.vMain, this.mMain);
    }
    
    /**
     * GUI를 초기화하고 화면에 표시합니다.
     */
    public void initialize() {
        // 프로그램 시작 시 "loginPanel"을 먼저 보여주도록 설정
    	this.vMain.contentPanel("loginPanel");
    
        // 메인 프레임을 화면에 표시
        this.vMain.setVisible(true);
    }
    
    /**
     * 프로그램을 종료합니다.
     */
    public void finish() {
        System.exit(0);
    }
    
    /**
     * 프로그램의 메인 진입점입니다.
     * @param args (사용되지 않음)
     */
    public static void main(String[] args) {
    	RMain rMain = new RMain();
        rMain.initialize();
        
    }
}