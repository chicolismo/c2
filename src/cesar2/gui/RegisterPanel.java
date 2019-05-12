package cesar2.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cesar2.gui.display.BinaryDisplay;
import cesar2.gui.display.DigitalDisplay;
import cesar2.util.Shorts;

public class RegisterPanel extends JPanel {
    private static final long serialVersionUID = -2886129797948530474L;

    private boolean isDecimal = true;
    private PropertyChangeSupport support;
    private short value;
    private final String title;
    private final DigitalDisplay digitalDisplay;
    private final BinaryDisplay binaryDisplay;

    public RegisterPanel(String title) {
        this.support = new PropertyChangeSupport(this);
        this.title = title;
        this.value = (short) 0;
        this.digitalDisplay = new DigitalDisplay();
        this.binaryDisplay = new BinaryDisplay();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(digitalDisplay);
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(binaryDisplay);

        initEvents();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public String getTitle() {
        return title;
    }

    public void setValue(short value) {
        digitalDisplay.setValue(Shorts.toUnsignedInt(value));
        binaryDisplay.setValue(Shorts.toUnsignedInt(value));
    }

    public void setDecimal(boolean isDecimal) {
        this.isDecimal = isDecimal;
        digitalDisplay.setDecimal(isDecimal);
    }

    private void initEvents() {
        final String decMessage = String.format("Digite um valor decimal para %s", getTitle());
        final String hexMessage = String.format("Digite um valor decimal para %s", getTitle());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    String text = JOptionPane.showInputDialog(isDecimal ? decMessage : hexMessage);
                    if (text != null) {
                        try {
                            short newValue = (short) Integer.parseInt(text, isDecimal ? 10 : 16);
                            support.firePropertyChange("value", value, newValue);
                            RegisterPanel.this.setValue(newValue);
                        }
                        catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(RegisterPanel.this, "Valor inválido", null,
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    RegisterPanel.this.requestFocus();
                }
            }
        });
    }
}
