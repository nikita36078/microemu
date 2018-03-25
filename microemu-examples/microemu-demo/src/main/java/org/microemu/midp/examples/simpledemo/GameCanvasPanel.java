/**
 *  MicroEmulator
 *  Copyright (C) 2007 Rushabh Doshi <radoshi@cs.stanford.edu> Pelago, Inc 
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
 *  @version $Id$
 */
package org.microemu.midp.examples.simpledemo;

import java.util.Random;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public final class GameCanvasPanel extends GameCanvas implements CommandListener, HasRunnable {

    private static final int POSNUMBER = 20;

    private static final Command neCommand = new Command("3 NE Move", Command.ITEM, 1);

    private static final Command nwCommand = new Command("1 NW Move", Command.ITEM, 2);

    private static final Command seCommand = new Command("9 SE Move", Command.ITEM, 3);

    private static final Command swCommand = new Command("7 SW Move", Command.ITEM, 4);

    private boolean cancel = false;

    private boolean moving = true;

    private int moveX = 2, moveY = 2;

    private int posX = 0, posY = 0;

    private int ballMoveX = 2, ballMoveY = -3;

    private int ballPosX = 15, ballPosY = 15;

    private int ballColor = 0x5691F0;

    private Random ballRandom = new Random();

    private Runnable timerTask = new Runnable() {

        public void run() {

            while (!cancel) {
                if (moving && isShown()) {
                    synchronized (this) {
                        if (moveX > 0) {
                            if (posX >= POSNUMBER) {
                                posX = 0;
                            }
                        } else {
                            if (posX < 0) {
                                posX = POSNUMBER;
                            }
                        }
                        if (moveY > 0) {
                            if (posY >= POSNUMBER) {
                                posY = 0;
                            }
                        } else {
                            if (posY < 0) {
                                posY = POSNUMBER;
                            }
                        }
                        posX += moveX;
                        posY += moveY;
                    }

                    paintScreen();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    };

    public GameCanvasPanel() {

        super(false);

        super.setTitle("Game Canvas Panel");

        addCommand(BaseExamplesForm.backCommand);
        addCommand(fullScreenModeCommand);
        setCommandListener(this);

        addCommand(neCommand);
        addCommand(nwCommand);
        addCommand(seCommand);
        addCommand(swCommand);
    }

    public void startRunnable() {

        cancel = false;
        Thread thread = new Thread(timerTask, "CanvasPanelThread");
        thread.start();
    }

    public void stopRunnable() {

        cancel = true;
    }

    public void commandAction(Command c, Displayable d) {

        if (d == this) {
            synchronized (this) {
                if (c == nwCommand) {
                    moveX = -1;
                    moveY = -1;
                    moving = true;
                } else if (c == neCommand) {
                    moveX = 1;
                    moveY = -1;
                    moving = true;
                } else if (c == swCommand) {
                    moveX = -1;
                    moveY = 1;
                    moving = true;
                } else if (c == seCommand) {
                    moveX = 1;
                    moveY = 1;
                    moving = true;
                }
            }
        }

        if (d == this) {
            if (c == BaseExamplesForm.backCommand) {
                SimpleDemoMIDlet.showMenu();
            } else if (c == fullScreenModeCommand) {
                setFullScreenMode(!fullScreenMode);
                repaint();
            }
        }
    }

    protected void keyPressed(int keyCode) {
        int actionCode = getGameAction(keyCode);
        if (keyCode == '1' /* nwCommand */) {
            moveX = -1;
            moveY = -1;
            moving = true;
        } else if (keyCode == '2') {
            moveX = 0;
            moveY = -1;
            moving = true;
        } else if (keyCode == '3' /* neCommand */) {
            moveX = 1;
            moveY = -1;
            moving = true;
        } else if (keyCode == '4') {
            moveX = -1;
            moveY = 0;
            moving = true;
        } else if (keyCode == '6') {
            moveX = 1;
            moveY = 0;
            moving = true;
        } else if (keyCode == '7' /* swCommand */) {
            moveX = -1;
            moveY = 1;
            moving = true;
        } else if (keyCode == '8') {
            moveX = 0;
            moveY = 1;
            moving = true;
        } else if (keyCode == '9' /* seCommand */) {
            moveX = 1;
            moveY = 1;
            moving = true;
        } else if (keyCode == '5' /* fullScreenModeCommand */) {
            setFullScreenMode(!fullScreenMode);
            repaint();
        } else if (keyCode == KEY_POUND) {
            moving = !moving;
        } else if (actionCode == UP) {
            if (ballMoveY > 0) {
                ballMoveY = -ballMoveY;
            }
        } else if (actionCode == DOWN) {
            if (ballMoveY < 0) {
                ballMoveY = -ballMoveY;
            }
        } else if (actionCode == LEFT) {
            if (ballMoveX > 0) {
                ballMoveX = -ballMoveX;
            }
        } else if (actionCode == RIGHT) {
            if (ballMoveX < 0) {
                ballMoveX = -ballMoveX;
            }
        } else if (keyCode == '0' /* backCommand */) {
            SimpleDemoMIDlet.showMenu();
        } else if (fullScreenMode) {
            setFullScreenMode(false);
        }
    }

    private void paintScreen() {

        Graphics g = getGraphics();

        int width = getWidth();
        int height = getHeight();

        g.setGrayScale(255);
        g.fillRect(0, 0, width, height);

        g.setColor(0x5691F0);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setGrayScale(0);
        g.drawRect(2, 2, width - 5, height - 5);

        int pos = posX;
        while (pos < width - 5) {
            g.drawLine(3 + pos, 3, 3 + pos, height - 4);
            pos += POSNUMBER;
        }
        pos = posY;
        while (pos < height - 5) {
            g.drawLine(3, 3 + pos, width - 4, 3 + pos);
            pos += POSNUMBER;
        }

        // Paint canvas info in the middle
        String text = width + " x " + height;

        Font f = g.getFont();
        int w = f.stringWidth(text) + 4;
        int h = 2 * f.getHeight() + 4;

        int arcWidth = w;
        int arcHeight = h;
        g.setColor(0xFFCC11);
        g.drawRoundRect((width - w) / 2, (height - h) / 2, w, h, arcWidth, arcHeight);
        g.setColor(0xFFEE99);
        g.fillRoundRect((width - w) / 2, (height - h) / 2, w, h, arcWidth, arcHeight);

        g.setColor(0xBB5500);
        g.drawString(text, width / 2, (height - f.getHeight()) / 2, Graphics.HCENTER | Graphics.TOP);

        // Pint Ball
        g.setColor(ballColor);
        g.fillRoundRect(ballPosX - 4, ballPosY - 4, 8, 8, 8, 8);

        ballPosX += ballMoveX;
        ballPosY += ballMoveY;

        boolean changeColor = false;
        if ((ballPosX < 4) || (ballPosX > width - 4)) {
            ballMoveX = -ballMoveX;
            changeColor = true;
        }
        if ((ballPosY < 4) || (ballPosY > height - 4)) {
            ballMoveY = -ballMoveY;
            changeColor = true;
        }
        if (changeColor) {
            ballColor = ballRandom.nextInt(0xFF) + (ballRandom.nextInt(0xFF) << 8) + (ballRandom.nextInt(0xFF) << 16);
        }

        flushGraphics();
    }

    protected boolean fullScreenMode = false;

    protected static final Command fullScreenModeCommand = new Command("Full Screen", Command.ITEM, 5);

    public int writeln(Graphics g, int line, String s) {

        int y = (g.getFont().getHeight() + 1) * line;
        g.drawString(s, 0, y, Graphics.LEFT | Graphics.TOP);
        return y;
    }

    public void setFullScreenMode(boolean mode) {

        fullScreenMode = mode;
        super.setFullScreenMode(mode);
    }
}
