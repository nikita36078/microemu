/*
 *  PC Media MIDP Java Library
 *  Copyright (C) 2006 Travis Berthelot
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

package org.microemu.media.audio;

public class ToneInfo
{
   
   public ToneInfo()
   {
      this.setVolume(100);
   }
   
   private int sleepDelay;
   private int frequency;
   private int lengthOfTime;

   private int volume;

   public int getSleepDelay()
   {
      return sleepDelay;
   }

   public void setSleepDelay(int sleepDelay)
   {
      this.sleepDelay = sleepDelay;
   }

   public int getFrequency()
   {
      return frequency;
   }

   public void setFrequency(int frequency)
   {
      this.frequency = frequency;
   }

   public int getLengthOfTime()
   {
      return lengthOfTime;
   }

   public void setLengthOfTime(int lengthOfTime)
   {
      this.lengthOfTime = lengthOfTime;
   }
   
   public int getVolume()
   {
      return volume;
   }

   public void setVolume(int volume)
   {
      this.volume = volume;
   }

   public String toString()
   {
      return "Frequency: " + this.getFrequency() +
      " LengthOfTime: " + this.getLengthOfTime() +
      " SleepDelay: " + this.getSleepDelay() +
      " Volume: " + this.getVolume();
   }
}
