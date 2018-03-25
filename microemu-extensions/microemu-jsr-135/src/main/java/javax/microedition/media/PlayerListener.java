/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.microedition.media;

public interface PlayerListener
{

	public static final String STARTED = "started";
	
	public static final String STOPPED = "stopped";
	
	public static final String END_OF_MEDIA = "endOfMedia";
	
	public static final String DURATION_UPDATED = "durationUpdated";
	
	public static final String DEVICE_UNAVAILABLE = "deviceUnavailable";
	
	public static final String DEVICE_AVAILABLE = "deviceAvailable";
	
	public static final String VOLUME_CHANGED = "volumeChanged";
	
	public static final String ERROR = "error";
	
	public static final String CLOSED = "closed";
	
	public void playerUpdate(Player player, String event, Object eventData);
	
}
