/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hello;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author teodoroadinolfi
 */
class VictoryManager {

    private final Node guiNode;
    private final AssetManager assetManager;
    private final Camera cam;
    
    public VictoryManager(Node guiNode, AssetManager assetManager, Camera cam){
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        this.cam = cam;
    }
    
   public void victory(String victoryMessage){
    BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText hintText = new BitmapText(guiFont, false);
    hintText.setSize(20f);
    hintText.setText(victoryMessage);
    hintText.setColor(ColorRGBA.White);
    float screenWidth = cam.getWidth();
    float screenHeight = cam.getHeight();
    float textWidth = hintText.getLineWidth();
    float textHeight = hintText.getLineHeight();
    float posX = (screenWidth - textWidth) * 0.5f;
    float posY = (screenHeight - textHeight) * 0.5f;
    hintText.setLocalTranslation(posX, posY, 0);
    
    guiNode.attachChild(hintText);
}

    
}
