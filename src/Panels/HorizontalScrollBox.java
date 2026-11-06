package Panels;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public class HorizontalScrollBox extends JComboBox<String> {

    public HorizontalScrollBox(String[] items) {
        super(items);
        // Override UI to inject custom popup scroller
        BasicComboBoxUI UI = new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane pane = new JScrollPane(list,
                                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        pane.setPreferredSize(new Dimension(300, 600));
                        return pane;
                    }
                };
                popup.setPreferredSize(new Dimension(300, 600));
                return popup;
            }
        };
        setUI(UI);
    }
}
