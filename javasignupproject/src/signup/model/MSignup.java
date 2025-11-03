package signup.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 회원가입 처리를 담당하는 모델 클래스입니다.
 * Account 파일에 접근하여 학번 중복을 검사하고 새 사용자를 등록합니다.
 */
public class MSignup {

    // (참고) 이 경로는 MLogin의 ACCOUNT_FILE 경로와 반드시 일치해야 합니다.
    private static final String ACCOUNT_FILE = "data/user/Account.txt";

    /**
     * 학번이 Account 파일에 이미 존재하는지 확인합니다.
     * (파일 형식: 이름,학번,아이디,비밀번호)
     *
     * @param studentId CSignup 컨트롤러에서 전달받은 확인할 학번
     * @return 중복된 학번이 존재하면 true, 그렇지 않으면 false를 반환합니다.
     */
    public boolean isStudentIdDuplicate(String studentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userInfo = line.split(",");
                // 학번은 2번째(인덱스 1)에 위치
                if (userInfo.length >= 2) {
                    String storedStudentId = userInfo[1].trim(); // 파일 값 공백 제거
                    
                    if (storedStudentId.equals(studentId)) {
                        return true; // 중복된 학번 발견
                    }
                }
            }
        } catch (IOException e) {
            // 파일이 최초로 생성되는 경우(FileNotFoundException) 등은
            // 중복이 아니므로 false를 반환하게 됩니다.
        }
        return false; // 중복된 학번 없음
    }

    /**
     * 새로운 사용자 정보를 Account 파일에 한 줄로 추가합니다.
     * 저장하기 전에 모든 값의 앞뒤 공백을 제거합니다.
     *
     * @param studentId 사용자 학번
     * @param id        사용자 아이디
     * @param password  사용자 비밀번호
     * @param name      사용자 이름
     * @return 저장이 성공하면 true, 실패(IOException)하면 false를 반환합니다.
     */
    public boolean registerUser(String studentId, String id, String password, String name) {
        // FileWriter(ACCOUNT_FILE, true)는 파일을 이어쓰기(append) 모드로 엽니다.
        try (FileWriter fw = new FileWriter(ACCOUNT_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) 
        {
            // 저장할 데이터의 앞뒤 공백을 모두 제거 (데이터 일관성 유지)
            String dataLine = String.join(",", 
                name.trim(), 
                studentId.trim(), 
                id.trim(), 
                password.trim() // 비밀번호는 CSignup에서 이미 검증됨
            );
            
            out.println(dataLine); // 파일에 한 줄 쓰기
            return true; // 저장 성공
            
        } catch (IOException e) {
            e.printStackTrace(); // 파일 쓰기 오류 시 콘솔에 로그 출력
            return false; // 저장 실패
        }
    }
}