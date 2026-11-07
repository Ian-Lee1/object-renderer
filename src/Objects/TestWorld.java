package Objects;

import Objects.Physical.Physical;

import java.awt.*;
import java.util.HashMap;

public class TestWorld implements World{

    private Color background = new Color(0,0,0);
    private Color lines = new Color(255, 255, 255);

    @Override
    public int getIndex(Physical obj) {
        return 0;
    }

    @Override
    public Viewport getViewport() {
        return null;
    }

    public void update(){
        //World is static and without objects
    }

    public void draw(Graphics g){
        int midX = (int)g.getClipBounds().getWidth()/2;
        int midY = (int)g.getClipBounds().getHeight()/2;
        g.setColor(background);
        g.fillRect(0, 0, midX * 2, midY * 2);
        g.setColor(lines);
        g.drawOval(midX - (int)(midX * 0.5), midY - (int)(midY * 0.5), (int)(midX*0.2), (int)(midY*0.2));
        g.drawOval(midX + (int)(midX * 0.5), midY - (int)(midY * 0.5), (int)(midX*0.2), (int)(midY*0.2));
        g.drawOval(midX - (int)(midX * 0.5), midY, (int)(midX*1.2), (int)(midY*0.45));
        g.setColor(background);
        g.fillRect(midX - (int)(midX * 0.5) -5, midY, (int)(midX*1.2) + 5, (int)(midY*0.25));


    }
    public void command(String command) {

        if (command.toLowerCase().startsWith("set ")) {
            if (command.toLowerCase().startsWith("b ", 4)) {
                String colorStr = command.substring(6);
                background = strToColor(colorStr);
            } else if (command.toLowerCase().startsWith("l ", 4)) {
                String colorStr = command.substring(6);
                lines = strToColor(colorStr);
            }
        }else {
            System.out.println("Unknown command.");
        }
    }

    @Override
    public Physical detectAt(int vpX, int vpY, int x, int y) {
        return null;
    }

    @Override
    public HashMap getAttributes() {
        return null;
    }

    private Color strToColor(String str){
            String[] parts = str.split(",");

            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());

            // Construct the Color object
            return new Color(r, g, b);
    }
}
