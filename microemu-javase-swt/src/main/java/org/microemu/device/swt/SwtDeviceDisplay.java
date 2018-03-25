/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.swt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.microemu.DisplayAccess;
import org.microemu.device.EmulatorContext;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.swt.ImageFilter;
import org.microemu.app.ui.swt.SwtDeviceComponent;
import org.microemu.app.ui.swt.SwtGraphics;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.MutableImage;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.PositionedImage;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;
import org.microemu.device.impl.SoftButton;

public class SwtDeviceDisplay implements DeviceDisplayImpl {
	EmulatorContext context;

	Rectangle displayRectangle;

	Rectangle displayPaintable;

	boolean isColor;

	int numColors;

	int numAlphaLevels;

	Color backgroundColor;

	Color foregroundColor;

	PositionedImage mode123Image;

	PositionedImage modeAbcUpperImage;

	PositionedImage modeAbcLowerImage;

	public SwtDeviceDisplay(EmulatorContext context) {
		this.context = context;
	}

	public MutableImage getDisplayImage() {
		return ((SwtDeviceDisplay) context.getDisplayComponent()).getDisplayImage();
	}

	public int getHeight() {
		return displayPaintable.height;
	}

	public int getWidth() {
		return displayPaintable.width;
	}

	public int getFullHeight() {
		return displayRectangle.height;
	}

	public int getFullWidth() {
		return displayRectangle.width;
	}

	public boolean isColor() {
		return isColor;
	}

	public boolean isFullScreenMode() {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		} else {
			DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return false;
			} else {
				return da.isFullScreenMode();
			}
		}
	}

	public int numAlphaLevels() {
		return numAlphaLevels;
	}

	public int numColors() {
		return numColors;
	}

	public void paintControls(SwtGraphics g) {
		Device device = DeviceFactory.getDevice();

		g.setBackground(g.getColor(new RGB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor
				.getBlue())));
		g.fillRectangle(0, 0, displayRectangle.width, displayPaintable.y);
		g.fillRectangle(0, displayPaintable.y, displayPaintable.x, displayPaintable.height);
		g.fillRectangle(displayPaintable.x + displayPaintable.width, displayPaintable.y, displayRectangle.width
				- displayPaintable.x - displayPaintable.width, displayPaintable.height);
		g.fillRectangle(0, displayPaintable.y + displayPaintable.height, displayRectangle.width,
				displayRectangle.height - displayPaintable.y - displayPaintable.height);

		g.setForeground(g.getColor(new RGB(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor
				.getBlue())));
		for (Enumeration s = device.getSoftButtons().elements(); s.hasMoreElements();) {
			((SwtSoftButton) s.nextElement()).paint(g);
		}

		int inputMode = device.getInputMethod().getInputMode();
		if (inputMode == InputMethod.INPUT_123) {
			g.drawImage(((SwtImmutableImage) mode123Image.getImage()).getImage(), mode123Image.getRectangle().x,
					mode123Image.getRectangle().y);
		} else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
			g.drawImage(((SwtImmutableImage) modeAbcUpperImage.getImage()).getImage(),
					modeAbcUpperImage.getRectangle().x, modeAbcUpperImage.getRectangle().y);
		} else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
			g.drawImage(((SwtImmutableImage) modeAbcLowerImage.getImage()).getImage(),
					modeAbcLowerImage.getRectangle().x, modeAbcLowerImage.getRectangle().y);
		}
	}

	public void paintDisplayable(SwtGraphics g, int x, int y, int width, int height) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		Displayable current = da.getCurrent();
		if (current == null) {
			return;
		}

		g.setForeground(g.getColor(new RGB(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor
				.getBlue())));

		org.eclipse.swt.graphics.Rectangle oldclip = g.getClipping();
		if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
				|| ((Canvas) current).getHeight() != displayRectangle.height) {
			g.translate(displayPaintable.x, displayPaintable.y);
		}
		g.setClipping(new org.eclipse.swt.graphics.Rectangle(x, y, width, height));
		Font oldf = g.getFont();
		ma.getDisplayAccess().paint(new SwtDisplayGraphics(g, getDisplayImage()));
		g.setFont(oldf);
		if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
				|| ((Canvas) current).getHeight() != displayRectangle.height) {
			g.translate(-displayPaintable.x, -displayPaintable.y);
		}
		g.setClipping(oldclip);
	}

	public void repaint(int x, int y, int width, int height) {
		context.getDisplayComponent().repaintRequest(x, y, width, height);
	}

	public void setScrollDown(boolean state) {
		Enumeration en = DeviceFactory.getDevice().getSoftButtons().elements();
		while (en.hasMoreElements()) {
			SoftButton button = (SoftButton) en.nextElement();
			if (button.getType() == SoftButton.TYPE_ICON && button.getName().equals("down")) {
				button.setVisible(state);
			}
		}
	}

	public void setScrollUp(boolean state) {
		Enumeration en = DeviceFactory.getDevice().getSoftButtons().elements();
		while (en.hasMoreElements()) {
			SoftButton button = (SoftButton) en.nextElement();
			if (button.getType() == SoftButton.TYPE_ICON && button.getName().equals("up")) {
				button.setVisible(state);
			}
		}
	}

	public Rectangle getDisplayRectangle() {
		return displayRectangle;
	}

	public Rectangle getDisplayPaintable() {
		return displayPaintable;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public Image createImage(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		return new SwtMutableImage(width, height);
	}

	public Image createImage(String name) throws IOException {
		return getImage(name);
	}

	public Image createImage(javax.microedition.lcdui.Image source) {
		if (source.isMutable()) {
			return new SwtImmutableImage((SwtMutableImage) source);
		} else {
			return source;
		}
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) {
		ByteArrayInputStream is = new ByteArrayInputStream(imageData, imageOffset, imageLength);
		try {
			return getImage(is);
		} catch (IOException ex) {
			throw new IllegalArgumentException(ex.toString());
		}
	}

	public Image createImage(InputStream is) throws IOException {
		if (is == null) {
			throw new IOException();
		}

		return getImage(is);
	}

	public void setNumAlphaLevels(int i) {
		numAlphaLevels = i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setNumColors(int)
	 */
	public void setNumColors(int i) {
		numColors = i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setIsColor(boolean)
	 */
	public void setIsColor(boolean b) {
		isColor = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setBackgroundColor(java.awt.Color)
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setForegroundColor(java.awt.Color)
	 */
	public void setForegroundColor(Color color) {
		foregroundColor = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setDisplayRectangle(java.awt.Rectangle)
	 */
	public void setDisplayRectangle(Rectangle rectangle) {
		displayRectangle = rectangle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setDisplayPaintable(java.awt.Rectangle)
	 */
	public void setDisplayPaintable(Rectangle rectangle) {
		displayPaintable = rectangle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setMode123Image(com.barteo.emulator.device.impl.PositionedImage)
	 */
	public void setMode123Image(PositionedImage object) {
		mode123Image = object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setModeAbcLowerImage(com.barteo.emulator.device.impl.PositionedImage)
	 */
	public void setModeAbcLowerImage(PositionedImage object) {
		modeAbcLowerImage = object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setModeAbcUpperImage(com.barteo.emulator.device.impl.PositionedImage)
	 */
	public void setModeAbcUpperImage(PositionedImage object) {
		modeAbcUpperImage = object;
	}

	public Image createSystemImage(URL url) throws IOException {
		return new SwtImmutableImage(SwtDeviceComponent.createImage(url.openStream()));
	}

	private Image getImage(String str) throws IOException {
		// TODO not always true, there could be some loading images before
		// invoke startApp, right now getCurrentMIDlet returns prevoius MIDlet
		Object midlet = MIDletBridge.getCurrentMIDlet();
		if (midlet == null) {
			midlet = getClass();
		}
		InputStream is = midlet.getClass().getResourceAsStream(str);

		if (is == null) {
			throw new IOException(str + " could not be found.");
		}
		try {
			return getImage(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private Image getImage(InputStream is) throws IOException {
		ImageFilter filter = null;
		if (isColor()) {
			filter = new RGBImageFilter();
		} else {
			if (numColors() == 2) {
				filter = new BWImageFilter();
			} else {
				filter = new GrayImageFilter();
			}
		}

		return new SwtImmutableImage(SwtDeviceComponent.createImage(is, filter));
	}

	public Button createButton(int skinVersion, String name, Shape shape, int keyCode, String keyboardKeys,
			String keyboardChars, Hashtable inputToChars, boolean modeChange) {
		return new SwtButton(name, shape, keyCode, keyboardKeys, inputToChars);
	}

	public SoftButton createSoftButton(int skinVersion, String name, Shape shape, int keyCode, String keyName,
			Rectangle paintable, String alignmentName, Vector commands, javax.microedition.lcdui.Font font) {
		return new SwtSoftButton(name, shape, keyCode, keyName, paintable, alignmentName, commands, font);
	}

	public SoftButton createSoftButton(int skinVersion, String name, Rectangle paintable, Image normalImage,
			Image pressedImage) {
		return new SwtSoftButton(name, paintable, normalImage, pressedImage);
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
		if (rgb == null)
			throw new NullPointerException();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();

		org.eclipse.swt.graphics.Image img = SwtDeviceComponent.createImage(width, height);
		ImageData imageData = img.getImageData();

		if (!processAlpha) {
			// we should eliminate the transparency info
			// but can't touch the original array
			// so we just create another
			int l = rgb.length;

			int[] rgbAux = new int[l];
			for (int i = 0; i < l; i++)
				rgbAux[i] = rgb[i] | 0xff000000;
			rgb = rgbAux;
		}

		for (int y = 0; y < height; y++) {
			imageData.setPixels(0, y, width, rgb, y * width);
		}

		// TODO now apply the corresponding filter
		ImageFilter filter = null;
		if (isColor()) {
			filter = new RGBImageFilter();
		} else {
			if (numColors() == 2) {
				filter = new BWImageFilter();
			} else {
				filter = new GrayImageFilter();
			}
		}

		return new SwtImmutableImage(SwtDeviceComponent.createImage(imageData));
	}

	public Image createImage(Image image, int x, int y, int width, int height, int transform) {
		if (image == null) {
			throw new NullPointerException();
		}
		if (x + width > image.getWidth() || y + height > image.getHeight() || width <= 0 || height <= 0 || x < 0
				|| y < 0) {
			throw new IllegalArgumentException("Area out of Image");
		}

		int[] rgbData = new int[height * width];
		int[] rgbTransformedData = new int[height * width];
		if (image instanceof SwtImmutableImage) {
			((SwtImmutableImage) image).getRGB(rgbData, 0, width, x, y, width, height);
		} else {
			((SwtMutableImage) image).getRGB(rgbData, 0, width, x, y, width, height);
		}

		int colIncr, rowIncr, offset;

		switch (transform) {
		case Sprite.TRANS_NONE: {
			offset = 0;
			colIncr = 1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT90: {
			offset = (height - 1) * width;
			colIncr = -width;
			rowIncr = (height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_ROT180: {
			offset = (height * width) - 1;
			colIncr = -1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT270: {
			offset = width - 1;
			colIncr = width;
			rowIncr = -(height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR: {
			offset = width - 1;
			colIncr = -1;
			rowIncr = width << 1;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT90: {
			offset = (height * width) - 1;
			colIncr = -width;
			rowIncr = (height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT180: {
			offset = (height - 1) * width;
			colIncr = 1;
			rowIncr = -(width << 1);
			break;
		}
		case Sprite.TRANS_MIRROR_ROT270: {
			offset = 0;
			colIncr = width;
			rowIncr = -(height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform");
		}

		// now the loops!
		for (int row = 0, i = 0; row < height; row++, offset += rowIncr) {
			for (int col = 0; col < width; col++, offset += colIncr, i++) {
				rgbTransformedData[i] = rgbData[offset];
			}
		}
		// to aid gc
		rgbData = null;
		image = null;

		return createRGBImage(rgbTransformedData, width, height, true);
	}

	public boolean isResizable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setResizable(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image createImage(int width, int height, boolean withAlpha,
			int fillColor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flushGraphics(GameCanvas gameCanvas, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Graphics getGraphics(GameCanvas gameCanvas) {
		// TODO Auto-generated method stub
		return null;
	}

}