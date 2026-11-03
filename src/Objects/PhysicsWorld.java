package Objects;
import Objects.Forces.*;

import Objects.Physical.*;
import Objects.Physical.Polygon;
import Solver.Solver;
import Vector.Vec2;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        viewport.colors.add(Color.RED);
        viewport.origin = new Vec2(5, 5);
        viewport.zoom = 12;
        resetAttributes();
        resetObjects();
    }

    private void resetViewport(){
        viewport.zoom = Math.max(attributes.get("width"),attributes.get("height"))*1.1;
        viewport.origin = new Vec2(attributes.get("width")/2,attributes.get("height")/2);
    }

    private void resetAttributes(){
        attributes.clear();
        attributes.put("width", 10.0);
        attributes.put("height", 10.0);
        attributes.put("substeps", (double) SUBSTEPSDEFAULT);
    }

    private void resetObjects() {
        objects.clear();
        forces.clear();
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
            double stepScale = (1.0 / FPS) * (1.0 / attributes.get("substeps"));
            for (int i = 0; i < attributes.get("substeps"); i++) {

                for (Physical obj : objects) {

                    if (obj.movable) {
                        //ACCELERATION
                        obj.acc =new Vec2(0, 0);
                        for (Force force : forces) {
                            force.applyForces(obj, objects, attributes);
                        }

                        //VELOCITY


                        //obj.applyRotate(stepScale);
                        obj.ang = 15;
                        obj.vel = (obj.vel.add(obj.acc.mul(stepScale)));
                        //MOVE
                        obj.origin = (obj.origin.add(obj.vel.mul(stepScale)));
                    }
                }
                solver.resolve(objects, stepScale);
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
        System.out.println("> " + command);
        try {
            if (command.toLowerCase().startsWith("vp_")) {
                if (command.toLowerCase().startsWith("zoom", 3)) {
                    if (command.length() > 8) {
                        viewport.zoom = Double.parseDouble(command.substring(8));
                    }else {
                        System.out.println("Viewport Zoom: " + viewport.zoom);
                    }
                }
                else if (command.toLowerCase().startsWith("origin", 3)) {
                    if (command.length() > 10) {
                    viewport.origin = Vec2.parseVec2(command.substring(10));
                    }else {
                        System.out.println("Viewport Origin: " + viewport.origin);
                    }
                }
                else if (command.toLowerCase().startsWith("reset", 3)) {
                    System.out.println("Viewport reset.");
                    resetViewport();
                }
                else if (command.toLowerCase().startsWith("colors", 3)) {
                    if (command.toLowerCase().startsWith("clear", 10)){
                        System.out.println("Cleared viewport colors");
                        viewport.colors.clear();
                        viewport.colors.add(Color.RED);
                    }
                    else if (command.toLowerCase().startsWith("add", 10)){
                        String section = command.substring(14);
                        String[] parts = section.trim().split(" ");
                        if (parts.length < 3){
                            System.out.println("RGB components not detected properly. Format colors as r g b.");
                        }else {
                            viewport.colors.add(new Color(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                            System.out.println("Add new color.");
                        }
                    }
                    else if (command.toLowerCase().startsWith("random", 10)){
                        int c = Integer.parseInt(command.substring(17));
                        for (int i = 0 ; i < c; i++){
                            viewport.colors.add(Color.getHSBColor((float)Math.random(), (float)(Math.random()*0.3 + 0.7), (float)(Math.random()*0.3 + 0.7)));
                        }
                        System.out.println("Added "+c+ " colors.");
                    }
                }
            }
            else if (command.toLowerCase().startsWith("sm_reset")) {
                resetAttributes();
                resetObjects();
                System.out.println("Cleared all objects.");
            }
            else if (command.toLowerCase().startsWith("sm_step")) {
                if (command.length() > 8){
                    System.out.println("Steping " + command.substring(8) + " times...");
                    for(int i = 0 ; i < Integer.parseInt(command.substring(8)); i++){
                        update();
                    }
                }else {
                    update();
                    System.out.println("Stepped once");
                }
            }
            else if (command.toLowerCase().startsWith("sm_attribute")) {
                if (command.toLowerCase().startsWith("clear", 13)){
                    resetAttributes();
                    System.out.println("Attributes cleared.");
                } else if (command.length() > 14){
                    String section = command.substring(13);
                    int space = section.indexOf(' ');
                    if (space <= 0 || space == section.length() - 1){
                        System.out.println("Invalid formatting.");
                    }
                    System.out.println("Added " + section.substring(0, space) + ": " + section.substring(space));
                    attributes.put(section.substring(0, space), Double.parseDouble(section.substring(space)));
                }
            }
            else if (command.toLowerCase().startsWith("sm_bounds_regen")) {
                    resetObjects();
                    addBounds();
                System.out.println("Bounds regenerated.");
            }
            else if (command.toLowerCase().startsWith("sm_bounds")) {
                if(command.length() > 12) {
                    double number = Double.parseDouble(command.substring(12));
                    System.out.println("Changed bound " + command.charAt(10)+ ": " + number);
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
                }
                else{ System.out.println("Width: " + attributes.get("width") + " | Height: " + attributes.get("height"));}
            }
            else if (command.toLowerCase().startsWith("ent_create")) {
                System.out.println("Creating object...");
                try {
                    if (command.startsWith("force_", 11)){
                        forces.add(Force.parseForce(command.substring(17)));
                    } else if (command.startsWith("phys_", 11)){
                        objects.add(Physical.parsePhysical(command.substring(16)));
                    }
                    System.out.println("Object created successfully.");
                } catch (IllegalArgumentException e){
                    System.out.println("Creation failure: ");
                }
            }
            else if (command.toLowerCase().startsWith("exec ")){
                String filePath = command.substring(5);
                try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                    lines.forEach(this::command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (command.toLowerCase().startsWith("help")) {
                System.out.println(
                        """
                                VIEWPORT - vp_
                                    vp_reset | resets zoom and origin based on width and height
                                    vp_colors [clear/add/random] [null/[R] [G] [B]/[amt] | clears colors, add specific color, or generate amt random colors.
                                    vp_origin [double],[double] | sets the viewpoint's origin.
                                    vp_zoom [double]| set the viewport's zoom level. zoom describes units visible on the y-axis.
                                SIMULATION - sm_
                                    sm_step [Optional int] | take 1 or more steps in the simulation
                                    sm_reset | fully resets the simulation
                                    sm_bounds [w/h/regen] [double/double/null] | changes the width and height of the simulation, or regenerats the bounds, clearing all objects.
                                    sm_attributes [clear/[attribute]] [null/[double]] | clears all attributes, or add/change the specified attribute.
                                ENTITIES - ent_
                                    ent_create [entity] [keyvalues] | create an entity with the given keyvalues
                                EXECUTE FILE - exec
                                    exec [filePath] | execute each line in the file as a command in the command field. Beware of infinite loops if exec calls are nested.
                                """);
            } else {
                System.out.println("Unknown command. Input \"help\" for command information.");
            }
        } catch(IllegalArgumentException _){
            System.out.println("Bad command. Input \"help\" for command information. \n");
        }

    }
}
