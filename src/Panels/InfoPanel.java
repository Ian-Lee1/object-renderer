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
    JTextField objCmdField;
    public InfoPanel(){
        objectInfo = new JTextArea();
        objCmdField = new JTextField(30);
        objCmdField.setMaximumSize(new Dimension(350,40));
        objectInfo.setEditable(false);
        objectInfo.setText("No object selected.");
        JScrollPane scrollPane = new JScrollPane(objectInfo);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(350,200));

        objCmdField.addActionListener(e -> processCommand(objCmdField.getText()));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setFocusable(true);
        this.add(scrollPane);
        this.add(objCmdField);
        this.setVisible(false);
    }

    private void processCommand(String cmd){
        if (cmd.startsWith("tracer")){
            selectedObj.attributes.put("t_l", 500.0);
            selectedObj.attributes.put("t_f", 5.0);
        }
        else if (cmd.startsWith("kill")){
            wp.world.command("ent_remove i " + wp.world.getIndex(selectedObj));

        objCmdField.setText("");
        String recreate = selectedObj.toString();

        System.out.println("Recreate: ent_create " + recreate
                .replace(':', ' ')
                .replace(" | ", " ")
                .replace("_  ", "_")
                .replace(", ", ",")
                .replace(" (", "")
                .replace(") ", " ")
                .replace("  ", " ")
                .replace("true", "1")
                .replace("false", "0").toLowerCase()
        );
        } else {
            selectedObj.changeFromString(" "+ objCmdField.getText());
        }

        wp.repaint();

    }
}
