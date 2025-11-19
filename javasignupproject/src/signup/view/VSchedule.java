package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLaf;

import signup.model.MLecture;

 // 시간표를 보여주는 팝업 창(Dialog)입니다.
public class VSchedule extends JDialog {
	private static final long serialVersionUID = 1L;
    
    private JRadioButton radioRegister; 
    private JRadioButton radioBasket;   
    private TimetablePanel timetablePanel; // 시간표를 직접 그릴 패널
    private String FONT = "SansSerif";

	private VSchedule vSchedule;

    
    public VSchedule(JFrame owner) {
        super(owner, "내 시간표", false); 
        setSize(600, 800); // 세로로 좀 더 길게
        setLocationRelativeTo(owner); 
        setLayout(new BorderLayout());
        setResizable(false);
        
        // --- 1. 상단 패널 (전환 버튼) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        radioRegister = new JRadioButton("수강신청 시간표");
        radioBasket = new JRadioButton("미리담기 시간표");
        
        ButtonGroup group = new ButtonGroup();
        group.add(radioRegister);
        group.add(radioBasket);
        
        radioRegister.setSelected(true);
		radioRegister.setFont(new Font(FONT, Font.BOLD, 14));
        radioBasket.setFont(new Font(FONT, Font.BOLD, 14));

        topPanel.add(radioRegister);
        topPanel.add(radioBasket);
        
        add(topPanel, BorderLayout.NORTH);

        // --- 2. 중앙 패널 (커스텀 시간표) ---
        timetablePanel = new TimetablePanel();
        // 스크롤 가능하도록 JScrollPane에 넣음
        JScrollPane scrollPane = new JScrollPane(timetablePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 조절
        
        add(scrollPane, BorderLayout.CENTER);
    }

    
    /**
     * 내부 클래스: 실제 시간표 그림을 그리는 패널
     */
    class TimetablePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        
        private static final int START_HOUR = 9;  // 09:00 시작
        private static final int END_HOUR = 19;   // 19:00 종료
        private static final int HOUR_HEIGHT = 60; // 1시간당 높이 (픽셀)
        private static final int HEADER_HEIGHT = 30; // 요일 헤더 높이
        private static final int TIME_COL_WIDTH = 50; // 시간 표시 열 너비
        
        private List<MLecture> lectures = new ArrayList<>();

     // [1] 다크 모드용 차분한 색상
        private Color[] darkColors = { 
        	new Color(65, 95, 155),  // Deep Blue (차분한 딥 블루)
        	new Color(50, 120, 80),  // Deep Green (어두운 숲색)
        	new Color(150, 60, 60),  // Deep Red (와인색)
        	new Color(160, 120, 30), // Deep Gold (어두운 황금색)
        	new Color(100, 70, 150), // Deep Purple (어두운 보라)
        	new Color(40, 100, 120)  // Deep Teal (어두운 청록)
            };
        
        // [2] 라이트 모드용 밝고 화사한 색상 (요청하신 부분)
        private Color[] lightColors = {
            new Color(173, 216, 230), // Light Blue
            new Color(144, 238, 144), // Light Green
            new Color(255, 182, 193), // Light Pink
            new Color(255, 218, 185), // Peach Puff (밝은 주황)
            new Color(221, 160, 221), // Plum (밝은 보라)
            new Color(176, 224, 230)  // Powder Blue
        };

        public TimetablePanel() {
            int totalHeight = HEADER_HEIGHT + (END_HOUR - START_HOUR) * HOUR_HEIGHT + 50;
            setPreferredSize(new Dimension(500, totalHeight));

        }

        public void setLectures(List<MLecture> lectures) {
            this.lectures = lectures;
        }	

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int dayWidth = (width - TIME_COL_WIDTH) / 5; 

            // [테마 감지] 현재 다크모드인지 확인
            boolean isDark = FlatLaf.isLafDark();

            // 테마에 따른 그리드 및 글자 색상 설정
            Color gridColor = isDark ? new Color(80, 80, 80) : new Color(200, 200, 200);
            Color timeTextColor = isDark ? new Color(180, 180, 180) : new Color(100, 100, 100);
            Color headerTextColor = isDark ? Color.WHITE : Color.BLACK;
            Color lineColor = isDark ? new Color(85, 85, 85) : new Color(220, 220, 220);

            // 1. 세로선 (시간 영역 구분)
            g2.setColor(gridColor);
            g2.drawLine(TIME_COL_WIDTH, 0, TIME_COL_WIDTH, height); 

            g2.setFont(new Font(FONT, Font.PLAIN, 12));

            // 2. 시간 행 그리기
            for (int i = START_HOUR; i < END_HOUR; i++) {
                int y = HEADER_HEIGHT + (i - START_HOUR) * HOUR_HEIGHT;
                
                // 시간 텍스트
                g2.setColor(timeTextColor);
                g2.drawString(String.format("%02d:00", i), 5, y + 15);
                
                // 가로선
                g2.setColor(lineColor);
                g2.drawLine(0, y, width, y);
            }

            // 3. 요일 헤더 그리기
            String[] days = {"월", "화", "수", "목", "금"};
            for (int i = 0; i < 5; i++) {
                int x = TIME_COL_WIDTH + i * dayWidth;
                
                g2.setColor(headerTextColor); 
                g2.setFont(new Font(FONT, Font.BOLD, 14));
                g2.drawString(days[i], x + dayWidth / 2 - 10, 20);
                
                g2.setColor(gridColor);
                g2.drawLine(x, 0, x, height);
            }

            // 4. 강의 블록 그리기
            if (lectures != null) {
                int colorIdx = 0;
                // 테마에 맞는 색상 팔레트 선택
                Color[] currentPalette = isDark ? darkColors : lightColors ;
                
                for (MLecture lecture : lectures) {
                    drawLectureBlock(g2, lecture, dayWidth, currentPalette[colorIdx % currentPalette.length], isDark);
                    colorIdx++;
                }
            }
        }

        // [핵심 수정] 시간 계산 로직 점검
        private void drawLectureBlock(Graphics2D g2, MLecture lecture, int dayWidth, Color color, boolean isDark) {
            String timeStr = lecture.getSchedule(); // "월수0900-1015"
            if (timeStr == null || timeStr.length() < 5) return;
            
            try {
                // 1. 파싱 (이전과 동일)
                String dayStr = timeStr.replaceAll("[0-9\\-]", ""); 
                String timePart = timeStr.replaceAll("[^0-9\\-]", ""); 
                String[] times = timePart.split("-");
                
                int startH = Integer.parseInt(times[0].substring(0, 2));
                int startM = Integer.parseInt(times[0].substring(2));
                int endH = Integer.parseInt(times[1].substring(0, 2));
                int endM = Integer.parseInt(times[1].substring(2));

                // 2. [정밀 계산] 분 단위 -> 픽셀 변환
                // 09:00부터 흐른 시간을 분 단위로 계산
                double pixelsPerMinute = (double)HOUR_HEIGHT / 60.0;
                int startTotalMinutes = (startH - START_HOUR) * 60 + startM;
                int endTotalMinutes = (endH - START_HOUR) * 60 + endM;
                
                int startY = HEADER_HEIGHT + (int)(startTotalMinutes * pixelsPerMinute);
                int endY = HEADER_HEIGHT + (int)(endTotalMinutes * pixelsPerMinute);
                
                int blockHeight = endY - startY;

                // 3. 그리기
                for (char day : dayStr.toCharArray()) {
                    int dayIdx = getDayIndex(day);
                    if (dayIdx == -1) continue;

                    int x = TIME_COL_WIDTH + dayIdx * dayWidth;

                    // 블록 그리기
                    g2.setColor(color);
                    g2.fillRoundRect(x + 2, startY + 1, dayWidth - 4, blockHeight - 2, 8, 8);
                    
                    // 텍스트 그리기 
                    // 다크 모드 -> 흰색 글씨 (배경이 어두움)
                    // 라이트 모드 -> 검정 글씨 (배경이 밝음)
                    Color mainTextColor = isDark ? Color.WHITE : Color.BLACK;
                    Color subTextColor = isDark ? new Color(220, 220, 220) : new Color(60, 60, 60);
                    Color timeColor = isDark ? new Color(200, 200, 200) : new Color(80, 80, 80);
                    
                    g2.setColor(mainTextColor);
                    
                    int fontSize = 13;
                    if (blockHeight < 40) fontSize = 10;
                    // 과목명
                    g2.setFont(new Font(FONT, Font.BOLD, fontSize));
                    g2.drawString(lecture.getName(), x + 5, startY + 25);
                    
                    // 교수명 
                    if (blockHeight > 45) {
                         g2.setFont(new Font(FONT, Font.PLAIN, fontSize - 2));
                         g2.setColor(subTextColor);
                         g2.drawString(lecture.getProfessor(), x + 5, startY + 45);
                    }
                    
                    // 시간 텍스트 
                    if (blockHeight > 55) {
                        g2.setColor(timeColor);
                        g2.drawString(String.format("%02d:%02d~%02d:%02d", startH, startM, endH, endM), x + 5, startY + 60);
                    }
                }
            } catch (Exception e) {
            	JOptionPane.showMessageDialog(vSchedule, "시간표를 불러올 수 없습니다.", "오류", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        private int getDayIndex(char day) {
        	switch(day) {
        	case '월': return 0; case '화': return 1; case '수': return 2; 
        	case '목': return 3; case '금': return 4; default: return -1;
        	}
        }
    }

	public void clearTable() {
		if (timetablePanel != null) {
            timetablePanel.setLectures(new ArrayList<>());
            timetablePanel.repaint();
		}	
	}

    /**
     * 컨트롤러가 데이터를 전달해주면 시간표 패널을 다시 그립니다.
     */
    public void updateSchedule(List<MLecture> lectures) {
        timetablePanel.setLectures(lectures);
        timetablePanel.repaint(); // 다시 그리기 요청 
    }
    
    // --- Getters & Methods ---
    public JRadioButton getRadioRegister() { return radioRegister; }
    public JRadioButton getRadioBasket() { return radioBasket; }
    
}