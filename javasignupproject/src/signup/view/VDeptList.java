package signup.view;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VDeptList extends JDialog {
    private static final long serialVersionUID = 1L;

    public VDeptList(JFrame owner, List<String> deptList) {
        super(owner, "학과 코드표", true); // Modal (뒤에 클릭 불가)
        setSize(300, 400);
        setLocationRelativeTo(owner);
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        
        // 리스트 내용을 텍스트 영역에 채움
        StringBuilder sb = new StringBuilder();
        for (String info : deptList) {
            sb.append(info).append("\n");
        }
        textArea.setText(sb.toString());
        
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}