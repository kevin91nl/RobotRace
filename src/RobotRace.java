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
		
		/*========================================================================*/
		// GLOBAL METHODS
		/*========================================================================*/
		/**
		 * Set the material.
		 * 
		 * @param material 
		 */
		public void applyMaterial(Material material) {
			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, material.diffuse, 0);
			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, material.specular, 0);
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, material.ambient, 0);
            gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, material.shininess);
		}
		
		/*========================================================================*/
		// END GLOBAL METHODS
		/*========================================================================*/
    
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
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
        
        // Set viewing properties.
        gs.theta = 0.5f * 0.5f * (float)Math.PI;
        gs.phi = 0.5f * 0.5f * (float)Math.PI;

        // Setup the lighting
        initializeLighting();
    }

    /**
     * Initialize the lighting.
     */
    public void initializeLighting() {
        // Enable shading and lighting
        float[] ambientColor = {0.5f, 0.5f, 0.5f, 1f};
        gl.glEnable(GL_LIGHTING);
        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glEnable(GL_NORMALIZE);
        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambientColor, 0);
    }

    /**
     * Create lights.
     */
    public void createLights() {
        // Draw the ambient lighting
        float[] ambientColor = {1f, 1f, 1f, 1f};
        float[] ambientPosition = {0f, 0f, 0f, 1f};
        float[] ambientDiffuse = {1f, 1f, 1f, 1f};
        float[] ambientSpecular = {1f, 1f, 1f, 1f};
        gl.glBegin(GL_LINES);
        gl.glVertex3d(camera.eye.x() + 1, camera.eye.y() + 1, camera.eye.z());
        gl.glVertex3d(camera.center.x(), camera.center.y(), camera.center.z());
        gl.glEnd();
        //gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambientColor, 0);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, ambientPosition, 0);
        //gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, ambientDiffuse, 0);
        //gl.glLightfv(GL_LIGHT0, GL_SPECULAR, ambientSpecular, 0);
        gl.glEnable(GL_LIGHT0);

        // Reposition the light at the left-top camera position.
        // We need to move the camera to the left.
        // This can be done by calculating (0, 0, 1) x (C - E) (which is "left").
        // Then, "up" is (0, 0, 1).
        Vector Vup = new Vector(0, 0, 1);
        Vector Vleft = Vup.cross(camera.center.subtract(camera.eye)).normalized();
        
        // Calculate the light position and convert to float array.
        // Light position = E + Vleft + Vup.
        Vector cameralightPosition = camera.eye.add(Vleft.scale(0.1)).add(Vup.scale(0.1));
        float[] fCameralightPosition = {
            (float)cameralightPosition.x(),
            (float)cameralightPosition.y(),
            (float)cameralightPosition.z()
        };
        
        // Calculate the light direction. This is simply C - E.
        float[] fCameralightDirection = {
            (float)camera.center.subtract(camera.eye).scale(50).x(),
            (float)camera.center.subtract(camera.eye).scale(50).y(),
            (float)camera.center.subtract(camera.eye).scale(50).z(),
            1f
        };
        
        // Now create a spotlight which starts at the camera light position
        // and goes in the direction of C - E with an angle of 60 degrees.
        float[] lightSpecular = {1f, 1f, 1f, 1f};
        float[] lightDiffuse = {1f, 1f, 1f, 0.5f};
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightSpecular, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, fCameralightPosition, 0);
        gl.glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 60f);
        gl.glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 100f);
        gl.glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, fCameralightDirection, 0);
        gl.glEnable(GL_LIGHT1);
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        // Let alpha be the viewing angle.
        // Then tan(1/2 alpha) = 1/2 * viewWidth / viewDistance
        // So alpha = 2 * arcTan(viewWidth / (2 * viewDistance))
        float alpha = 2f * (float)Math.atan(gs.vWidth / (2 * gs.vDist));
        glu.gluPerspective(40, (float)gs.w / (float)gs.h, 0.1 * gs.vDist, 10 * gs.vDist);
        
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
               
        // Update the view according to the camera mode
        camera.update(gs.camMode);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());

        // Create the lights before doing anything else
        createLights();
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
        robots[0].draw(gs.showStick);
		gl.glTranslatef(1f, 0f, 0f);
		robots[1].draw(gs.showStick);
		gl.glTranslatef(1f, 0f, 0f);
		robots[2].draw(gs.showStick);
		gl.glTranslatef(1f, 0f, 0f);
		robots[3].draw(gs.showStick);
		gl.glPopMatrix();

        // Reset the color
        applyMaterial(Material.BLACK);
        
        // Draw race track
        raceTrack.draw(gs.trackNr);
        
        // Draw terrain
        terrain.draw();
    }
    
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        // Set the size of the cube to 1m
        float cubeSize = 1f;

        // Set the radius of the sphere
        float sphereRadius = 0.1f * cubeSize;
        
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
        applyMaterial(Material.YELLOW);
        // Then, draw the sphere
        glut.glutSolidSphere(sphereRadius, 30, 30);
        // Reset the color to black
        applyMaterial(Material.BLACK);
        
        // The cones need first to be rotated when they are at the origin.
        // After the rotation, a translation is needed. So let V be the
        // coordinate vector. Let R be the rotation matrix and T be the translation
        // matrix. Then the transformed coordinates V' after the rotation
        // are V' = RV. The transformed coordinates V'' after the translation
        // are thus V'' = TV' = TRV. So, first the translation matrix need
        // to be applied and after that the rotation vector must be applied.
        
        // Draw the red cone at (1, 0, 0)
        // Set the color for the x-axis cone (red)
        applyMaterial(Material.RED);
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
        applyMaterial(Material.GREEN);
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
        applyMaterial(Material.BLUE);
        // Translate to (0, 0, 1)
        gl.glTranslatef(0f, 0f, 1f * cubeSize);
        // Draw the cone
        glut.glutSolidCone(coneBase, coneHeight, 20, 20);
        // Undo the translation
        gl.glTranslatef(0f, 0f, -1f * cubeSize);
        
        // Reset the color
        applyMaterial(Material.BLACK);
    }
    
    /**
     * Materials that can be used for the robots.
     */
    public enum Material {
			
		/**
		 * Black material for testing purposes.
		 */
		BLACK (
            new float[] {0f, 0f, 0f, 0f},
            new float[] {0f, 0f, 0f, 0f},
            new float[] {0f, 0f, 0f, 0f},
            0f),
        
        /** 
         * Gold material properties.
         * Modify the default values to make it look like gold.
         */
        GOLD (
            new float[] {0.75164f, 0.60648f, 0.22648f, 1.0f},
            new float[] {0.628281f, 0.555802f, 0.366065f, 1.0f},
            new float[] {0.24725f, 0.1995f, 0.0745f, 1f},
            4f),
        
        /**
         * Silver material properties.
         * Modify the default values to make it look like silver.
         */
        SILVER (
            new float[] {0.50754f, 0.50754f, 0.50754f, 1.0f},
            new float[] {0.508273f, 0.508273f, 0.508273f, 1.0f},
            new float[] {0.19225f, 0.19225f, 0.19225f, 1f},
            4f),
        
        /** 
         * Wood material properties.
         * Modify the default values to make it look like wood.
         */
        WOOD (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {0.3f, 0.1f, 0.1f, 1.0f},
            new float[] {0.3f, 0.106f, 0f, 1.0f},
            3f),
        
        /**
         * Orange material properties.
         * Modify the default values to make it look like orange.
         */
        ORANGE (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {1.0f, 0.6f, 0.0f, 1.0f},
            new float[] {0.25f, 0.25f, 0.25f, 1.0f},
            1f),

        /**
         * Red material properties.
         * Modify the default values to make it look like red.
         */
        RED (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {1.0f, 0.0f, 0.0f, 1.0f},
            new float[] {0f, 0f, 0f, 1f},
            0f),

        /**
         * BLUE material properties.
         * Modify the default values to make it look like BLUE.
         */
        BLUE (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {0.0f, 0.0f, 1.0f, 1.0f},
            new float[] {0f, 0f, 0f, 1f},
            0f),

        /**
         * GREEN material properties.
         * Modify the default values to make it look like GREEN.
         */
        GREEN (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {0.0f, 1.0f, 0.0f, 1.0f},
            new float[] {0f, 0f, 0f, 1f},
            0f),

        /**
         * YELLOW material properties.
         * Modify the default values to make it look like YELLOW.
         */
        YELLOW (
            new float[] {1.0f, 1.0f, 1.0f, 1.0f},
            new float[] {1.0f, 1.0f, 0.0f, 1.0f},
            new float[] {0f, 0f, 0f, 1f},
            0f);
        
        /** The diffuse RGBA reflectance of the material. */
        public float[] diffuse;
        
        /** The specular RGBA reflectance of the material. */
        public float[] specular;

        /** The ambient RGBA reflectance of the material. **/
        public float[] ambient;

        /** The shininess reflectance of the material. **/
        public float shininess;
        
        /**
         * Constructs a new material with diffuse and specular properties.
         */
        private Material(float[] diffuse, float[] specular, float[] ambient, float shininess) {
            this.diffuse = diffuse;
            this.specular = specular;
            this.ambient = ambient;
            this.shininess = shininess;
        }
    }
    
    /**
     * Represents a Robot, to be implemented according to the Assignments.
     */
    private class Robot {
			
				public double angleLUpperArm = 180.0;
				public double angleLLowerArm = -0.0;
				public double angleLHand = 90.0;
				
				public double angleRUpperArm = 180.0;
				public double angleRLowerArm = -0.0;
				public double angleRHand = 90.0;
				
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
        }
        
        /**
         * Draws this robot (as a {@code stickfigure} if specified).
         */
        public void draw(boolean stickFigure) {
					applyMaterial(this.material);
					
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
						glut.glutSolidCylinder(0.05f, 0.1f + 0.15f / 2.0f, 30, 30);
						
						// Head
						gl.glTranslatef(0f, 0f, 0.1f + 0.15f);
						glut.glutSolidSphere(0.15f, 100, 100);
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
            this.eye = gs.cnt.add(V);
            this.center = gs.cnt;
            
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

            } else {
                setDefaultMode();
            }
        }
        
        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based
         * on the camera's default mode.
         */
        private void setDefaultMode() {
            setTopMode();
        }

        /**
         * View from top (fixed). Computes {@code eye}, {@code center} and {@code up}.
         */
        private void setTopMode() {
            this.eye = new Vector(0, 0, gs.phi * 100);
            this.center = new Vector(1, 1, 1);
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

        /** Size of the tracks */
        private double trackSize = 4.0;
        
        /**
         * Constructs the race track, sets up display lists.
         */
        public RaceTrack() {
            initOTrack(20, 20);
            initLTrack(15, 20);
            initCTrack(20, 20);
            initCustomTrack(20, 20);
        }

        /**
         * Initialize the control points of the custom track (8-shape).
         * 
         * @param width  Maximum width (x-value) of the figure.
         * @param height Maximum height (y-value) of the figure.
         */
        private void initCustomTrack(double width, double height) {
            double halfTrackSize = trackSize / 2.0;

            controlPointsCustomTrack = new Vector[13];
            controlPointsCustomTrack[0] = new Vector(0.5 * width, 0.5 * height, 0);
            controlPointsCustomTrack[1] = new Vector(halfTrackSize, 0.5 * height, 0);
            controlPointsCustomTrack[2] = new Vector(halfTrackSize, height, 0);
            controlPointsCustomTrack[3] = new Vector(0.5 * width, height - halfTrackSize, 0);

            controlPointsCustomTrack[4] = new Vector(width - halfTrackSize, height, 0);
            controlPointsCustomTrack[5] = new Vector(width - halfTrackSize, 0.5 * height, 0);
            controlPointsCustomTrack[6] = new Vector(0.5 * width, 0.5 * height, 0);

            // Symmetry
            controlPointsCustomTrack[7] = new Vector(width - halfTrackSize, height - 0.5 * height, 0);
            controlPointsCustomTrack[8] = new Vector(width - halfTrackSize, height - height, 0);

            controlPointsCustomTrack[9] = new Vector(0.5 * width, height - (height - halfTrackSize), 0);
            controlPointsCustomTrack[10] = new Vector(halfTrackSize, height - height, 0);
            controlPointsCustomTrack[11] = new Vector(halfTrackSize, height - 0.5 * height, 0);

            controlPointsCustomTrack[12] = new Vector(0.5 * width, height - 0.5 * height, 0);
        }

        /**
         * Initialize the control points of the C track.
         * 
         * @param width  Maximum width (x-value) of the figure.
         * @param height Maximum height (y-value) of the figure.
         */
        private void initCTrack(double width, double height) {
            // Half the size of the track
            double halfTrackSize = trackSize / 2.0;

            // The size between two lanes
            double size = 2.5;

            // Radius for turnarounds
            double radius = 0.1;

            // Initialize the control points
            controlPointsCTrack = new Vector[31];

            // Mid-left points
            controlPointsCTrack[0] = new Vector(0, 0.5 * height, 0);
            controlPointsCTrack[1] = new Vector(0, 0.5 * (0.5 * height), 0);
            controlPointsCTrack[2] = new Vector(0.5 * (0.5 * width), 0, 0);
            controlPointsCTrack[3] = new Vector(0.5 * width, 0, 0);

            // Bottom point
            controlPointsCTrack[4] = new Vector(0.5 * width + 0.25 * width, 0, 0);
            controlPointsCTrack[5] = new Vector(width, 0.5 * (0.25 * height), 0);
            controlPointsCTrack[6] = new Vector(width, 0.25 * height, 0);

            // Right point
            controlPointsCTrack[7] = new Vector(width, (radius + 0.25) * height, 0);
            controlPointsCTrack[8] = new Vector(width - size, (radius + 0.25) * height, 0);
            controlPointsCTrack[9] = new Vector(width - size, 0.25 * height, 0);

            // Mid corner point
            controlPointsCTrack[10] = new Vector(width - size, 0.5 * (0.25 * height) + size, 0);
            controlPointsCTrack[11] = new Vector(0.5 * width + 0.25 * width - size, size, 0);
            controlPointsCTrack[12] = new Vector(0.5 * width, size, 0);

            // Left corner point
            controlPointsCTrack[13] = new Vector(0.5 * (0.5 * width) + size, size, 0);
            controlPointsCTrack[14] = new Vector(size, 0.5 * (0.5 * height) + size, 0);
            controlPointsCTrack[15] = new Vector(size, 0.5 * height, 0);

            // Symmetry (top)
            controlPointsCTrack[16] = new Vector(size, height - (0.5 * (0.5 * height) + size), 0);
            controlPointsCTrack[17] = new Vector(0.5 * (0.5 * width) + size, height - size, 0);

            // Left (top) corner point
            controlPointsCTrack[18] = new Vector(0.5 * width, height - size, 0);
            controlPointsCTrack[19] = new Vector(0.5 * width + 0.25 * width - size, height - size, 0);
            controlPointsCTrack[20] = new Vector(width - size, height - (0.5 * (0.25 * height) + size), 0);

            // Mid (top) corner point
            controlPointsCTrack[21] = new Vector(width - size, height - 0.25 * height, 0);
            controlPointsCTrack[22] = new Vector(width - size, height - (radius + 0.25) * height, 0);
            controlPointsCTrack[23] = new Vector(width, height - (radius + 0.25) * height, 0);

            // Right (top) point
            controlPointsCTrack[24] = new Vector(width, height - 0.25 * height, 0);
            controlPointsCTrack[25] = new Vector(width, height - 0.5 * (0.25 * height), 0);
            controlPointsCTrack[26] = new Vector(0.5 * width + 0.25 * width, height, 0);

            // Top point
            controlPointsCTrack[27] = new Vector(0.5 * width, height, 0);
            controlPointsCTrack[28] = new Vector(0.5 * (0.5 * width), height, 0);
            controlPointsCTrack[29] = new Vector(0, height - 0.5 * (0.5 * height), 0);

            // Left point
            controlPointsCTrack[30] = new Vector(0, 0.5 * height, 0);

            // Lay all points half size of the tracks from the border
            for (int i = 0; i < controlPointsCTrack.length; i++) {
                double x = controlPointsCTrack[i].x();
                double y = controlPointsCTrack[i].y();
                double z = controlPointsCTrack[i].z();
                if (x > 0.5 * width) {
                    x = x - halfTrackSize;
                } else if (x < 0.5 * width) {
                    x = x + halfTrackSize;
                }
                if (y > 0.5 * height) {
                    y = y - halfTrackSize;
                } else if (y < 0.5 * height) {
                    y = y + halfTrackSize;
                }
                controlPointsCTrack[i] = new Vector(x, y, z);
            }
            
        }

        /**
         * Initialize the control points of the L track.
         * 
         * @param width  Maximum width (x-value) of the figure.
         * @param height Maximum height (y-value) of the figure.
         */
        private void initLTrack(double width, double height) {
            // Size between two lanes
            double size = 0.01 * (width + height) / 2.0;

            // Radius for turnaround point
            double radius = 0.2 * (width + height) / 2.0;

            // Half the size of the track
            double halfTrackSize = trackSize / 2.0;

            controlPointsLTrack = new Vector[31];

            // Initialize all control points
            controlPointsLTrack[0] = new Vector(halfTrackSize, halfTrackSize + radius, 0);
            controlPointsLTrack[1] = new Vector(halfTrackSize, halfTrackSize, 0);
            controlPointsLTrack[2] = new Vector(halfTrackSize, halfTrackSize, 0);
            controlPointsLTrack[3] = new Vector(halfTrackSize + radius, halfTrackSize, 0);

            controlPointsLTrack[4] = new Vector(halfTrackSize + radius, halfTrackSize, 0);
            controlPointsLTrack[5] = new Vector(halfTrackSize + radius, halfTrackSize, 0);
            controlPointsLTrack[6] = new Vector(width - radius - halfTrackSize, halfTrackSize, 0);
            
            controlPointsLTrack[7] = new Vector(width - halfTrackSize, halfTrackSize, 0);
            controlPointsLTrack[8] = new Vector(width - halfTrackSize, halfTrackSize, 0);
            controlPointsLTrack[9] = new Vector(width - halfTrackSize, size / 2.0 + 2 * halfTrackSize, 0);

            controlPointsLTrack[10] = new Vector(width - halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[11] = new Vector(width - halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[12] = new Vector(width - radius - halfTrackSize, size + 3 * halfTrackSize, 0);

            controlPointsLTrack[13] = new Vector(width - radius - halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[14] = new Vector(width - radius - halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[15] = new Vector(size + radius + 3 * halfTrackSize, size + 3 * halfTrackSize, 0);

            controlPointsLTrack[16] = new Vector(size + 3 * halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[17] = new Vector(size + 3 * halfTrackSize, size + 3 * halfTrackSize, 0);
            controlPointsLTrack[18] = new Vector(size + 3 * halfTrackSize, size + 3 * halfTrackSize + radius, 0);

            controlPointsLTrack[19] = new Vector(size + 3 * halfTrackSize, size + radius + 3 * halfTrackSize, 0);
            controlPointsLTrack[20] = new Vector(size + 3 * halfTrackSize, size + radius + 3 * halfTrackSize, 0);
            controlPointsLTrack[21] = new Vector(size + 3 * halfTrackSize, height - radius - halfTrackSize, 0);

            controlPointsLTrack[22] = new Vector(size + 3 * halfTrackSize, height - halfTrackSize, 0);
            controlPointsLTrack[23] = new Vector(size + 3 * halfTrackSize, height - halfTrackSize, 0);
            controlPointsLTrack[24] = new Vector(size / 2.0 + 2 * halfTrackSize, height - halfTrackSize, 0);

            controlPointsLTrack[25] = new Vector(halfTrackSize, height - halfTrackSize, 0);
            controlPointsLTrack[26] = new Vector(halfTrackSize, height - halfTrackSize, 0);
            controlPointsLTrack[27] = new Vector(halfTrackSize, height - halfTrackSize - radius, 0);

            controlPointsLTrack[28] = new Vector(halfTrackSize, height - halfTrackSize - radius, 0);
            controlPointsLTrack[29] = new Vector(halfTrackSize, height - halfTrackSize - radius, 0);
            controlPointsLTrack[30] = new Vector(halfTrackSize, halfTrackSize + radius, 0);
        }

        /**
         * Initialize the control points of the O track.
         * 
         * @param width  Maximum width (x-value) of the figure.
         * @param height Maximum height (y-value) of the figure.
         */
        private void initOTrack(double width, double height) {
            // Initialize all control points
            controlPointsOTrack = new Vector[13];
            controlPointsOTrack[0] = new Vector(trackSize / 2.0, 0.5 * height, 0);

            controlPointsOTrack[1] = new Vector(trackSize / 2.0, 0.25 * height, 0);
            controlPointsOTrack[2] = new Vector(0.25 * width, trackSize / 2.0, 0);

            controlPointsOTrack[3] = new Vector(0.5 * width, trackSize / 2.0, 0);

            controlPointsOTrack[4] = new Vector(0.75 * width, trackSize / 2.0, 0);
            controlPointsOTrack[5] = new Vector(width - trackSize / 2.0, 0.25 * height, 0);

            controlPointsOTrack[6] = new Vector(width - trackSize / 2.0, 0.5 * height, 0);

            controlPointsOTrack[7] = new Vector(width - trackSize / 2.0, 0.75 * height, 0);
            controlPointsOTrack[8] = new Vector(0.75 * width, height - trackSize / 2.0, 0);

            controlPointsOTrack[9] = new Vector(0.5 * width, height - trackSize / 2.0, 0);

            controlPointsOTrack[10] = new Vector(0.25 * width, height - trackSize / 2.0, 0);
            controlPointsOTrack[11] = new Vector(trackSize / 2.0, 0.75 * height, 0);

            controlPointsOTrack[12] = new Vector(trackSize / 2.0, 0.5 * height, 0);
            
        }

        /**
         * Calculate {@code n} faculty.
         * 
         * @param  n
         * @return n!
         */
        public double faculty(double n) {
            if (n == 0) {
                return 1;
            }
            return n * faculty(n - 1);
        }

        /**
         * Calculate binomial coefficients.
         * 
         * @param  n
         * @param  k
         * @return n! / (k! (n - k)!)
         */
        public double binomial(double n, double k) {
            return faculty(n) / (faculty(k) * faculty(n - k));
        }

        /**
         * Calculate a cubic bezier curve.
         * 
         * @param  t
         * @return
         */
        public Vector BezierCurve(double t, Vector[] controlPoints) {
            Vector sum = new Vector(0, 0, 0);
            int n = controlPoints.length - 1;
            for (int i = 0; i <= n; i++) {
                sum = sum.add(controlPoints[i].scale(BernsteinPolynomial(i, n, t)));
            }
            return sum;
        }

        /**
         * Calculate the derivative of a Bezier curve.
         * 
         * @param  t              
         * @param  controlPoints Control points for the curve
         * @return Tangent at point t
         */
        public Vector BezierCurveTangent(double t, Vector[] controlPoints) {
            Vector sum = new Vector(0, 0, 0);
            int n = controlPoints.length - 1;
            for (int i = 0; i <= n - 1; i++) {
                sum = sum.add(controlPoints[i + 1].subtract(controlPoints[i]).scale(n * BernsteinPolynomial(i, n - 1, t)));
            }
            return sum;
        }

        /**
         * Get a point at a cubic Bezier curve.
         * 
         * @param  t  Parameter in [0, 1].
         * @param  P0 First point.
         * @param  P1 First control point.
         * @param  P2 Second control point.
         * @param  P3 Second point.
         * @return Bezier curve at parameter t.
         */
        public Vector getCubicBezierPoint(double t, Vector P0, Vector P1, Vector P2, Vector P3) {
            Vector[] vectors = new Vector[4];
            vectors[0] = P0;
            vectors[1] = P1;
            vectors[2] = P2;
            vectors[3] = P3;
            return BezierCurve(t, vectors);
        }

        /**
         * Get the value of a Bernstein polynomial.
         * 
         * @param  i Parameter.
         * @param  n Parameter.
         * @param  t Parameter.
         * @return   Value of the Bernstein polynomial at point t.
         */
        public double BernsteinPolynomial(int i, int n, double t) {
            return binomial(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i);
        }

        /**
         * Draw a curve given some control points.
         * 
         * @param controlPoints Controlpoints for the curve to draw.
         */
        public void drawCurve(Vector[] controlPoints) {
            // Precision
            int steps = 400;

            // Loop through the steps
            for (int i = 0; i < steps; i++) {
                // Calculate first and next point
                double t1 = (float)i / (float)steps;
                double t2 = (float)(i + 1) / (float)steps;
                Vector V1 = getCurvePoint(t1, controlPoints);
                Vector V2 = getCurvePoint(t2, controlPoints);

                // Draw them
                gl.glBegin(GL_LINES);
                gl.glVertex3d(V1.x(), V1.y(), V1.z());
                gl.glVertex3d(V2.x(), V2.y(), V2.z());
                gl.glEnd();
            }
        }

        /**
         * Draw the path given some control points.
         * 
         * @param controlPoints Controlpoints for the curve to draw.
         */
        public void drawPath(Vector[] controlPoints) {
            // Precision
            int steps = 400;

            // (Base) calculate some vectors
            Vector V1 = getCurvePoint(0, controlPoints);
            Vector Q1 = new Vector(0, 0, 0);
            Vector Q3 = new Vector(0, 0, 0);
            Vector Q5 = new Vector(0, 0, 0);
            Vector Q7 = new Vector(0, 0, 0);

            // Loop through all points. Skip the first point to calculate the next points.
            // The first point is drawn when i == steps.
            for (int i = 0; i <= steps; i++) {
                double t1 = (float)i / (float)steps % 1;
                double t2 = (float)(i + 1) / (float)steps % 1;

                // Calculate mid point
                Vector V2 = getCurvePoint(t2, controlPoints);

                // Calculate directions
                Vector up = new Vector(0, 0, 1);
                Vector down = up.scale(-1);
                Vector forward = V2.subtract(V1);
                Vector left = forward.cross(up).normalized().scale(2);
                Vector right = left.scale(-1);

                // Calculate side points
                Vector Q0 = V1.add(left).subtract(up);
                Vector Q2 = Q0.add(up);
                Vector Q4 = Q2.add(right).add(right);
                Vector Q6 = Q4.add(down);

                // Draw when not base case
                if (i > 0) {
                    applyMaterial(Material.ORANGE);
                    gl.glBegin(GL_QUAD_STRIP);
                    gl.glVertex3d(Q0.x(), Q0.y(), Q0.z());
                    gl.glVertex3d(Q1.x(), Q1.y(), Q1.z());
                    gl.glVertex3d(Q2.x(), Q2.y(), Q2.z());
                    gl.glVertex3d(Q3.x(), Q3.y(), Q3.z());
                    gl.glVertex3d(Q4.x(), Q4.y(), Q4.z());
                    gl.glVertex3d(Q5.x(), Q5.y(), Q5.z());
                    gl.glVertex3d(Q6.x(), Q6.y(), Q6.z());
                    gl.glVertex3d(Q7.x(), Q7.y(), Q7.z());
                    gl.glVertex3d(Q0.x(), Q0.y(), Q0.z());
                    gl.glVertex3d(Q1.x(), Q1.y(), Q1.z());
                    gl.glEnd();
                }

                // Next points are previous points
                V1 = V2;
                Q1 = Q0;
                Q3 = Q2;
                Q5 = Q4;
                Q7 = Q6;
            }
        }
        
        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr) { 
            // The test track is selected
            if (0 == trackNr) {
                int steps = 200;
                for (int i = 0; i < steps; i++) {
                    double t1 = (float)i / (float)steps;
                    double t2 = (float)(i + 1) / (float)steps;
                    Vector V1 = getPoint(t1);
                    Vector V2 = getPoint(t2);
                    gl.glBegin(GL_LINES);
                    gl.glVertex3d(V1.x(), V1.y(), V1.z());
                    gl.glVertex3d(V2.x(), V2.y(), V2.z());
                    gl.glEnd();   
                }
            // The O-track is selected
            } else if (1 == trackNr) {
                drawPath(controlPointsOTrack);
            // The L-track is selected
            } else if (2 == trackNr) {
                drawPath(controlPointsLTrack);
            // The C-track is selected
            } else if (3 == trackNr) {
                drawPath(controlPointsCTrack);
            // The custom track is selected (8-track)
            } else if (4 == trackNr) {
                drawPath(controlPointsCustomTrack);
            }
        }

        /**
         * Get the relative time in a segment.
         * 
         * @param  t             Global time.
         * @param  controlPoints All control points.
         * @return               Relative time in a segment.
         */
        public double getSegmentTime(double t, Vector[] controlPoints) {
            // Bring t back in interval [0, 1)
            t = t % 1;

             // Calculate # segments
            double segmentCount = (controlPoints.length - 1) / 3.0;

            // Calculate in which segment the t falls
            int segment = (int)(t * segmentCount) % (int)segmentCount;

            // Calculate relative t
            double tRel = t * segmentCount - segment;

            return tRel;
        }

        /**
         * Get all points of a segment.
         *
         * @param  t Global time.
         * @param  controlPoints All control points.
         * @return All points of the segment where the t is in.
         */
        public Vector[] getSegmentPoints(double t, Vector[] controlPoints) {
            // Bring t back in interval [0, 1)
            t = t % 1;

            // Calculate # segments
            double segmentCount = (controlPoints.length - 1) / 3.0;

            // Calculate in which segment the t falls
            int segment = (int)(t * segmentCount) % (int)segmentCount;

            // Calculate relative t
            double tRel = t * segmentCount - segment;

            // Calculate the cubic Bezier curve points
            Vector[] segmentPoints = new Vector[4];
            segmentPoints[0] = controlPoints[3 * segment];
            segmentPoints[1] = controlPoints[3 * segment + 1];
            segmentPoints[2] = controlPoints[3 * segment + 2];
            segmentPoints[3] = controlPoints[3 * segment + 3];

            return segmentPoints;
        }

        /**
         * Get a point at a curve.
         * 
         * @param  t             Parameter 0 <= {@code t} <= 1.
         * @param  controlPoints Control points for the curve.
         * @return               Point at curve (based on {@code controlPoints}) at time {@code t}.
         */
        public Vector getCurvePoint(double t, Vector[] controlPoints) {
            Vector[] segmentPoints = getSegmentPoints(t, controlPoints);
            return BezierCurve(getSegmentTime(t, controlPoints), segmentPoints);
        }

        /**
         * Get the tangent at a curve.
         * 
         * @param  t             Parameter 0 <= {@code t} <= 1.
         * @param  controlPoints Control points for the curve.
         * @return               Tangent at curve (based on {@code controlPoints}) at time {@code t}.
         */
        public Vector getCurveTangent(double t, Vector[] controlPoints) {
            Vector[] segmentPoints = getSegmentPoints(t, controlPoints);
            return BezierCurveTangent(getSegmentTime(t, controlPoints), segmentPoints).normalized().scale(3);
        }
        
        /**
         * Returns the position of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getPoint(double t) {
            // P(t) = (10 cos(2 Pi t), 14 sin(2 Pi t), 1)
            Vector P = new Vector(
                10 * Math.cos(2 * Math.PI * t),
                14 * Math.sin(2 * Math.PI * t),
                0
            );
            
            return P;
        }
        
        /**
         * Returns the tangent of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getTangent(double t) {
            // T(t) = P'(t) = (- 10 * 2 * Pi * sin(2 Pi t), 14 * 2 * Pi * cos(2 Pi t), 0)
            Vector T = new Vector(
                -10 * 2 * Math.PI * Math.sin(2 * Math.PI * t),
                14 * 2 * Math.PI * Math.cos(2 * Math.PI * t),
                0
            );

            return T;
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
