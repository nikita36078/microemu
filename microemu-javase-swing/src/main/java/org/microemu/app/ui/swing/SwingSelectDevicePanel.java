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

package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.app.ui.Message;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.EmulatorContext;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.j2se.J2SEDevice;

public class SwingSelectDevicePanel extends SwingDialogPanel {
	private static final long serialVersionUID = 1L;

	private EmulatorContext emulatorContext;

	private JScrollPane spDevices;

	private JButton btAdd;

	private JButton btRemove;

	private JButton btDefault;

	private DefaultListModel lsDevicesModel;

	private JList lsDevices;

	private ActionListener btAddListener = new ActionListener() {
		private JFileChooser fileChooser = null;

		public void actionPerformed(ActionEvent ev) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser();
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("Device profile (*.jar, *.zip)");
				fileFilter.addExtension("jar");
				fileFilter.addExtension("zip");
				fileChooser.setFileFilter(fileFilter);
			}

			if (fileChooser.showOpenDialog(SwingSelectDevicePanel.this) == JFileChooser.APPROVE_OPTION) {
				String manifestDeviceName = null;
				URL[] urls = new URL[1];
				ArrayList descriptorEntries = new ArrayList();
				JarFile jar = null;
				try {
					jar = new JarFile(fileChooser.getSelectedFile());

					Manifest manifest = jar.getManifest();
					if (manifest != null) {
						Attributes attrs = manifest.getMainAttributes();
						manifestDeviceName = attrs.getValue("Device-Name");
					}

					for (Enumeration en = jar.entries(); en.hasMoreElements();) {
						String entry = ((JarEntry) en.nextElement()).getName();
						if ((entry.toLowerCase().endsWith(".xml") || entry.toLowerCase().endsWith("device.txt"))
								&& !entry.toLowerCase().startsWith("meta-inf")) {
							descriptorEntries.add(entry);
						}
					}
					urls[0] = fileChooser.getSelectedFile().toURL();
				} catch (IOException e) {
					Message.error("Error reading file: " + fileChooser.getSelectedFile().getName() + ", "
							+ Message.getCauseMessage(e), e);
					return;
				} finally {
					if (jar != null) {
						try {
							jar.close();
						} catch (IOException ignore) {
						}
					}
				}

				if (descriptorEntries.size() == 0) {
					Message.error("Cannot find any device profile in file: " + fileChooser.getSelectedFile().getName());
					return;
				}

				if (descriptorEntries.size() > 1) {
					manifestDeviceName = null;
				}

				ClassLoader classLoader = Common.createExtensionsClassLoader(urls);
				HashMap devices = new HashMap();
				for (Iterator it = descriptorEntries.iterator(); it.hasNext();) {
					String entryName = (String) it.next();
					try {
						devices.put(entryName, DeviceImpl.create(emulatorContext, classLoader, entryName,
								J2SEDevice.class));
					} catch (IOException e) {
						Message.error("Error parsing device profile, " + Message.getCauseMessage(e), e);
						return;
					}
				}

				for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements();) {
					DeviceEntry entry = (DeviceEntry) en.nextElement();
					if (devices.containsKey(entry.getDescriptorLocation())) {
						devices.remove(entry.getDescriptorLocation());
					}
				}
				if (devices.size() == 0) {
					Message.info("Device profile already added");
					return;
				}

				try {
					File deviceFile = new File(Config.getConfigPath(), fileChooser.getSelectedFile().getName());
					if (deviceFile.exists()) {
						deviceFile = File.createTempFile("device", ".jar", Config.getConfigPath());
					}
					IOUtils.copyFile(fileChooser.getSelectedFile(), deviceFile);

					DeviceEntry entry = null;
					for (Iterator it = devices.keySet().iterator(); it.hasNext();) {
						String descriptorLocation = (String) it.next();
						Device device = (Device) devices.get(descriptorLocation);
						if (manifestDeviceName != null) {
							entry = new DeviceEntry(manifestDeviceName, deviceFile.getName(), descriptorLocation, false);
						} else {
							entry = new DeviceEntry(device.getName(), deviceFile.getName(), descriptorLocation, false);
						}
						lsDevicesModel.addElement(entry);
						Config.addDeviceEntry(entry);
					}
					lsDevices.setSelectedValue(entry, true);
				} catch (IOException e) {
					Message.error("Error adding device profile, " + Message.getCauseMessage(e), e);
					return;
				}
			}
		}
	};

	private ActionListener btRemoveListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();

			boolean canDeleteFile = true;
			for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements();) {
				DeviceEntry test = (DeviceEntry) en.nextElement();
				if (test != entry && test.getFileName() != null && test.getFileName().equals(entry.getFileName())) {
					canDeleteFile = false;
					break;
				}
			}
			if (canDeleteFile) {
				File deviceFile = new File(Config.getConfigPath(), entry.getFileName());
				deviceFile.delete();
			}

			if (entry.isDefaultDevice()) {
				for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements();) {
					DeviceEntry tmp = (DeviceEntry) en.nextElement();
					if (!tmp.canRemove()) {
						tmp.setDefaultDevice(true);
						break;
					}
				}
			}
			lsDevicesModel.removeElement(entry);
			Config.removeDeviceEntry(entry);
		}
	};

	private ActionListener btDefaultListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
			for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements();) {
				DeviceEntry tmp = (DeviceEntry) en.nextElement();
				if (tmp == entry) {
					tmp.setDefaultDevice(true);
				} else {
					tmp.setDefaultDevice(false);
				}
				Config.changeDeviceEntry(tmp);
			}
			lsDevices.repaint();
			btDefault.setEnabled(false);
		}
	};

	ListSelectionListener listSelectionListener = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent ev) {
			DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
			if (entry != null) {
				if (entry.isDefaultDevice()) {
					btDefault.setEnabled(false);
				} else {
					btDefault.setEnabled(true);
				}
				if (entry.canRemove()) {
					btRemove.setEnabled(true);
				} else {
					btRemove.setEnabled(false);
				}
				btOk.setEnabled(true);
			} else {
				btDefault.setEnabled(false);
				btRemove.setEnabled(false);
				btOk.setEnabled(false);
			}
		}
	};

	public SwingSelectDevicePanel(EmulatorContext emulatorContext) {
		this.emulatorContext = emulatorContext;

		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(), "Installed devices"));

		lsDevicesModel = new DefaultListModel();
		lsDevices = new JList(lsDevicesModel);
		lsDevices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsDevices.addListSelectionListener(listSelectionListener);
		spDevices = new JScrollPane(lsDevices);
		add(spDevices, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		btAdd = new JButton("Add...");
		btAdd.addActionListener(btAddListener);
		btRemove = new JButton("Remove");
		btRemove.addActionListener(btRemoveListener);
		btDefault = new JButton("Set as default");
		btDefault.addActionListener(btDefaultListener);
		panel.add(btAdd);
		panel.add(btRemove);
		panel.add(btDefault);

		add(panel, BorderLayout.SOUTH);

		for (Enumeration e = Config.getDeviceEntries().elements(); e.hasMoreElements();) {
			DeviceEntry entry = (DeviceEntry) e.nextElement();
			lsDevicesModel.addElement(entry);
			if (entry.isDefaultDevice()) {
				lsDevices.setSelectedValue(entry, true);
			}
		}
	}

	public DeviceEntry getSelectedDeviceEntry() {
		return (DeviceEntry) lsDevices.getSelectedValue();
	}

}
