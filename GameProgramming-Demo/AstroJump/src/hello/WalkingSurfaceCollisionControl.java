/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.Node;

/**
 *
 * @author teodoroadinolfi
 */
public class WalkingSurfaceCollisionControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private final Node walkingSurface;
    private final Ray ray = new Ray();
    private final CollisionResults collisionResults = new CollisionResults();
    private final Vector3f vec = new Vector3f();

    private float maxHeightFound = 0.0f;
    private boolean collide = false;

    public float getMaxHeightFound() {
        return maxHeightFound;
    }

    public WalkingSurfaceCollisionControl(Node walkingSurface, float offset) {
        this.walkingSurface = walkingSurface;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public void adjustHeight(boolean jump) {

        Spatial spatial = getSpatial();
        Vector3f pos = spatial.getLocalTranslation();
        float height = findHeight(pos.x, pos.z, pos.y);

        if ((collide && height <= -1 && !jump)) {
            System.out.println("Falling detection");
            spatial.getControl(MotionControl.class).setFalling(true);
            spatial.getControl(MotionControl.class).setFallingHeight(maxHeightFound);
            float terrainHeight = spatial.getControl(TerrainHeightControl.class).getHeight();
            spatial.getControl(MotionControl.class).setFallingMinHeight(terrainHeight);
            spatial.getControl(MotionControl.class).setObastacleTouched(false);
            collide = false;
            maxHeightFound = 0;
        } else if (height >= maxHeightFound) {
            maxHeightFound = height;
        }
    }

    private float findHeight(float x, float z, float y) {
        vec.set(x, y + 0.5f, z);
        ray.setOrigin(vec);
        vec.set(0.0f, -1.0f, 0.f);
        ray.setDirection(vec);
        collisionResults.clear();

        boolean collisionFound = false;
        float height = 0.0f;

        for (Spatial collidableElement : walkingSurface.getChildren()) {
            collidableElement.collideWith(ray, collisionResults);
            if (collisionResults.size() > 0) {
                CollisionResult c = collisionResults.getClosestCollision();
                Vector3f pos = c.getContactPoint();
                if (getSpatial().getLocalTranslation().y > pos.y && pos.y > 0) {
                    // Sono sopra un oggetto
                    collide = true;
                    spatial.getControl(MotionControl.class).setObastacleTouched(true);
                    height = pos.y;
                    collisionFound = true;
                    break;
                } else if (pos.y > 0) {
                    height = pos.y;
                    collisionFound = true;
                    break;
                }
            }
        }

        if (!collisionFound) {
            height = -1.0f;
        }

        return height;
    }

}
