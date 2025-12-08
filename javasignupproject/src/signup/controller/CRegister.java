package signup.controller;

import java.util.List;

import signup.constants.StatusConstants;
import signup.model.MLecture;
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VRegister;

/**
 * '수강신청 내역' 뷰(VRegister)를 제어하는 자식 컨트롤러입니다.
 * CListController를 상속받아 공통 기능을 사용합니다.
 */
public class CRegister extends CListController {

    private VRegister vRegister; // 이 컨트롤러가 직접 제어할 뷰

    /**
     * CRegister 컨트롤러를 생성합니다.
     * 부모(CListController) 생성자에 뷰, 모델, DAO 및 "reg" 상태를 전달합니다.
     *
     * @param vRegister 제어할 수강신청 뷰 (VRegister)
     * @param mMain 전역 모델 (MMain)
     * @param saveDAO DB 접근 객체 (SaveDAO)
     */
    public CRegister(VRegister vRegister, MMain mMain, SaveDAO saveDAO) {
        // 부모 생성자(CListController) 호출
        super(
            vRegister,         // 1. 제어할 뷰(JPanel) 전달
            vRegister.getTable(), // 2. 제어할 테이블(JTable) 전달
            mMain,             // 3. MMain 전달
            saveDAO,           // 4. SaveDAO 전달
            StatusConstants.REGISTER // 5. 이 컨트롤러는 "reg" (수강신청) 상태를 담당
        );
        
        this.vRegister = vRegister;

        // "신청 취소" 버튼에 부모의 'handleDelete' 메서드를 연결
        this.vRegister.getCancelButton().addActionListener(this::handleDelete);
    }

    /**
     * CListController(부모)의 추상 메서드를 구현합니다.
     * 부모의 'refreshTable' 메서드가 호출할 때, VRegister 뷰의
     * 테이블을 실제로 업데이트하는 역할을 합니다.
     *
     * @param data DB에서 가져온 최신 강의 목록
     */
    @Override
    protected void updateViewTable(List<MLecture> data) {
        this.vRegister.updateTable(data);
    }
}