package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
    private JButton cancelButton; // 신청 취소 버튼
    private JLabel lblTotalCredits; // 총 학점 라벨

    public VRegister() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. (CENTER) 테이블 생성
        tableModel = new DefaultTableModel(
            new Object[]{"과목코드", "과목명", "교수명", "학점", "시간표"}, 0
        ) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        registerTable = new JTable(tableModel);
        registerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 단일 선택만 허용
        registerTable.getTableHeader().setResizingAllowed(false);
        registerTable.getTableHeader().setReorderingAllowed(false);
        
        //  정렬을 끄고 싶은 컬럼의 인덱스(순서)를 지정합니다.
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(registerTable.getModel());
        sorter.setSortable(3, false); // 3번(학점) 정렬 끄기
        sorter.setSortable(4, false); // 4번(시간표) 정렬 끄기
        registerTable.setRowSorter(sorter);
             
        // 2-1. 줄무늬(Zebra) 스타일 적용 (한 줄 건너 색상 변경)
        registerTable.putClientProperty("JTable.stripe", true);
        
        // 2-2. 가로/세로 그리드 라인 표시
        registerTable.setShowGrid(true); // 그리드 켜기
        registerTable.setShowHorizontalLines(true); // 가로선
        registerTable.setShowVerticalLines(true);   // 세로선
        
        // 2,3. 행 높이 및 열 너비 설정
        registerTable.setRowHeight(25);
        registerTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        registerTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        registerTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        registerTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        registerTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        // 2-4. 테이블 아래 빈 공간도 배경색 채우기
        registerTable.setFillsViewportHeight(true);
        
        
        add(new JScrollPane(registerTable), BorderLayout.CENTER);
        
        // 2. (SOUTH) 버튼 및 정보 라벨 패널
        JPanel southPanel = new JPanel(new BorderLayout());
        
        lblTotalCredits = new JLabel("총 신청 학점: 0 학점");
        lblTotalCredits.setBorder(new EmptyBorder(0, 5, 0, 0)); // 왼쪽 여백
        
        cancelButton = new JButton("신청 취소");
        
        southPanel.add(lblTotalCredits, BorderLayout.WEST);
        southPanel.add(cancelButton, BorderLayout.EAST);
        
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
        return cancelButton;
    }
}