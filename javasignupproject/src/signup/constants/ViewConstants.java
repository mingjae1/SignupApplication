package signup.constants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * View 관련 모든 상수와 공통 UI 유틸리티 메서드를 관리하는 클래스입니다.
 * 버튼 스타일, 폰트, 색상, 크기 등의 상수를 중앙에서 관리합니다.
 */
public final class ViewConstants {
    
    private ViewConstants() {
    }
    
    // ==================== 폰트 관련 상수 ====================
    public static final String FONT_SANS_SERIF = "SansSerif";
    public static final String FONT_DIGITAL = "Digital-7";
    public static final int FONT_SIZE_HEADER = 14;
    public static final int FONT_SIZE_BUTTON = 14;
    public static final int FONT_SIZE_LABEL = 13;
    public static final int FONT_SIZE_CLOCK = 48;
    public static final int FONT_SIZE_DATE = 14;
    
    // ==================== 색상 관련 상수 ====================
    public static final Color COLOR_PRIMARY = new Color(0, 120, 215);
    public static final Color COLOR_SECONDARY = new Color(100, 100, 100);
    public static final Color COLOR_LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color COLOR_WHITE = Color.WHITE;
    public static final Color COLOR_SEPARATOR = Color.LIGHT_GRAY;
    public static final Color COLOR_CLOCK_BG_LIGHT = COLOR_WHITE;
    public static final Color COLOR_CLOCK_BG_DARK = new Color(30, 30, 30);
    public static final Color COLOR_CLOCK_TIME_DARK = new Color(156, 220, 254);
    public static final Color COLOR_CLOCK_DATE_DARK = new Color(180, 180, 180);
    
    // ==================== 크기 관련 상수 ====================
    public static final Dimension HEADER_SIZE = new Dimension(0, 50);
    public static final Dimension SIDEBAR_SIZE = new Dimension(200, 0);
    public static final Dimension SIDEBAR_BUTTON_SIZE = new Dimension(180, 45);
    public static final Dimension CLOCK_POPUP_SIZE = new Dimension(300, 150);
    public static final Dimension BUTTON_DEFAULT = new Dimension(100, 40);
    
    // ==================== 패딩/여백 상수 ====================
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int BUTTON_GAP = 5;
    public static final int MENU_GAP = 5;
    
    // ==================== 시계 관련 상수 ====================
    public static final int CLOCK_UPDATE_INTERVAL = 1000; // 1초
    public static final int CLOCK_X_OFFSET = 20;
    public static final int CLOCK_Y_OFFSET = 80;
    
    // ==================== 날짜/시간 포맷 ====================
    public static final String DATE_FORMAT_TIME = "HH:mm:ss";
    public static final String DATE_FORMAT_FULL = "yyyy년 MM월 dd일 EEEE";
    
    // ==================== UI 텍스트 상수 ====================
    public static final String TEXT_PROGRAM_TITLE = "수강신청 프로그램";
    public static final String TEXT_MENU_TOGGLE = "☰";
    public static final String TEXT_BACK = "◀";
    public static final String TEXT_NEXT = "▶";
    public static final String TEXT_REFRESH = "새로고침";
    public static final String TEXT_LOGOUT = "로그아웃";
    public static final String TEXT_SEARCH = "🔍  강좌 검색";
    public static final String TEXT_REGISTER = "📝  수강신청 내역";
    public static final String TEXT_PREREGISTER = "🛒  미리담기 내역";
    public static final String TEXT_TIMETABLE = "📅  시간표";
    public static final String TEXT_MY_INFO = "👤  내 정보";
    public static final String TEXT_CLOCK = "🕐  시계";
    public static final String TEXT_THEME = "🌗  테마 변경";
    public static final String TEXT_ADMIN = "⚙️  강의 관리";
    public static final String TEXT_SEPARATOR = "  |  ";
    public static final String TEXT_SPACE = " ";
    public static final String TEXT_CLOCK_TITLE = "시계";
    public static final String TEXT_MY_INFO_TITLE = "학적 사항 / 비밀번호";
    public static final String TEXT_PASSWORD_CHANGE = "비밀번호 변경";
    public static final String TEXT_CURRENT_PASSWORD = "현재 비밀번호:";
    public static final String TEXT_NEW_PASSWORD = "새 비밀번호:";
    public static final String TEXT_CONFIRM_PASSWORD = "새 비밀번호 확인:";
    
    // ==================== 공통 UI 유틸리티 메서드 ====================
    
    /**
     * 표준 사이드바 버튼을 생성합니다.
     */
    public static JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_BUTTON));
        btn.setPreferredSize(SIDEBAR_BUTTON_SIZE);
        btn.setFocusPainted(false);
        return btn;
    }
    
    /**
     * 헤더 버튼을 생성합니다.
     */
    public static JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_BUTTON));
        btn.setFocusPainted(false);
        return btn;
    }
    
    /**
     * 헤더 라벨을 생성합니다.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_HEADER));
        return lbl;
    }
    
    /**
     * 표준 패널을 생성합니다.
     */
    public static JPanel createStandardPanel(LayoutManager layout) {
        return new JPanel(layout);
    }
    
    /**
     * 구분선을 가진 패널 보더를 생성합니다.
     */
    public static Border createSeparatorBorder() {
        return BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SEPARATOR)
        );
    }
    
    /**
     * 좌우 구분선 보더를 생성합니다.
     */
    public static Border createVerticalSeparatorBorder() {
        return BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_SEPARATOR);
    }
    
    /**
     * 빈 보더를 생성합니다.
     */
    public static Border createEmptyBorder(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }
    
    /**
     * FlowLayout을 생성합니다.
     */
    public static FlowLayout createFlowLayout(int align, int hgap, int vgap) {
        return new FlowLayout(align, hgap, vgap);
    }
    
    /**
     * GridLayout을 생성합니다.
     */
    public static GridLayout createGridLayout(int rows, int cols, int hgap, int vgap) {
        return new GridLayout(rows, cols, hgap, vgap);
    }
    
    /**
     * 오류 메시지를 표시합니다.
     */
    public static void showErrorMessage(JFrame parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 오류 메시지를 표시합니다. (Component 버전)
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 정보 메시지를 표시합니다.
     */
    public static void showInfoMessage(JFrame parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 정보 메시지를 표시합니다. (Component 버전)
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 확인 다이얼로그를 표시합니다.
     */
    public static int showConfirmDialog(JFrame parent, Object message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title, 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * 입력 다이얼로그를 표시합니다.
     */
    public static String showInputDialog(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }
    
    // ==================== View 상태 관리 메서드 ====================
    
    /**
     * Dialog를 모달로 표시합니다.
     */
    public static void showModal(JDialog dialog) {
        dialog.setVisible(true);
    }
    
    /**
     * Dialog를 숨깁니다.
     */
    public static void hideModal(JDialog dialog) {
        dialog.setVisible(false);
    }
    
    /**
     * Frame 크기를 변경합니다.
     */
    public static void resizeFrame(JFrame frame, int width, int height) {
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
    }
    
    /**
     * Frame을 화면 중앙에 위치시킵니다.
     */
    public static void centerFrame(JFrame frame) {
        frame.setLocationRelativeTo(null);
    }
    
    /**
     * Dialog를 Frame 기준으로 중앙에 위치시킵니다.
     */
    public static void centerDialog(JDialog dialog, JFrame owner) {
        dialog.setLocationRelativeTo(owner);
    }
}