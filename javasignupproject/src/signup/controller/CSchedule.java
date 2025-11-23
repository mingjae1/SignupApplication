package signup.controller;

import java.util.List;


import signup.dao.SaveDAO;
import signup.model.MLecture;
import signup.model.MMain;
import signup.view.VSchedule;

public class CSchedule {

    private VSchedule vSchedule;
    private MMain mMain;
    private SaveDAO saveDAO;

    public CSchedule(VSchedule vSchedule, MMain mMain, SaveDAO saveDAO) {
        this.vSchedule = vSchedule;
        this.mMain = mMain;
        this.saveDAO = saveDAO;

        // 팝업창 내부의 라디오 버튼 리스너 연결
        this.vSchedule.getRadioRegister().addActionListener(e -> loadSchedule("reg"));
        this.vSchedule.getRadioBasket().addActionListener(e -> loadSchedule("pre"));
    }

    /**
     * [수정됨] CMain에서 호출하는 공개 메서드입니다.
     * 시간표 팝업창을 화면에 띄우고 데이터를 로드합니다.
     */
    public void showSchedule() {
        if (mMain.getCurrentUserId() == null) {
            // (주의) vSchedule이 아직 안 떴으므로 vSchedule을 부모로 쓰면 안 됨
            // 하지만 JDialog인 vSchedule을 부모로 쓰면 자동으로 화면에 나타나지 않으므로
            // 여기서는 그냥 실행을 중단하거나 로그만 남깁니다. 
            // (실제로는 CMain에서 로그인 체크를 먼저 하므로 여기까지 올 일이 거의 없습니다)
            return;
        }

        // 기본값: 수강신청 시간표 보여주기
        vSchedule.getRadioRegister().setSelected(true);
        loadSchedule("reg");
       
        // 팝업창 보이기
        vSchedule.setVisible(true);
    }

    /**
     * 선택된 모드(reg/pre)에 따라 데이터를 가져와 시간표를 채웁니다.
     */
    private void loadSchedule(String status) {
        String userId = mMain.getCurrentUserId();
        if (userId == null) return;
        // 1. 테이블 초기화 (그림 지우기)
        vSchedule.clearTable();
        // 2. 데이터 가져오기
        new Thread(() -> {
            // (이 작업은 백그라운드에서 실행되므로 메인 화면을 멈추지 않습니다)
            List<MLecture> lectures = saveDAO.getLecturesByStatus(userId, status);
            
            // 3. 데이터가 도착하면 화면을 그립니다. (반드시 UI 스레드에서 실행)
            vSchedule.updateSchedule(lectures);

            
        }).start();
    }
}