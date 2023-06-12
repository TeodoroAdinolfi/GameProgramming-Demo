package hello;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture.WrapMode;
import java.util.Random;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private Node walkingSurface;

    private static final int NUM_PLATFORMS = 8; // Numero di piattaforme da generare
    private static final float PLATFORM_VARIANCE_ALONG_X = 18f;
    private static final float PLATFORM_VARIANCE_ALONG_Z = 14f;

    private static final float PLATFORM_SIZE = 2.8f;
    private static final float MIN_DISTANCE = 5f; // Distanza minima tra le piattaforme

    private Random random;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        random = new Random();

        configureInputs();

        Light amb = new AmbientLight(ColorRGBA.DarkGray);
        rootNode.addLight(amb);
        Light dir = new DirectionalLight(new Vector3f(-1f, -1f, 0f));
        rootNode.addLight(dir);
        Light dir2 = new DirectionalLight(new Vector3f(1f, 1f, 0f),
                new ColorRGBA(0.1f, 0.1f, 0.1f, 1f));
        rootNode.addLight(dir2);

        Spatial terrain = createTerrain();
        rootNode.attachChild(terrain);

        Spatial sky = createSky();
        rootNode.attachChild(sky);

        Node enemies = new Node("Enemies");
        rootNode.attachChild(enemies);

        walkingSurface = new Node("walkingSurface");
        rootNode.attachChild(walkingSurface);
        
        // Spawn Point
        float prevX = 5f;
        float prevY = 98f;
        float prevZ = 5f;

        spawnPlatform(5f, 98f, 5f, 5f);


        Spatial astro = createAstro();
        astro.setLocalTranslation(prevX, prevY, prevZ);
        rootNode.attachChild(astro);

        for (int i = 0; i < NUM_PLATFORMS; i++) {
            // Genera dimensioni casuali per la piattaforma
            float x_increment = (random.nextFloat() - 0.5f) * 2f * PLATFORM_VARIANCE_ALONG_X;
            float z_increment = random.nextFloat() * PLATFORM_VARIANCE_ALONG_Z;
            float size = random.nextFloat() * PLATFORM_SIZE + PLATFORM_SIZE;

            // Calcola la distanza orizzontale tra le piattaforme
            float distance = random.nextFloat() * MIN_DISTANCE + MIN_DISTANCE;

            // Calcola la nuova posizione y relativa alla piattaforma precedente
            prevY += distance;
            prevX += x_increment;
            prevZ += z_increment;

            spawnPlatform(prevX, prevY, prevZ, size);

            if (i == NUM_PLATFORMS - 1) {
                Spatial collectable = createCollectable();
                collectable.setLocalTranslation(prevX, prevY + 0.5f, prevZ);
                rootNode.attachChild(collectable);

                CollectableControl collectableControl = new CollectableControl(collectable, new VictoryManager(this.guiNode, this.assetManager, this.getCamera()));
                astro.addControl(collectableControl);
            }

        }

        Spatial ufo = createUfo();
        ufo.move(10f, 120f, 10f);
        enemies.attachChild(ufo);

        spawnUFOs(5, 80f, 150f, -20f, 20f, -20f, 20f);

        cam.setLocation(new Vector3f(0f, 1.5f, -4f));

    }

    private Geometry createBox(float size) {
        Box b = new Box(size, 0.1f, size);
        Geometry geom = new Geometry("Box", b);
        Texture texture = assetManager.loadTexture("Textures/box.jpeg");
        texture.setWrap(WrapMode.Repeat); 
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", texture);       
        geom.setMaterial(material);
        return geom;
    }

    private Spatial createTerrain() {
        Texture heightImage
                = assetManager.loadTexture("Textures/terrain/height_map.png");
        ImageBasedHeightMap hmap = new ImageBasedHeightMap(
                heightImage.getImage(),
                0.5f);
        hmap.load();
        Material mat = new Material(assetManager,
                "Common/MatDefs/Terrain/Terrain.j3md");
        Texture splat
                = assetManager.loadTexture("Textures/terrain/splat_map.png");
        mat.setTexture("Alpha", splat);
        Texture grass
                = assetManager.loadTexture("Textures/terrain/base.png");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex1", grass);
        mat.setFloat("Tex1Scale", 128.0f);

        Texture road
                = assetManager.loadTexture("Textures/terrain/dirt.png");
        road.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex2", road);
        mat.setFloat("Tex2Scale", 128.0f);

        Texture dirt
                = assetManager.loadTexture("Textures/terrain/base.png");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex3", dirt);
        mat.setFloat("Tex3Scale", 128.0f);

        final int IMAGE_SIZE = 512, BLOCK_SIZE = 64;

        TerrainQuad terrain = new TerrainQuad("Terrain",
                BLOCK_SIZE + 1, IMAGE_SIZE + 1,
                hmap.getHeightMap());

        terrain.setMaterial(mat);

        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator(new DistanceLodCalculator(BLOCK_SIZE + 1, 2.7f));
        terrain.addControl(control);

        return terrain;
    }

    private Spatial createSky() {
        // Il cielo è rappresentato dallo spazio quindi uso la stessa texture
        Texture up = assetManager.loadTexture("Textures/sky_up.jpg");
        Texture down = assetManager.loadTexture("Textures/sky_up.jpg");
        Texture side = assetManager.loadTexture("Textures/sky_up.jpg");
        return SkyFactory.createSky(assetManager, side, side, side, side, up, down);
    }



    private Spatial createAstro() {
        Spatial astro = assetManager.loadModel("Models/astro.j3o");
        Control c = new MotionControl(inputManager);
        astro.addControl(c);
        Control cc = new CamControl(cam, inputManager);
        astro.addControl(cc);
        Control dc = new DamageControl(rootNode);
        astro.addControl(dc);
        Control hc = new TerrainHeightControl(rootNode, 0.6f);
        astro.addControl(hc);
        Control wc = new WalkingSurfaceCollisionControl(walkingSurface, 3f);
        astro.addControl(wc);
        astro.setName("Astro");
        return astro;
    }

    private Spatial createUfo() {
        Spatial ufo = assetManager.loadModel("Models/UFO.j3o");
        ufo.scale(0.2f);
        UfoControl mc = new UfoControl(rootNode);
        ufo.addControl(mc);
        return ufo;
    }

    private void configureInputs() {
        inputManager.addMapping("Forward",
                new KeyTrigger(KeyInput.KEY_I),
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward",
                new KeyTrigger(KeyInput.KEY_K),
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("RotateLeft",
                new KeyTrigger(KeyInput.KEY_J),
                new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RotateRight",
                new KeyTrigger(KeyInput.KEY_L),
                new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("CamLeft",
                new KeyTrigger(KeyInput.KEY_LEFT),
                new MouseAxisTrigger(MouseInput.AXIS_X, true)
        );
        inputManager.addMapping("CamRight",
                new KeyTrigger(KeyInput.KEY_RIGHT),
                new MouseAxisTrigger(MouseInput.AXIS_X, false)
        );

        inputManager.addMapping("CamUp",
                new KeyTrigger(KeyInput.KEY_UP),
                new MouseAxisTrigger(MouseInput.AXIS_Y, false)
        );
        inputManager.addMapping("CamDown",
                new KeyTrigger(KeyInput.KEY_DOWN),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true)
        );

        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addMapping("Wave", new KeyTrigger(KeyInput.KEY_R));

    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    private void spawnPlatform(float x, float y, float z, float size) {
        Spatial box = createBox(size);
        box.setLocalTranslation(x, y, z);
        walkingSurface.attachChild(box);
    }

    private void spawnUFOs(int numUFOs, float minAltitude, float maxAltitude, float minX, float maxX, float minZ, float maxZ) {
        Node enemies = (Node) rootNode.getChild("Enemies");
        for (int i = 0; i < numUFOs; i++) {
            float x = random.nextFloat() * (maxX - minX) + minX;
            float y = random.nextFloat() * (maxAltitude - minAltitude) + minAltitude;
            float z = random.nextFloat() * (maxZ - minZ) + minZ;
            Spatial ufo = createUfo();
            ufo.setLocalTranslation(x, y, z);
            enemies.attachChild(ufo);
        }
    }

    private Spatial createCollectable() {
        // Spawna un piccolo cubo della vittoria!
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry geometry = new Geometry("Collectable", box);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Yellow);
        geometry.setMaterial(material);
        return geometry;
    }

}
