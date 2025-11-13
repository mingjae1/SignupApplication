package signup.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 프로그램 전역에서 사용될 '상태' 정보를 저장하는 모델 클래스입니다.
 * (예: 현재 로그인한 사용자의 ID)
 */
public class MMain {
	
	private static final Logger logger = Logger.getLogger(MMain.class.getName());
    private String currentUserId; // 현재 로그인된 사용자 ID

    /**
     * MMain 모델을 초기화합니다.
     */
    public MMain() {
        this.currentUserId = null; // 로그인이 안 된 상태(null)로 시작
    }

    /**
     * 현재 로그인된 사용자의 ID를 반환합니다.
     * @return currentUserId (로그인 전이면 null)
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * CLogin 컨트롤러가 로그인 성공 시 호출하여 현재 사용자 ID를 설정합니다.
     * @param userId 로그인한 사용자의 ID
     */
    public void setCurrentUserId(String userId) {
        if(userId == null) {
			logger.log(Level.WARNING, "MMain: 로그아웃 되었습니다.");
		}
        else {
        	this.currentUserId = userId;
        	logger.log(Level.INFO, "MMain: {0}로 로그인 되었습니다..", userId);
        }
    }
}