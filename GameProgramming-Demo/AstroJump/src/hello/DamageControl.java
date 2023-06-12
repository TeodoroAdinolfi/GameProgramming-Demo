/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author foggia
 */
public class DamageControl extends AbstractControl {

    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private static final float DURATION = 3.0f,
            ACCELERATION = 9.8f,
            INITAL_SPEED = 0.5f * ACCELERATION * DURATION,
            OMEGA = 2.0f * FastMath.PI * 5.0f;
    private BoundingSphere sphere1 = new BoundingSphere(0.6f, Vector3f.ZERO);
    private BoundingSphere sphere2 = new BoundingSphere(0.4f, Vector3f.ZERO);
    private Vector3f pos = new Vector3f();
    private CollisionResults collisionResults = new CollisionResults();
    private boolean collided = false;
    private boolean dead = false;
    private Node enemies;
    private float time;

    public DamageControl(Node rootNode) {
        enemies = (Node) rootNode.getChild("Enemies");
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateSpheres();
        if (dead) {
            updateDead(tpf);
        } else {
            updateAlive(tpf);
        }
    }

    private void updateAlive(float tpf) {
        collided = false;
        checkSphere(sphere1);
        checkSphere(sphere2);
        if (collided) {
            dead = true;
            time = 0.0f;
            Spatial astro = getSpatial();
            MotionControl mc = astro.getControl(MotionControl.class);
            mc.setEnabled(false);
        }
    }

    private void updateDead(float tpf) {
        time += tpf;
        Spatial astro = getSpatial();
        float height = astro.getControl(TerrainHeightControl.class).getHeight();
        if (time < DURATION) {
            pos.set(astro.getLocalTranslation());
            pos.y = height + INITAL_SPEED * time - 0.5f * ACCELERATION * time * time;
            astro.setLocalTranslation(pos);
            astro.rotate(0.0f, OMEGA * tpf, 0.0f);
        } else {
            dead = false;
            pos.set(astro.getLocalTranslation());
            pos.y = height;
            astro.setLocalTranslation(pos);
            MotionControl mc = astro.getControl(MotionControl.class);
            mc.setEnabled(true);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    private void updateSpheres() {
        Spatial astro = getSpatial();
        pos.set(astro.getLocalTranslation());
        pos.y += 0.5f;
        sphere1.setCenter(pos);
        Quaternion rot = astro.getLocalRotation();
        pos.set(0f, 1f, 1f);
        rot.multLocal(pos);
        pos.addLocal(astro.getLocalTranslation());
        sphere2.setCenter(pos);
    }

    private void checkSphere(BoundingSphere sphere) {
        Spatial astro = getSpatial();
        collisionResults.clear();
        enemies.collideWith(sphere, collisionResults);
        for (CollisionResult collision : collisionResults) {
            Geometry other = collision.getGeometry();
            if (other instanceof ParticleEmitter) {
                continue;
            }
            removeEnemy(other);
            collided = true;
        }
    }

    private void removeEnemy(Spatial other) {
        Node parent = other.getParent();
        if (parent == enemies) {
            parent.detachChild(other);
        } else if (parent != null) {
            removeEnemy(parent);
        }
    }

}
