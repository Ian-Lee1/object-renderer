package Objects.tracer;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.ArrayList;

public interface Tracer { //classes that will be given a list of points, and convert such list to a curve to draw on the given Graphics component.
    void draw(Graphics2D g2, Viewport vp, ArrayList<Vec2> points, int colorIdx);
}
