package signup.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.WindowConstants;

import signup.model.Lecture;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * 프로그램의 메인 프레임(JFrame)입니다.
 * CardLayout을 사용하여 로그인, 회원가입, 메인 컨텐츠 패널을 전환합니다.
 */
public class VMain extends JFrame {

    // --- 뷰 컴포넌트 ---
    private CardLayout cardLayout;
    private JPanel mainCardPanel; // RMain이 VLogin/VSignup을 추가할 메인 카드 패널
    
    private JPanel mainContentPanel; // 로그인 후 보여줄 메인 화면 (툴바 + 하위 패널)
    private CardLayout contentCardLayout; // 메인 화면 내부의 카드 레이아웃 (수강신청/미리담기)
    private JPanel panelForRegisterAndBasket; 

    private JPanel vRegisterPanel;
    private JPanel vBasketPanel;
    
    private JTable registerTable;
    private JTable basketTable;

    // --- 툴바 버튼 ---
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
        setLocationRelativeTo(null); // 프레임을 화면 중앙에 배치
        
        // RMain이 사용할 메인 CardLayout 패널 초기화
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        
        // 로그인 후 사용할 "메인 컨텐츠" 패널 생성 (BorderLayout)
        mainContentPanel = new JPanel(new BorderLayout());
        
        // 툴바 생성 (FlowLayout)
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registerbt = new JButton("수강신청");
        basketbt = new JButton("미리담기");
        beforeButton = new JButton("이전");
        afterButton = new JButton("다음");
        refreshButton = new JButton("새로고침");
        
        toolbarPanel.add(registerbt);
        toolbarPanel.add(basketbt);
        toolbarPanel.add(beforeButton);
        toolbarPanel.add(afterButton);
        toolbarPanel.add(refreshButton);
        
        mainContentPanel.add(toolbarPanel, BorderLayout.NORTH);

        // "메인 컨텐츠" 하위의 수강신청/미리담기용 CardLayout 패널
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
        panelForRegisterAndBasket.add(vRegisterPanel, "registerPanel");
        panelForRegisterAndBasket.add(vBasketPanel, "basketPanel");
        
        mainContentPanel.add(panelForRegisterAndBasket, BorderLayout.CENTER);

        // RMain이 사용할 메인 CardLayout에 "mainContentPanel" 추가
        mainCardPanel.add(mainContentPanel, "mainContentPanel");
        
        // JFrame에 메인 CardLayout 패널을 최종 추가
        this.add(mainCardPanel);
    }

    /**
     * RMain이 VLogin, VSignup 같은 외부 패널을 조립할 때 호출하는 메소드입니다.
     * @param panel 추가할 JPanel
     * @param name CardLayout에서 사용할 패널의 이름
     */
    public void addPanel(JPanel panel, String name) {
        mainCardPanel.add(panel, name);
    }

    /**
     * 컨트롤러가 화면 전환을 요청할 때 호출하는 메소드입니다.
     * @param panelName 보여줄 패널의 이름 (e.g., "loginPanel", "registerPanel")
     */
    public void contentPanel(String panelName) {
        // "registerPanel" 또는 "basketPanel"은 메인 컨텐츠 *내부*의 CardLayout을 사용
        if (panelName.equals("registerPanel") || panelName.equals("basketPanel")) {
            contentCardLayout.show(panelForRegisterAndBasket, panelName);
            // 동시에 메인 CardLayout이 "mainContentPanel"을 보여주도록 보장
            cardLayout.show(mainCardPanel, "mainContentPanel");
        } else {
            // "loginPanel", "signupPanel", "mainContentPanel" 등은 메인 CardLayout을 사용
            cardLayout.show(mainCardPanel, panelName);
        }
    }

    /**
     * CMain이 '수강신청 패널'의 테이블을 새로고침할 때 호출합니다.
     * @param registeredData MMain에서 가져온 최신 수강신청 목록
     */
    public void updateRegisterPanel(List<Lecture> registeredData) {
        DefaultTableModel model = (DefaultTableModel) registerTable.getModel();
        model.setRowCount(0); // 테이블 비우기
        
        for (Lecture lecture : registeredData) {
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
     * @param basketData MMain에서 가져온 최신 미리담기 목록
     */
    public void updateBasketPanel(List<Lecture> basketData) {
        DefaultTableModel model = (DefaultTableModel) basketTable.getModel();
        model.setRowCount(0); // 테이블 비우기
        
        for (Lecture lecture : basketData) {
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
    
    public JButton getRegisterbt() { return registerbt; }
    public JButton getBasketbt() { return basketbt; }
    public JButton getBeforeButton() { return beforeButton; }
    public JButton getAfterButton() { return afterButton; }
    public JButton getRefreshButton() { return refreshButton; }
}