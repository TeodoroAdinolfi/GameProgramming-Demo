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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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
public class CircleControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private static final float OMEGA=2*FastMath.PI/10;
    private static final float VELOCITY=0.5f;
    private Vector3f s=new Vector3f();
    private Vector3f w=new Vector3f();

    @Override
    protected void controlUpdate(float tpf) {
        float ang=OMEGA*tpf;
        float d=VELOCITY*tpf;
        s.set(0f, 0f, d);
        Spatial dino=getSpatial();
        
        dino.rotate(0f, ang, 0f);
        
        Quaternion localRotation=dino.getLocalRotation();
        localRotation.mult(s, w);
        
        dino.move(w);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    
}
