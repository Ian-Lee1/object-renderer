package Panels;

import Objects.World;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import static Constants.Constants.FPS;

public class WorldPanel extends JPanel implements KeyListener, MouseInputListener {
    private final World world;
    private boolean cmdMode = false;
    private boolean paused = false;
    private CmdPanel cmdPanel;

    public WorldPanel(World world){
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.world = world;
    }

    public void setCmdPanel(CmdPanel cmdPanel){
        this.cmdPanel = cmdPanel;
    }

    @Override
    public void paint(Graphics g){
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
        world.draw(g);

        if (cmdMode) {
            g.setFont(new Font("SansSerif", Font.BOLD, 32));
            g.setColor(Color.RED);
            g.drawString("ACCEPTING COMMANDS", (int)(g.getClipBounds().getWidth()*0.05), (int)(g.getClipBounds().getHeight()*0.05));
        }

        if(!paused && !cmdMode){
            world.update();
            try {
                Thread.sleep((int)(1/(0.0+FPS)*1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.repaint();}
    }

    public void processCommand(String str){
        world.command(str);
        this.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Fires when a key physically goes down (ideal for game controls)
        if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
            cmdPanel.setVisible(false);
            cmdPanel.setFocusable(false);
            cmdMode = !cmdMode;
            if (cmdMode) {
                cmdPanel.setVisible(true);
                cmdPanel.setFocusable(true);
            }
            this.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //No actions
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //No actions
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton()==MouseEvent.BUTTON1) {
            cmdMode = false;
            cmdPanel.setVisible(false);
            cmdPanel.setFocusable(false);
            this.requestFocusInWindow();
            this.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
