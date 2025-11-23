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
        GridBagConstraints gbc_collegeLabel = new GridBagConstraints();
        gbc_collegeLabel.insets = new Insets(5, 5, 5, 5);
        gbc_collegeLabel.anchor = GridBagConstraints.EAST;
        gbc_collegeLabel.gridx = 0; 
        gbc_collegeLabel.gridy = 0;
        searchPanel.add(new JLabel("단과대학:"), gbc_collegeLabel);
        
        // 단과대학 콤보박스
        comboCollege = new JComboBox<>(new Object[]{"- 대학 선택 -"}); // Object 타입
        GridBagConstraints gbc_comboCollege = new GridBagConstraints();
        gbc_comboCollege.insets = new Insets(5, 5, 5, 5);
        gbc_comboCollege.anchor = GridBagConstraints.WEST;
        gbc_comboCollege.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboCollege.weightx = 0.3;
        gbc_comboCollege.gridx = 1; 
        gbc_comboCollege.gridy = 0;
        searchPanel.add(comboCollege, gbc_comboCollege);

        // 학과 레이블
        GridBagConstraints gbc_deptLabel = new GridBagConstraints();
        gbc_deptLabel.insets = new Insets(5, 5, 5, 5);
        gbc_deptLabel.anchor = GridBagConstraints.EAST;
        gbc_deptLabel.gridx = 2; 
        gbc_deptLabel.gridy = 0;
        searchPanel.add(new JLabel("학과:"), gbc_deptLabel);
        
        // 학과 콤보박스
        comboDept = new JComboBox<>(new Object[]{"- 학과 선택 -"}); // Object 타입
        comboDept.setEnabled(false);
        GridBagConstraints gbc_comboDept = new GridBagConstraints();
        gbc_comboDept.insets = new Insets(5, 5, 5, 5);
        gbc_comboDept.anchor = GridBagConstraints.WEST;
        gbc_comboDept.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboDept.weightx = 0.3;
        gbc_comboDept.gridx = 3; 
        gbc_comboDept.gridy = 0;
        searchPanel.add(comboDept, gbc_comboDept);

        // 검색어 레이블
        GridBagConstraints gbc_searchLabel = new GridBagConstraints();
        gbc_searchLabel.insets = new Insets(5, 5, 5, 5);
        gbc_searchLabel.anchor = GridBagConstraints.EAST;
        gbc_searchLabel.gridx = 0; 
        gbc_searchLabel.gridy = 1;
        searchPanel.add(new JLabel("과목명/교수명:"), gbc_searchLabel);
        
        // 검색어 필드
        searchField = new JTextField(20);
        GridBagConstraints gbc_searchField = new GridBagConstraints();
        gbc_searchField.insets = new Insets(5, 5, 5, 5);
        gbc_searchField.anchor = GridBagConstraints.WEST;
        gbc_searchField.gridwidth = 3; // 3칸 차지
        gbc_searchField.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchField.weightx = 1.0;
        gbc_searchField.gridx = 1; 
        gbc_searchField.gridy = 1;
        searchPanel.add(searchField, gbc_searchField);

        // 조회 버튼
        searchButton = new JButton("조회");
        GridBagConstraints gbc_searchButton = new GridBagConstraints();
        gbc_searchButton.insets = new Insets(5, 5, 5, 5);
        gbc_searchButton.gridheight = 2; // 2줄 차지
        gbc_searchButton.anchor = GridBagConstraints.CENTER;
        gbc_searchButton.fill = GridBagConstraints.BOTH;
        gbc_searchButton.gridx = 4; 
        gbc_searchButton.gridy = 0;
        searchPanel.add(searchButton, gbc_searchButton);
        
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
        GridBagConstraints gbc_emptyLabel = new GridBagConstraints();
        gbc_emptyLabel.weightx = 1.0;
        actionPanel.add(new JLabel(""), gbc_emptyLabel);
        
        // 미리담기 버튼
        preRegisterButton = new JButton("미리담기");
        GridBagConstraints gbc_preRegisterButton = new GridBagConstraints();
        gbc_preRegisterButton.insets = new Insets(5, 5, 5, 5);
        gbc_preRegisterButton.gridx = 2; // (수정) 1 -> 2
        actionPanel.add(preRegisterButton, gbc_preRegisterButton);
        
        // 수강신청 버튼
        registerButton = new JButton("수강신청");
        GridBagConstraints gbc_registerButton = new GridBagConstraints(); // (수정) gbcAction -> gbc_registerButton
        gbc_registerButton.insets = new Insets(5, 5, 5, 5);
        gbc_registerButton.gridx = 1; // (수정) 2 -> 1
        actionPanel.add(registerButton, gbc_registerButton);

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