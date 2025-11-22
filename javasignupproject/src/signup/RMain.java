package signup;

import java.util.logging.Logger;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

// 컨트롤러
import signup.controller.CLogin;
import signup.controller.CMain;
import signup.controller.CPreRegister;
import signup.controller.CRegister;
import signup.controller.CSearch;
import signup.controller.CSignup;
import signup.controller.CSchedule;

// DAO (데이터 접근)
import signup.dao.LectureDAO;
import signup.dao.SaveDAO;
import signup.dao.UserDAO;

// 모델 (DTO 및 상태)
import signup.model.MMain;

// 뷰 (GUI)
import signup.view.VLogin;
import signup.view.VMain;
import signup.view.VPreRegister;
import signup.view.VRegister;
import signup.view.VSchedule;
import signup.view.VSearch;
import signup.view.VSignup;


/**
 * 프로그램을 시작하고 모든 MVC 구성 요소를 조립(Assembly)하는 메인 클래스입니다.
 * Model, View, Controller, DAO 객체를 생성하고 서로 의존성을 주입합니다.
 */
public class RMain {

    // --- 1. 뷰 (프레임과 패널) ---
    private VMain vMain;
    private VLogin vLogin;
    private VSignup vSignup;
    private VSearch vSearch;
    private VRegister vRegister;
    private VPreRegister vPreRegister;
    private VSchedule vSchedule;
    
    // --- 2. 모델 (전역 상태) ---
    private MMain mMain;

    // --- 3. DAO (데이터 접근 객체) ---
    // (RMain이 생성하여 각 컨트롤러에 전달해줍니다)
    private LectureDAO lectureDAO;
    private SaveDAO saveDAO;
    private UserDAO userDAO;
    
    // --- 4. 컨트롤러 (이벤트 처리) ---
    private CSearch cSearch;
    private CRegister cRegister;
    private CPreRegister cPreRegister;
    private CSchedule cSchedule;
    
    /**
     * RMain 생성자:
     * 모든 핵심 MVC 컴포넌트를 생성하고 의존성을 주입합니다.
     */
    public RMain() {
        // --- 1. 모델과 DAO 생성 ---
        this.mMain = new MMain();
        this.lectureDAO = new LectureDAO();
        this.saveDAO = new SaveDAO();
        this.userDAO = new UserDAO();
        
        // --- 2. 뷰 생성 ---
        this.vMain = new VMain();
        this.vLogin = new VLogin();
        this.vSignup = new VSignup();
        this.vSearch = new VSearch();
        this.vRegister = new VRegister();
        this.vPreRegister = new VPreRegister();
        this.vSchedule = new VSchedule(this.vMain);
        
        
        // --- 3. 뷰 조립 ---
        // VMain(메인 프레임)의 메인 CardLayout에 패널 추가
        this.vMain.addPanel(this.vLogin, "loginPanel");
        this.vMain.addPanel(this.vSignup, "signupPanel");
        
        // VMain 내부의 컨텐츠 CardLayout에 패널 추가
        // (VMain.java에 getPanel() Getter가 필요합니다)
        this.vMain.getPanel().add(this.vSearch, "searchPanel");
        this.vMain.getPanel().add(this.vRegister, "registerPanel");
        this.vMain.getPanel().add(this.vPreRegister, "preregisterPanel");
        // (참고: vRegisterPanel, vBasketPanel은 VMain이 자체적으로 생성함)
        

        // --- 4. 컨트롤러 생성 (의존성 주입) ---
        // 각 컨트롤러에 필요한 뷰, 모델, DAO를 생성자의 인자로 전달합니다.
        this.cSearch = new CSearch(this.vSearch, this.mMain, this.lectureDAO, this.saveDAO, this.userDAO);
        this.cSchedule = new CSchedule(this.vSchedule, this.mMain, this.saveDAO);
        new CLogin(this.vMain, this.vLogin, this.mMain, this.userDAO, this.cSearch);
        new CSignup(this.vMain, this.vSignup, this.lectureDAO, this.userDAO);
        this.cRegister = new CRegister(this.vRegister, this.mMain, this.saveDAO);
        this.cPreRegister = new CPreRegister(this.vPreRegister, this.mMain, this.saveDAO);
        new CMain(this.vMain, this.mMain, this.cSearch, this.cRegister, this.cPreRegister, this.cSchedule);
    }
    
    /**
     * GUI를 초기화하고 화면에 표시합니다.
     */
    public void initialize() {
    	this.vMain.contentPanel("loginPanel");
        this.vMain.setVisible(true);
    }
       
    /**
     * 프로그램의 메인 진입점입니다.
     * @param args (사용되지 않음)
     */
    public static void main(String[] args) {
    	System.setProperty("flatlaf.uiScale", "1.2");
    	try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) { Logger.getLogger(RMain.class.getName()).severe("Failed to initialize LaF"); }
    	
    	RMain rMain = new RMain();
        rMain.initialize();
    }
}