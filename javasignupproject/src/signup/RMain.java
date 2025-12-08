package signup;

import java.util.logging.Logger;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import signup.constants.PanelNames;
import signup.controller.CAdmin;
import signup.controller.CLogin;
import signup.controller.CMain;
import signup.controller.CPreRegister;
import signup.controller.CRegister;
import signup.controller.CSearch;
import signup.controller.CSignup;
import signup.controller.CSchedule;

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

public class RMain {

    private VMain vMain;
    private VLogin vLogin;
    private VSignup vSignup;
    private VSearch vSearch;
    private VRegister vRegister;
    private VPreRegister vPreRegister;
    private VSchedule vSchedule;
    private VAdmin vAdmin;
    
    private MMain mMain;

    private LectureDAO lectureDAO;
    private SaveDAO saveDAO;
    private UserDAO userDAO;
    
    private CSearch cSearch;
    private CRegister cRegister;
    private CPreRegister cPreRegister;
    private CSchedule cSchedule;
    private CMain cMain;
    private CLogin cLogin;
    private CAdmin cAdmin;
    
    public RMain() {
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
        this.cMain = new CMain(this.vMain, this.mMain, this.userDAO, this.cSearch, this.cRegister, this.cPreRegister, this.cSchedule, this.cAdmin);
        this.cLogin = new CLogin(this.vMain, this.vLogin, this.mMain, this.userDAO, this.cSearch, this.cAdmin);
        new CSignup(this.vMain, this.vSignup, this.lectureDAO, this.userDAO);
        
        cLogin.setCMain(this.cMain);
    }
    
    public void initialize() {
    	this.vMain.contentPanel(PanelNames.LOGIN_PANEL);
        this.vMain.setVisible(true);
    }

    public static void main(String[] args) {
    	System.setProperty("flatlaf.uiScale", "1.2");
    	try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) { Logger.getLogger(RMain.class.getName()).severe("Failed to initialize LaF"); }
    	
    	RMain rMain = new RMain();
        rMain.initialize();
    }
}