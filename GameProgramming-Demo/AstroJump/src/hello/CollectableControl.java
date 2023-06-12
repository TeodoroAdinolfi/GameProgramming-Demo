/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.bounding.BoundingSphere;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author teodoroadinolfi
 */
public class CollectableControl extends AbstractControl {
    
    private Spatial collectable;
    private BoundingSphere sphere;
    private final VictoryManager victory;

    public CollectableControl(Spatial collectable,VictoryManager victory) {
        this.collectable = collectable;
        this.victory = victory;
        sphere = new BoundingSphere();
        sphere.setCenter(collectable.getLocalTranslation());
        sphere.setRadius(2f);
    }

     
    @Override
    protected void controlUpdate(float tpf) {
        // Controlla la collisione con il personaggio
        if (sphere.intersects(getSpatial().getWorldBound())){
            removeCollectable();
        }
    }

    private void removeCollectable() {
        if (spatial != null) {
            // Rimuovi l'oggetto raccoglibile dallo scenario
            collectable.removeFromParent();
            victory.victory("Complimenti, hai vinto!!");
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

}
