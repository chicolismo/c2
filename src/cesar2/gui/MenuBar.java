package cesar2.gui;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBar extends JMenuBar {
    private static final long serialVersionUID = 5263940640841948006L;

    private final JMenuItem itemLoad;
    private final JMenuItem itemSave;
    private final JMenuItem itemSaveAs;
    private final JMenuItem itemExit;
    private final JMenuItem[] items;

    public MenuBar() {
        super();
        setBorderPainted(false);

        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        itemLoad = new JMenuItem("Carregar...");
        itemLoad.setActionCommand("load");
        itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));

        itemSave = new JMenuItem("Salvar");
        itemSave.setActionCommand("save");
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));

        itemSaveAs = new JMenuItem("Salvar como...");
        itemSaveAs.setActionCommand("save as");
        itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask | KeyEvent.SHIFT_DOWN_MASK));

        itemExit = new JMenuItem("Sair");
        itemExit.setActionCommand("exit");

        items = new JMenuItem[] { itemLoad, itemSave, itemSaveAs, itemExit };

        JMenu menuFile = new JMenu("Arquivo");
        menuFile.add(itemLoad);
        menuFile.add(itemSave);
        menuFile.add(itemSaveAs);
        menuFile.addSeparator();
        menuFile.add(itemSaveAs);

        JMenu menuEdit = new JMenu("Editar");
        JMenu menuView = new JMenu("Visualizar");
        JMenu menuHelp = new JMenu("?");

        add(menuFile);
        add(menuEdit);
        add(menuView);
        add(menuHelp);
    }

    public void addActionListener(ActionListener listener) {
        for (JMenuItem item : items) {
            item.addActionListener(listener);
        }
    }
}
