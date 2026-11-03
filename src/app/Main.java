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


//ent_create phys_pill origin 100,0 size 3 vel 0,10, movable 1 colorIdx 0
//ent_create phys_pill origin 200,0 size 1 vel -600,0, movable 1 colorIdx 0 mass 15

//ent_create phys_pill origin -200,0 size 1 vel 600,0, movable 1 colorIdx 0 mass 15 length 60