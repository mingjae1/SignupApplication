package signup.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox; // [추가]
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder; // 여백용
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;


/**
 * 프로그램의 메인 프레임(JFrame)입니다.
 * [수정] 툴바를 좌/우로 분리하고 테마 선택 기능을 추가했습니다.
 */
public class VMain extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
    private CardLayout cardLayout;
    private JPanel mainCardPanel; 
    
    private JPanel mainContentPanel; 
    private CardLayout contentCardLayout; 
    private JPanel panel; 

    // --- 툴바 컴포넌트 ---
    private JButton searchbt;
    private JButton registerbt;
    private JButton preRegisterbt;
    private JButton scheduleButton;
    
    private JButton beforeButton;
    private JButton afterButton;
    private JButton refreshButton;
    
    private JComboBox<String> themeCombo; // [추가] 테마 선택 콤보박스
    private JButton btnLogout; 
    private JButton myinfoButton;
    private JLabel mynameLable;

    /**
     * VMain 프레임 및 내부 컴포넌트들을 생성하고 초기화합니다.
     */
    public VMain() {
        setTitle("수강신청 프로그램");
        setSize(1220, 873); // (기본 크기 조정)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainContentPanel = new JPanel(new BorderLayout());
        
        // --- [툴바 섹션] ---
        // 전체 툴바를 감싸는 컨테이너 (BorderLayout 사용)
        JPanel toolbarContainer = new JPanel(new BorderLayout());
        toolbarContainer.setBorder(new EmptyBorder(5, 5, 5, 5)); // 여백 추가

        // 1. 왼쪽 (네비게이션 & 제어)
        JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchbt = new JButton("강좌 검색");
        registerbt = new JButton("수강신청 내역");
        preRegisterbt = new JButton("미리담기 내역");
        btnLogout = new JButton("로그아웃");
        
        beforeButton = new JButton("◀");
        leftToolbar.add(beforeButton);
        afterButton = new JButton("▶");
        leftToolbar.add(afterButton);
        refreshButton = new JButton("새로고침");
        leftToolbar.add(refreshButton);
        
        leftToolbar.add(searchbt);
        leftToolbar.add(registerbt);
        leftToolbar.add(preRegisterbt);

        // 2. 오른쪽 (설정 & 계정)
        JPanel rightToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        scheduleButton = new JButton("시간표");
        rightToolbar.add(scheduleButton);
        
        myinfoButton = new JButton("내 정보");
        rightToolbar.add(myinfoButton);
        
                // 구분용 공백 라벨
                JLabel spacer = new JLabel(" | ");
                rightToolbar.add(spacer);
        
        mynameLable = new JLabel("님");
        rightToolbar.add(mynameLable);
        rightToolbar.add(btnLogout);
        
        JPanel centerToolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JLabel label = new JLabel("테마: ");
        centerToolbar.add(label);

        // 테마 선택 콤보박스
        themeCombo = new JComboBox<>(new String[]{"다크 테마", "라이트 테마"});
        themeCombo.setFocusable(false);
        centerToolbar.add(themeCombo);
        
        // 좌/우 패널을 컨테이너에 부착
        toolbarContainer.add(leftToolbar, BorderLayout.WEST);
        toolbarContainer.add(rightToolbar, BorderLayout.EAST);
        toolbarContainer.add(centerToolbar, BorderLayout.CENTER);

        mainContentPanel.add(toolbarContainer, BorderLayout.NORTH);
        // ------------------------

        // --- 메인 컨텐츠 하위 CardLayout 패널 ---
        contentCardLayout = new CardLayout();
        panel = new JPanel(contentCardLayout);
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainCardPanel.add(mainContentPanel, "mainContentPanel");
        
        getContentPane().add(mainCardPanel);
    }

    // ... (addPanel, getPanelForRegisterAndBasket, contentPanel 메서드 동일) ...
    public void addPanel(JPanel panel, String name) {
        mainCardPanel.add(panel, name);
    }

    public void contentPanel(String panelName) {
        if (panelName.equals("registerPanel") || panelName.equals("preregisterPanel") || 
            panelName.equals("searchPanel") || panelName.equals("schedulePanel")) {
            cardLayout.show(mainCardPanel, "mainContentPanel");
            contentCardLayout.show(panel, panelName);
        } 
        else { cardLayout.show(mainCardPanel, panelName); }
    }

    public void setMyNameLabel(String name) {
		mynameLable.setText(name + "님");
	}
    
    // --- Getters ---
    public JButton getSearchbt() { return searchbt; }
    public JButton getRegisterbt() { return registerbt; }
    public JButton getPreRegisterbt() { return preRegisterbt; }
    public JButton getScheduleButton() { return scheduleButton; }
    public JButton getBeforeButton() { return beforeButton; }
    public JButton getAfterButton() { return afterButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getLogoutButton() { return btnLogout; }
    public JButton getMyinfoButton() { return myinfoButton; }
    public JPanel getPanel() { return panel; }
    public JComboBox<String> getThemeCombo() { return themeCombo; }
}