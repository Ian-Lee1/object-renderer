package Panels;

import Objects.Physical.Physical;

import javax.swing.*;

import java.awt.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class InfoPanel extends JPanel {
    //Responsible for displaying a selected object's keyvalues, text field for more direct modification of selected keyvalues (ent_keyvalue command)
    //dropdown of forces, info, and text field
    public WorldPanel wp;
    public JTextArea objectInfo;
    public Physical selectedObj;
    public InfoPanel(){
        objectInfo = new JTextArea();
        JTextField objCmdField = new JTextField(30);
        objCmdField.setMaximumSize(new Dimension(350,40));
        objectInfo.setEditable(false);
        objectInfo.setText("No object selected.");
        JScrollPane scrollPane = new JScrollPane(objectInfo);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(350,200));

        objCmdField.addActionListener(e -> {
            wp.processCommand("ent_keyvalues " + wp.world.getIndex(selectedObj) + " " + objCmdField.getText());
            objCmdField.setText("");
        });
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setFocusable(true);
        this.add(scrollPane);
        this.add(objCmdField);
        this.setVisible(false);
    }

}
