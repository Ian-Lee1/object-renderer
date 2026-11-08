package Panels;

import Objects.World;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CmdPanel extends JPanel {
    public WorldPanel wp;
    public CmdPanel(){
        JTextField cmdField = new JTextField(30);
        this.setFocusable(true);
        this.add(cmdField);
        this.setVisible(false);

        cmdField.addActionListener(e -> {
            wp.processCommand(cmdField.getText());
            cmdField.setText("");

    });
    }


}
