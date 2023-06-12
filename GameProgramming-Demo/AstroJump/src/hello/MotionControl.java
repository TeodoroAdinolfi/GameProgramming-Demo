/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.anim.AnimComposer;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author foggia
 */
public class MotionControl extends AbstractControl implements ActionListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private static final float OMEGA=2*FastMath.PI/10;
    private static final float VELOCITY=3.0f;
    private static final float ANIM_SPEED_RATIO=1f;
    private static final float DURATION = 3.5f;
    private static final float ACCELERATION=6.8f,
                               INITIAL_SPEED=0.5f*ACCELERATION*DURATION;
    private Vector3f s=new Vector3f();
    private Vector3f w=new Vector3f();
    private Vector3f jumpingVec = new Vector3f();
    private AnimComposer animComposer=null;
    private boolean stopForward=false, stopBackward=false;
    private boolean moveForward=false, moveBackward=false,
                    rotateLeft=false, rotateRight=false,
                    jump = false,     stillJumping=false;
    private String currentAction=null;
    
    private boolean falling = false;
    private boolean obstacleTouched = false;
    private boolean wave = false;
    private float fallingHeight = 0f;
    
    private float fallingMinHeight = 0f;
    
    private float startingJumpHeight = 0f;
    
    private float time=0;
    
    private final float offset = 0.5f;
    
    public boolean setObastacleTouched(boolean obstacleTouched){
        return this.obstacleTouched = obstacleTouched;
    }

    public void setFallingMinHeight(float fallingMinHeight) {
        this.fallingMinHeight = fallingMinHeight;
    }
    
    
    
    public MotionControl(InputManager inputManager) {
        inputManager.addListener(this, "Forward", 
                "Backward",
                "RotateLeft", "RotateRight","Jump","Wave");
    }
    
    public boolean getJump(){
        return jump;
    }
    
    public boolean getFalling(){
        return falling;
    }
    
    public void setFallingHeight(float fallingHeight){
        this.fallingHeight = fallingHeight;
    }
    
    public void setFalling(boolean falling){    
        this.falling = falling;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (moveForward && !stopForward) {
            wave = false;
            if (!jump){
                setAction("moon_walk", VELOCITY*ANIM_SPEED_RATIO);
            }
            forward(tpf);
        } else if (moveBackward && !stopBackward ) {
            if ( !jump){
                setAction("moon_walk", -0.5f*VELOCITY*ANIM_SPEED_RATIO);
            }
            forward(-0.5f*tpf);
        }  else if (!jump && !wave) {
            setAction("idle", 1.0f);
        }
        
        if(wave){
            this.setAction("wave", 1f);
        }

        if (rotateLeft) 
            rotate(tpf);
        else if (rotateRight)
            rotate(-tpf);

        if (jump)  
            makeJump(tpf);
        if (falling)
            player_falls(tpf);
        
        getSpatial().getControl(WalkingSurfaceCollisionControl.class).adjustHeight(jump);
        getSpatial().getControl(TerrainHeightControl.class).adjustHeight(jump,falling);
        
        
        
        
 
    }
    
    private void forward(float value) {
        float d=VELOCITY*value;
        s.set(0f, 0f, d);
        Spatial astro=getSpatial();
        Quaternion localRotation=astro.getLocalRotation();
        localRotation.mult(s, w);
        
        astro.move(w);    
    }
    
    private void setAction(String action, float speed) {
        if (action.equals(currentAction))
            return;
        currentAction=action;
        AnimComposer a=getAnimComposer();
        if (a!=null) {    
            a.setCurrentAction(action);
            a.setGlobalSpeed(speed);
        }
    }
    
    private void rotate(float value) {
        float ang=OMEGA*value;
        Spatial dino=getSpatial();
        dino.rotate(0f, ang, 0f);
        
    }

    @Override
    public void onAction(String input, boolean active, float tpf) {
        if (!isEnabled())
            return;
        if (input.equals("Forward")) 
            moveForward=active;
        else if (input.equals("Backward"))
            moveBackward=active;
        else if (input.equals("RotateLeft"))
            rotateLeft=active;
        else if (input.equals("RotateRight"))
            rotateRight=active;
        else if (input.equals("Jump")){
            jump= true;
            this.setAction("floating", 1f);
        } else if (input.equals("Wave")){
            wave = true;
        }
    }
    
    private AnimComposer getAnimComposer() {
        if (animComposer==null) {
            animComposer=findAnimComposer(getSpatial());
        }
        return animComposer;
    }

    private AnimComposer findAnimComposer(Spatial s) {
        AnimComposer composer = s.getControl(AnimComposer.class);
        if (composer != null) {
            return composer;
        }
        if (s instanceof Node) {
            Node node = (Node) s;
            for (Spatial child : node.getChildren()) {
                composer = findAnimComposer(child);
                if (composer != null) {
                    System.out.println(composer.getAnimClipsNames());
                    return composer;
                }
            }
        }
        return null;
    }
    
    public void setStop(boolean stopForward, boolean stopBackward) {
        this.stopForward=stopForward;
        this.stopBackward=stopBackward;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        moveForward=false;
        moveBackward=false;
        rotateLeft=false;
        rotateRight=false;
        jump=false;
        setAction("idle", 1.0f);
        
    }

    private void makeJump(float tpf) {
    Spatial astro = this.getSpatial();
    float terrainHeight = astro.getControl(TerrainHeightControl.class).getHeight();
    if(!stillJumping){
        this.startingJumpHeight = this.getSpatial().getLocalTranslation().y;
        stillJumping = true;
    }
    time += tpf;
    jumpingVec = astro.getLocalTranslation();
    jumpingVec.y = startingJumpHeight +  INITIAL_SPEED* time - 0.5f * ACCELERATION * time * time;
    if (jumpingVec.y < terrainHeight ) {
        jumpingVec.y = terrainHeight;
        time = 0;
        jump = false;
        stillJumping = false;
    } else if (obstacleTouched){      
        float obstacleHeight = astro.getControl(WalkingSurfaceCollisionControl.class).getMaxHeightFound() ;
        if (jumpingVec.y < obstacleHeight) {
            jumpingVec.y = obstacleHeight;
            time = 0;
            jump = false;
            stillJumping = false;
        }
    
    }   
    astro.setLocalTranslation(jumpingVec);
}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }

    private void player_falls(float tpf) {
    Spatial astro = this.getSpatial();
    
    float height = this.fallingMinHeight;
    
    time += tpf;
    jumpingVec = astro.getLocalTranslation();
    jumpingVec.y = fallingHeight - 0.2f * ACCELERATION * time * time;
    fallingHeight = jumpingVec.y ;
    if (jumpingVec.y < height) {
        jumpingVec.y = height;
        time = 0;
        falling = false;
    }
    astro.setLocalTranslation(jumpingVec);
    }
    
}
