/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
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
public class UfoControl extends AbstractControl {

    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private static final float SPEED_INCREASE_MIN = 0.15f,
            SPEED_INCREASE_MAX = 0.5f,
            SPEED_CHANGE_TIME = 10.0f,
            ANGULAR_SPEED = 2 * FastMath.PI / 2f;
    private Spatial astro;
    private Vector3f offset = new Vector3f();
    private float speed = 0.0f;
    private float time = 0.0f;
    float ang;

    public UfoControl(Node rootNode) {
        astro = rootNode.getChild("Astro");
    }

    @Override
    protected void controlUpdate(float tpf) {
        Spatial ufo = getSpatial();
        offset.set(astro.getLocalTranslation());
        offset.subtractLocal(ufo.getLocalTranslation());

        offset.normalizeLocal();
        offset.multLocal(speed * tpf);
        ufo.move(offset);

        time += tpf;
        if (time > SPEED_CHANGE_TIME) {
            time = 0.0f;
            speed += FastMath.nextRandomFloat() * (SPEED_INCREASE_MAX - SPEED_INCREASE_MIN) + SPEED_INCREASE_MIN;
        }

        ang = ANGULAR_SPEED * tpf;
        ufo.rotate(0, ang, 0);

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

}
