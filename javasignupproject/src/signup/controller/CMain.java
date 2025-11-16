package signup.controller;

import javax.swing.JOptionPane;

import signup.model.MMain;
import signup.view.VMain;

import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * VMain(메인 뷰)의 툴바 기능(이전, 다음, 새로고침, 패널 전환)을 제어하는 컨트롤러입니다.
 * CardLayout의 패널 전환 히스토리(History)를 스택으로 관리합니다.
 */
public class CMain {

    // 뷰
    private VMain vMain;
    
    // 모델
    private MMain mMain;
    
    
    // CMain이 제어해야 할 다른 컨트롤러
    private CSearch cSearch; 
    private CRegister cRegister;
    private CPreRegister cPreRegister;

    // 네비게이션 히스토리 관리용 스택
    private Deque<String> previousStack;
    private Deque<String> forwardStack; 
    private String currentPanel;
    
    // CardLayout 패널 이름 상수
    private static final String PANEL_REGISTER = "registerPanel";
    private static final String PANEL_PREREGISTER = "preregisterPanel";
    private static final String PANEL_SEARCH = "searchPanel"; 

    /**
     * CMain 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO 및 CSearch 컨트롤러를 주입받습니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param mMain 현재 로그인한 사용자 ID를 가져올 메인 모델 (MMain)
     * @param saveDAO 수강/미리담기 내역을 처리할 SaveDAO
     * @param cSearch '새로고침' 시 제어할 CSearch 컨트롤러
     * @param vPreRegister 수강신청 목록화면
     * @param vRegister 미리담기 목록화면
     */
    public CMain(VMain vMain, MMain mMain, CSearch cSearch, CRegister cRegister, CPreRegister cPreRegister) {
        this.vMain = vMain;
        this.mMain = mMain;
        this.cSearch = cSearch;
        this.cRegister = cRegister;
        this.cPreRegister = cPreRegister;

        this.previousStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        
        // 로그인 성공 시 CLogin이 'searchPanel'로 이동시키므로, 초기 패널을 'searchPanel'로 설정
        this.currentPanel = PANEL_SEARCH; 

        // 툴바 버튼 리스너 연결
        this.vMain.getSearchbt().addActionListener(e -> { cSearch.setMode("REGISTER"); navigateTo(PANEL_SEARCH); });
        this.vMain.getRegisterbt().addActionListener(e -> navigateTo(PANEL_REGISTER));
        this.vMain.getPreRegisterbt().addActionListener(e -> navigateTo(PANEL_PREREGISTER));
        
        this.vMain.getBeforeButton().addActionListener(this::handlePrevious);
        this.vMain.getAfterButton().addActionListener(this::handleNext);
		this.vMain.getRefreshButton().addActionListener(this::handleRefresh);
		this.vMain.getLogoutButton().addActionListener(this::handleLogout);
		
        updateNavigationButtons();
    }

    /**
     * 툴바 버튼 클릭 시, 해당 패널로 이동하고 데이터를 즉시 로드합니다.
     * @param panelName VMain에 보여줄 패널의 이름
     */
    private void navigateTo(String panelName) {
        // 1. 패널 이동 로직 (현재 패널과 다른 패널을 눌렀을 때만 히스토리 기록)
        if (!panelName.equals(currentPanel)) {
            previousStack.push(currentPanel);
            currentPanel = panelName;
            vMain.contentPanel(panelName); // 뷰(VMain)에 화면 전환 요청
            forwardStack.clear(); // '다음' 스택은 초기화
            updateNavigationButtons();
        }
        
        // 2. 패널 이동 후 즉시 데이터 로드
        // (같은 패널 버튼을 다시 눌러도 새로고침되도록 if문 밖에 위치)
        switch (panelName) {
            case PANEL_REGISTER:
                refreshRegisterPanel();
                break;
            case PANEL_PREREGISTER:
                refreshPreRegisterPanel();
                break;
            default :	
                // VSearch는 자체 '조회' 버튼이 있으므로, 툴바 버튼 클릭 시
                // 강제로 새로고침하지 않고 화면만 보여줍니다.
                break;
        }
    }

    /**
     * '이전' 버튼 클릭을 처리합니다. (데이터 로드 없이 화면만 전환)
     */
    private void handlePrevious(ActionEvent e) {
        if (!previousStack.isEmpty()) {
            forwardStack.push(currentPanel); // 현재 패널을 '다음' 스택에 추가
            currentPanel = previousStack.pop(); // '이전' 스택에서 패널 가져오기
            vMain.contentPanel(currentPanel); 
            updateNavigationButtons();
        }
    }

    /**
     * '다음' 버튼 클릭을 처리합니다. (데이터 로드 없이 화면만 전환)
     */
    private void handleNext(ActionEvent e) {
        if (!forwardStack.isEmpty()) {
            previousStack.push(currentPanel); // 현재 패널을 '이전' 스택에 추가
            currentPanel = forwardStack.pop(); // '다음' 스택에서 패널 가져오기
            vMain.contentPanel(currentPanel); 
            updateNavigationButtons();
        }
    }

    /**
     * '새로고침' 버튼 클릭을 처리합니다.
     * 현재 활성화된 패널에 따라 적절한 데이터 로드 메서드를 호출합니다.
     */
    private void handleRefresh(ActionEvent e) {
        switch (currentPanel) {
            case PANEL_REGISTER:
                refreshRegisterPanel();
                break;
            case PANEL_PREREGISTER:
                refreshPreRegisterPanel();
                break;
            case PANEL_SEARCH:
                // VSearch의 "조회" 버튼을 프로그래밍적으로 클릭하여 새로고침
            	this.cSearch.refreshSearch(); 
                break;
            default:
                JOptionPane.showMessageDialog(vMain, "'" + currentPanel + "' 패널 새로고침 (미구현)");
                break;
        }
    }
    
    public void handleLogout(ActionEvent e) {
    	// 1. MMain 모델의 현재 사용자 ID를 null로 초기화
        mMain.setCurrentUserId(null);
        
        // 2. VMain(프레임)의 창 크기를 원래 로그인 창 크기로 복구
        vMain.setSize(380, 280);
        vMain.setLocationRelativeTo(null); // 화면 중앙 정렬
        
        // 3. 메인 뷰(CardLayout)를 "loginPanel"로 전환
        vMain.contentPanel("loginPanel");
        
        // (선택사항) 히스토리 스택 초기화
        previousStack.clear();
        forwardStack.clear();
        updateNavigationButtons();
        currentPanel = "loginPanel"; // 현재 패널 상태도 업데이트
	}
    
    
    
    // --- 데이터 로딩 헬퍼(Helper) 메서드 ---

    /**
     * '수강신청 내역' 패널의 데이터를 새로고침합니다.
     */
    private void refreshRegisterPanel() {
        String currentUserId = mMain.getCurrentUserId();
        if (currentUserId == null) {
            JOptionPane.showMessageDialog(vMain, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // CRegister에게 새로고침을 '요청'합니다.
        this.cRegister.refreshTable();
    }

    /**
     * '미리담기 내역' 패널의 데이터를 새로고침합니다.
     */
    private void refreshPreRegisterPanel() {
        String currentUserId = mMain.getCurrentUserId();
        if (currentUserId == null) {
            JOptionPane.showMessageDialog(vMain, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // CPreRegister에게 새로고침을 '요청'합니다.
        this.cPreRegister.refreshTable();
    }

    /**
     * '이전' 및 '다음' 버튼의 활성화/비활성화 상태를 스택 크기에 따라 업데이트합니다.
     */
    private void updateNavigationButtons() {
        vMain.getBeforeButton().setEnabled(!previousStack.isEmpty());
        vMain.getAfterButton().setEnabled(!forwardStack.isEmpty());
    }
}