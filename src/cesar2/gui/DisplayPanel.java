package cesar2.gui;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import cesar2.gui.display.CharDisplay;

public class DisplayPanel extends JDialog {
    private static final long serialVersionUID = 6104897776450994666L;

    private final CharDisplay display;
    private int length;

    public DisplayPanel(Frame owner) {
        super(owner);
        setUndecorated(true);

        display = new CharDisplay();
        length = display.length();

        JPanel contentPane = new JPanel();
        setContentPane(contentPane);

        contentPane.setBorder(new BevelBorder(BevelBorder.RAISED));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(display);
        contentPane.setPreferredSize(contentPane.getPreferredSize());

        pack();
        setResizable(false);
        initEvents();
    }

    public void setValue(byte[] bytes) {
        int size = Math.min(bytes.length, length);
        for (int i = 0; i < size; ++i) {
            display.setValueAt(i, (char) bytes[i]);
        }
        display.repaint();
    }

    public void setValueAt(int index, char value) {
        if (isIndexInRange(index)) {
            display.setValueAt(index, value);
            display.repaint();
        }
    }

    private boolean isIndexInRange(int index) {
        return index > 0 && index < length;
    }

    private void initEvents() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point clickPoint = null;

            @Override
            public void mousePressed(MouseEvent event) {
                clickPoint = event.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                Point newPoint = event.getLocationOnScreen();
                newPoint.translate(-clickPoint.x, -clickPoint.y);
                DisplayPanel.this.setLocation(newPoint);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
}
