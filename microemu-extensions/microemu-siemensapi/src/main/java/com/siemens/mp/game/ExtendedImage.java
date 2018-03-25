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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ExtendedImage extends com.siemens.mp.misc.NativeMem {
    Image image;
    
    public ExtendedImage(Image image) {
        this.image=image;
    }
    
    public void clear(byte color) {
        Graphics g=image.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,image.getWidth(),image.getHeight());
    }
    
    
    public Image getImage() {
        return image;
    }
    
    
    public int getPixel(int x, int y) {
        System.out.println("int getPixel(int x, int y)");
        return 1;
    }
    
    
    public void getPixelBytes(byte[] pixels, int x, int y, int width, int height) {
        System.out.println("void getPixelBytes(byte[] pixels, int x, int y, int width, int height)");
    }
    
    
    public void setPixel(int x, int y, byte color) {
        System.out.println("void setPixel(int x, int y, byte color)");
    }
    
    
    public void setPixels(byte[] pixels, int x, int y, int width, int height) {
        Image img=com.siemens.mp.ui.Image.createImageFromBitmap(pixels,width,height);
        image.getGraphics().drawImage(img, x,y, 0);
    }
}
