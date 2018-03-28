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
package org.microemu.app.util;

import java.io.OutputStream;
import java.io.PrintStream;

import org.microemu.log.Logger;

/**
 * @author vlads
 *
 * This class allow redirection of stdout and stderr from MIDlet to
 * MicroEmulator logger console
 *
 */
public class MIDletOutputStreamRedirector extends PrintStream {

	private final static boolean keepMultiLinePrint = true;

	public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	private boolean isErrorStream;

	static {
		Logger.addLogOrigin(MIDletOutputStreamRedirector.class);
		Logger.addLogOrigin(OutputStream2Log.class);
	}

	private static class OutputStream2Log extends OutputStream {

		boolean isErrorStream;

		StringBuffer buffer = new StringBuffer();

		OutputStream2Log(boolean error) {
			this.isErrorStream = error;
		}

		@Override
		public void flush() {
			if (buffer.length() > 0) {
				write('\n');
			}
		}

		@Override
		public void write(int b) {
			if ((b == '\n') || (b == '\r')) {
				if (buffer.length() > 0) {
					if (isErrorStream) {
						Logger.error(buffer.toString());
					} else {
						Logger.info(buffer.toString());
					}
					buffer = new StringBuffer();
				}
			} else {
				buffer.append((char) b);
			}
		}

	}

	private MIDletOutputStreamRedirector(boolean error) {
		super(new OutputStream2Log(error));

		this.isErrorStream = error;
	}

	private static PrintStream outPrintStream() {
		return new MIDletOutputStreamRedirector(false);
	}

	private static PrintStream errPrintStream() {
		return new MIDletOutputStreamRedirector(true);
	}

	// Override methods to be able to get proper stack trace

	@Override
	public void print(boolean b) {
		super.print(b);
	}

	@Override
	public void print(char c) {
		super.print(c);
	}

	@Override
	public void print(char[] s) {
		super.print(s);
	}

	@Override
	public void print(double d) {
		super.print(d);
	}

	@Override
	public void print(float f) {
		super.print(f);
	}

	@Override
	public void print(int i) {
		super.print(i);
	}

	@Override
	public void print(long l) {
		super.print(l);
	}

	@Override
	public void print(Object obj) {
		super.print(obj);
	}

	@Override
	public void print(String s) {
		super.print(s);
	}

	@Override
	public void println() {
		super.println();
	}

	@Override
	public void println(boolean x) {
		super.println(x);
	}

	@Override
	public void println(char x) {
		super.println(x);
	}

	@Override
	public void println(char[] x) {
		super.println(x);
	}

	@Override
	public void println(double x) {
		super.println(x);
	}

	@Override
	public void println(float x) {
		super.println(x);
	}

	@Override
	public void println(int x) {
		super.println(x);
	}

	@Override
	public void println(long x) {
		super.println(x);
	}

	@Override
	public void println(Object x) {
		if (keepMultiLinePrint) {
			super.flush();
			if (isErrorStream) {
				Logger.error(x);
			} else {
				Logger.info(x);
			}
		} else {
			super.println(x);
		}
	}

	@Override
	public void println(String x) {
		if (keepMultiLinePrint) {
			super.flush();
			if (isErrorStream) {
				Logger.error(x);
			} else {
				Logger.info(x);
			}
		} else {
			super.println(x);
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
	}

	@Override
	public void write(int b) {
		super.write(b);
	}

}
