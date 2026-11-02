package Objects;
import Objects.Forces.*;

import Objects.Physical.*;
import Objects.Physical.Polygon;
import Solver.Solver;
import Vector.Vec2;

import java.awt.*;
import java.util.*;

import static Constants.Constants.FPS;
import static Constants.Constants.SUBSTEPSDEFAULT;

public class PhysicsWorld implements World{
    private ArrayList<Physical> objects = new ArrayList<>();
    private ArrayList<Force> forces = new ArrayList<>();

    private Viewport viewport;
    private HashMap<String, Double> attributes = new HashMap<>();

    private Solver solver = null;

    public PhysicsWorld(){
        viewport = new Viewport();
        viewport.origin = new Vec2(5, 5);
        viewport.zoom = 12;
        reset();
    }

    private void reset() {

        //Simulation Attributes
        attributes.clear();
        attributes.put("gravity", -20.0);
        attributes.put("drag", 0.1);
        attributes.put("substeps", (double) SUBSTEPSDEFAULT);
        attributes.put("width", 10.0);
        attributes.put("height", 10.0);

        //Colors
        viewport.colors.clear();
        int cs = (int)(Math.random()*0) + 35;
        viewport.colors.add(new Color(255, 0, 0));
        for (int i = 0; i < cs; i++) {
            viewport.colors.add(Color.getHSBColor((float)Math.random(), (float)(Math.random()*0.45 + 0.5), (float)(Math.random()*0.45 + 0.5)));
        }

        //Objects
        objects.clear();
        addBounds();
        for (int i = objects.size() ; i < 50; i++) {
            objects.add(new Pill(new Vec2(Math.random()*attributes.get("width"), Math.random()*attributes.get("height")), Math.random()*360, Math.random()*1, Math.random()*2, (int)(Math.random()*cs-1)+1));
            objects.add(randomPolygon(cs));
        }
        //Forces
        forces.clear();
        forces.add(new GlobalGravity());
        forces.add(new Drag());
    }

    private Polygon randomPolygon(int cs) {
        Random random = new Random();

        double width = attributes.get("width");
        double height = attributes.get("height");

        Vec2 origin = new Vec2(
                Math.random() * width,
                Math.random() * height
        );

        double angle = Math.random() * 360.0;

        int vertexCount = 3 + random.nextInt(3); // 2-6 vertices

        // Overall size of the polygon
        double radius = Math.random() * 2;

        Vec2[] vertices = new Vec2[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            double theta = (Math.PI * 2 * i) / vertexCount;

            // Vary radius a little for irregular shapes
            double r = radius * (0.5 + random.nextDouble() * 0.5);

            vertices[i] = new Vec2(
                    Math.cos(theta) * r,
                    Math.sin(theta) * r
            );
        }

        return new Polygon(origin, angle, vertices, (int)(Math.random()*cs-1)+1);
    }

    private void addBounds() {
        // in NESW order //TODO replace lines with new polygon objects
/*
        // North
        objects.add(new Line(
                new Vec2(attributes.get("width"), attributes.get("height")),
                new Vec2(0, attributes.get("height")),
                0
        ));
        // East
        objects.add(new Line(
                new Vec2(attributes.get("width"), attributes.get("height")),
                new Vec2(attributes.get("width"), 0),
                0
        ));
        // South
        objects.add(new Line(
                new Vec2(0, 0),
                new Vec2(attributes.get("width"), 0),
                0
        ));
        // West
        objects.add(new Line(
                new Vec2(0, attributes.get("height")),
                new Vec2(0, 0),
                0
        ));
        */

    }

    public void setSolver(Solver solver){
        this.solver = solver;
    }

    public void update(){
        if (solver != null) {
            for (int i = 0; i < attributes.get("substeps"); i++) {
                for (Physical obj : objects) {
                    if (obj.movable) {
                        //ACCELERATION
                        obj.acc =new Vec2(0, 0);
                        for (Force force : forces) {
                            force.applyForces(obj, objects, attributes);
                        }

                        //VELOCITY
                        double stepScale = (1.0 / FPS) * (1.0 / attributes.get("substeps"));
                        obj.vel = (obj.vel.add(obj.acc.mul(stepScale)));
                        //MOVE
                        obj.origin = (obj.origin.add(obj.vel.mul(stepScale)));
                    }
                }
                //solver.resolveCollisions(objects); TODO
            }
        }
    }

    public void draw(Graphics g){
            //draw objs
            for (Physical obj : objects){
                obj.draw(g, viewport);
            }
    }

    public void command(String command) {
        try {
            if (command.toLowerCase().startsWith("vp_")) {
                if (command.toLowerCase().startsWith("zoom ", 3)) {
                    viewport.zoom = Double.parseDouble(command.substring(8));
                } else if (command.toLowerCase().startsWith("origin ", 3)) {
                    viewport.origin = Vec2.parseVec2(command.substring(10));
                }
            } else if (command.toLowerCase().startsWith("db_")) {
                if (command.toLowerCase().startsWith("vp", 3)) {
                    System.out.println("origin: " + viewport.origin + " zoom: " + viewport.zoom);
                }
            } else if (command.toLowerCase().startsWith("sm_reset")) {
                reset();
            }else if (command.toLowerCase().startsWith("sm_step")) {
                if (command.length() > 8){
                    for(int i = 0 ; i < Integer.parseInt(command.substring(8)); i++){
                        update();
                    }
                }else {
                    update();
                }
            }else if (command.toLowerCase().startsWith("sm_bounds ")) {
                double number = Double.parseDouble(command.substring(12));
                switch(command.charAt(10)){
                    case 'n':

                        break;
                    case 'e':

                        break;
                    case 's':

                        break;
                    case 'w':

                        break;
                    default:
                        throw new IllegalArgumentException();
                }

            }else if (command.toLowerCase().startsWith("help")) {
                System.out.println(
                        """
                                DEBUG - db_
                                    db_vp | prints the viewport's origin and zoom.
                                VIEWPORT - vp_
                                    vp_origin [double],[double] | sets the viewpoint's origin.
                                    vp_zoom [double]| set the viewport's zoom level. zoom describes units visible on the y-axis.
                                SIMULATION - sm_
                                    sm_step [Optional int] | take 1 or more steps in the simulation
                                    sm_reset | resets the simulation
                                    sm_bounds [n/e/s/w] [double] | changes the boundaries of the simulation
                                """);
            } else {
                System.out.println("Unknown command. Input \"help\" for command information.");
            }
        } catch(IllegalArgumentException _){
            System.out.println("Bad command. Input \"help\" for command information. \n");
        }

    }
}
