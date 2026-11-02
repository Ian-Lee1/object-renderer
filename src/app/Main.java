package app;

import Objects.Physical.Circle;
import Objects.Physical.Line;
import Solver.Solver;
import Vector.Vec2;

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

        Line obj = new Line(new Vec2(0, 0), new Vec2(1, 1), 0);
        Circle obj2 = new Circle(new Vec2(0.65, 1), 0.3, 0);
        //System.out.println(circle.closest(new Vec2(2, 1)));
        double margin = 0.00001;

        Vec2 closestPoint1 = obj.definingPoints()[0];
        Vec2 closestPoint2 = obj2.definingPoints()[0];
        Vec2 lastClosestPoint = new Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (int i = 0; i < 10; i++){
            closestPoint1 = obj.closest(closestPoint2);  //closest point of obj to last closest
            System.out.println("CP1 " + closestPoint1 + "dist to obj2 " + obj2.minDist(closestPoint1));
            if (obj2.minDist(closestPoint1) < margin){ //is the closest point on obj inside of obj2?
                return;
            }
            closestPoint2 = obj2.closest(closestPoint1); //repeat but for other object
            System.out.println("CP2 " + closestPoint2 + "dist to obj1 " + obj.minDist(closestPoint2));
            if (obj.minDist(closestPoint2) < margin){
                return;
            }
            if (lastClosestPoint.distSq(closestPoint1) < margin){ // is the closest point moving or has it been found already?
                return; //has been found, but is not in objects.
            }
            lastClosestPoint = closestPoint1;
        }

    }
}
