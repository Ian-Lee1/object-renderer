package app;

import Panels.HorizontalScrollBox;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class Main {
    public static void main(String[] args) {
/*
        JFrame frame = new JFrame("Horizontal Scroll Dropdown");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        String[] longItems = {
                "Short Item",
                "This is an exceptionally long menu item text that would normally cut off on a small dropdown window."
        };
        JTextArea text = new JTextArea("text\ntext");
        text.setEditable(false);
        text.setText("new text");
        JScrollPane scrollPane = new JScrollPane(text);
       scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(350,200));
        frame.add(scrollPane);
        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
*/
        ViewBuilder viewBuilder = new ViewBuilder();
        JFrame application = viewBuilder
                .addPhysWorld()
                .addWorldFrame()
                .build();
        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);

    }

}


//ent_create phys_pill origin 100,0 size 3 vel 0,10, movable 1 colorIdx 0
//ent_create phys_pill origin 200,0 size 1 vel -600,0, movable 1 colorIdx 0 mass 15

//ent_create phys_pill origin -200,0 size 1 vel 600,0, movable 1 colorIdx 0 mass 15 length 60