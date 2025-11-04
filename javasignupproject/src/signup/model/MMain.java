package signup.model;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 프로그램의 모든 데이터 로직을 처리하는 메인 모델 클래스입니다.
 * 현재 로그인한 사용자 정보를 관리하고, 파일에서 데이터를 로드하는 책임을 집니다.
 */
public class MMain {

    private String currentUserId; // 현재 로그인된 사용자 ID

    /**
     * MMain 모델을 초기화합니다.
     * (testUser는 CLogin이 setCurrentUserId를 호출하기 전의 임시 기본값입니다.)
     */
    public MMain() {
        this.currentUserId = "testUser"; 
    }

    /**
     * 현재 로그인된 사용자의 ID를 반환합니다.
     * @return currentUserId
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * CLogin 컨트롤러가 로그인 성공 시 호출하여 현재 사용자 ID를 설정합니다.
     * @param userId 로그인한 사용자의 ID
     */
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    /**
     * 현재 사용자의 '미리담기' 목록을 파일에서 로드합니다.
     * (경로: data/user/Pre/userID_PreList.txt)
     * @return '미리담기' 강의 목록 (ArrayList<Lecture>)
     */
    public List<Lecture> getBasketLectures() {
        String fileName = this.currentUserId + "_PreList.txt";
        String filePath = "data/user/Pre/" + fileName;

        System.out.println("Model: " + currentUserId + "의 최신 미리담기 목록 로드 (from " + filePath + ")");
        
        return loadLecturesFromFile(filePath);
    }

    /**
     * 현재 사용자의 '수강신청' 목록을 파일에서 로드합니다.
     * (경로: data/user/Register/userID_RegisterList.txt)
     * @return '수강신청' 강의 목록 (ArrayList<Lecture>)
     */
    public List<Lecture> getRegisteredLectures() {
        String fileName = this.currentUserId + "_RegisterList.txt";
        String filePath = "data/user/Register/" + fileName;

        System.out.println("Model: " + currentUserId + "의 최신 수강신청 목록 로드 (from " + filePath + ")");
        
        return loadLecturesFromFile(filePath);
    }

    /**
     * 지정된 경로의 텍스트 파일에서 강의 목록을 읽어옵니다.
     * (파일 형식: "과목코드 과목명 교수명 학점 시간표" - 5개 항목, 공백 구분)
     * @param filePath 읽어올 파일의 전체 경로
     * @return 파일에서 파싱된 강의 목록 (ArrayList<Lecture>)
     */
    private List<Lecture> loadLecturesFromFile(String filePath) {
        ArrayList<Lecture> lectures = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                
                String[] data = line.split(" ");
                
                if (data.length == 5) {
                    try {
                        String id = data[0].trim();
                        String name = data[1].trim();
                        String professor = data[2].trim();
                        int credits = Integer.parseInt(data[3].trim()); 
                        String schedule = data[4].trim();
                        
                        Lecture lecture = new Lecture(id, name, professor, credits, schedule);
                        lectures.add(lecture);

                    } catch (NumberFormatException e) {
                        System.err.println("데이터 파싱 오류: 학점이 숫자가 아닙니다. " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("정보: 파일이 아직 없습니다. 새 리스트를 반환합니다. " + filePath);
        } catch (IOException e) {
            System.err.println("오류: 파일을 읽는 중 예외 발생. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("데이터 처리 중 알 수 없는 오류: " + e.getMessage());
        }
        
        return lectures; // 파일이 없거나 비어있으면 빈 리스트 반환
    }
}