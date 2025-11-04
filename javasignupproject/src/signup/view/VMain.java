package signup.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.WindowConstants;

import signup.model.MLecture; // 님의 DTO 클래스

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.List;

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
    private JPanel panelForRegisterAndBasket; // VSearch, VRegister, VBasket이 들어갈 패널

    private JPanel vRegisterPanel;
    private JPanel vBasketPanel;
    // (VSearch 패널은 RMain에서 생성 후 여기에 추가됨)
    
    private JTable registerTable;
    private JTable basketTable;

    // --- 툴바 버튼 ---
    private JButton searchbt; // [추가됨] 강좌 검색 버튼
    private JButton registerbt;
    private JButton basketbt;
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
        basketbt = new JButton("미리담기 내역"); // (이름 명확하게 변경)
        beforeButton = new JButton("이전");
        afterButton = new JButton("다음");
        refreshButton = new JButton("새로고침");
        
        toolbarPanel.add(searchbt); // [추가됨]
        toolbarPanel.add(registerbt);
        toolbarPanel.add(basketbt);
        toolbarPanel.add(beforeButton);
        toolbarPanel.add(afterButton);
        toolbarPanel.add(refreshButton);
        
        mainContentPanel.add(toolbarPanel, BorderLayout.NORTH);

        // --- 메인 컨텐츠 하위 CardLayout 패널 ---
        contentCardLayout = new CardLayout();
        panelForRegisterAndBasket = new JPanel(contentCardLayout);

        // 수강신청 패널 (vRegisterPanel) 생성
        vRegisterPanel = new JPanel(new BorderLayout()); 
        registerTable = new JTable(new DefaultTableModel(
            new Object[]{"과목코드", "과목명", "교수명", "학점", "시간표"}, 0
        ));
        vRegisterPanel.add(new JScrollPane(registerTable), BorderLayout.CENTER);
        
        // 미리담기 패널 (vBasketPanel) 생성
        vBasketPanel = new JPanel(new BorderLayout());
        basketTable = new JTable(new DefaultTableModel(
            new Object[]{"과목코드", "과목명", "교수명", "학점", "시간표"}, 0
        ));
        vBasketPanel.add(new JScrollPane(basketTable), BorderLayout.CENTER);

        // 메인 컨텐츠 하위 CardLayout에 두 패널 추가
        // (VSearch 패널은 RMain에서 "searchPanel"이라는 이름으로 이곳에 추가될 것임)
        panelForRegisterAndBasket.add(vRegisterPanel, "registerPanel");
        panelForRegisterAndBasket.add(vBasketPanel, "basketPanel");
        
        mainContentPanel.add(panelForRegisterAndBasket, BorderLayout.CENTER);

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
        return panelForRegisterAndBasket;
    }

    /**
     * 컨트롤러가 화면 전환을 요청할 때 호출하는 메소드입니다.
     */
    public void contentPanel(String panelName) {
        // "registerPanel", "basketPanel", "searchPanel"은 내부 CardLayout을 사용
        if (panelName.equals("registerPanel") || panelName.equals("basketPanel") || panelName.equals("searchPanel")) {
            // 1. 메인 CardLayout을 "mainContentPanel"로 먼저 바꾼다.
            cardLayout.show(mainCardPanel, "mainContentPanel");
            // 2. 그 *후*에, 내부 CardLayout을 요청된 패널로 바꾼다.
            contentCardLayout.show(panelForRegisterAndBasket, panelName);
        } else {
            // "loginPanel", "signupPanel" 등은 메인 CardLayout을 사용
            cardLayout.show(mainCardPanel, panelName);
        }
    }

    /**
     * CMain이 '수강신청 패널'의 테이블을 새로고침할 때 호출합니다.
     */
    public void updateRegisterPanel(List<MLecture> registeredData) {
        DefaultTableModel model = (DefaultTableModel) registerTable.getModel();
        model.setRowCount(0); // 테이블 비우기
        
        for (MLecture lecture : registeredData) {
            Object[] row = {
                lecture.getId(), 
                lecture.getName(), 
                lecture.getProfessor(),
                lecture.getCredits(),
                lecture.getSchedule()
            };
            model.addRow(row);
        }
    }

    /**
     * CMain이 '미리담기 패널'의 테이블을 새로고침할 때 호출합니다.
     */
    public void updateBasketPanel(List<MLecture> basketData) {
        DefaultTableModel model = (DefaultTableModel) basketTable.getModel();
        model.setRowCount(0); // 테이블 비우기
        
        for (MLecture lecture : basketData) {
            Object[] row = {
                lecture.getId(), 
                lecture.getName(), 
                lecture.getProfessor(),
                lecture.getCredits(),
                lecture.getSchedule()
            };
            model.addRow(row);
        }
    }

    // --- Getters for Controller ---
    
    public JButton getSearchbt() { return searchbt; } // [추가됨]
    public JButton getRegisterbt() { return registerbt; }
    public JButton getBasketbt() { return basketbt; }
    public JButton getBeforeButton() { return beforeButton; }
    public JButton getAfterButton() { return afterButton; }
    public JButton getRefreshButton() { return refreshButton; }
}