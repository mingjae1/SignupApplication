package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * 관리자 모드 팝업 창(Dialog)입니다.
 * [수정] 학과 조회 버튼(돋보기) 레이아웃을 수정하고 닫기 버튼을 복구했습니다.
 */
public class VAdmin extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- 컴포넌트 필드 ---
    private JTable lectureTable;
    private DefaultTableModel tableModel;
    
    // 입력 필드
    private JTextField tfId;        
    private JTextField tfName;      
    private JTextField tfProfessor; 
    private JTextField tfCredit;    
    private JTextField tfTime;      
    private JTextField tfDeptId;    
    
    // 버튼
    private JButton btnAdd;    
    private JButton btnUpdate; 
    private JButton btnDelete; 
    private JButton btnClear;  
    private JButton btnDeptSearch;

    /**
     * 관리자 팝업 창을 생성합니다.
     * @param owner 부모 프레임 (VMain)
     */
    public VAdmin(JFrame owner) {
        super(owner, "관리자 모드 - 강의 관리", true); // Modal
        setSize(900, 700); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // ===========================================================================
        // 1. 상단 패널 (타이틀 & 닫기 버튼)
        // ===========================================================================
        
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("강의 데이터 관리 시스템");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        
        add(topPanel, BorderLayout.NORTH);

        // ===========================================================================
        // 2. 중앙 패널 (강의 목록 테이블)
        // ===========================================================================
        
        String[] colNames = {"과목코드", "과목명", "교수명", "학점", "시간표", "학과ID"};
        
        tableModel = new DefaultTableModel(null, colNames) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3 || columnIndex == 5) {
                    return Integer.class;
                }
                return String.class;
            }
        };
        
        lectureTable = new JTable(tableModel);
        lectureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lectureTable.setAutoCreateRowSorter(true); 
        lectureTable.setRowHeight(25);
        
        lectureTable.putClientProperty("JTable.stripe", true);
        lectureTable.setShowGrid(true);
        lectureTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(lectureTable);
        scrollPane.setBorder(new TitledBorder("전체 강의 목록 (실시간 DB 조회)"));
        
        add(scrollPane, BorderLayout.CENTER);

        // ===========================================================================
        // 3. 하단 패널 (입력 폼 & 조작 버튼)
        // ===========================================================================
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new TitledBorder("강의 정보 입력 / 수정"));
        bottomPanel.setPreferredSize(new Dimension(800, 200)); 

        // --- 3-1. 입력 필드 영역 (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        
        // 1행
        addLabel(formPanel, "과목코드(ID):", 0, 0);
        tfId = addTextField(formPanel, 1, 0);
        
        addLabel(formPanel, "과목명:", 2, 0);
        tfName = addTextField(formPanel, 3, 0);
        
        addLabel(formPanel, "교수명:", 4, 0);
        tfProfessor = addTextField(formPanel, 5, 0);
        
        // 2행
        addLabel(formPanel, "학점:", 0, 1);
        tfCredit = addTextField(formPanel, 1, 1);
        
        addLabel(formPanel, "시간표:", 2, 1);
        tfTime = addTextField(formPanel, 3, 1);
        
        // 2행 마지막: 학과ID + 조회 버튼
        addLabel(formPanel, "학과ID:", 4, 1);
        
        // 텍스트필드와 돋보기 버튼을 묶을 패널 생성
        JPanel deptPanel = new JPanel(new BorderLayout(5, 0));
        tfDeptId = new JTextField();
        btnDeptSearch = new JButton("🔍"); 
        btnDeptSearch.setToolTipText("학과 코드표 보기");
        
        deptPanel.add(tfDeptId, BorderLayout.CENTER);
        deptPanel.add(btnDeptSearch, BorderLayout.EAST);
        
        // 패널을 폼에 추가
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 5;
        gbc.gridy = 1;
        formPanel.add(deptPanel, gbc);
        
        bottomPanel.add(formPanel, BorderLayout.CENTER);

        // --- 3-2. 버튼 영역 (FlowLayout) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("추가 (Insert)");
        btnUpdate = new JButton("수정 (Update)");
        btnDelete = new JButton("삭제 (Delete)");
        btnClear = new JButton("입력 초기화");
        
        btnAdd.setForeground(new Color(0, 100, 0)); 
        btnDelete.setForeground(Color.RED);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(new JLabel(" | "));
        btnPanel.add(btnClear);
        
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- 헬퍼 메서드 ---
    private void addLabel(JPanel panel, String text, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(new JLabel(text), gbc);
    }

    private JTextField addTextField(JPanel panel, int x, int y) {
        JTextField tf = new JTextField();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(tf, gbc);
        return tf;
    }
    
    // --- Getters ---
    public JTable getTable() { return lectureTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    
    public JTextField getTfId() { return tfId; }
    public JTextField getTfName() { return tfName; }
    public JTextField getTfProfessor() { return tfProfessor; }
    public JTextField getTfCredit() { return tfCredit; }
    public JTextField getTfTime() { return tfTime; }
    public JTextField getTfDeptId() { return tfDeptId; }
    
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnDeptSearch() { return btnDeptSearch; }
    
    public void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfProfessor.setText("");
        tfCredit.setText("");
        tfTime.setText("");
        tfDeptId.setText("");
        lectureTable.clearSelection();
        tfId.setEditable(true); 
    }
}