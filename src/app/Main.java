package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
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
