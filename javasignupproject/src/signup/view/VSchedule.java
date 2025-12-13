package signup.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLaf;

import signup.model.MLecture;

 // 시간표를 보여주는 팝업 창(Dialog)입니다.
public class VSchedule extends JDialog {
	private static final long serialVersionUID = 1L;
    private static final String BASE_FONT = "SansSerif";
    
    private JRadioButton regScheduleButton; 
    private JRadioButton preregScheduleButton;
    private JButton saveImageButton; // 이미지 저장 버튼
    private TimetablePanel timetablePanel; // 시간표를 직접 그릴 패널
    private static final Logger logger = Logger.getLogger(VSchedule.class.getName());
    
    // --- [추가] 외부 클래스에서 강의 목록 관리 ---
    private transient List<MLecture> lectures = new ArrayList<>();

    
    public VSchedule(JFrame vSchedule) {
        super(vSchedule, "내 시간표", false); 
        setSize(600, 800); // 세로로 좀 더 길게
        setLocationRelativeTo(vSchedule); 
        setLayout(new BorderLayout());
        setResizable(false);
        
        // --- 1. 상단 패널 (전환 버튼) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        regScheduleButton = new JRadioButton("수강신청 시간표");
        preregScheduleButton = new JRadioButton("미리담기 시간표");
        
        ButtonGroup group = new ButtonGroup();
        group.add(regScheduleButton);
        group.add(preregScheduleButton);
        
        regScheduleButton.setSelected(true);
		regScheduleButton.setFont(new Font(BASE_FONT, Font.BOLD, 14));
        preregScheduleButton.setFont(new Font(BASE_FONT, Font.BOLD, 14));
        
        saveImageButton = new JButton("이미지 저장 📸");
        saveImageButton.setFocusPainted(false);
        
        topPanel.add(regScheduleButton);
        topPanel.add(preregScheduleButton);
        topPanel.add(new JLabel("  |  ")); // 구분선
        topPanel.add(saveImageButton);
        
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
        private static final int START_HOUR = 9;
        private static final int END_HOUR = 19;
        private static final int HOUR_HEIGHT = 60;
        private static final int HEADER_HEIGHT = 30;
        private static final int TIME_COL_WIDTH = 50;
        private static final Color[] DARK_PALETTE = {
            new Color(65, 95, 155),
            new Color(50, 120, 80),
            new Color(150, 60, 60),
            new Color(160, 120, 30),
            new Color(100, 70, 150),
            new Color(40, 100, 120)
        };
        private static final Color[] LIGHT_PALETTE = {
            new Color(173, 216, 230),
            new Color(144, 238, 144),
            new Color(255, 182, 193),
            new Color(255, 218, 185),
            new Color(221, 160, 221),
            new Color(176, 224, 230)
        };

        TimetablePanel() {
            int totalHeight = HEADER_HEIGHT + (END_HOUR - START_HOUR) * HOUR_HEIGHT + 50;
            setPreferredSize(new Dimension(500, totalHeight));
        }

        void setLectures(List<MLecture> lectures) {
            VSchedule.this.lectures = lectures == null ? new ArrayList<>() : new ArrayList<>(lectures);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int dayWidth = (width - TIME_COL_WIDTH) / 5;
            boolean isDark = FlatLaf.isLafDark();

            renderGrid(g2, width, height, dayWidth, isDark);
            paintLectures(g2, dayWidth, isDark);
        }

        private void renderGrid(Graphics2D g2, int width, int height, int dayWidth, boolean isDark) {
            Color gridColor = isDark ? new Color(80, 80, 80) : new Color(200, 200, 200);
            Color lineColor = isDark ? new Color(85, 85, 85) : new Color(220, 220, 220);
            Color timeTextColor = isDark ? new Color(180, 180, 180) : new Color(100, 100, 100);
            Color headerTextColor = isDark ? Color.WHITE : Color.BLACK;

            drawTimeRows(g2, width, timeTextColor, lineColor);
            drawDayHeaders(g2, height, dayWidth, gridColor, headerTextColor);
        }

        private void drawTimeRows(Graphics2D g2, int width, Color textColor, Color lineColor) {
            g2.setFont(new Font(BASE_FONT, Font.PLAIN, 12));
            for (int hour = START_HOUR; hour < END_HOUR; hour++) {
                int y = HEADER_HEIGHT + (hour - START_HOUR) * HOUR_HEIGHT;
                g2.setColor(textColor);
                g2.drawString(String.format("%02d:00", hour), 5, y + 15);
                g2.setColor(lineColor);
                g2.drawLine(0, y, width, y);
            }
        }

        private void drawDayHeaders(Graphics2D g2, int height, int dayWidth, Color gridColor, Color textColor) {
            String[] days = {"월", "화", "수", "목", "금"};
            g2.setFont(new Font(BASE_FONT, Font.BOLD, 14));
            for (int idx = 0; idx < days.length; idx++) {
                int x = TIME_COL_WIDTH + idx * dayWidth;
                g2.setColor(textColor);
                g2.drawString(days[idx], x + dayWidth / 2 - 10, 20);
                g2.setColor(gridColor);
                g2.drawLine(x, 0, x, height);
            }
            g2.drawLine(TIME_COL_WIDTH, 0, TIME_COL_WIDTH, height);
        }

        private void paintLectures(Graphics2D g2, int dayWidth, boolean isDark) {
            if (lectures == null || lectures.isEmpty()) {
                return;
            }
            Color[] palette = isDark ? DARK_PALETTE : LIGHT_PALETTE;
            int colorIdx = 0;
            for (MLecture lecture : lectures) {
                Color blockColor = palette[colorIdx % palette.length];
                drawLectureBlock(g2, lecture, dayWidth, blockColor, isDark);
                colorIdx++;
            }
        }

        private void drawLectureBlock(Graphics2D g2, MLecture lecture, int dayWidth, Color color, boolean isDark) {
            ScheduleWindow window = parseSchedule(lecture.getSchedule());
            if (window == null) {
                return;
            }

            double pixelsPerMinute = HOUR_HEIGHT / 60.0;
            int startY = HEADER_HEIGHT + (int) (window.startMinutes * pixelsPerMinute);
            int blockHeight = (int) ((window.endMinutes - window.startMinutes) * pixelsPerMinute);
            Font nameFont = determineNameFont(blockHeight);
            g2.setFont(nameFont);
            FontMetrics fontMetrics = g2.getFontMetrics();
            NameLines nameLines = splitName(lecture.getName(), fontMetrics, dayWidth - 10);

            for (char day : window.days) {
                int dayIdx = getDayIndex(day);
                if (dayIdx == -1) {
                    continue;
                }
                int x = TIME_COL_WIDTH + dayIdx * dayWidth;
                paintLectureBackground(g2, x, startY, dayWidth, blockHeight, color);
                renderLectureTexts(g2, x, startY, blockHeight, lecture, nameLines, isDark, nameFont, window);
            }
        }

        private Font determineNameFont(int blockHeight) {
            int fontSize = blockHeight < 40 ? 10 : 12;
            return new Font(BASE_FONT, Font.BOLD, fontSize);
        }

        private void paintLectureBackground(Graphics2D g2, int x, int startY, int dayWidth, int blockHeight, Color color) {
            g2.setColor(color);
            g2.fillRoundRect(x + 2, startY + 1, dayWidth - 4, blockHeight - 2, 8, 8);
        }

        private void renderLectureTexts(Graphics2D g2, int x, int startY, int blockHeight,
                                         MLecture lecture, NameLines nameLines, boolean isDark,
                                         Font nameFont, ScheduleWindow window) {
            Color mainTextColor = isDark ? Color.WHITE : Color.BLACK;
            Color subTextColor = isDark ? new Color(220, 220, 220) : new Color(60, 60, 60);
            Color timeColor = isDark ? new Color(200, 200, 200) : new Color(80, 80, 80);

            g2.setColor(mainTextColor);
            g2.setFont(nameFont);
            int currentY = startY + 20;
            g2.drawString(nameLines.primary, x + 5, currentY);
            if (!nameLines.secondary.isEmpty()) {
                g2.drawString(nameLines.secondary, x + 5, currentY + 15);
                currentY += 15;
            }

            if (shouldRenderProfessor(blockHeight, nameLines.secondary)) {
                g2.setFont(new Font(BASE_FONT, Font.PLAIN, nameFont.getSize() - 2));
                g2.setColor(subTextColor);
                g2.drawString(lecture.getProfessor(), x + 5, currentY + 25);
                currentY += 15;
            }

            g2.setColor(timeColor);
            g2.drawString(window.formatDuration(), x + 5, currentY + 30);
        }

        private boolean shouldRenderProfessor(int blockHeight, String secondLine) {
            return blockHeight > (secondLine.isEmpty() ? 45 : 60);
        }

        private NameLines splitName(String name, FontMetrics fm, int maxWidth) {
            if (name == null) {
                return new NameLines("", "");
            }
            if (fm.stringWidth(name) <= maxWidth) {
                return new NameLines(name, "");
            }
            int approxChars = Math.max(1, maxWidth / fm.charWidth('가'));
            int splitIndex = Math.min(approxChars, name.length());
            return new NameLines(name.substring(0, splitIndex), name.substring(splitIndex));
        }

        private ScheduleWindow parseSchedule(String schedule) {
            if (schedule == null || schedule.length() < 5) {
                return null;
            }
            try {
                String dayStr = schedule.replaceAll("[0-9\\-]", "");
                String timePart = schedule.replaceAll("[^0-9\\-]", "");
                String[] times = timePart.split("-");
                int startMinutes = toRelativeMinutes(times[0]);
                int endMinutes = toRelativeMinutes(times[1]);
                return new ScheduleWindow(dayStr.toCharArray(), startMinutes, endMinutes);
            } catch (RuntimeException ex) {
                final String invalidSchedule = schedule;
                logger.log(Level.WARNING, ex, () -> "강의 시간 파싱 오류: " + invalidSchedule);
                return null;
            }
        }

        private int toRelativeMinutes(String hhmm) {
            int hour = Integer.parseInt(hhmm.substring(0, 2));
            int minute = Integer.parseInt(hhmm.substring(2));
            return (hour - START_HOUR) * 60 + minute;
        }

        private int getDayIndex(char day) {
         switch(day) {
         case '월': return 0; case '화': return 1; case '수': return 2; 
         case '목': return 3; case '금': return 4; default: return -1;
         }
        }

        private record NameLines(String primary, String secondary) {}

        private record ScheduleWindow(char[] days, int startMinutes, int endMinutes) {
            String formatDuration() {
                int absoluteStart = START_HOUR * 60 + startMinutes;
                int absoluteEnd = START_HOUR * 60 + endMinutes;
                return String.format("%02d:%02d~%02d:%02d",
                    absoluteStart / 60, absoluteStart % 60,
                    absoluteEnd / 60, absoluteEnd % 60);
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
    public JRadioButton getRadioRegister() { return regScheduleButton; }
    public JRadioButton getRadioBasket() { return preregScheduleButton; }
    public JButton getSaveImageButton() { return saveImageButton; }
    public JPanel getTimetablePanel() { return timetablePanel; }
    
}