package signup.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * 강좌 검색, 수강신청, 미리담기 기능을 수행하는 메인 패널(View)입니다.
 */
public class VSearch extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // --- 1. (NORTH) 검색 조건 패널 ---
    private JComboBox<Object> comboCollege; // 단과대학
    private JComboBox<Object> comboDept;    // 학과
    private JTextField searchField;
    private JButton searchButton;

    // --- 2. (CENTER) 검색 결과 패널 ---
    private JTable resultTable;
    private DefaultTableModel tableModel; // 테이블 데이터를 관리할 모델

    // --- 3. (SOUTH) 동작 버튼 패널 ---
    private JButton registerButton; // 수강신청 버튼
    private JButton preRegisterButton; // 미리담기 버튼

    /**
     * VSearch 패널의 GUI 컴포넌트를 생성하고 배치합니다.
     */
	public VSearch() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new TitledBorder("강좌 검색"));

        // --- 1. NORTH: 검색 패널 (GridBagLayout) ---
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간 여백

        // 단과대학
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(new JLabel("단과대학:"), gbc);
        
        comboCollege = new JComboBox<>(new String[]{"- 대학 선택 -"});
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3; // 30% 너비
        searchPanel.add(comboCollege, gbc);

        // 학과
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0; // 너비 리셋
        searchPanel.add(new JLabel("학과:"), gbc);
        
        comboDept = new JComboBox<>(new String[]{"- 학과 선택 -"});
        comboDept.setEnabled(false); // 처음엔 비활성화
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3; // 30% 너비
        searchPanel.add(comboDept, gbc);

        // 검색어
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("과목명/교수명:"), gbc);
        
        searchField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 3; // 3칸을 차지
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        searchPanel.add(searchField, gbc);

        // 조회 버튼
        searchButton = new JButton("조회");
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.gridheight = 2; // 2줄을 차지
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH; // 위아래로 꽉 차게
        gbc.weightx = 0;
        searchPanel.add(searchButton, gbc);
        
        add(searchPanel, BorderLayout.NORTH);

        // --- 2. CENTER: 결과 테이블 ---
        String[] columnNames = {"과목코드", "과목명", "교수명", "학점", "시간표"};
        
        // JTable이 수정 불가능하도록 DefaultTableModel을 상속받아 오버라이드
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               // 모든 셀을 수정 불가능하게 설정
               return false;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION); // 단일 선택만 허용
        JScrollPane resultScrollPane = new JScrollPane(resultTable);

        add(resultScrollPane, BorderLayout.CENTER);
        
        // --- 3. SOUTH: 동작 버튼 ---
        JPanel actionPanel = new JPanel(new GridBagLayout()); // GridBagLayout으로 변경
        
        // (디자인) 왼쪽 정렬용 빈 공간
        GridBagConstraints gbcAction = new GridBagConstraints();
        gbcAction.weightx = 1.0; // 빈 공간이 모든 너비를 차지
        actionPanel.add(new JLabel(""), gbcAction);
        
        registerButton = new JButton("수강신청");
        preRegisterButton = new JButton("미리담기");
        
        gbcAction = new GridBagConstraints(); // GBC 리셋
        gbcAction.insets = new Insets(5, 5, 5, 5);
        
        gbcAction.gridx = 1;
        actionPanel.add(registerButton, gbcAction); // 수강신청
        
        gbcAction.gridx = 2;
        actionPanel.add(preRegisterButton, gbcAction); // 미리담기
        
        
        
        add(actionPanel, BorderLayout.SOUTH);
        
    }
    
	/**
     * CSearch 컨트롤러로부터 모드를 전달받아,
     * "수강신청" 버튼의 활성화/비활성화 상태를 제어합니다.
     * @param mode "REGISTER" (모두 활성화) 또는 "BASKET" (미리담기만 활성화)
     */
    public void setMode(String mode) {
        if (mode.equals("PREREGISTER")) {
            // "미리담기" 모드일 경우, '수강신청' 버튼을 비활성화
            this.registerButton.setEnabled(false);
            this.preRegisterButton.setEnabled(true);
        } else {
            // "수강신청" (기본) 모드일 경우, 모든 버튼을 활성화
            this.registerButton.setEnabled(true);
            this.preRegisterButton.setEnabled(true);
        }
    }
	
	
	
    // --- CSearch 컨트롤러가 접근할 Getter들 ---
    
    public JComboBox<Object> getComboCollege() { return comboCollege; }
    public JComboBox<Object> getComboDept() { return comboDept; }
    public JTextField getSearchField() { return searchField; }
    public JButton getSearchButton() { return searchButton; }
    public JTable getResultTable() { return resultTable; }
    public DefaultTableModel getTableModel() { return tableModel; } // TableModel Getter
    public JButton getRegisterButton() { return registerButton; }
    public JButton getPreRegisterButton() { return preRegisterButton; }
}