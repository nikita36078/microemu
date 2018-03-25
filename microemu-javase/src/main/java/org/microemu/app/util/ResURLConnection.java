/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

public class ResURLConnection extends URLConnection {
	
	private static final String PREFIX = "res:";

	private Hashtable entries;
	
	protected ResURLConnection(URL url, Hashtable entries) {
		super(url);
		
		this.entries = entries;
	}

	public void connect() throws IOException {
	}

	public InputStream getInputStream() throws IOException {
		String location = url.toString();
		int idx = location.indexOf(PREFIX);
		if (idx == -1) {
			throw new IOException();
		}
		location = location.substring(idx + PREFIX.length());
		byte[] data = (byte[]) entries.get(location);
		if (data == null) {
			throw new IOException();
		}
		return new ByteArrayInputStream(data);
	}
	
}
