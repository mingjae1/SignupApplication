package signup.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

/**
 * 프로그램의 메인 프레임(JFrame)입니다.
 * CardLayout을 사용하여 로그인, 회원가입, 메인 컨텐츠 패널을 전환합니다.
 */
public class VMain extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	// --- 뷰 컴포넌트 ---
    private CardLayout cardLayout;
    private JPanel mainCardPanel; // RMain이 VLogin/VSignup을 추가할 메인 카드 패널
    
    private JPanel mainContentPanel; // 로그인 후 보여줄 메인 화면 (툴바 + 하위 패널)
    private CardLayout contentCardLayout; // 메인 화면 내부의 카드 레이아웃
    private JPanel controlpanel; // VSearch, VRegister, VBasket이 들어갈 패널

    // --- 툴바 버튼 ---
    private JButton searchbt; // [추가됨] 강좌 검색 버튼
    private JButton registerbt;
    private JButton preregisterbt;
    private JButton beforeButton;
    private JButton afterButton;
    private JButton refreshButton;

    /**
     * VMain 프레임 및 내부 컴포넌트들을 생성하고 초기화합니다.
     */
    public VMain() {
        // JFrame 기본 설정
        setTitle("수강신청 프로그램");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        
        mainContentPanel = new JPanel(new BorderLayout());
        
        // --- 툴바 생성 ---
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchbt = new JButton("강좌 검색"); // [추가됨]
        registerbt = new JButton("수강신청 내역"); // (이름 명확하게 변경)
        preregisterbt = new JButton("미리담기 내역"); // (이름 명확하게 변경)
        beforeButton = new JButton("이전");
        afterButton = new JButton("다음");
        refreshButton = new JButton("새로고침");
        
        toolbarPanel.add(searchbt); // [추가됨]
        toolbarPanel.add(registerbt);
        toolbarPanel.add(preregisterbt);
        toolbarPanel.add(beforeButton);
        toolbarPanel.add(afterButton);
        toolbarPanel.add(refreshButton);
        
        mainContentPanel.add(toolbarPanel, BorderLayout.NORTH);

        // --- 메인 컨텐츠 하위 CardLayout 패널 ---
        contentCardLayout = new CardLayout();
        controlpanel = new JPanel(contentCardLayout);
        
        mainContentPanel.add(controlpanel, BorderLayout.CENTER);

        // RMain이 사용할 메인 CardLayout에 "mainContentPanel" 추가
        mainCardPanel.add(mainContentPanel, "mainContentPanel");
        
        this.add(mainCardPanel);
    }

    /**
     * RMain이 VLogin, VSignup 같은 *외부* 패널을 조립할 때 호출합니다.
     */
    public void addPanel(JPanel panel, String name) {
        mainCardPanel.add(panel, name);
    }
    
    /**
     * [추가됨] RMain이 VSearch 같은 *내부* 컨텐츠 패널을 조립할 때 호출합니다.
     * @return VSearch, VRegister 등이 추가될 내부 CardLayout 패널
     */
    public JPanel getPanelForRegisterAndBasket() {
        return controlpanel;
    }

    /**
     * 컨트롤러가 화면 전환을 요청할 때 호출하는 메소드입니다.
     */
    public void contentPanel(String panelName) {
        // "registerPanel", "basketPanel", "searchPanel"은 내부 CardLayout을 사용
        if (panelName.equals("registerPanel") || panelName.equals("preregisterPanel") || panelName.equals("searchPanel")) {
            // 1. 메인 CardLayout을 "mainContentPanel"로 먼저 바꾼다.
            cardLayout.show(mainCardPanel, "mainContentPanel");
            // 2. 그 *후*에, 내부 CardLayout을 요청된 패널로 바꾼다.
            contentCardLayout.show(controlpanel, panelName);
        } else {
            // "loginPanel", "signupPanel" 등은 메인 CardLayout을 사용
            cardLayout.show(mainCardPanel, panelName);
        }
    }
 

    // --- Getters for Controller ---
    
    public JButton getSearchbt() { return searchbt; } // [추가됨]
    public JButton getRegisterbt() { return registerbt; }
    public JButton getPreRegisterbt() { return preregisterbt; }
    public JButton getBeforeButton() { return beforeButton; }
    public JButton getAfterButton() { return afterButton; }
    public JButton getRefreshButton() { return refreshButton; }
}