/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.app.ui.swing;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.microemu.app.Config;
import org.microemu.app.ui.swing.logconsole.LogTextArea;
import org.microemu.app.util.RuntimeDetect;
import org.microemu.log.Logger;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;
import org.microemu.log.QueueAppender;
import org.microemu.log.StdOutAppender;

public class SwingLogConsoleDialog extends JFrame implements LoggerAppender {

	private static final long serialVersionUID = 1L;

	private static final boolean tests = false;

	private boolean isShown;

	private LogTextArea logArea;

	private Vector logLinesQueue = new Vector();

	private int testEventCounter = 0;

	private class SwingLogUpdater implements Runnable {

		private String getNextLine() {
			synchronized (logLinesQueue) {
				if (logLinesQueue.isEmpty()) {
					return null;
				}
				String line = (String) logLinesQueue.firstElement();
				logLinesQueue.removeElementAt(0);
				return line;
			}
		}

		public void run() {
			String line;
			while ((line = getNextLine()) != null) {
				logArea.append(line);
			}
		}
	}

	public SwingLogConsoleDialog(Frame owner, QueueAppender logQueueAppender) {
		super("Log console");

		setIconImage(owner.getIconImage());

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Log");

		JMenuItem menuClear = new JMenuItem("Clear");
		menuClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingLogConsoleDialog.this.logArea.setText("");
			}
		});
		menu.add(menuClear);

		menu.addSeparator();

		final JCheckBoxMenuItem menuRecordLocation = new JCheckBoxMenuItem("Show record location");
		menuRecordLocation.setState(Logger.isLocationEnabled());
		menuRecordLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logger.setLocationEnabled(menuRecordLocation.getState());
				Config.setLogConsoleLocationEnabled(menuRecordLocation.getState());
			}
		});
		menu.add(menuRecordLocation);

		final JCheckBoxMenuItem menuStdOut = new JCheckBoxMenuItem("Write to standard output");
		menuStdOut.setState(StdOutAppender.enabled);
		menuStdOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StdOutAppender.enabled = menuStdOut.getState();
			}
		});
		menu.add(menuStdOut);

		menuBar.add(menu);

		if (RuntimeDetect.isJava15()) {
			JMenu j5Menu = new JMenu("Threads");
			JMenuItem menuThreadDump = new JMenuItem("ThreadDump to console");
			menuThreadDump.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Logger.threadDumpToConsole();
				}
			});
			j5Menu.add(menuThreadDump);
			JMenuItem menuThreadDumpFile = new JMenuItem("ThreadDump to file");
			menuThreadDumpFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Logger.threadDumpToFile();
				}
			});
			j5Menu.add(menuThreadDumpFile);
			menuBar.add(j5Menu);
		}

		if (tests) {
			JMenu testMenu = new JMenu("Tests");
			JMenuItem testLog = new JMenuItem("Log 10 events");
			testLog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < 10; i++) {
						log(testEventCounter++ + " " + new Date() + "\n\t data tests.......\n");
					}
				}
			});
			testMenu.add(testLog);
			menuBar.add(testMenu);
		}

		setJMenuBar(menuBar);

		this.logArea = new LogTextArea(20, 40, 1000);
		Font logFont = new Font("Monospaced", Font.PLAIN, 12);
		this.logArea.setFont(logFont);

		JScrollPane scrollPane = new JScrollPane(this.logArea);
		scrollPane.setAutoscrolls(false);

		getContentPane().add(scrollPane);

		Logger.addAppender(this);
		Logger.removeAppender(logQueueAppender);

		LoggingEvent event = null;
		while ((event = logQueueAppender.poll()) != null) {
			append(event);
		}

	}

	public void setVisible(boolean b) {
		super.setVisible(b);
		isShown = true;
		if (isShown) {
			SwingUtilities.invokeLater(new SwingLogUpdater());
		}
	}

	private void log(String message) {
		boolean createUpdater = false;
		synchronized (logLinesQueue) {
			if (logLinesQueue.isEmpty()) {
				createUpdater = true;
			}
			logLinesQueue.addElement(message);
		}
		if (createUpdater && isShown) {
			SwingUtilities.invokeLater(new SwingLogUpdater());
		}
	}

	private String formatLocation(StackTraceElement ste) {
		if (ste == null) {
			return "";
		}
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()
				+ ")";
	}

	private String formatEventTime(long eventTime) {
		DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS ");
		return format.format(new Date(eventTime));
	}

	public void append(LoggingEvent event) {
		StringBuffer bug = new StringBuffer(formatEventTime(event.getEventTime()));
		if (event.getLevel() == LoggingEvent.ERROR) {
			bug.append("Error:");
		}
		bug.append(event.getMessage());
		if (event.hasData()) {
			bug.append(" [").append(event.getFormatedData()).append("]");
		}
		String location = formatLocation(event.getLocation());
		if (location.length() > 0) {
			bug.append("\n\t  ");
		}
		bug.append(location);
		if (event.getThrowable() != null) {
			OutputStream out = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(out);
			event.getThrowable().printStackTrace(stream);
			stream.flush();
			bug.append(out.toString());
		}
		bug.append("\n");
		log(bug.toString());
	}

}
