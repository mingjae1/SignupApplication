package signup.controller;

import javax.swing.JOptionPane;

import signup.model.Lecture;
import signup.model.MMain;
import signup.view.VMain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * VMain(메인 뷰)의 툴바 기능(이전, 다음, 새로고침, 패널 전환)을 제어하는 컨트롤러입니다.
 * CardLayout의 패널 전환 히스토리(History)를 스택으로 관리합니다.
 */
public class CMain {

    private VMain vMain; // VMain 뷰 (화면 제어용)
    private MMain mMain; // MMain 모델 (데이터 접근용)

    // --- 네비게이션 히스토리 관리 ---
    private Deque<String> previousStack; // '이전' 패널 이름 스택
    private Deque<String> forwardStack;  // '다음' 패널 이름 스택
    private String currentPanel;         // 현재 활성화된 패널 이름

    /**
     * CMain 컨트롤러를 생성합니다.
     * 뷰와 모델을 연결하고, 툴바 버튼들에 대한 액션 리스너를 설정합니다.
     * @param vMain 제어할 메인 뷰 (VMain)
     * @param mMain 데이터를 가져올 메인 모델 (MMain)
     */
    public CMain(VMain vMain, MMain mMain) {
        this.vMain = vMain;
        this.mMain = mMain;

        this.previousStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
        
        // (주의) VMain의 초기 패널 이름과 일치해야 함
        this.currentPanel = "registerPanel"; 

        // 툴바 버튼 리스너 연결
        this.vMain.getRegisterbt().addActionListener(e -> navigateTo("registerPanel"));
        this.vMain.getBasketbt().addActionListener(e -> navigateTo("basketPanel"));
        
        this.vMain.getBeforeButton().addActionListener(e -> handlePrevious());
        this.vMain.getAfterButton().addActionListener(e -> handleNext());
        this.vMain.getRefreshButton().addActionListener(e -> handleRefresh());

        // '이전', '다음' 버튼 초기 비활성화
        updateNavigationButtons();
    }

    /**
     * '수강신청' 또는 '미리담기' 버튼 클릭 시, 해당 패널로 이동합니다.
     * 이 이동은 '이전' 히스토리에 기록되며, '다음' 히스토리는 초기화됩니다.
     * @param panelName VMain에 보여줄 패널의 이름 (e.g., "registerPanel")
     */
    private void navigateTo(String panelName) {
        if (!panelName.equals(currentPanel)) {
            previousStack.push(currentPanel); // 현재 패널을 '이전' 스택에 추가
            
            currentPanel = panelName; // 현재 패널 업데이트
            
            vMain.contentPanel(panelName); // 뷰(VMain)에 화면 전환 요청
            
            forwardStack.clear(); // '다음' 스택은 초기화 (새로운 이동이므로)
            
            updateNavigationButtons(); // 버튼 활성화/비활성화 업데이트
        }
    }

    /**
     * '이전' 버튼 클릭을 처리합니다.
     * '이전' 스택에서 패널을 가져와 보여주고, 현재 패널은 '다음' 스택에 추가합니다.
     */
    private void handlePrevious() {
        if (!previousStack.isEmpty()) {
            forwardStack.push(currentPanel); // 현재 패널을 '다음' 스택에 추가
            
            currentPanel = previousStack.pop(); // '이전' 스택에서 패널 가져오기
            
            vMain.contentPanel(currentPanel); // 화면 전환
            
            updateNavigationButtons();
        }
    }

    /**
     * '다음' 버튼 클릭을 처리합니다.
     * '다음' 스택에서 패널을 가져와 보여주고, 현재 패널은 '이전' 스택에 추가합니다.
     */
    private void handleNext() {
        if (!forwardStack.isEmpty()) {
            previousStack.push(currentPanel); // 현재 패널을 '이전' 스택에 추가
            
            currentPanel = forwardStack.pop(); // '다음' 스택에서 패널 가져오기
            
            vMain.contentPanel(currentPanel); // 화면 전환
            
            updateNavigationButtons();
        }
    }

    /**
     * '새로고침' 버튼 클릭을 처리합니다.
     * 현재 활성화된 패널(currentPanel)을 식별하여,
     * MMain으로부터 최신 데이터를 다시 로드하고 VMain의 테이블을 업데이트합니다.
     */
    private void handleRefresh() {
        switch (currentPanel) {
            case "registerPanel":
                List<Lecture> registeredData = mMain.getRegisteredLectures(); 
                vMain.updateRegisterPanel(registeredData);
                break;
                
            case "basketPanel":
                List<Lecture> basketData = mMain.getBasketLectures();
                vMain.updateBasketPanel(basketData);
                break;
                
            default:
                JOptionPane.showMessageDialog(vMain, "'" + currentPanel + "' 패널 새로고침");
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