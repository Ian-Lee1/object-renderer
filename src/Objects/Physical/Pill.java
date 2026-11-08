package Objects.Physical;

import Objects.Viewport;
import Vector.Vec2;

import java.awt.*;
import java.util.HashMap;

import static java.lang.Math.pow;

public class Pill extends Physical{
    public double size;
    public double length; //length extending according to angle from origin

    public Pill(Vec2 origin, double ang, double size, double length, int ind){
        this.origin = origin;
        this.ang = ang;
        this.size = size;
        this.length = length;
        this.colorIdx = ind;
        this.vel = new Vec2(0, 0);
        this.force = new Vec2(0, 0);
        movable = false;
        attributes = new HashMap<>();
    }

    public Pill(String cmdParameters){
        this.origin = new Vec2(0, 0);
        movable = false;
        this.ang = 0;
        this.size = 1;
        this.length = 0;
        this.colorIdx = 0;
        this.vel = new Vec2(0, 0);
        this.force = new Vec2(0, 0);
        attributes = new HashMap<>();
        changeFromString(cmdParameters);
        if (Math.random()<0.1) {
            attributes.put("t_l", 30.0);
            attributes.put("t_f", 15.0);
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("PHYS_: ").append("pill").append(" | ")
                .append("ORIGIN: ").append(origin).append(" | ")
                .append("VEL: ").append(vel).append(" | ")
                .append("ACC: ").append(force).append(" | ")
                .append("ANG: ").append(ang).append(" | ")
                .append("ANGVEL: ").append(angVel).append(" | ")
                .append("MASS: ").append(1/inverseMass).append(" | ")
                .append("MOVABLE: ").append(movable).append(" | ")

                .append("SIZE: ").append(size).append(" | ")
                .append("LENGTH: ").append(length).append(" | ")

                .append("COLORIDX: ").append(colorIdx).append("\n")
                .append("===ATTRIBUTES===").append("\n");
        attributes.forEach((key, value) -> builder.append(key).append(": ").append(value).append("\n"));
        return builder.toString();
    }

    @Override
    public double getBoundLeft() {
        return this.getCenterOfMass().getX() - length / 2 - size;
    }

    @Override
    public double getBoundRight() {
        return this.getCenterOfMass().getX() + length / 2 + size;
    }

    @Override
    public double getBoundTop() {
        return this.getCenterOfMass().getY() + length / 2 + size;
    }

    @Override
    public double getBoundBottom() {
        return this.getCenterOfMass().getY() - length / 2 - size;
    }

    @Override
    public void changeFromString(String cmdParameters) {
        double doubleValue;
        Vec2 vecValue;
        // === REQUIRED (raise exception listing first missing parameter) ===
        // origin
        vecValue = getVec2(cmdParameters.toLowerCase(), " origin ");
        if (vecValue == null){
            vecValue = origin;
        }
        origin = vecValue;
        // size
        doubleValue = getDouble(cmdParameters.toLowerCase(), " size ");
        if (Double.isNaN(doubleValue) ){
            doubleValue = size;
        }
        size = doubleValue;

        // === OPTIONAL ===
        // ang - default 0
        doubleValue = getDouble(cmdParameters.toLowerCase(), " ang ");
        if (Double.isNaN(doubleValue) ){
            doubleValue = ang;
        }
        ang = doubleValue;
        // length - 0
        doubleValue = getDouble(cmdParameters.toLowerCase(), " length ");
        if (Double.isNaN(doubleValue) ){
            doubleValue = length;
        }
        length = doubleValue;

        // angVel - 0
        doubleValue = getDouble(cmdParameters.toLowerCase(), " angvel ");
        if (Double.isNaN(doubleValue) ){
            doubleValue = angVel;
        }
        angVel = doubleValue;

        // colorIdx - default 0 (color index, does not directly store color but only the index from a color list)
        doubleValue = getDouble(cmdParameters.toLowerCase(), " coloridx "); //casting converts NaN to 0
        if (Double.isNaN(doubleValue)){
            doubleValue = colorIdx;
        }
        colorIdx = (int)doubleValue;

        // vel - default 0,0 - Vec2 has parseVec2 method, in format "x,y".
        vecValue = getVec2(cmdParameters.toLowerCase(), " vel ");
        if (vecValue == null){
            vecValue = vel;
        }
        vel = vecValue;

        // mass - INF = inverseMass = 0
        doubleValue = 1.0/getDouble(cmdParameters.toLowerCase(), " mass ");
        if (Double.isNaN(doubleValue) ){
            doubleValue = inverseMass;
        }
        inverseMass = doubleValue;

        if (!attributes.containsKey("hash")) {
            attributes.put("hash", getDouble(cmdParameters.toLowerCase(), " hash "));
            if (attributes.get("hash").isNaN()) {
                attributes.put("hash", (double) (int) (Math.random() * 1000000) / 1000);
            }
        }

        // movable - false
        if (cmdParameters.contains(" movable ")) {
            movable = getDouble(cmdParameters.toLowerCase(), "movable ") > 0; //if NaN, false.
        }
        if (cmdParameters.contains(" att ")) {
            int section = cmdParameters.indexOf(' ', cmdParameters.indexOf(" att ")+ 6);
            String key = cmdParameters.substring(cmdParameters.indexOf(" att ")+ 5, section);
            attributes.put(key, getDouble(cmdParameters, " att " + key + " "));
        }
    }

    private Vec2 getVec2(String cmd, String paraType){
        /// parse the vector associated with the passed in parameter. If cmd does not contain paraType, returns null.///
        if (!cmd.contains(paraType)){
            return null;
        }
        String starting = cmd.substring(cmd.indexOf(paraType) + paraType.length());
        String value = getNextSegment(starting);
        return Vec2.parseVec2(value);
    }

    private double getDouble(String cmd, String paraType){
        /// parse the double associated with the passed in parameter. If cmd does not contain paraType, returns nan.///
        if (!cmd.contains(paraType)){
            return Double.NaN;
        }
        String starting = cmd.substring(cmd.indexOf(paraType) + paraType.length());
        String value = getNextSegment(starting);
        return Double.parseDouble(value);
    }

    private String getNextSegment(String str){
        if (str.indexOf(' ') == -1){
            return str;
        }
        return str.substring(0, str.indexOf(' '));
    }

    @Override
    public void applyRotate(double stepScale) {
        ang += angVel * stepScale;
        if (length > 0) { //center of mass of circle = origin.
            double radAng = Math.toRadians(angVel * stepScale);
            Vec2 centerOfMass = getCenterOfMass();
            double x2 = centerOfMass.getX() + (origin.getX() - centerOfMass.getX()) * Math.cos(radAng) - (origin.getY() - centerOfMass.getY()) * Math.sin(radAng);
            double y2 = centerOfMass.getY() + (origin.getX() - centerOfMass.getX()) * Math.sin(radAng) + (origin.getY() - centerOfMass.getY()) * Math.cos(radAng);
            origin = new Vec2(x2, y2);
        }
    }

    @Override
    public Vec2 getCenterOfMass() {
        //Vec2 angVec = Vec2.radToVec(Math.toRadians(ang));
        return origin;//.add(angVec.mul(length/2));
    }

    @Override
    public double getInertia() {
                       //inertia of rectangle with fake "mass"                  inertia of circle with fake "mass"     multiply real mass back in
        double calculated = ( (((pow(length, 2) + pow(2*size, 2)) * (length * 2*size))/12) + ((pow(size, 2)/2 + pow(length, 2)/4) * Math.PI*pow(size, 2))) /inverseMass;
        if (movable){
            return  calculated / (length * 2*size + Math.PI * pow(size, 2));
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public void draw(Graphics g, Viewport viewport) {
        Graphics2D g2 = (Graphics2D) g;
        if (viewport.antialiasing) {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
        } else {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF
            );
        }

        double scale = viewport.worldToScreenScale(g);

        double radians = Math.toRadians(ang);
        Vec2 start = origin.sub(
                new Vec2(
                        Math.cos(radians) * length/2,
                        Math.sin(radians) * length/2
                )
        );
        Vec2 end = origin.add(
                new Vec2(
                        Math.cos(radians) * length/2,
                        Math.sin(radians) * length/2
                )
        );

        Vec2 screenPos = viewport.worldToScreen(g, start);
        int x1 = (int) (screenPos.getX());// + g.getClipBounds().getWidth()/2);
        int y1 = (int) (screenPos.getY());// - g.getClipBounds().getHeight()/2);

        screenPos = viewport.worldToScreen(g, end);
        int x2 = (int) (screenPos.getX());// + g.getClipBounds().getWidth()/2);
        int y2 = (int) (screenPos.getY());// - g.getClipBounds().getHeight()/2);

        float thickness = (float)(size * 2 * scale);

        Stroke stroke = g2.getStroke();

        g2.setStroke(new BasicStroke(
                thickness + 5,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        g.setColor(viewport.getColor(colorIdx).darker().darker());
        g2.drawLine(x1, y1, x2, y2);

        g.setColor(viewport.getColor(colorIdx));
        g2.setStroke(new BasicStroke(
                thickness,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(stroke);
    }

    @Override
    public Physical clone() {
        Pill pill = new Pill(origin, ang, size, length, colorIdx);
        pill.vel = vel;
        pill.inverseMass = inverseMass;
        pill.angVel = angVel;
        pill.movable = movable;
        pill.attributes.putAll(attributes);
        pill.attributes.put("hash", (double) (int) (Math.random() * 1000000) / 1000);
        return pill;
    }
}
