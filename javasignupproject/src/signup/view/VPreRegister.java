package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout; // [추가] 버튼 2개를 배치하기 위해
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
 * '미리담기 내역'을 보여주는 JPanel입니다.
 * JTable과 "수강신청", "삭제" 버튼 2개를 가집니다.
 */
public class VPreRegister extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JTable preRegisterTable;
    private DefaultTableModel tableModel;
    private JButton btnApply;  // "수강신청" 버튼
    private JButton btnDelete; // "목록 삭제" 버튼
    private JLabel lblTotalCredits; // 총 학점 라벨

    public VPreRegister() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. (CENTER) 테이블 생성 (VRegister와 동일)
        tableModel = new DefaultTableModel(
            new Object[]{"과목코드", "과목명", "교수명", "학점", "시간표"}, 0
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        preRegisterTable = new JTable(tableModel);
        preRegisterTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(preRegisterTable), BorderLayout.CENTER);
        
        // 2. (SOUTH) 버튼 및 정보 라벨 패널
        JPanel southPanel = new JPanel(new BorderLayout());
        
        lblTotalCredits = new JLabel("총 미리담기 학점: 0 학점");
        lblTotalCredits.setBorder(new EmptyBorder(0, 5, 0, 0)); // 왼쪽 여백
        
        // [수정] 버튼 2개를 담을 별도의 패널 (FlowLayout.RIGHT)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnApply = new JButton("수강신청");
        btnApply.setForeground(new Color(0, 102, 204)); // 파란색 계열
        
        btnDelete = new JButton("목록 삭제");
        btnDelete.setForeground(Color.RED);
        
        buttonPanel.add(btnApply);
        buttonPanel.add(btnDelete);
        
        southPanel.add(lblTotalCredits, BorderLayout.WEST);
        southPanel.add(buttonPanel, BorderLayout.EAST); // 버튼 패널을 오른쪽에 배치
        
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * 컨트롤러가 테이블을 새로고침할 때 호출하는 메서드입니다.
     * @param lectureList DB에서 가져온 최신 미리담기 목록
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
        
        lblTotalCredits.setText("총 미리담기 학점: " + totalCredits + " 학점");
    }
    
    // --- CPreRegister 컨트롤러가 접근할 Getter ---
    
    public JTable getTable() {
        return preRegisterTable;
    }
    
    public JButton getApplyButton() {
        return btnApply;
    }
    
    public JButton getDeleteButton() {
        return btnDelete;
    }
}