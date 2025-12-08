package signup.controller;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import signup.dao.LectureDAO;
import signup.model.MLecture;
import signup.view.VAdmin;
import signup.view.VDeptList;

/**
 * 관리자 화면(VAdmin)의 이벤트를 처리하는 컨트롤러입니다.
 * 강의 목록 조회, 추가, 수정, 삭제 및 로그아웃 기능을 수행합니다.
 */
public class CAdmin {

    private VAdmin vAdmin;
    private LectureDAO lectureDAO;
    
    // 생성자: 뷰와 DAO를 주입받아 리스너를 설정합니다.
    public CAdmin(VAdmin vAdmin, LectureDAO lectureDAO) {
        this.vAdmin = vAdmin;
        this.lectureDAO = lectureDAO;

        // 1. 버튼 리스너 연결
        this.vAdmin.getBtnAdd().addActionListener(this::handleAdd);
        this.vAdmin.getBtnUpdate().addActionListener(this::handleUpdate);
        this.vAdmin.getBtnDelete().addActionListener(this::handleDelete);
        this.vAdmin.getBtnClear().addActionListener(e -> vAdmin.clearForm());
        this.vAdmin.getBtnDeptSearch().addActionListener(e -> showDeptList());
        
        // 2. 테이블 마우스 클릭 리스너 (데이터 채우기)
        this.vAdmin.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    public void showAdminDialog() {
        loadAllLectures(); // 데이터 최신화
        vAdmin.setVisible(true); // 모달 창 띄우기 (여기서 코드 대기)
    }
    
    // 모든 강의 데이터를 테이블에 로드
    public void loadAllLectures() {
        List<MLecture> list = lectureDAO.getAllLectures();
        DefaultTableModel model = vAdmin.getTableModel();
        model.setRowCount(0); // 초기화
        
        for (MLecture l : list) {
            
            Object[] row = {
                l.getId(),
                l.getName(),
                l.getProfessor(),
                l.getCredits(),
                l.getSchedule(),
                l.getDeptId()
            };
            model.addRow(row);
        }
    }

    // [추가] 버튼 클릭 핸들러
    private void handleAdd(ActionEvent e) {
        try {
            LectureFormData formData = getLectureFormData();
            
            if (lectureDAO.insertLecture(formData.id, formData.name, formData.prof, 
                                         formData.credit, formData.time, formData.deptId)) {
                JOptionPane.showMessageDialog(vAdmin, "강의가 추가되었습니다.");
                loadAllLectures();
                vAdmin.clearForm();
            } else {
                JOptionPane.showMessageDialog(vAdmin, "추가 실패 (중복된 ID 등)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vAdmin, "ID, 학점, 학과ID는 숫자여야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUpdate(ActionEvent e) {
        try {
            LectureFormData formData = getLectureFormData();

            if (lectureDAO.updateLecture(formData.id, formData.name, formData.prof, 
                                        formData.credit, formData.time, formData.deptId)) {
                JOptionPane.showMessageDialog(vAdmin, "강의 정보가 수정되었습니다.");
                loadAllLectures();
                vAdmin.clearForm();
            } else {
                JOptionPane.showMessageDialog(vAdmin, "수정 실패 (존재하지 않는 ID)", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vAdmin, "숫자 입력 형식을 확인하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private LectureFormData getLectureFormData() {
        return new LectureFormData(
            Integer.parseInt(vAdmin.getTfId().getText()),
            vAdmin.getTfName().getText(),
            vAdmin.getTfProfessor().getText(),
            Integer.parseInt(vAdmin.getTfCredit().getText()),
            vAdmin.getTfTime().getText(),
            Integer.parseInt(vAdmin.getTfDeptId().getText())
        );
    }
    
    /**
     * 강의 폼 데이터를 캡슐화하는 데이터 전송 객체
     */
    private static class LectureFormData {
        final int id;
        final String name;
        final String prof;
        final int credit;
        final String time;
        final int deptId;
        
        LectureFormData(int id, String name, String prof, int credit, String time, int deptId) {
            this.id = id;
            this.name = name;
            this.prof = prof;
            this.credit = credit;
            this.time = time;
            this.deptId = deptId;
        }
    }

    // [삭제] 버튼 클릭 핸들러
    private void handleDelete(ActionEvent e) {
        String idStr = vAdmin.getTfId().getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(vAdmin, "삭제할 강의를 선택(클릭)하세요.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(vAdmin, 
                "정말 삭제하시겠습니까? (ID: " + idStr + ")", "삭제 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idStr);
            if (lectureDAO.deleteLecture(id)) {
                JOptionPane.showMessageDialog(vAdmin, "삭제되었습니다.");
                loadAllLectures();
                vAdmin.clearForm();
            } else {
                JOptionPane.showMessageDialog(vAdmin, "삭제 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 테이블 행 클릭 핸들러
    private void handleTableClick() {
        JTable table = vAdmin.getTable();
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        populateFormFromTable(table, row);
        vAdmin.getTfId().setEditable(false);
    }
    
    private void populateFormFromTable(JTable table, int row) {
        vAdmin.getTfId().setText(getStringValue(table, row, 0));
        vAdmin.getTfName().setText(getStringValue(table, row, 1));
        vAdmin.getTfProfessor().setText(getStringValue(table, row, 2));
        vAdmin.getTfCredit().setText(getStringValue(table, row, 3));
        vAdmin.getTfTime().setText(getStringValue(table, row, 4));
        vAdmin.getTfDeptId().setText(getStringValue(table, row, 5));
    }
    
    // 학과 목록 다이얼로그 표시
    private void showDeptList() {
        List<String> depts = lectureDAO.getAllDepartments();
        // VAdmin(JPanel)의 최상위 부모(JFrame = VMain)를 찾아서 Owner로 넘김
        JFrame owner = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(vAdmin);
        
        VDeptList dialog = new VDeptList(owner, depts);
        dialog.setVisible(true);
    }
    private String getStringValue(JTable table, int row, int col) {
        Object value = table.getValueAt(row, col);
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}