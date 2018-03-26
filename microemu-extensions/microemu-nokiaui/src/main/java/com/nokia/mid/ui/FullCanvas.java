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
 *
 *  Contributor(s):
 *    daniel(at)angrymachine.com.ar
 */

package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

/** The Softkey Codes are not generated by normal devices. In this class they are
 * emulated with commands. SOFTKEY1 is mapped to Command.OK and SOFTKEY2 to
 * Command.BACK. You should edit your device config file, so that OK is on the
 * left and Back on the right.
 */
public abstract class FullCanvas extends Canvas {
	public static final int KEY_SOFTKEY1 = -6;

	public static final int KEY_SOFTKEY2 = -7;

	public static final int KEY_SEND = -10;

	public static final int KEY_END = -11;

	public static final int KEY_SOFTKEY3 = -5;

	public static final int KEY_UP_ARROW = -1;

	public static final int KEY_DOWN_ARROW = -2;

	public static final int KEY_LEFT_ARROW = -3;

	public static final int KEY_RIGHT_ARROW = -4;

	/** Creates a new FullCanvas.
	 * Adds two empty commands to emulate softkey functions.
	 */
	protected FullCanvas() {
		super();
		super.setFullScreenMode(true);
		super.addCommand(new NokiaCommand(KEY_SOFTKEY1, Command.OK));
		super.addCommand(new NokiaCommand(KEY_SOFTKEY2, Command.BACK));
		super.setCommandListener(new NokiaCommandListener(this));
	}

	/** Commands are not supported by FullCanvas
	 * @param cmd
	 */
	public void addCommand(Command cmd) {
		throw new IllegalStateException();
	}

	public int getWidth() {
		return super.getWidth();
	}

	public int getHeight() {
		return super.getHeight();
	}

	/** Commands are not supported by FullCanvas
	 * @param l
	 */
	public void setCommandListener(CommandListener l) {
		throw new IllegalStateException();
	}

	//used to simulate softbutton key events
	void press(int i) {
		keyPressed(i);
		keyReleased(i);
	}

}

class NokiaCommandListener implements CommandListener {
	FullCanvas fc;

	NokiaCommandListener(FullCanvas f) {
		fc = f;
	}

	public void commandAction(Command c, Displayable d) {
		fc.press(((NokiaCommand) c).getKey());
		//this.keyReleased(((NokiaCommand)c).getKey());
	}

}

class NokiaCommand extends Command {
	int key;

	NokiaCommand(int key, int type) {
		super("", type, 1);
		this.key = key;
	}

	int getKey() {
		return key;
	}

}