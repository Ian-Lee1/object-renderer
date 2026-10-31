package Frames;

import Objects.World;

import javax.swing.*;
import java.awt.*;

public class WorldFrame extends JFrame {
    World world;
    public WorldFrame(World world){
        this.world = world;
    }

    public void paint(Graphics g){
        world.draw(g);
    }
}
