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
        resetAttributes();
        resetObjects();
    }

    private void resetAttributes(){
        attributes.clear();
        attributes.put("gravity", -20.0);
        attributes.put("drag", 0.1);
        attributes.put("substeps", (double) SUBSTEPSDEFAULT);
        attributes.put("width", 10.0);
        attributes.put("height", 10.0);

        viewport.zoom = Math.max(attributes.get("width"),attributes.get("height"))*1.1;
        viewport.origin = new Vec2(attributes.get("width")/2,attributes.get("height")/2);
    }

    private void resetObjects() {
        viewport.zoom = Math.max(attributes.get("width"),attributes.get("height"))*1.1;
        viewport.origin = new Vec2(attributes.get("width")/2,attributes.get("height")/2);
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
            objects.add(new Pill(new Vec2(Math.random()*attributes.get("width"), Math.random()*attributes.get("height")), Math.random()*360, Math.random()*0.3, Math.random()*1, (int)(Math.random()*cs-1)+1));
            objects.getLast().movable = true;
            objects.add(randomPolygon(cs));
            objects.getLast().movable = true;
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
        double radius = Math.random() * 0.5;

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
        // in NESW order
        // North
        objects.add(new Pill(new Vec2(0, attributes.get("height")), 0, 0.01, attributes.get("width"), 0 ));
        // East
        objects.add(new Pill(new Vec2( attributes.get("width"), 0), 90, 0.01, attributes.get("height"), 0 ));
        // South
        objects.add(new Pill(new Vec2(0, 0), 0, 0.01, attributes.get("width"), 0 ));
        // West
        objects.add(new Pill(new Vec2( 0, 0), 90, 0.01, attributes.get("height"), 0 ));


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
                solver.resolve(objects);
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
                if (command.toLowerCase().startsWith("zoom", 3)) {
                    if (command.length() > 8) {
                        viewport.zoom = Double.parseDouble(command.substring(8));
                    }else {
                        System.out.println("Viewport Zoom: " + viewport.zoom);
                    }
                } else if (command.toLowerCase().startsWith("origin", 3)) {
                    if (command.length() > 10) {
                    viewport.origin = Vec2.parseVec2(command.substring(10));
                    }else {
                        System.out.println("Viewport Origin: " + viewport.origin);
                    }
                }
            } else if (command.toLowerCase().startsWith("sm_reset")) {
                resetAttributes();
                resetObjects();
            }else if (command.toLowerCase().startsWith("sm_regen")) {
                resetObjects();
            } else if (command.toLowerCase().startsWith("sm_step")) {
                if (command.length() > 8){
                    for(int i = 0 ; i < Integer.parseInt(command.substring(8)); i++){
                        update();
                    }
                }else {
                    update();
                }
            }else if (command.toLowerCase().startsWith("sm_bounds")) {
                if(command.length() > 12) {
                    double number = Double.parseDouble(command.substring(12));
                    switch (command.charAt(10)) {
                        case 'w':
                            attributes.put("width", number);
                            break;
                        case 'h':
                            attributes.put("height", number);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    resetObjects();
                }
                else{ System.out.println("Width: " + attributes.get("width") + " | Height: " + attributes.get("height"));}
            }else if (command.toLowerCase().startsWith("help")) {
                System.out.println(
                        """
                                VIEWPORT - vp_
                                    vp_origin [double],[double] | sets the viewpoint's origin.
                                    vp_zoom [double]| set the viewport's zoom level. zoom describes units visible on the y-axis.
                                SIMULATION - sm_
                                    sm_step [Optional int] | take 1 or more steps in the simulation
                                    sm_reset | fully resets the simulation
                                    sm_regen | regenerates simulation objects
                                    sm_bounds [w/h] [double] | changes the width and height of the simulation
                                """);
            } else {
                System.out.println("Unknown command. Input \"help\" for command information.");
            }
        } catch(IllegalArgumentException _){
            System.out.println("Bad command. Input \"help\" for command information. \n");
        }

    }
}
