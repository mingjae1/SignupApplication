package signup.model;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MMain {
	
	private static final Logger logger = Logger.getLogger(MMain.class.getName());
    private String currentUserId;

    public MMain() {
        this.currentUserId = null;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public void setCurrentUserId(String userId) {
        if (userId == null) {
            logger.log(Level.WARNING, "MMain: 로그아웃 되었습니다.");
            this.currentUserId = null;
        } else {
            this.currentUserId = userId;
            logger.log(Level.INFO, "MMain: {0}로 로그인 되었습니다.", userId);
        }
    }
}