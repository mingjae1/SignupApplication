package signup.controller;

import javax.swing.JOptionPane;

import signup.model.MLecture; // 님의 DTO 클래스
import signup.model.MMain;
import signup.view.VMain;
import signup.dao.SaveDAO; // SaveDAO 임포트

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * VMain(메인 뷰)의 툴바 기능(이전, 다음, 새로고침, 패널 전환)을 제어하는 컨트롤러입니다.
 * CardLayout의 패널 전환 히스토리(History)를 스택으로 관리합니다.
 */
public class CMain {

    private VMain vMain; // VMain 뷰 (화면 제어용)
    private MMain mMain; // MMain 모델 (현재 로그인한 사용자 ID 접근용)
    
    private SaveDAO saveDAO; // DB 작업을 위한 SaveDAO

    // --- 네비게이션 히스토리 관리 ---
    private Deque<String> previousStack;
    private Deque<String> forwardStack; 
    private String currentPanel;
    
    // 패널 이름을 상수로 정의
    private static final String PANEL_REGISTER = "registerPanel";
    private static final String PANEL_BASKET = "basketPanel";
    private static final String PANEL_SEARCH = "searchPanel"; 

    /**
     * CMain 컨트롤러를 생성합니다.
     * RMain으로부터 뷰, 모델, DAO 객체를 주입받습니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param mMain 현재 로그인한 사용자 ID를 가져올 메인 모델 (MMain)
     * @param saveDAO 수강/미리담기 내역을 처리할 SaveDAO
     */
    public CMain(VMain vMain, MMain mMain, SaveDAO saveDAO) {
        this.vMain = vMain;
        this.mMain = mMain;
        this.saveDAO = saveDAO; // RMain으로부터 DAO를 주입받음

        this.previousStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        
        // CLogin이 로그인 성공 시 "searchPanel"로 이동시키므로
        
        this.currentPanel = PANEL_SEARCH; // 초기 패널 설정

        // 툴바 버튼 리스너 연결
        this.vMain.getSearchbt().addActionListener(e -> navigateTo(PANEL_SEARCH));
        this.vMain.getRegisterbt().addActionListener(e -> navigateTo(PANEL_REGISTER));
        this.vMain.getBasketbt().addActionListener(e -> navigateTo(PANEL_BASKET));
        this.vMain.getSearchbt().addActionListener(e -> navigateTo(PANEL_SEARCH)); // VSearch 버튼 리스너
        
        this.vMain.getBeforeButton().addActionListener(e -> handlePrevious());
        this.vMain.getAfterButton().addActionListener(e -> handleNext());
        this.vMain.getRefreshButton().addActionListener(e -> handleRefresh());

        updateNavigationButtons();
    }

    /**
     * 툴바 버튼 클릭 시, 해당 패널로 이동합니다.
     * 이 이동은 '이전' 히스토리에 기록되며, '다음' 히스토리는 초기화됩니다.
     * @param panelName VMain에 보여줄 패널의 이름
     */
    private void navigateTo(String panelName) {
        if (!panelName.equals(currentPanel)) {
            previousStack.push(currentPanel);
            currentPanel = panelName;
            vMain.contentPanel(panelName);
            forwardStack.clear();
            updateNavigationButtons();
        }
    }

    /**
     * '이전' 버튼 클릭을 처리합니다.
     */
    private void handlePrevious() {
        if (!previousStack.isEmpty()) {
            forwardStack.push(currentPanel);
            currentPanel = previousStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    /**
     * '다음' 버튼 클릭을 처리합니다.
     */
    private void handleNext() {
        if (!forwardStack.isEmpty()) {
            previousStack.push(currentPanel);
            currentPanel = forwardStack.pop();
            vMain.contentPanel(currentPanel);
            updateNavigationButtons();
        }
    }

    /**
     * '새로고침' 버튼 클릭을 처리합니다.
     * MMain에서 현재 사용자 ID를 가져온 뒤, SaveDAO를 통해 최신 목록을 DB에서 로드합니다.
     */
    private void handleRefresh() {
        
        String currentUserId = mMain.getCurrentUserId();
        
        if (currentUserId == null) {
            JOptionPane.showMessageDialog(vMain, "로그인이 필요합니다.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 필드에 주입된 saveDAO를 사용 (new SaveDAO() 삭제)
        
        switch (currentPanel) {
            case PANEL_REGISTER: // "registerPanel"
                List<MLecture> registeredData = this.saveDAO.getLecturesByStatus(currentUserId, "reg"); 
                vMain.updateRegisterPanel(registeredData);
                break;
                
            case PANEL_BASKET: // "basketPanel"
                List<MLecture> basketData = this.saveDAO.getLecturesByStatus(currentUserId, "pre");
                vMain.updateBasketPanel(basketData);
                break;
            
            case PANEL_SEARCH: // "searchPanel"
                JOptionPane.showMessageDialog(vMain, "검색 패널 새로고침 (미구현)");
                break;
                
            default:
                JOptionPane.showMessageDialog(vMain, "'" + currentPanel + "' 패널 새로고침 (미구현)");
                break;
        }
    }

    /**
     * '이전' 및 '다음' 버튼의 활성화 상태를 스택의 크기에 따라 업데이트합니다.
     */
    private void updateNavigationButtons() {
        vMain.getBeforeButton().setEnabled(!previousStack.isEmpty());
        vMain.getAfterButton().setEnabled(!forwardStack.isEmpty());
    }
}