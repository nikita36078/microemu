/*
 *  Nokia API for MicroEmulator
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

package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** This class is a placeholder for utility methods. It contains methods for converting standard lcdui classes to Nokia UI classes and vice versa, and a method for creating images that are empty with pixels either transparent or colored, and creating mutable images from encoded image byte arrays. */
public class DirectUtils
{

	/** Converts standard javax.microedition.lcdui.Graphics to DirectGraphics. The returned object refers to the same graphics context. This means that calling draw operations or changing the state, for example, drawing color etc., via the original Graphics reference affect the DirectGraphics object, and vice versa.
	 *
	 * Note that even though the graphics context that the DirectGraphics and Graphics refer to is the same, the object reference returned from this method may or may not be equal compared to the Graphics reference passed to this method. This means that purely casting Graphics object (g) passed in paint method of lcdui Canvas to DirectGraphics may not work ok. The safest way is to always do the conversion with this method.
	 * @param g Graphics object for which DirectGraphics should be returned
	 * @return the DirectGraphics object based on Graphics
	 */    
	public static DirectGraphics getDirectGraphics(Graphics g)
	{
		return new DirectGraphicsImp(g);
	}

	/** Creates a mutable image that is decoded from the data stored in the specified byte array at the specified offset and length. The data must be in a self-identifying image file format supported by the implementation, e.g., PNG.
	 *
	 * Note that the semantics of this method are exactly the same as Image.createImage(byte[],int,int) except that the returned image is mutable.
	 * @param imageData the array of image data in a supported image format
	 * @param imageOffset the offset of the start of the data in the array
	 * @param imageLength the length of the data in the array
	 * @return the created mutable image
	 */    
	public static Image createImage(byte imageData[], int imageOffset, int imageLength)
	{
		Image source = Image.createImage(imageData, imageOffset, imageLength);
		Image target = Image.createImage(source.getWidth(), source.getHeight());
		target.getGraphics().drawImage(source,0,0,0);
		return target;
	}

	/** The method will return a newly created mutable Image with the specified dimension and all the pixels of the image defined by the specified ARGB color. The color can contain alpha channel transparency information.
	 * @param width the width of the new image, in pixels
	 * @param height the height of the new image, in pixels
	 * @param argb the initial color for image<br>Note: This is argb color, but alpha channel is currently
	 * not supported by this emulation.
	 * @return the created image
	 */    
	public static Image createImage(int width, int height, int argb)
	{
		Image img = Image.createImage(width, height);
		Graphics g = img.getGraphics();
		g.setColor(argb);
		g.fillRect(0,0, width,height);
		g.setColor(0);
		return img;
	}

}