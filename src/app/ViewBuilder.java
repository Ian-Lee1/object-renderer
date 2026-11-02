package app;

import Constants.Constants;
import Objects.PhysicsWorld;
import Panels.CmdPanel;
import Panels.WorldPanel;
import Objects.TestWorld;
import Objects.World;
import Solver.LegacySolver.CircleCollisions;

import javax.swing.*;
import java.awt.*;

public class ViewBuilder {
    private final JPanel mainPanel = new JPanel();
    private WorldPanel worldPanel;
    private World world;

    public ViewBuilder() {
        BorderLayout borderLayout = new BorderLayout();
        mainPanel.setLayout(borderLayout);
        }

    public ViewBuilder addTestWorld(){
        world = new TestWorld();
        world.command("set b 100,0,0");
        world.command("set l 255,255,155");
        return this;
    }

    public ViewBuilder addPhysWorld(){
        world = new PhysicsWorld();
        //((PhysicsWorld) world).setSolver(new CircleCollisions()); //TODO
        return this;
    }

    public ViewBuilder addWorldFrame(){
        worldPanel = new WorldPanel(world);
        CmdPanel cmdPanel = new CmdPanel();
        cmdPanel.wp = worldPanel;
        worldPanel.setCmdPanel(cmdPanel);
        mainPanel.add(worldPanel);
        mainPanel.add(cmdPanel, BorderLayout.SOUTH);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("View");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setPreferredSize(new Dimension(Constants.DEFAULT_PROGRAM_WIDTH, Constants.DEFAULT_PROGRAM_HEIGHT));
        application.add(mainPanel);
        return application;
    }
}
