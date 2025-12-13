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
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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

        // 단과대학 레이블
        GridBagConstraints collegeLabelConstraints = new GridBagConstraints();
        collegeLabelConstraints.insets = new Insets(5, 5, 5, 5);
        collegeLabelConstraints.anchor = GridBagConstraints.EAST;
        collegeLabelConstraints.gridx = 0; 
        collegeLabelConstraints.gridy = 0;
        searchPanel.add(new JLabel("단과대학:"), collegeLabelConstraints);
        
        // 단과대학 콤보박스
        comboCollege = new JComboBox<>(new Object[]{"- 대학 선택 -"}); // Object 타입
        GridBagConstraints collegeComboConstraints = new GridBagConstraints();
        collegeComboConstraints.insets = new Insets(5, 5, 5, 5);
        collegeComboConstraints.anchor = GridBagConstraints.WEST;
        collegeComboConstraints.fill = GridBagConstraints.HORIZONTAL;
        collegeComboConstraints.weightx = 0.3;
        collegeComboConstraints.gridx = 1; 
        collegeComboConstraints.gridy = 0;
        searchPanel.add(comboCollege, collegeComboConstraints);

        // 학과 레이블
        GridBagConstraints departmentLabelConstraints = new GridBagConstraints();
        departmentLabelConstraints.insets = new Insets(5, 5, 5, 5);
        departmentLabelConstraints.anchor = GridBagConstraints.EAST;
        departmentLabelConstraints.gridx = 2; 
        departmentLabelConstraints.gridy = 0;
        searchPanel.add(new JLabel("학과:"), departmentLabelConstraints);
        
        // 학과 콤보박스
        comboDept = new JComboBox<>(new Object[]{"- 학과 선택 -"}); // Object 타입
        comboDept.setEnabled(false);
        GridBagConstraints departmentComboConstraints = new GridBagConstraints();
        departmentComboConstraints.insets = new Insets(5, 5, 5, 5);
        departmentComboConstraints.anchor = GridBagConstraints.WEST;
        departmentComboConstraints.fill = GridBagConstraints.HORIZONTAL;
        departmentComboConstraints.weightx = 0.3;
        departmentComboConstraints.gridx = 3; 
        departmentComboConstraints.gridy = 0;
        searchPanel.add(comboDept, departmentComboConstraints);

        // 검색어 레이블
        GridBagConstraints searchLabelConstraints = new GridBagConstraints();
        searchLabelConstraints.insets = new Insets(5, 5, 5, 5);
        searchLabelConstraints.anchor = GridBagConstraints.EAST;
        searchLabelConstraints.gridx = 0; 
        searchLabelConstraints.gridy = 1;
        searchPanel.add(new JLabel("과목명/교수명:"), searchLabelConstraints);
        
        // 검색어 필드
        searchField = new JTextField(20);
        GridBagConstraints searchFieldConstraints = new GridBagConstraints();
        searchFieldConstraints.insets = new Insets(5, 5, 5, 5);
        searchFieldConstraints.anchor = GridBagConstraints.WEST;
        searchFieldConstraints.gridwidth = 3; // 3칸 차지
        searchFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        searchFieldConstraints.weightx = 1.0;
        searchFieldConstraints.gridx = 1; 
        searchFieldConstraints.gridy = 1;
        searchPanel.add(searchField, searchFieldConstraints);

        // 조회 버튼
        searchButton = new JButton("조회");
        GridBagConstraints searchButtonConstraints = new GridBagConstraints();
        searchButtonConstraints.insets = new Insets(5, 5, 5, 5);
        searchButtonConstraints.gridheight = 2; // 2줄 차지
        searchButtonConstraints.anchor = GridBagConstraints.CENTER;
        searchButtonConstraints.fill = GridBagConstraints.BOTH;
        searchButtonConstraints.gridx = 4; 
        searchButtonConstraints.gridy = 0;
        searchPanel.add(searchButton, searchButtonConstraints);
        
        add(searchPanel, BorderLayout.NORTH);

        // --- 2. CENTER: 결과 테이블 ---
        String[] columnNames = {"과목코드", "과목명", "교수명", "학점", "시간표"};
        
        // JTable이 수정 불가능하도록 DefaultTableModel을 상속받아 오버라이드
        tableModel = new DefaultTableModel(null, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               // 모든 셀을 수정 불가능하게 설정
               return false;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 단일 선택만 허용
        resultTable.getTableHeader().setResizingAllowed(false);
        resultTable.getTableHeader().setReorderingAllowed(false);
        
        //  정렬을 끄고 싶은 컬럼의 인덱스(순서)를 지정합니다.
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(resultTable.getModel());
        sorter.setSortable(3, false); // 3번(학점) 정렬 끄기
        sorter.setSortable(4, false); // 4번(시간표) 정렬 끄기
        resultTable.setRowSorter(sorter);
             
        // 2-1. 줄무늬(Zebra) 스타일 적용 (한 줄 건너 색상 변경)
        resultTable.putClientProperty("JTable.stripe", true);
        
        // 2-2. 가로/세로 그리드 라인 표시
        resultTable.setShowGrid(true); // 그리드 켜기
        resultTable.setShowHorizontalLines(true); // 가로선
        resultTable.setShowVerticalLines(true);   // 세로선
        
        // 2,3. 행 높이 및 열 너비 설정
        resultTable.setRowHeight(25);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        // 2-4. 테이블 아래 빈 공간도 배경색 채우기
        resultTable.setFillsViewportHeight(true);
        
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        add(resultScrollPane, BorderLayout.CENTER);
        
        
        // --- 3. SOUTH: 동작 버튼 ---
        JPanel actionPanel = new JPanel(new GridBagLayout()); 
        
        // 왼쪽 빈 공간
        GridBagConstraints spacerConstraints = new GridBagConstraints();
        spacerConstraints.weightx = 1.0;
        actionPanel.add(new JLabel(""), spacerConstraints);
        
        // 미리담기 버튼
        preRegisterButton = new JButton("미리담기");
        GridBagConstraints preregButtonConstraints = new GridBagConstraints();
        preregButtonConstraints.insets = new Insets(5, 5, 5, 5);
        preregButtonConstraints.gridx = 2; // (수정) 1 -> 2
        actionPanel.add(preRegisterButton, preregButtonConstraints);
        
        // 수강신청 버튼
        registerButton = new JButton("수강신청");
        GridBagConstraints registerButtonConstraints = new GridBagConstraints(); // (수정) gbcAction -> gbc_registerButton
        registerButtonConstraints.insets = new Insets(5, 5, 5, 5);
        registerButtonConstraints.gridx = 1; // (수정) 2 -> 1
        actionPanel.add(registerButton, registerButtonConstraints);

        add(actionPanel, BorderLayout.SOUTH);
        
    }
    
	/**
     * CSearch 컨트롤러로부터 모드를 전달받아,
     * "수강신청" 버튼의 활성화/비활성화 상태를 제어합니다.
     * @param mode "REGISTER" (모두 활성화) 또는 "PREREGISTER" (미리담기만 활성화)
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