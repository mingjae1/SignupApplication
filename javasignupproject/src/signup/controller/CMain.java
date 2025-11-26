package signup.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import signup.dao.UserDAO;
import signup.model.MMain;
import signup.model.MUser;
import signup.view.VMain;

import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * VMain(메인 뷰)의 툴바 및 네비게이션을 제어하는 컨트롤러입니다.
 * 사이드바 메뉴, 테마 변경, 로그아웃 등의 전역 기능을 담당합니다.
 */
public class CMain {

    // --- 뷰 & 모델 ---
    private VMain vMain;
    private MMain mMain;
    
    // --- 하위 컨트롤러 ---
    private CSearch cSearch; 
    private CRegister cRegister;
    private CPreRegister cPreRegister;
    private CSchedule cSchedule;
    private CAdmin cAdmin;
    // --- DAO ---
    private UserDAO userDAO;
    
    // --- 네비게이션 ---
    private Deque<String> previousStack;
    private Deque<String> forwardStack; 
    private String currentPanel;
    
    // 패널 상수
    private static final String PANEL_REGISTER = "registerPanel";
    private static final String PANEL_PREREGISTER = "preRegisterPanel";
    private static final String PANEL_SEARCH = "searchPanel"; 
    private static final String PANEL_LOGIN = "loginPanel";
    private static final String PANEL_ADMIN = "adminPanel";
    
    /**
     * CMain 생성자: 모든 의존성을 주입받고 리스너를 설정합니다.
     * @param cAdmin 
     */
    public CMain(VMain vMain, MMain mMain, UserDAO userDAO, 
                 CSearch cSearch, CRegister cRegister, CPreRegister cPreRegister, CSchedule cSchedule, CAdmin cAdmin) {
        this.vMain = vMain;
        this.mMain = mMain;
        
		this.userDAO = userDAO;
        
        this.cSearch = cSearch;
        this.cRegister = cRegister;
        this.cPreRegister = cPreRegister;
        this.cSchedule = cSchedule;
        this.cAdmin = cAdmin;

        this.previousStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        
        // 초기 상태: 로그인 화면
        this.currentPanel = PANEL_LOGIN; 

        // --- 1. 상단 헤더 리스너 ---
        this.vMain.getMenuToggleButton().addActionListener(e -> vMain.toggleSidebar());
        this.vMain.getBeforeButton().addActionListener(this::handlePrevious);
        this.vMain.getAfterButton().addActionListener(this::handleNext);
        this.vMain.getRefreshButton().addActionListener(this::handleRefresh);
        this.vMain.getLogoutButton().addActionListener(this::handleLogout);
        
        // --- 2. 사이드바 메뉴 리스너 ---
        // 강좌 검색
        this.vMain.getBtnSideSearch().addActionListener(e -> { 
            cSearch.setMode("REGISTER"); 
            navigateTo(PANEL_SEARCH); 
        });
        
        // 수강신청 내역
        this.vMain.getBtnSideRegister().addActionListener(e -> {
        	cSearch.setMode("REGISTER");
        	navigateTo(PANEL_REGISTER);
        	});
        
        // 미리담기 내역
        this.vMain.getBtnSidePreRegister().addActionListener(e -> { 
        	cSearch.setMode("PREREGISTER");
        	navigateTo(PANEL_PREREGISTER);
        });
        
        // 관리자 모드
        this.vMain.getBtnSideAdmin().addActionListener(e -> {
            this.cAdmin.showAdminDialog(); 
        });
        
        // 시간표 (팝업)
        this.vMain.getBtnSideTimeTable().addActionListener(e -> this.cSchedule.showSchedule());
        
        // 내 정보 (팝업)
        this.vMain.getBtnSideMyInfo().addActionListener(this::handleMyInfo);
        
        // 테마 변경 (토글)
        this.vMain.getBtnSideTheme().addActionListener(this::handleThemeChange);

        updateNavigationButtons();
    }

    /**
     * 로그인 성공 시 호출되어 네비게이션을 초기화하고 홈 화면으로 이동합니다.
     */
    public void resetNavigation(String panelName) {
        previousStack.clear();
        forwardStack.clear();
        
        currentPanel = panelName;
        vMain.contentPanel(panelName);
        
        updateNavigationButtons();
    }
    // 관리자 모드 버튼 표시/숨김
    public void setAdminMode(boolean isAdmin) {
        vMain.getBtnSideAdmin().setVisible(isAdmin);
    }
    
    /**
     * 패널 이동 및 데이터 로드 로직
     */
    private void navigateTo(String panelName) {
        if (panelName.equals(PANEL_LOGIN)) return;
        
        if (!panelName.equals(currentPanel)) {
        	if(!PANEL_LOGIN.equals(currentPanel)) {
        		previousStack.push(currentPanel);
        	}
            currentPanel = panelName;
            vMain.contentPanel(panelName);
            forwardStack.clear();
            updateNavigationButtons();
        }
        
        // 패널별 데이터 새로고침
        switch (panelName) {
            case PANEL_REGISTER:
                refreshRegisterPanel();
                break;
            case PANEL_PREREGISTER:
                refreshPreRegisterPanel();
                break;
                
            case PANEL_ADMIN:
				cAdmin.loadAllLectures();
				break;
            // 검색 패널은 조회 버튼이 있으므로 자동 로드 생략
            default: break;
        }
    }

    // --- 핸들러 메서드 ---

    private void handlePrevious(ActionEvent e) {
        if (!previousStack.isEmpty()) {
            forwardStack.push(currentPanel);
            currentPanel = previousStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    private void handleNext(ActionEvent e) {
        if (!forwardStack.isEmpty()) {
            previousStack.push(currentPanel);
            currentPanel = forwardStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    private void handleRefresh(ActionEvent e) {
        switch (currentPanel) {
            case PANEL_REGISTER:
                refreshRegisterPanel();
                break;
            case PANEL_PREREGISTER:
                refreshPreRegisterPanel();
                break;
            case PANEL_SEARCH:
                this.cSearch.refreshSearch();
                break;
            default: break;
        }
    }

    public void handleLogout(ActionEvent e) {
        mMain.setCurrentUserId(null);
        vMain.setMyNameLabel(""); // 이름 지우기
        setAdminMode(false);
        // 창 크기 복구
        vMain.setSize(420, 320);
        vMain.setLocationRelativeTo(null);
        
        // 로그인 화면으로 이동
        vMain.contentPanel(PANEL_LOGIN);
        
        // 히스토리 초기화
        previousStack.clear();
        forwardStack.clear();
        updateNavigationButtons();
        currentPanel = PANEL_LOGIN;
    }
    
    private void handleThemeChange(ActionEvent e) {
        try {
            if (FlatLaf.isLafDark()) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            FlatLaf.updateUI();
            // 필요 시 vMain 갱신
            SwingUtilities.updateComponentTreeUI(vMain);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vMain, "테마 변경 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMyInfo(ActionEvent e) {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        
        MUser user = userDAO.getUserInfo(userId);
        
        if (user != null) {
            String message = "<html><body style='width: 200px'>" +
                             "<h2>내 정보</h2><hr>" +
                             "<b>이름:</b> " + user.getName() + "<br>" +
                             "<b>학번:</b> " + user.getCode() + "<br>" +
                             "<b>ID:</b> " + user.getUserid() + "<br>" +
                             "<b>이메일:</b> " + user.getEmail() + "<br><br>" +
                             "<b>소속:</b><br>" +
                             user.getCampus() + " / " + user.getCollege() + "<br>" +
                             user.getDepartment() +
                             "</body></html>";
            JOptionPane.showMessageDialog(vMain, message, "학적 사항", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // 로그인 시 이름 표시 업데이트 (외부 호출용)
    public void refreshUserInfo() {
        String userId = mMain.getCurrentUserId();
        if (userId != null) {
            MUser user = userDAO.getUserInfo(userId);
            if (user != null) {
                vMain.setMyNameLabel(user.getName());
            }
        } else {
            vMain.setMyNameLabel(null);
        }
    }

    // --- Helper Methods ---
    
    private void refreshRegisterPanel() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        this.cRegister.refreshTable();
    }

    private void refreshPreRegisterPanel() {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        this.cPreRegister.refreshTable();
    }

    private void updateNavigationButtons() {
        vMain.getBeforeButton().setEnabled(!previousStack.isEmpty());
        vMain.getAfterButton().setEnabled(!forwardStack.isEmpty());
    }
}