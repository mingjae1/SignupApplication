package signup.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import signup.constants.StatusConstants;
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
        this.vSchedule.getRadioRegister().addActionListener(e -> loadSchedule(StatusConstants.REGISTER));
        this.vSchedule.getRadioBasket().addActionListener(e -> loadSchedule(StatusConstants.PRE_REGISTER));
        this.vSchedule.getSaveImageButton().addActionListener(e -> saveAsImage());
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
        loadSchedule(StatusConstants.REGISTER);
       
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
    
    /**
     * [이동됨] 현재 그려진 시간표 패널을 이미지 파일(.png)로 저장합니다.
     */
    private void saveAsImage() {
        // 1. 저장할 파일 선택 (JFileChooser)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("시간표 이미지 저장");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG 이미지 (*.png)", "png"));
        fileChooser.setSelectedFile(new File(signup.constants.AppConstants.DEFAULT_SCHEDULE_FILENAME));

        int userSelection = fileChooser.showSaveDialog(vSchedule);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(signup.constants.AppConstants.PNG_EXTENSION)) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + signup.constants.AppConstants.PNG_EXTENSION);
            }

            try {
                // 2. 뷰에서 시간표 패널(JPanel)을 가져옴
                JPanel panel = vSchedule.getTimetablePanel();
                
                // 3. 패널 크기만큼의 빈 이미지 버퍼 생성
                BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                
                // 4. 패널의 그림을 이미지 버퍼에 그림 (캡처)
                Graphics2D g2 = image.createGraphics();
                panel.print(g2); // paint() 대신 print() 사용 (더 안정적)
                g2.dispose();

                // 5. 파일로 저장
                ImageIO.write(image, "png", fileToSave);
                
                JOptionPane.showMessageDialog(vSchedule, "시간표가 저장되었습니다!\n" + fileToSave.getAbsolutePath(), "저장 완료", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(vSchedule, "저장 중 오류가 발생했습니다.", "실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}