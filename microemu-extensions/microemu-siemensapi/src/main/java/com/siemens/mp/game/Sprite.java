/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
package com.siemens.mp.game;

import javax.microedition.lcdui.*;
/**
 *
 * @author  markus
 * @version
 */
public class Sprite extends GraphicObject{
    Image pixels[];
    Image mask[];
    int x;
    int y;
    int frame;
    int collx,colly,collw,collh;
    
    public Sprite(byte[] pixels, int pixel_offset, int width, int height, byte[] mask, int mask_offset, int numFrames) {
        this(
        com.siemens.mp.ui.Image.createImageFromBitmap(pixels,mask,width,height*numFrames),
        com.siemens.mp.ui.Image.createImageFromBitmap(mask,width,height*numFrames),
        numFrames
        );
    }
    
    public Sprite(ExtendedImage pixels, ExtendedImage mask, int numFrames) {
        this(pixels.getImage(),mask.getImage(),numFrames);
    }
    
    public Sprite(Image pixels, Image mask, int numFrames) {
        this.pixels=new Image[numFrames];
        
        for (int i=0;i<numFrames;i++) {
            Image img=Image.createImage(pixels.getWidth(), pixels.getHeight()/numFrames);
            
            img.getGraphics().drawImage(pixels, 0, -i*pixels.getHeight()/numFrames,0);
            this.pixels[i]=img;
         }
        
        if(mask!=null) {
            this.mask=new Image[numFrames];
            for (int i=0;i<numFrames;i++) {
                Image img=Image.createImage(mask.getWidth(), mask.getHeight()/numFrames);
                
                img.getGraphics().drawImage(mask, 0, -i*mask.getHeight()/numFrames,0);
                this.mask[i]=img;
            }
        }
        //this.pixels=pixels;
        //this.mask=mask;
        collx=0;
        colly=0;
        collw=this.pixels[0].getWidth();
        collh=this.pixels[0].getHeight();
    }
    
    public int getFrame() {
        //System.out.println("public int getFrame()");
        return frame;
    }
    
    public int getXPosition() {
        //System.out.println("public int getXPosition()");
        return x;
    }
    
    public int getYPosition() {
        //System.out.println("public int getYPosition()");
        return y;
    }
    
    public boolean isCollidingWith(Sprite other) {
        //System.out.println("public boolean isCollidingWith(Sprite other)");
        return false;
    }
    
    public boolean isCollidingWithPos(int xpos, int ypos) {
        //System.out.println("public boolean isCollidingWithPos(int xpos, int ypos)");
        return  (xpos>=x+collx)&&(xpos<x+collw)&&
                (ypos>=y+colly)&&(ypos<y+collh);
    }
    
    public void setCollisionRectangle(int x, int y, int width, int height) {
        //System.out.println("public void setCollisionRectangle(int x, int y, int width, int height)");
    collx=x;
    colly=y;
    collw=width;
    collh=height;
    }
    
    public void setFrame(int framenumber) {
        //System.out.println("public void setFrame(int framenumber)");
        frame=framenumber;
    }
    
    public void setPosition(int x, int y) {
        //System.out.println("public void setPosition(int x, int y)");
        this.x=x;
        this.y=y;
    }
    
    protected void paint(Graphics g) {
        //System.out.println(frame);
        g.drawImage(pixels[frame], x,y,0);
        //for(int i=0;i<pixels.length;i++) g.drawImage(pixels[i].getImage(), 20,y*pixels[i].getImage().getHeight(),0);
        //g.drawImage(mask.getImage(), x,y,0);
    }
}
