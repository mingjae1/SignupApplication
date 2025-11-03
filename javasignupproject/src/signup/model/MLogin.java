package signup.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 사용자 로그인 인증을 처리하는 모델 클래스입니다.
 * Account 파일에 접근하여 사용자 정보를 검증합니다.
 */
public class MLogin {

    // (참고) 이 경로는 MSignup의 ACCOUNT_FILE 경로와 반드시 일치해야 합니다.
    private static final String ACCOUNT_FILE = "data/user/Account.txt";

    /**
     * 사용자가 입력한 아이디와 비밀번호가 파일에 저장된 정보와 일치하는지 검사합니다.
     * (파일 형식: 이름,학번,아이디,비밀번호)
     *
     * @param inputId 사용자가 입력한 아이디
     * @param inputPassword 사용자가 입력한 비밀번호
     * @return 정보가 일치하면 "사용자 이름(String)"을, 그렇지 않으면 null을 반환합니다.
     */
    public String validateUser(String inputId, String inputPassword) {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userInfo = line.split(",");
                
                if (userInfo.length == 4) {
                    // 파일에서 읽은 값의 앞뒤 공백을 제거합니다.
                    String storedName = userInfo[0].trim();
                    // String storedStudentId = userInfo[1].trim(); // (학번, 현재 불필요)
                    String storedId = userInfo[2].trim();
                    String storedPassword = userInfo[3].trim();

                    // 입력된 ID/PW와 파일의 ID/PW가 일치하는지 확인
                    if (storedId.equals(inputId) && storedPassword.equals(inputPassword)) {
                        return storedName; // 일치하면 "이름" 반환
                    }
                }
            }
        } catch (IOException e) {
             // 파일 읽기 오류 발생 시 콘솔에 로그 출력
             e.printStackTrace(); 
        }
        
        // 일치하는 사용자를 찾지 못했거나 오류가 발생하면 null 반환
        return null;
    }
}