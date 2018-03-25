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

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 *
 * @author  markus
 * @version
 */
public class GraphicObjectManager  extends com.siemens.mp.misc.NativeMem{
    Vector v=new Vector();
    
    public void addObject(GraphicObject gobject) {
        //System.out.println("void addObject(GraphicObject "+gobject+")");
        v.addElement(gobject);
    }
    
    public static byte[] createTextureBits(int width, int height, byte[] texture) {
        System.out.println("static byte[] createTextureBits(int width, int height, byte[] texture)");
        return null;
    }
    
    
    public void deleteObject(GraphicObject gobject) {
        //System.out.println("void deleteObject(GraphicObject gobject)");
        v.removeElement(gobject);
    }
    
    
    public void deleteObject(int position) {
        //System.out.println("void deleteObject(int position)");
        v.removeElementAt(position);
    }
    
    
    public GraphicObject getObjectAt(int index) {
        //System.out.println("GraphicObject getObjectAt(int index)");
        Object o=v.elementAt(index);
        return (GraphicObject)o;
    }
    
    
    public int getObjectPosition(GraphicObject gobject) {
        //System.out.println("int getObjectPosition(GraphicObject gobject)");
        return v.indexOf(gobject);
    }
    
    
    public void insertObject(GraphicObject gobject, int position) {
        //System.out.println("void insertObject(GraphicObject gobject, int position)");
        v.insertElementAt(gobject, position);
    }
    
    
    public void paint(ExtendedImage eimage, int x, int y) {
        this.paint(eimage.getImage(),x,y);
    }
    
    
    public void  paint(Image image, int x, int y) {
        Graphics g=image.getGraphics();
        g.translate(x, y);

        for (int i=0;i<v.size();i++) {
            GraphicObject go=(GraphicObject)v.elementAt(i);
            go.paint(g);
        }
        g.translate(-x, -y);
        
    }
    
}
