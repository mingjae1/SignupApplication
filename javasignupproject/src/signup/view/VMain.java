package signup.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import signup.constants.PanelNames;
import signup.constants.ViewConstants;

/**
 * 프로그램의 메인 프레임(JFrame) 클래스입니다.
 * 상단 헤더(네비게이션, 계정)와 좌측 사이드바(메뉴), 중앙 컨텐츠 패널로 구성됩니다.
 */
public class VMain extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // --- 레이아웃 및 패널 ---
    private transient CardLayout cardLayout;          // 전체 화면 전환 (로그인 <-> 메인)
    private transient JPanel mainCardPanel;           // 전체를 담는 컨테이너
    private transient JPanel mainContentPanel;        // 로그인 성공 후 보여지는 메인 화면
    private transient CardLayout contentCardLayout;   // 메인 화면 내부 컨텐츠 전환 (검색/신청/담기)
    private transient JPanel contentPanel;            // 실제 기능 패널들이 들어가는 곳

    // --- 상단 헤더 컴포넌트 ---
    private transient JButton btnMenuToggle; // [≡] 사이드바 토글
    private transient JButton btnBack;       // [◀] 이전
    private transient JButton btnNext;       // [▶] 다음
    private transient JButton btnRefresh;    // [새로고침]
    
    private transient JLabel lblUserName;    // "OOO님"
    private transient JButton btnLogout;     // [로그아웃]
    
    // --- 좌측 사이드바 컴포넌트 ---
    private transient JPanel sidebarPanel;
    private transient JButton btnSideSearch;    // 강좌 검색
    private transient JButton btnSideRegister;  // 수강신청 내역
    private transient JButton btnSidePreRegister;    // 미리담기 내역
    private transient JButton btnSideTimeTable; // 시간표
    private transient JButton btnSideMyInfo;    // 내 정보
    private transient JButton btnSideClock;     // 시계
    private transient JButton btnSideTheme;     // 테마 변경
    private transient JButton btnSideAdmin;     // 강의 관리 (관리자 전용)
    
    // --- 시계 팝업 ---
    private transient JFrame clockFrame;
    private transient JLabel clockLabel;
    private transient JLabel dateLabel;
    private transient javax.swing.Timer clockTimer;
    
    // --- 컨트롤러 참조 (내 정보 다이얼로그에서 사용) ---
    private transient signup.controller.CMain cMain;
    
    // (참고: 테마 변경용 콤보박스는 사이드바 버튼 토글 방식으로 대체됨)
    private transient JComboBox<String> themeCombo;
    
    /**
     * VMain 프레임 및 내부 컴포넌트를 생성하고 레이아웃을 초기화합니다.
     */
    public VMain() {
        setTitle(ViewConstants.TEXT_PROGRAM_TITLE);
        setSize(420, 320);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainContentPanel = new JPanel(new BorderLayout());
        
        initHeader();
        initSidebar();
        
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        mainCardPanel.add(mainContentPanel, PanelNames.MAIN_CONTENT_PANEL);
        
        this.add(mainCardPanel);
        themeCombo = new JComboBox<>(new String[]{"다크 테마", "라이트 테마"});
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopClockTimer();
            }
        });
    }
    
    /**
     * 상단 헤더(네비게이션 버튼 + 유저 정보)를 생성하고 배치합니다.
     */
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(ViewConstants.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(ViewConstants.HEADER_SIZE);
        
        // 좌측: 메뉴 토글 및 네비게이션
        JPanel leftHeader = ViewConstants.createStandardPanel(
            ViewConstants.createFlowLayout(FlowLayout.LEFT, 10, 5));
        
        btnMenuToggle = new JButton(ViewConstants.TEXT_MENU_TOGGLE);
        btnMenuToggle.setFont(new Font(ViewConstants.FONT_SANS_SERIF, Font.BOLD, 18));
        btnMenuToggle.setFocusPainted(false);
        
        btnBack = ViewConstants.createHeaderButton(ViewConstants.TEXT_BACK);
        btnNext = ViewConstants.createHeaderButton(ViewConstants.TEXT_NEXT);
        btnRefresh = ViewConstants.createHeaderButton(ViewConstants.TEXT_REFRESH);
        
        leftHeader.add(btnMenuToggle);
        leftHeader.add(new JLabel(ViewConstants.TEXT_SEPARATOR));
        leftHeader.add(btnBack);
        leftHeader.add(btnNext);
        leftHeader.add(btnRefresh);
        
        // 우측: 유저 정보 및 로그아웃
        JPanel rightHeader = ViewConstants.createStandardPanel(
            ViewConstants.createFlowLayout(FlowLayout.RIGHT, 10, 5));
        lblUserName = ViewConstants.createHeaderLabel("");
        btnLogout = ViewConstants.createHeaderButton(ViewConstants.TEXT_LOGOUT);
        
        rightHeader.add(lblUserName);
        rightHeader.add(new JLabel(ViewConstants.TEXT_SPACE));
        rightHeader.add(btnLogout);
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        headerPanel.setBorder(ViewConstants.createSeparatorBorder());
        
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * 좌측 사이드바(메뉴 리스트)를 생성하고 배치합니다.
     */
    private void initSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(ViewConstants.SIDEBAR_SIZE);
        sidebarPanel.setBorder(ViewConstants.createVerticalSeparatorBorder());
        
        JPanel menuContainer = ViewConstants.createStandardPanel(
            ViewConstants.createGridLayout(0, 1, 0, 5));
        menuContainer.setBorder(ViewConstants.createEmptyBorder(10, 10, 10, 10));
        
        btnSideSearch = ViewConstants.createSidebarButton(ViewConstants.TEXT_SEARCH);
        btnSideRegister = ViewConstants.createSidebarButton(ViewConstants.TEXT_REGISTER);
        btnSidePreRegister = ViewConstants.createSidebarButton(ViewConstants.TEXT_PREREGISTER);
        btnSideTimeTable = ViewConstants.createSidebarButton(ViewConstants.TEXT_TIMETABLE);
        btnSideMyInfo = ViewConstants.createSidebarButton(ViewConstants.TEXT_MY_INFO);
        btnSideClock = ViewConstants.createSidebarButton(ViewConstants.TEXT_CLOCK);
        btnSideTheme = ViewConstants.createSidebarButton(ViewConstants.TEXT_THEME);
        btnSideAdmin = ViewConstants.createSidebarButton(ViewConstants.TEXT_ADMIN);
        btnSideAdmin.setVisible(false);
        
        menuContainer.add(btnSideSearch);
        menuContainer.add(btnSideRegister);
        menuContainer.add(btnSidePreRegister);
        menuContainer.add(btnSideTimeTable);
        menuContainer.add(btnSideMyInfo);
        menuContainer.add(new JLabel(ViewConstants.TEXT_SPACE));
        menuContainer.add(btnSideClock);
        menuContainer.add(btnSideTheme);
        menuContainer.add(new JLabel(ViewConstants.TEXT_SPACE));
        menuContainer.add(btnSideAdmin);
        
        sidebarPanel.add(menuContainer, BorderLayout.NORTH);
        mainContentPanel.add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * 사이드바의 표시/숨김 상태를 토글합니다.
     */
    public void toggleSidebar() {
        sidebarPanel.setVisible(!sidebarPanel.isVisible());
    }
    
    /**
     * 시계 팝업을 초기화합니다.
     */
    public void initClockPopup() {
        if (clockFrame != null) {
            return;
        }
        
        clockFrame = new JFrame(ViewConstants.TEXT_CLOCK_TITLE);
        clockFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        clockFrame.setResizable(false);
        clockFrame.setAlwaysOnTop(true);
        clockFrame.setLayout(new BorderLayout());
        
        clockLabel = new JLabel();
        clockLabel.setFont(new Font(ViewConstants.FONT_DIGITAL, Font.BOLD, ViewConstants.FONT_SIZE_CLOCK));
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clockLabel.setVerticalAlignment(SwingConstants.CENTER);
        clockLabel.setBorder(ViewConstants.createEmptyBorder(
            ViewConstants.PADDING_LARGE, ViewConstants.PADDING_LARGE, 
            ViewConstants.PADDING_LARGE, ViewConstants.PADDING_LARGE));
        clockLabel.setForeground(ViewConstants.COLOR_PRIMARY);
        
        dateLabel = new JLabel();
        dateLabel.setFont(new Font(ViewConstants.FONT_SANS_SERIF, Font.PLAIN, ViewConstants.FONT_SIZE_DATE));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setBorder(ViewConstants.createEmptyBorder(
            ViewConstants.PADDING_MEDIUM, ViewConstants.PADDING_MEDIUM, 
            ViewConstants.PADDING_MEDIUM, ViewConstants.PADDING_MEDIUM));
        dateLabel.setForeground(ViewConstants.COLOR_SECONDARY);
        
        JPanel panel = ViewConstants.createStandardPanel(new BorderLayout());
        updateClockTheme(panel);
        panel.add(clockLabel, BorderLayout.CENTER);
        panel.add(dateLabel, BorderLayout.SOUTH);
        
        clockFrame.add(panel);
        clockFrame.setSize(ViewConstants.CLOCK_POPUP_SIZE);
        
        int x = getX() + getWidth() - clockFrame.getWidth() - ViewConstants.CLOCK_X_OFFSET;
        int y = getY() + ViewConstants.CLOCK_Y_OFFSET;
        clockFrame.setLocation(x, y);
        
        clockTimer = new Timer(ViewConstants.CLOCK_UPDATE_INTERVAL, e -> updateClockDisplay());
        updateClockDisplay();
        clockTimer.start();
    }
    
    /**
     * 시계 표시를 업데이트합니다.
     */
    private void updateClockDisplay() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(ViewConstants.DATE_FORMAT_TIME);
        SimpleDateFormat dateFormat = new SimpleDateFormat(ViewConstants.DATE_FORMAT_FULL);
        
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());
        
        if (clockLabel != null) {
            clockLabel.setText(currentTime);
        }
        if (dateLabel != null) {
            dateLabel.setText(currentDate);
        }
    }
    
    /**
     * 시계 팝업을 토글합니다.
     */
    public void toggleClockPopup() {
        if (clockFrame == null) {
            initClockPopup();
        }
        clockFrame.setVisible(!clockFrame.isVisible());
    }
    
    /**
     * 시계 타이머를 중지합니다.
     */
    public void stopClockTimer() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        if (clockFrame != null) {
            clockFrame.dispose();
        }
    }
    
    /**
     * 시계 팝업을 테마 변경에 따라 업데이트합니다.
     */
    public void refreshClockTheme() {
        if (clockFrame == null || !clockFrame.isVisible()) {
            return; // 시계가 표시되지 않으면 업데이트 불필요
        }
        
        // 현재 시계 프레임의 위치 저장
        int x = clockFrame.getX();
        int y = clockFrame.getY();
        
        // 시계 팝업 재생성
        if (clockFrame != null) {
            clockFrame.dispose();
            clockFrame = null;
        }
        
        initClockPopup();
        clockFrame.setLocation(x, y);
        clockFrame.setVisible(true);
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
        if (panelName.equals(PanelNames.REGISTER_PANEL) || panelName.equals(PanelNames.PREREGISTER_PANEL) || 
            panelName.equals(PanelNames.SEARCH_PANEL) || panelName.equals(PanelNames.SCHEDULE_PANEL) || panelName.equals(PanelNames.ADMIN_PANEL)) {
            cardLayout.show(mainCardPanel, PanelNames.MAIN_CONTENT_PANEL);
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
    
    /**
     * 사용자 정보 표시 다이얼로그를 보여줍니다.
     */
    public void showUserInfoDialog(String name, int code, String userid, String email, 
                                    String campus, String college, String department) {
        String infoHtml = "<html><body style='width: 280px'>" +
                          "<h2>내 정보</h2><hr>" +
                          "<b>이름:</b> " + name + "<br>" +
                          "<b>학번:</b> " + code + "<br>" +
                          "<b>ID:</b> " + userid + "<br>" +
                          "<b>이메일:</b> " + email + "<br><br>" +
                          "<b>소속:</b><br>" + campus + " / " + college + "<br>" +
                          department + "</body></html>";
        
        JPanel panel = ViewConstants.createStandardPanel(new BorderLayout());
        JLabel infoLabel = new JLabel(infoHtml);
        panel.add(infoLabel, BorderLayout.NORTH);
        
        JButton changePwBtn = ViewConstants.createHeaderButton(ViewConstants.TEXT_SECRET_CHANGE);
        changePwBtn.addActionListener(e -> {
            if (cMain != null) {
                cMain.handlePasswordChange();
            }
        });
        JPanel btnPanel = ViewConstants.createStandardPanel(
            ViewConstants.createFlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.add(changePwBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        javax.swing.JOptionPane.showMessageDialog(this, panel, ViewConstants.TEXT_MY_INFO_TITLE, 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 비밀번호 변경 다이얼로그를 보여줍니다.
     */
    public boolean showPasswordChangeDialog() {
        JPasswordField currentPf = new JPasswordField();
        JPasswordField newPf = new JPasswordField();
        JPasswordField confirmPf = new JPasswordField();
        
        JPanel pwPanel = ViewConstants.createStandardPanel(
            ViewConstants.createGridLayout(0, 1, 5, 5));
        pwPanel.add(new JLabel(ViewConstants.TEXT_CURRENT_SECRET));
        pwPanel.add(currentPf);
        pwPanel.add(new JLabel(ViewConstants.TEXT_NEW_SECRET));
        pwPanel.add(newPf);
        pwPanel.add(new JLabel(ViewConstants.TEXT_CONFIRM_SECRET));
        pwPanel.add(confirmPf);
        
        int option = javax.swing.JOptionPane.showConfirmDialog(
            this, pwPanel, ViewConstants.TEXT_SECRET_CHANGE,
            javax.swing.JOptionPane.OK_CANCEL_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE
        );
        
        if (option == javax.swing.JOptionPane.OK_OPTION) {
            currentPasswordInput = new String(currentPf.getPassword());
            newPasswordInput = new String(newPf.getPassword());
            confirmPasswordInput = new String(confirmPf.getPassword());
            
            java.util.Arrays.fill(currentPf.getPassword(), '0');
            java.util.Arrays.fill(newPf.getPassword(), '0');
            java.util.Arrays.fill(confirmPf.getPassword(), '0');
            
            return true;
        }
        return false;
    }
    
    // 비밀번호 입력값을 임시 저장하는 필드
    private String currentPasswordInput;
    private String newPasswordInput;
    private String confirmPasswordInput;
    
    public String getCurrentPasswordInput() { return currentPasswordInput; }
    public String getNewPasswordInput() { return newPasswordInput; }
    public String getConfirmPasswordInput() { return confirmPasswordInput; }
    
    /**
     * 오류 메시지를 표시합니다.
     */
    public void showErrorMessage(String message, String title) {
        ViewConstants.showErrorMessage(this, message, title);
    }
    
    /**
     * 정보 메시지를 표시합니다.
     */
    public void showInfoMessage(String message, String title) {
        ViewConstants.showInfoMessage(this, message, title);
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
    public JButton getBtnSideClock() { return btnSideClock; }
    public JButton getBtnSideTheme() { return btnSideTheme; }
    public JButton getBtnSideAdmin() { return btnSideAdmin; }
    
    // (호환용)
    public JComboBox<String> getThemeCombo() { return themeCombo; }
    public JButton getScheduleButton() { return btnSideTimeTable; }
    
    /**
     * CMain 컨트롤러를 설정합니다. (내 정보 다이얼로그에서 사용)
     */
    public void setMainController(signup.controller.CMain cMain) {
        this.cMain = cMain;
    }
    
    private void updateClockTheme(JPanel panel) {
        boolean dark = javax.swing.UIManager.getLookAndFeelDefaults().getBoolean("laf.dark");
        Color bg = dark ? ViewConstants.COLOR_CLOCK_BG_DARK : ViewConstants.COLOR_CLOCK_BG_LIGHT;
        Color timeColor = dark ? ViewConstants.COLOR_CLOCK_TIME_DARK : ViewConstants.COLOR_PRIMARY;
        Color dateColor = dark ? ViewConstants.COLOR_CLOCK_DATE_DARK : ViewConstants.COLOR_SECONDARY;
        panel.setBackground(bg);
        clockLabel.setForeground(timeColor);
        dateLabel.setForeground(dateColor);
    }
}
