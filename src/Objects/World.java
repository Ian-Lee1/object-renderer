package Objects;

import java.awt.*;

public interface World {
    void update();
    void draw(Graphics g);
    void command(String command);

}
