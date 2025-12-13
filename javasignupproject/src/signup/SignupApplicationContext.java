package signup;

import signup.constants.PanelNames;
import signup.controller.CAdmin;
import signup.controller.CLogin;
import signup.controller.CMain;
import signup.controller.CPreRegister;
import signup.controller.CRegister;
import signup.controller.CSearch;
import signup.controller.CSignup;
import signup.controller.CSchedule;
import signup.controller.ControllerBundle;
import signup.dao.LectureDAO;
import signup.dao.SaveDAO;
import signup.dao.UserDAO;
import signup.model.MMain;
import signup.view.VAdmin;
import signup.view.VLogin;
import signup.view.VMain;
import signup.view.VPreRegister;
import signup.view.VRegister;
import signup.view.VSchedule;
import signup.view.VSearch;
import signup.view.VSignup;

/**
 * 애플리케이션 전체 객체를 조립하는 컨텍스트.
 * RMain에서의 의존성 수를 줄이고, 생성 순서를 한곳에서 관리합니다.
 */
public class SignupApplicationContext {

    private final VMain vMain;
    private final VLogin vLogin;
    private final VSignup vSignup;
    private final VSearch vSearch;
    private final VRegister vRegister;
    private final VPreRegister vPreRegister;
    private final VSchedule vSchedule;
    private final VAdmin vAdmin;

    private final MMain mMain;
    private final LectureDAO lectureDAO;
    private final SaveDAO saveDAO;
    private final UserDAO userDAO;

    private final CSearch cSearch;
    private final CRegister cRegister;
    private final CPreRegister cPreRegister;
    private final CSchedule cSchedule;
    private final CAdmin cAdmin;
    private final CMain cMain;
    private final CLogin cLogin;

    public SignupApplicationContext() {
        this.mMain = new MMain();
        this.lectureDAO = new LectureDAO();
        this.saveDAO = new SaveDAO();
        this.userDAO = new UserDAO();

        this.vMain = new VMain();
        this.vLogin = new VLogin();
        this.vSignup = new VSignup();
        this.vSearch = new VSearch();
        this.vRegister = new VRegister();
        this.vPreRegister = new VPreRegister();
        this.vSchedule = new VSchedule(this.vMain);
        this.vAdmin = new VAdmin(this.vMain);

        this.vMain.addPanel(this.vLogin, PanelNames.LOGIN_PANEL);
        this.vMain.addPanel(this.vSignup, PanelNames.SIGNUP_PANEL);
        this.vMain.getPanel().add(this.vSearch, PanelNames.SEARCH_PANEL);
        this.vMain.getPanel().add(this.vRegister, PanelNames.REGISTER_PANEL);
        this.vMain.getPanel().add(this.vPreRegister, PanelNames.PREREGISTER_PANEL);

        this.cSearch = new CSearch(this.vSearch, this.mMain, this.lectureDAO, this.saveDAO, this.userDAO);
        this.cSchedule = new CSchedule(this.vSchedule, this.mMain, this.saveDAO);
        this.cRegister = new CRegister(this.vRegister, this.mMain, this.saveDAO);
        this.cPreRegister = new CPreRegister(this.vPreRegister, this.mMain, this.saveDAO);
        this.cAdmin = new CAdmin(this.vAdmin, this.lectureDAO);

        ControllerBundle controllers = new ControllerBundle(this.cSearch, this.cRegister, this.cPreRegister, this.cSchedule, this.cAdmin);
        this.cMain = new CMain(this.vMain, this.mMain, this.userDAO, controllers);
        this.vMain.setMainController(this.cMain);

        this.cLogin = new CLogin(this.vMain, this.vLogin, this.mMain, this.userDAO, this.cSearch);
        new CSignup(this.vMain, this.vSignup, this.lectureDAO, this.userDAO);
        this.cLogin.setCMain(this.cMain);
    }

    public VMain getMainView() {
        return vMain;
    }

    public void initialize() {
        this.vMain.contentPanel(PanelNames.LOGIN_PANEL);
        this.vMain.setVisible(true);
    }
}
