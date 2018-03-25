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

package org.microemu.applet;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

//import netscape.javascript.JSObject;

import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.log.Logger;
import org.microemu.util.Base64Coder;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

// TODO add JSObject import again
public class CookieRecordStoreManager implements RecordStoreManager {

	private static final int MAX_SPLIT_COOKIES = 5; // max 10

	private static final int MAX_COOKIE_SIZE = 4096 * 3 / 4; // Base64

	private ExtendedRecordListener recordListener = null;

	private Applet applet;

//	private JSObject document;

	private HashMap cookies;

	private String expires;

	public CookieRecordStoreManager(Applet applet) {
		this.applet = applet;

		Calendar c = Calendar.getInstance();
		c.add(java.util.Calendar.YEAR, 1);
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MM-yyyy hh:mm:ss z");
		this.expires = "; Max-Age=" + (60 * 60 * 24 * 365);
		System.out.println("CookieRecordStoreManager: " + this.expires);
	}

	public void init(MicroEmulator emulator) {
	}

	public String getName() {
		return this.getClass().toString();
	}

	public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
		CookieContent cookieContent = (CookieContent) cookies.get(recordStoreName);
		if (cookieContent == null) {
			throw new RecordStoreNotFoundException(recordStoreName);
		}

		removeCookie(recordStoreName, cookieContent);
		cookies.remove(recordStoreName);

		fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_DELETE, recordStoreName);

		System.out.println("deleteRecordStore: " + recordStoreName);
	}

	public void deleteStores() {
		for (Iterator it = cookies.keySet().iterator(); it.hasNext();) {
			try {
				deleteRecordStore((String) it.next());
			} catch (RecordStoreException ex) {
				Logger.error(ex);
			}
		}
		System.out.println("deleteStores:");
	}

	public void init() {
/*		JSObject window = (JSObject) JSObject.getWindow(applet);
		document = (JSObject) window.getMember("document");
		cookies = new HashMap();

		String load = (String) document.getMember("cookie");
		if (load != null) {
			StringTokenizer st = new StringTokenizer(load, ";");
			while (st.hasMoreTokens()) {
				String token = st.nextToken().trim();
				int index = token.indexOf("=");
				if (index != -1) {
					if (token.charAt(index + 1) == 'a') {
						String first = token.substring(0, 1);
						String name = token.substring(1, index).trim();
						CookieContent content = (CookieContent) cookies.get(name);
						if (content == null) {
							content = new CookieContent();
							cookies.put(name, content);
						}
						if (first.equals("x")) {
							content.setPart(0, token.substring(index + 2));
						} else {
							try {
								content.setPart(Integer.parseInt(first), token.substring(index + 2));
							} catch (NumberFormatException ex) {
							}
						}
						System.out.println("init: " + token.substring(0, index) + "(" + token.substring(index + 2)
								+ ")");
					}
				}
			}
		}
		System.out.println("init: " + cookies.size());*/
	}

	public String[] listRecordStores() {
		System.out.println("listRecordStores:");
		String[] result = (String[]) cookies.keySet().toArray();

		if (result.length == 0) {
			result = null;
		}

		return result;
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
			throws RecordStoreNotFoundException {
		RecordStoreImpl result;

		CookieContent load = (CookieContent) cookies.get(recordStoreName);
		if (load != null) {
			try {
				byte[] data = Base64Coder.decode(load.toCharArray());
				result = new RecordStoreImpl(this);
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
				int size = result.readHeader(dis);
				for (int i = 0; i < size; i++) {
					result.readRecord(dis);
				}
				dis.close();
			} catch (IOException ex) {
				Logger.error(ex);
				throw new RecordStoreNotFoundException(ex.getMessage());
			}
			System.out.println("openRecordStore: " + recordStoreName + " (" + load.getParts().length + ")");
		} else {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException(recordStoreName);
			}
			result = new RecordStoreImpl(this, recordStoreName);
			System.out.println("openRecordStore: " + recordStoreName + " (" + load + ")");
		}
		result.setOpen(true);
		if (recordListener != null) {
			result.addRecordListener(recordListener);
		}

		fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_OPEN, recordStoreName);

		return result;
	}
	
	public void deleteRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreNotOpenException, RecordStoreException {
		saveRecord(recordStoreImpl, recordId);
	}
	
	public void loadRecord(RecordStoreImpl recordStoreImpl, int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException 
	{
		// records are loaded when record store opens
	}

	public void saveRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreException {
/*		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			recordStoreImpl.writeHeader(dos);
			RecordEnumeration re = recordStoreImpl.enumerateRecords(null, null, false);
			while (re.hasNextElement()) {
				recordStoreImpl.writeRecord(dos, re.nextRecordId());
			}
			CookieContent cookieContent = new CookieContent(Base64Coder.encode(baos.toByteArray()));

			CookieContent previousCookie = (CookieContent) cookies.get(recordStoreImpl.getName());
			if (previousCookie != null) {
				removeCookie(recordStoreImpl.getName(), previousCookie);
			}

			cookies.put(recordStoreImpl.getName(), cookieContent);

			String[] parts = cookieContent.getParts();
			if (parts.length == 1) {
				document.setMember("cookie", "x" + recordStoreImpl.getName() + "=a" + parts[0] + expires);
			} else {
				for (int i = 0; i < parts.length; i++) {
					document.setMember("cookie", i + recordStoreImpl.getName() + "=a" + parts[i] + expires);
				}
			}

			System.out.println("saveChanges: " + recordStoreImpl.getName() + " (" + cookieContent.getParts().length
					+ ")");
		} catch (IOException ex) {
			Logger.error(ex);
		}*/
	}

	public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
		int size = MAX_COOKIE_SIZE * MAX_SPLIT_COOKIES;

		size -= recordStoreImpl.getHeaderSize();
		try {
			RecordEnumeration en = recordStoreImpl.enumerateRecords(null, null, false);
			while (en.hasNextElement()) {
				size -= en.nextRecord().length + recordStoreImpl.getRecordHeaderSize();
			}
		} catch (RecordStoreException ex) {
			Logger.error(ex);
		}

		// TODO Auto-generated method stub
		System.out.println("getSizeAvailable: " + size);
		return size;
	}

	private void removeCookie(String recordStoreName, CookieContent cookieContent) {
/*		String[] parts = cookieContent.getParts();
		if (parts.length == 1) {
			document.setMember("cookie", "x" + recordStoreName + "=r");
		} else {
			for (int i = 0; i < parts.length; i++) {
				document.setMember("cookie", i + recordStoreName + "=r");
			}
		}
		System.out.println("removeCookie: " + recordStoreName);*/
	}

	private class CookieContent {
		private String[] parts;

		public CookieContent() {
		}

		public CookieContent(char[] buffer) {
			parts = new String[buffer.length / MAX_COOKIE_SIZE + 1];
			System.out.println("CookieContent(before): " + parts.length);
			int index = 0;
			for (int i = 0; i < parts.length; i++) {
				int size = MAX_COOKIE_SIZE;
				if (index + size > buffer.length) {
					size = buffer.length - index;
				}
				System.out.println("CookieContent: " + i + "," + index + "," + size);
				parts[i] = new String(buffer, index, size);
				index += size;
			}
		}

		public void setPart(int index, String content) {
			if (parts == null) {
				parts = new String[index + 1];
			} else {
				if (parts.length <= index) {
					String[] newParts = new String[index + 1];
					System.arraycopy(parts, 0, newParts, 0, parts.length);
					parts = newParts;
				}
			}
			System.out.println("setPart: " + index + "," + parts.length);

			parts[index] = content;
		}

		public String[] getParts() {
			System.out.println("getParts: " + parts);
			return parts;
		}

		public char[] toCharArray() {
			int size = 0;
			for (int i = 0; i < parts.length; i++) {
				size += parts[i].length();
			}

			char[] result = new char[size];

			int index = 0;
			for (int i = 0; i < parts.length; i++) {
				System.out.println("toCharArray: " + i + "," + index + "," + size + "," + parts[i].length());
				System.arraycopy(parts[i].toCharArray(), 0, result, index, parts[i].length());
				index += parts[i].length();
			}

			return result;
		}
	}

	public void setRecordListener(ExtendedRecordListener recordListener) {
		this.recordListener = recordListener;
	}

	public void fireRecordStoreListener(int type, String recordStoreName) {
		if (recordListener != null) {
			recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
		}
	}
}
