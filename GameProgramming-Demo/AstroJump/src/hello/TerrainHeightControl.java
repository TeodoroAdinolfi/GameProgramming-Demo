/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;

import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author foggia
 */
public class TerrainHeightControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private Spatial terrain;
    private float offset;
    private Ray ray = new Ray();
    private CollisionResults collisionResults = new CollisionResults();
    private Vector3f vec = new Vector3f();

    public TerrainHeightControl(Node rootNode, float offset) {
        terrain = rootNode.getChild("Terrain");
        this.offset = offset;
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

    public float getHeight() {
        Spatial spatial = getSpatial();
        Vector3f pos = spatial.getLocalTranslation();
        float height = findHeight(pos.x, pos.z);
        float h2 = findHeight(pos.x + offset, pos.z);
        if (h2 > height) {
            height = h2;
        }
        h2 = findHeight(pos.x - offset, pos.z);
        if (h2 > height) {
            height = h2;
        }
        h2 = findHeight(pos.x, pos.z + offset);
        if (h2 > height) {
            height = h2;
        }
        h2 = findHeight(pos.x, pos.z - offset);
        if (h2 > height) {
            height = h2;
        }
        return height;
    }

    public void adjustHeight() {
        Spatial spatial = getSpatial();
        float height = getHeight();
        vec.set(spatial.getLocalTranslation());
        vec.y = height;
        spatial.setLocalTranslation(vec);
    }

    public void adjustHeight(boolean jump, boolean falling) {
        Spatial spatial = getSpatial();
        float height = getHeight();
        vec.set(spatial.getLocalTranslation());
        if (!jump && !falling) {
            if (vec.y < height) {
                vec.y = height;
            }
        }
        spatial.setLocalTranslation(vec);
    }

    private float findHeight(float x, float z) {
        vec.set(x, 200.0f, z);
        ray.setOrigin(vec);
        vec.set(0.0f, -1.0f, 0.f);
        ray.setDirection(vec);
        collisionResults.clear();
        terrain.collideWith(ray, collisionResults);
        if (collisionResults.size() > 0) {
            CollisionResult c = collisionResults.getClosestCollision();
            Vector3f pos = c.getContactPoint();
            return pos.y;
        } else {
            return 0.0f;
        }
    }

}
