package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import signup.model.MLecture; // 님의 DTO 클래스

/**
 * '수강신청 내역'을 보여주는 JPanel입니다.
 * JTable과 "신청 취소" 버튼 1개를 가집니다.
 */
public class VRegister extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JTable registerTable;
    private DefaultTableModel tableModel;
    private JButton btnCancel; // 신청 취소 버튼
    private JLabel lblTotalCredits; // 총 학점 라벨

    public VRegister() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. (CENTER) 테이블 생성
        tableModel = new DefaultTableModel(
            new Object[]{"과목코드", "과목명", "교수명", "학점", "시간표"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        registerTable = new JTable(tableModel);
        registerTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(registerTable), BorderLayout.CENTER);
        
        // 2. (SOUTH) 버튼 및 정보 라벨 패널
        JPanel southPanel = new JPanel(new BorderLayout());
        
        lblTotalCredits = new JLabel("총 신청 학점: 0 학점");
        lblTotalCredits.setBorder(new EmptyBorder(0, 5, 0, 0)); // 왼쪽 여백
        
        btnCancel = new JButton("신청 취소");
        btnCancel.setForeground(Color.RED); // 강조색
        
        southPanel.add(lblTotalCredits, BorderLayout.WEST);
        southPanel.add(btnCancel, BorderLayout.EAST);
        
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * CRegister 컨트롤러가 테이블을 새로고침할 때 호출하는 메서드입니다.
     * @param lectureList DB에서 가져온 최신 수강신청 목록
     */
    public void updateTable(List<MLecture> lectureList) {
        tableModel.setRowCount(0); // 테이블 비우기
        
        int totalCredits = 0;
        
        if (lectureList != null) { // Null 방지
            for (MLecture lecture : lectureList) {
                Object[] row = {
                    lecture.getId(), 
                    lecture.getName(), 
                    lecture.getProfessor(),
                    lecture.getCredits(),
                    lecture.getSchedule()
                };
                tableModel.addRow(row);
                totalCredits += lecture.getCredits();
            }
        }
        
        lblTotalCredits.setText("총 신청 학점: " + totalCredits + " 학점");
    }
    
    // --- CRegister 컨트롤러가 접근할 Getter ---
    
    public JTable getTable() {
        return registerTable;
    }
    
    public JButton getCancelButton() {
        return btnCancel;
    }
}