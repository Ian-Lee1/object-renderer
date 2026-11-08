package Panels;

import Objects.Physical.Physical;
import Objects.Viewport;
import Objects.World;
import Vector.Vec2;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

import static Constants.Constants.FPS;

public class WorldPanel extends JPanel implements KeyListener, MouseInputListener, MouseWheelListener {
    int mouselastX = -1;
    int mouselastY = -1;
    public final World world;
    private boolean cmdMode = false;
    private boolean paused = false;
    private CmdPanel cmdPanel;
    private InfoPanel infoPanel;
    private boolean tryUpdatePos = true;

    public WorldPanel(World world){
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.world = world;
    }

    public void setCmdPanel(CmdPanel cmdPanel){
        this.cmdPanel = cmdPanel;
    }
    public void setInfoPanel(InfoPanel infoPanel){
        this.infoPanel = infoPanel;
    }

    @Override
    public void paint(Graphics g){
        world.getViewport().antialiasing = cmdMode;
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
            cmdMode = !cmdMode;
            infoPanel.setVisible(cmdMode);
            infoPanel.setFocusable(cmdMode);
            cmdPanel.setVisible(cmdMode);
            cmdPanel.setFocusable(cmdMode);

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
        if(e.getButton()==MouseEvent.BUTTON1) { //click back into simulation
            cmdMode = !cmdMode;
            infoPanel.setVisible(cmdMode);
            infoPanel.setFocusable(cmdMode);
            cmdPanel.setVisible(cmdMode);
            cmdPanel.setFocusable(cmdMode);

            this.requestFocusInWindow();
            this.repaint();
        }else if (e.getButton()==MouseEvent.BUTTON3){ //detect selected object
            int x = e.getX();
            int y = e.getY();
            Physical obj = world.detectAt(this.getWidth(), this.getHeight(), x, this.getHeight()-y);
            infoPanel.objectInfo.setText("No object selected.");
            infoPanel.selectedObj = obj;
            if (obj != null){
                infoPanel.objectInfo.setText(obj.toString().replace('|', '\n'));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouselastX = -1;
        mouselastY = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouselastX < 0){
            mouselastX = e.getX();
            mouselastY = e.getY();
        } else {
            Viewport viewport = world.getViewport();
            int movedX = e.getX() - mouselastX;
            int movedY = e.getY() - mouselastY;

            double dx = (0.0 + movedX) / (this.getHeight() / viewport.zoom);
            double dy = (0.0 + movedY) / (this.getHeight() / viewport.zoom);
            viewport.origin = viewport.origin.add(new Vec2(-dx, dy));
            mouselastX = e.getX();
            mouselastY = e.getY();
        }
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(tryUpdatePos) {
            try {
                world.getAttributes().put("INTL_mouseX", (double)e.getX());
                world.getAttributes().put("INTL_mouseY", (double)e.getY());
            } catch (Exception _) {
                tryUpdatePos = false;
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation()>0){
            world.getViewport().zoom *= 1.1;
        } else {
            world.getViewport().zoom *= 1/1.1;
        }
        this.repaint();
    }
}
