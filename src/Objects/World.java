package Objects;

import Objects.Physical.Physical;

import java.awt.*;
import java.util.HashMap;

public interface World {
    int getIndex(Physical obj);
    Viewport getViewport();
    void update();
    void draw(Graphics g);
    void command(String command);
    Physical detectAt(int vpX, int vpY, int x, int y);

    HashMap getAttributes();
}
