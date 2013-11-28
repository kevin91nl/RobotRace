
import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import robotrace.Base;
import robotrace.Vector;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack raceTrack;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER
            /* add other parameters that characterize this robot */);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD
            /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE
            /* add other parameters that characterize this robot */);
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race track
        raceTrack = new RaceTrack();
        
        // Initialize the terrain
        terrain = new Terrain();
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {        
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Enable anti-aliasing.
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
				
				// Enable shading and lighting
				gl.glEnable(GL_LIGHTING);
				gl.glShadeModel(GL_SMOOTH);
				gl.glEnable(GL_COLOR_MATERIAL);
				
				// Set up ambient lightning far away
				float[] ambientColor = {0.3f, 0.3f, 0.3f, 0.001f};
				float[] ambientPosition = {10000f, 10000f, 10000f};
				gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambientColor, 0);
				gl.glLightfv(GL_LIGHT0, GL_POSITION, ambientPosition, 0);
				gl.glEnable(GL_LIGHT0);
				
				// Set up light spot (and reposition at camera movement)
				float[] cameralightColor = {1f, 0f, 0f, 1.0f};
				gl.glLightfv(GL_LIGHT1, GL_AMBIENT, cameralightColor, 0);
				gl.glEnable(GL_LIGHT1);
        
        // Set viewing properties.
        gs.theta = 0.5f * 0.5f * (float)Math.PI;
        gs.phi = 0.5f * 0.5f * (float)Math.PI;
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        //
        // Define C as center point (given by gs.cnt).
        // Let E be the eye point and V=E-C (a vector from C to E).
        // Then E=C+V.
        //
        // Given: |V| (gs.vDist), φ (gs.phi), θ (gs.theta) with:
        //    |V|:  Lenth of vector V.
        //    φ:    Angle between positive X-axis and V projected on the XY-plane.
        //    θ:    Angle between V and V projected on the XY-plane.
        //
        // Now we can calculate V = (x, y, z):
        //    z = |V| * sin(φ)
        //    
        // Let V' be V projected on the XY-plane. Then:
        //    |V'| = |V| * cos(φ)
        //
        // From this we get:
        //     x = |V'| * cos(θ)
        //     y = |V'| * sin(θ)
        //
        // Thus:
        //     V = (x, y, z) = (|V|cos(θ)cos(φ), |V|sin(θ)cos(φ), |V|sin(φ))
        
        Vector V = new Vector(
            gs.vDist * Math.cos(gs.theta) * Math.cos(gs.phi),
            gs.vDist * Math.sin(gs.theta) * Math.cos(gs.phi),
            gs.vDist * Math.sin(gs.phi)
        );
        
        // Thus, eye point E = C + V
        camera.eye = gs.cnt.add(V);
        camera.center = gs.cnt;
				
				// Reposition the light at the left-top camera position.
				// We need to move the camera to the left.
				// This can be done by calculating (0, 0, 1) x (C - E) (which is "left").
				// Then, "up" is (0, 0, 1).
				Vector Vleft = new Vector(0, 0, 1).cross(camera.center.subtract(camera.eye)).normalized();
				Vector Vup = new Vector(0, 0, 1);
				
				// Calculate the light position and convert to float array.
				Vector cameralightPosition = camera.eye;
				cameralightPosition = cameralightPosition.add(Vleft.scale(2)).add(Vup.scale(2));
				float[] fCameralightPosition = {
					(float)cameralightPosition.x(),
					(float)cameralightPosition.y(),
					(float)cameralightPosition.z()
				};
				float[] fCameralightDirection = {
					(float)camera.center.x(),
					(float)camera.center.y(),
					(float)camera.center.z()
				};
				gl.glLightfv(GL_LIGHT1, GL_POSITION, fCameralightPosition, 0);
				gl.glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 95f);
				gl.glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, fCameralightDirection, 0);
        
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective(40, (float)gs.w / (float)gs.h, 0.1 * gs.vDist, 10 * gs.vDist);
        
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
               
        // Update the view according to the camera mode
        camera.update(gs.camMode);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        // Draw the axis frame
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        //
        // Draw a horizontal line through the center point with width gs.vWidth and through
        // center point C, parallel with the XY-plane and perpendicular on the view
        // direction.
        //
        // So, the line can be represented as L(t) := C + t*W with W as vector
        // such that W perpendicular on V (=E-C) and in the XY-plane. If it is
        // in the XY-plane, it is perpendicular on the Z-axis. So, it is for
        // example perpendicular with (0, 0, 1).
        // Beware that V can be in the same direction as (0, 0, 1)! In that case,
        // we approach (0, 0, 1) by (0, 0, 0.999...).
        //
        Vector V = camera.eye.add(gs.cnt.scale(-1));
        Vector W;
        if (V.x() == 0 && V.y() == 0) {
            // Same direction! Use an approach for (0, 0, 1).
            W = V.cross(new Vector(0f, 0f, 0.99999f));
        } else {
            W = V.cross(new Vector(0f, 0f, 1f));
        }
        Vector C = gs.cnt;
        
        // Let F and G be the begin point and end point respectively of the line
        // segment.
        // So we need to find a parameter t such that:
        // F = C - tW
        // G = C + tW
        // And |F-G| = gs.vWidth.
        // F-G=-tW-tW=-2tW
        // So gs.vWidth = |-2tW| = 2|t||W| and therefor |t|=gs.vWidth/(2|W|).
        double t = gs.vWidth / (2 * W.length());
        Vector F = C.add(W.scale(-t));
        Vector G = C.add(W.scale(t));
        
        gl.glLineWidth(1.2f);
        gl.glBegin(GL_LINES);
        gl.glVertex3d(F.x(), F.y(), F.z());
        gl.glVertex3d(G.x(), G.y(), G.z());
        gl.glEnd();
        gl.glLineWidth(1);
        
        // Draw the robots
				gl.glPushMatrix();
        robots[0].draw(false);
				gl.glTranslatef(1f, 0f, 0f);
				robots[1].draw(false);
				gl.glTranslatef(1f, 0f, 0f);
				robots[2].draw(false);
				gl.glTranslatef(1f, 0f, 0f);
				robots[3].draw(false);
				gl.glPopMatrix();
        
        // Draw race track
        raceTrack.draw(gs.trackNr);
        
        // Draw terrain
        terrain.draw();
        
        // Unit box around origin.
        //glut.glutWireCube(1f);

        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);
        
        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(30f, 0f, 0f, 1f);
        
        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        glut.glutWireCube(1f);
    }
    
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        // Set the size of the cube to 1m
        float cubeSize = 1f;

        // Set the radius of the sphere
        float sphereRadius = 0.2f * cubeSize;
        
        // Set the base and height of the cones
        float coneBase = 0.1f * cubeSize;
        float coneHeight = 0.5f * cubeSize;
        
        // Draw the axis cube (from (0, 0, 0) to (1, 1, 1))
        // Since the center of the cube will be (0.5, 0.5, 0.5), we need to
        // translate the cube a bit
        gl.glTranslatef(0.5f, 0.5f, 0.5f);
        // Draw the cube
        glut.glutWireCube(cubeSize);
        // Undo the translation
        gl.glTranslatef(-0.5f * cubeSize, -0.5f * cubeSize, -0.5f * cubeSize);
        
        // Draw the yellow sphere at (0, 0, 0)
        // First, set the color of the sphere
        gl.glColor3f(1f, 1f, 0f);
        // Then, draw the sphere
        glut.glutSolidSphere(sphereRadius, 20, 20);
        // Reset the color to black
        gl.glColor3f(0f, 0f, 0f);
        
        // The cones need first to be rotated when they are at the origin.
        // After the rotation, a translation is needed. So let V be the
        // coordinate vector. Let R be the rotation matrix and T be the translation
        // matrix. Then the transformed coordinates V' after the rotation
        // are V' = RV. The transformed coordinates V'' after the translation
        // are thus V'' = TV' = TRV. So, first the translation matrix need
        // to be applied and after that the rotation vector must be applied.
        
        // Draw the red cone at (1, 0, 0)
        // Set the color for the x-axis cone (red)
        gl.glColor3f(1f, 0f, 0f);
        // Translate to (1, 0, 0)
        gl.glTranslatef(1f * cubeSize, 0f, 0f);
        // Now rotate over the y-axis
        gl.glRotatef(90f, 0f, 1f, 0f);
        // Draw the cone
        glut.glutSolidCone(coneBase, coneHeight, 20, 20);
        // Undo the rotation
        gl.glRotatef(-90f, 0f, 1f, 0f);
        // Undo the translation
        gl.glTranslatef(-1f * cubeSize, 0f, 0f);
        
        // Draw the green cone at (0, 1, 0)
        // Set the color for the y-axis cone (green)
        gl.glColor3f(0f, 1f, 0f);
        // Translate to (0, 1, 0)
        gl.glTranslatef(0f, 1f * cubeSize, 0f);
        // Now rotate over the x-axis
        gl.glRotatef(-90f, 1f, 0f, 0f);
        // Draw the cone
        glut.glutSolidCone(coneBase, coneHeight, 20, 20);
        // Undo the rotation
        gl.glRotatef(90f, 1f, 0f, 0f);
        // Undo the translation
        gl.glTranslatef(0f, -1f * cubeSize, 0f);
        
        // Draw the blue cone at (0, 0, 1)
        // Set the color for the z-axis cone (blue)
        gl.glColor3f(0f, 0f, 1f);
        // Translate to (0, 0, 1)
        gl.glTranslatef(0f, 0f, 1f * cubeSize);
        // Draw the cone
        glut.glutSolidCone(coneBase, coneHeight, 20, 20);
        // Undo the translation
        gl.glTranslatef(0f, 0f, -1f * cubeSize);
        
        // Reset the color
        gl.glColor3f(0f, 0f, 0f);
    }
    
    /**
     * Materials that can be used for the robots.
     */
    public enum Material {
        
        /** 
         * Gold material properties.
         * Modify the default values to make it look like gold.
         */
        GOLD (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /**
         * Silver material properties.
         * Modify the default values to make it look like silver.
         */
        SILVER (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /** 
         * Wood material properties.
         * Modify the default values to make it look like wood.
         */
        WOOD (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f}),
        
        /**
         * Orange material properties.
         * Modify the default values to make it look like orange.
         */
        ORANGE (
            new float[] {0.8f, 0.8f, 0.8f, 1.0f},
            new float[] {0.0f, 0.0f, 0.0f, 1.0f});
        
        /** The diffuse RGBA reflectance of the material. */
        float[] diffuse;
        
        /** The specular RGBA reflectance of the material. */
        float[] specular;
        
        /**
         * Constructs a new material with diffuse and specular properties.
         */
        private Material(float[] diffuse, float[] specular) {
            this.diffuse = diffuse;
            this.specular = specular;
        }
    }
    
    /**
     * Represents a Robot, to be implemented according to the Assignments.
     */
    private class Robot {
			
				public double angleLUpperArm = 180.0;
				public double angleLLowerArm = -0.0;
				public double angleLHand = 0.0;
				
				public double angleRUpperArm = 180.0;
				public double angleRLowerArm = -0.0;
				public double angleRHand = -0.0;
				
				public double angleLUpperLeg = 0.0;
				public double angleLLowerLeg = 0.0;
				public double angleLFoot = 0.0;
				
				public double angleRUpperLeg = 0.0;
				public double angleRLowerLeg = 0.0;
				public double angleRFoot = 0.0;
				
				public double angleHead = 0;
				public double angleNeck = 0;
        
        /** The material from which this robot is built. */
        private final Material material;
        
        /**
         * Constructs the robot with initial parameters.
         */
        public Robot(Material material
            /* add other parameters that characterize this robot */) {
            this.material = material;
            
            // code goes here ...
        }
        
        /**
         * Draws this robot (as a {@code stickfigure} if specified).
         */
        public void draw(boolean stickFigure) {
					// Left arm
					gl.glTranslatef(-0.3f, 0f, 1.5f);
					this.drawArm(stickFigure, this.angleLUpperArm, this.angleLLowerArm, this.angleLHand);
					gl.glTranslatef(0.3f, 0f, -1.5f);
					
					// Right arm
					gl.glTranslatef(0.3f, 0f, 1.5f);
					this.drawArm(stickFigure, this.angleRUpperArm, this.angleRLowerArm, this.angleRHand);
					gl.glTranslatef(-0.3f, 0f, -1.5f);
					
					// Left leg
					gl.glTranslatef(-0.2f, 0f, 0.8f);
					this.drawLeg(stickFigure, this.angleLUpperLeg, this.angleLLowerLeg, this.angleLFoot);
					gl.glTranslatef(0.2f, 0f, -0.8f);
					
					// Right leg
					gl.glTranslatef(0.2f, 0f, 0.8f);
					this.drawLeg(stickFigure, this.angleRUpperLeg, this.angleRLowerLeg, this.angleRFoot);
					gl.glTranslatef(-0.2f, 0f, -0.8f);
					
					// Head
					gl.glTranslatef(0f, 0f, 1.5f);
					this.drawHead(stickFigure);
					gl.glTranslatef(0f, 0f, -1.5f);
					
					// Torso
					gl.glTranslatef(0f, 0f, 0.8f);
					this.drawTorso(stickFigure);
					gl.glTranslatef(0f, 0f, -0.8f);
        }
				
				public void drawHead(boolean stickFigure) {
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the neck
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, 0.1f);

						// Draw the head
						gl.glVertex3f(0f, 0f, 0.1f);
						gl.glVertex3f(0f, 0f, 0.5f);
						gl.glEnd();
					} else {
						// Neck
						glut.glutSolidCylinder(0.05f, 0.1f + 0.15f / 2.0f, 20, 20);
						
						// Head
						gl.glTranslatef(0f, 0f, 0.1f + 0.15f);
						glut.glutSolidSphere(0.15f, 20, 20);
						gl.glTranslatef(0f, 0f, -0.1f - 0.15f);
					}
				}
				
				public void drawTorso(boolean stickFigure) {
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the back
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, 0.7f);

						// Draw the shoulders
						gl.glVertex3f(-0.3f, 0f, 0.7f);
						gl.glVertex3f(0.3f, 0f, 0.7f);

						// Draw the hips
						gl.glVertex3f(-0.2f, 0f, 0.0f);
						gl.glVertex3f(0.2f, 0f, 0.0f);
						gl.glEnd();
					} else {
						gl.glTranslatef(0f, 0f, 0.7f / 2f);
						gl.glScalef(0.6f, 0.2f, 0.7f);
						glut.glutSolidCube(1.0f);
						gl.glScalef(1f / 0.6f, 1f / 0.2f, 1f / 0.7f);
						gl.glTranslatef(0f, 0f, -0.7f / 2f);
					}
				}
				
				public void drawLeg(boolean stickFigure, double angleUpperLeg, double angleLowerLeg, double angleFoot) {
					// Upper leg rotation
					gl.glRotatef((float)angleUpperLeg, 1f, 0f, 0f);
					
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the upper leg
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, -0.4f);
						gl.glEnd();
					} else {
						// Draw the upper leg
						glut.glutSolidCylinder(0.08f, -0.4f, 20, 20);
					}
					
					gl.glTranslatef(0f, 0f, -0.4f);
					gl.glRotatef((float)angleLowerLeg, 1f, 0f, 0f);
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the lower leg
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, -0.4f);
						gl.glEnd();
					} else {
						// Draw lower leg
						glut.glutSolidCylinder(0.08f, -0.4f, 20, 20);
					}
					
					gl.glTranslatef(0f, 0f, -0.4f);
					gl.glRotatef((float)angleFoot, 1f, 0f, 0f);
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the foot
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, 0.1f);
						gl.glEnd();
					} else {
						glut.glutSolidCylinder(0.08f, 0.1f, 20, 20);
					}
					
					// Undo all transformations
					gl.glRotatef(-(float)angleFoot, 1f, 0f, 0f);
					gl.glTranslatef(0f, 0f, 0.4f);
					
					gl.glRotatef(-(float)angleLowerLeg, 1f, 0f, 0f);
					gl.glTranslatef(0f, 0f, 0.4f);
					
					gl.glRotatef(-(float)angleUpperLeg, 1f, 0f, 0f);
				}
				
				public void drawArm(boolean stickFigure, double angleUpperArm, double angleLowerArm, double angleHand) {
					// Upper arm rotation
					gl.glRotatef((float)angleUpperArm, 1f, 0f, 0f);
					
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the upper arm
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, -0.4f);
						gl.glEnd();
					} else {
						// Draw upper arm
						glut.glutSolidCylinder(0.08f, -0.4f, 20, 20);
					}
					
					// Lower arm translation and then rotation
					gl.glTranslatef(0f, 0f, -0.4f);
					gl.glRotatef((float)angleLowerArm, 1f, 0f, 0f);
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the lower arm
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0f, -0.4f);
						gl.glEnd();
					} else {
						// Draw lower arm
						glut.glutSolidCylinder(0.08f, -0.4f, 20, 20);
					}
					
					// Hand translation and then rotation
					gl.glTranslatef(0f, 0f, -0.4f);
					gl.glRotatef((float)angleHand, 1f, 0f, 0f);
					if (stickFigure) {
						gl.glBegin(GL_LINES);
						// Draw the hand
						gl.glVertex3f(0f, 0f, 0f);
						gl.glVertex3f(0f, 0.1f, 0f);
						gl.glEnd();
					} else {
						glut.glutSolidCylinder(0.08f, 0.1f, 20, 20);
					}
					
					// Undo all transformations
					gl.glRotatef(-(float)angleHand, 1f, 0f, 0f);
					gl.glTranslatef(0f, 0f, 0.4f);
					
					gl.glRotatef(-(float)angleLowerArm, 1f, 0f, 0f);
					gl.glTranslatef(0f, 0f, 0.4f);
					
					gl.glRotatef(-(float)angleUpperArm, 1f, 0f, 0f);
				}
    }
    
    /**
     * Implementation of a camera with a position and orientation. 
     */
    private class Camera {
        
        /** The position of the camera. */
        public Vector eye = new Vector(3f, 6f, 5f);
        
        /** The point to which the camera is looking. */
        public Vector center = Vector.O;
        
        /** The up vector. */
        public Vector up = Vector.Z;
        
        /**
         * Updates the camera viewpoint and direction based on the
         * selected camera mode.
         */
        public void update(int mode) {
            robots[0].toString();
            
            // Helicopter mode
            if (1 == mode) {  
                setHelicopterMode();
                
            // Motor cycle mode
            } else if (2 == mode) { 
                setMotorCycleMode();
                
            // First person mode
            } else if (3 == mode) { 
                setFirstPersonMode();
                
            // Auto mode
            } else if (4 == mode) { 
                // code goes here...
                
            // Default mode
            } else {
                setDefaultMode();
            }
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the camera's default mode.
         */
        private void setDefaultMode() {
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the helicopter mode.
         */
        private void setHelicopterMode() {
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the motorcycle mode.
         */
        private void setMotorCycleMode() {
            // code goes here ...
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the first person mode.
         */
        private void setFirstPersonMode() {
            // code goes here ...
        }
        
    }
    
    /**
     * Implementation of a race track that is made from Bezier segments.
     */
    private class RaceTrack {
        
        /** Array with control points for the O-track. */
        private Vector[] controlPointsOTrack;
        
        /** Array with control points for the L-track. */
        private Vector[] controlPointsLTrack;
        
        /** Array with control points for the C-track. */
        private Vector[] controlPointsCTrack;
        
        /** Array with control points for the custom track. */
        private Vector[] controlPointsCustomTrack;
        
        /**
         * Constructs the race track, sets up display lists.
         */
        public RaceTrack() {
            // code goes here ...
        }
        
        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr) {
            
            // The test track is selected
            if (0 == trackNr) {
                // code goes here ...
            
            // The O-track is selected
            } else if (1 == trackNr) {
                // code goes here ...
                
            // The L-track is selected
            } else if (2 == trackNr) {
                // code goes here ...
                
            // The C-track is selected
            } else if (3 == trackNr) {
                // code goes here ...
                
            // The custom track is selected
            } else if (4 == trackNr) {
                // code goes here ...
                
            }
        }
        
        /**
         * Returns the position of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getPoint(double t) {
            return Vector.O; // <- code goes here
        }
        
        /**
         * Returns the tangent of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getTangent(double t) {
            return Vector.O; // <- code goes here
        }
        
    }
    
    /**
     * Implementation of the terrain.
     */
    private class Terrain {
        
        /**
         * Can be used to set up a display list.
         */
        public Terrain() {
            // code goes here ...
        }
        
        /**
         * Draws the terrain.
         */
        public void draw() {
            // code goes here ...
        }
        
        /**
         * Computes the elevation of the terrain at ({@code x}, {@code y}).
         */
        public float heightAt(float x, float y) {
            return 0; // <- code goes here
        }
    }
    
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
    }
    
}
