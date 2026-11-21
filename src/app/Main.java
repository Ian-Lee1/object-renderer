package app;

import Objects.Forces.ParticleLifeInteractions;
import Vector.Vec2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;

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