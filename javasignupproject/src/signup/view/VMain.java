package signup.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * 프로그램의 메인 프레임(JFrame) 클래스입니다.
 * 상단 헤더(네비게이션, 계정)와 좌측 사이드바(메뉴), 중앙 컨텐츠 패널로 구성됩니다.
 */
public class VMain extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // --- 레이아웃 및 패널 ---
    private CardLayout cardLayout;          // 전체 화면 전환 (로그인 <-> 메인)
    private JPanel mainCardPanel;           // 전체를 담는 컨테이너
    private JPanel mainContentPanel;        // 로그인 성공 후 보여지는 메인 화면
    private CardLayout contentCardLayout;   // 메인 화면 내부 컨텐츠 전환 (검색/신청/담기)
    private JPanel contentPanel;            // 실제 기능 패널들이 들어가는 곳

    // --- 상단 헤더 컴포넌트 ---
    private JButton btnMenuToggle; // [≡] 사이드바 토글
    private JButton btnBack;       // [◀] 이전
    private JButton btnNext;       // [▶] 다음
    private JButton btnRefresh;    // [새로고침]
    
    private JLabel lblUserName;    // "OOO님"
    private JButton btnLogout;     // [로그아웃]
    
    // --- 좌측 사이드바 컴포넌트 ---
    private JPanel sidebarPanel;
    private JButton btnSideSearch;    // 강좌 검색
    private JButton btnSideRegister;  // 수강신청 내역
    private JButton btnSidePreRegister;    // 미리담기 내역
    private JButton btnSideTimeTable; // 시간표
    private JButton btnSideMyInfo;    // 내 정보
    private JButton btnSideTheme;     // 테마 변경
    private JButton btnSideAdmin;     // 강의 관리 (관리자 전용)
    
    // (참고: 테마 변경용 콤보박스는 사이드바 버튼 토글 방식으로 대체됨)
    private JComboBox<String> themeCombo; 
    
    private static final String fontSansSerif = "SansSerif";
    
    /**
     * VMain 프레임 및 내부 컴포넌트를 생성하고 레이아웃을 초기화합니다.
     */
    public VMain() {
        setTitle("수강신청 프로그램");
        setSize(420, 320); // 넉넉한 해상도
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 시작
        
        // 1. 메인 레이아웃 설정
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        
        // 2. 메인 컨텐츠 패널 (로그인 후 화면)
        mainContentPanel = new JPanel(new BorderLayout());
        
        // --- 상단 헤더 초기화 ---
        initHeader();
        
        // --- 좌측 사이드바 초기화 ---
        initSidebar();
        
        // --- 중앙 컨텐츠 영역 초기화 ---
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        mainCardPanel.add(mainContentPanel, "mainContentPanel");
        
        // 3. 프레임에 메인 패널 추가
        this.add(mainCardPanel);
        
        // (컨트롤러 호환용 히든 콤보박스 - 실제로는 안 보임)
        themeCombo = new JComboBox<>(new String[]{"다크 테마", "라이트 테마"});
    }
    
    /**
     * 상단 헤더(네비게이션 버튼 + 유저 정보)를 생성하고 배치합니다.
     */
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        
        // A. 좌측: 메뉴 토글 및 네비게이션
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        btnMenuToggle = new JButton("☰");
        btnMenuToggle.setFont(new Font(fontSansSerif, Font.BOLD, 18));
        btnMenuToggle.setFocusPainted(false);
        
        btnBack = new JButton("◀");
        btnNext = new JButton("▶");
        btnRefresh = new JButton("새로고침");
        
        leftHeader.add(btnMenuToggle);
        leftHeader.add(new JLabel("  |  ")); 
        leftHeader.add(btnBack);
        leftHeader.add(btnNext);
        leftHeader.add(btnRefresh);
        
        // B. 우측: 유저 정보 및 로그아웃
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        lblUserName = new JLabel("");
        lblUserName.setFont(new Font(fontSansSerif, Font.BOLD, 14));
        btnLogout = new JButton("로그아웃");
        
        rightHeader.add(lblUserName);
        rightHeader.add(new JLabel("  "));
        rightHeader.add(btnLogout);
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        // 헤더 하단 구분선
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 0, 0, 0),
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)
        ));
        
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * 좌측 사이드바(메뉴 리스트)를 생성하고 배치합니다.
     */
    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY)); // 오른쪽 구분선
        
        // 메뉴 버튼 컨테이너 (GridLayout)
        JPanel menuContainer = new JPanel(new GridLayout(0, 1, 0, 5));
        menuContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 버튼 생성
        btnSideSearch = createSidebarButton("🔍  강좌 검색");
        btnSideRegister = createSidebarButton("📝  수강신청 내역");
        btnSidePreRegister = createSidebarButton("🛒  미리담기 내역");
        btnSideTimeTable = createSidebarButton("📅  시간표");
        btnSideMyInfo = createSidebarButton("👤  내 정보");
        btnSideTheme = createSidebarButton("🌗  테마 변경");
        btnSideAdmin = createSidebarButton("⚙️  강의 관리");
        btnSideAdmin.setVisible(false);
        
        menuContainer.add(btnSideSearch);
        menuContainer.add(btnSideRegister);
        menuContainer.add(btnSidePreRegister);
        menuContainer.add(btnSideTimeTable);
        menuContainer.add(btnSideMyInfo);
        menuContainer.add(new JLabel(" ")); // 공백
        menuContainer.add(btnSideTheme);
        menuContainer.add(new JLabel(" ")); // 공백
        menuContainer.add(btnSideAdmin);
        // 위쪽 정렬을 위해 상단에 배치
        sidebarPanel.add(menuContainer, BorderLayout.NORTH);

        mainContentPanel.add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * 사이드바 버튼 스타일을 적용하는 헬퍼 메서드
     */
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font(fontSansSerif, Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setFocusPainted(false);
        return btn;
    }
    
    /**
     * 사이드바의 표시/숨김 상태를 토글합니다.
     */
    public void toggleSidebar() {
        sidebarPanel.setVisible(!sidebarPanel.isVisible());
    }

    /**
     * 외부 패널(로그인 등)을 메인 카드 레이아웃에 추가합니다.
     */
    public void addPanel(JPanel panel, String name) {
        mainCardPanel.add(panel, name);
    }

    /**
     * 내부 컨텐츠 패널 조립용 Getter
     */
    public JPanel getPanel() { 
        return contentPanel; 
    }

    /**
     * 화면 전환 메서드
     */
    public void contentPanel(String panelName) {
        if (panelName.equals("registerPanel") || panelName.equals("preRegisterPanel") || 
            panelName.equals("searchPanel") || panelName.equals("schedulePanel") || panelName.equals("adminPanel")) {
            cardLayout.show(mainCardPanel, "mainContentPanel");
            contentCardLayout.show(contentPanel, panelName);
        } else {
            cardLayout.show(mainCardPanel, panelName);
        }
    }

    /**
     * 사용자 이름 표시 업데이트
     */
    public void setMyNameLabel(String name) {
        lblUserName.setText(name == null ? "" : name + "님");
    }
    
    // --- Getters ---
    public JButton getMenuToggleButton() { return btnMenuToggle; }
    public JButton getBeforeButton() { return btnBack; }
    public JButton getAfterButton() { return btnNext; }
    public JButton getRefreshButton() { return btnRefresh; }
    public JButton getLogoutButton() { return btnLogout; }

    public JButton getBtnSideSearch() { return btnSideSearch; }
    public JButton getBtnSideRegister() { return btnSideRegister; }
    public JButton getBtnSidePreRegister() { return btnSidePreRegister; }
    public JButton getBtnSideTimeTable() { return btnSideTimeTable; }
    public JButton getBtnSideMyInfo() { return btnSideMyInfo; }
    public JButton getBtnSideTheme() { return btnSideTheme; }
    public JButton getBtnSideAdmin() { return btnSideAdmin; }
    
    // (호환용)
    public JComboBox<String> getThemeCombo() { return themeCombo; }
    public JButton getMyInfoButton() { return btnSideMyInfo; }
    public JButton getSearchbt() { return btnSideSearch; }
    public JButton getRegisterbt() { return btnSideRegister; }
    public JButton getPreRegisterbt() { return btnSidePreRegister; }
    public JButton getScheduleButton() { return btnSideTimeTable; }
}