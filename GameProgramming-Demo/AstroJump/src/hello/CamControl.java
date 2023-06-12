/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author foggia
 */
public class CamControl extends AbstractControl implements AnalogListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private Camera camera;
    private Vector3f offset;
    private Vector3f pos;
    private Quaternion rotation;
    private static final float OMEGA = 1.0f;

    public CamControl(Camera camera, InputManager inputManager) {
        this.camera = camera;
        offset = new Vector3f(0f, 1.5f, 5.5f);
        pos = new Vector3f();
        inputManager.addListener(this,
                "CamLeft", "CamRight", "CamUp", "CamDown");
        rotation = new Quaternion();
    }

    @Override
    protected void controlUpdate(float tpf) {
        Spatial dino = getSpatial();
        pos.set(dino.getLocalTranslation());
        pos.addLocal(offset);
        camera.setLocation(pos);

        pos.set(dino.getLocalTranslation());
        pos.y += 1.0f;
        camera.lookAt(pos, Vector3f.UNIT_Y);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public void onAnalog(String input, float value, float tpf) {
        if (input.equals("CamLeft")) {
            rotation.fromAngles(0f, -OMEGA * value, 0f);
            rotation.multLocal(offset);
        } else if (input.equals("CamRight")) {
            rotation.fromAngles(0f, OMEGA * value, 0f);
            rotation.multLocal(offset);
        } else if (input.equals("CamUp")) {
            rotation.fromAngles(-OMEGA * value, 0f, 0f);
            rotation.multLocal(offset);
        } else if (input.equals("CamDown")) {
            rotation.fromAngles(OMEGA * value, 0f, 0f);
            rotation.multLocal(offset);
        }
    }

}
