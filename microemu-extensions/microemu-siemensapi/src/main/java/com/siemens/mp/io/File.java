/*
 *  Siemens API for MicroEmulator
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
 */
package com.siemens.mp.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;

/**
 *
 * @author  markus
 * @version
 */
public class File {
    static Vector files=new Vector();
    static Hashtable data=new Hashtable();
    
    public int close(int fileDescriptor) {
        //System.out.println("public int close(int fileDescriptor)"+files.elementAt(fileDescriptor));
        files.setElementAt(null, fileDescriptor);
        return 1;
    }
    
    public static int copy(java.lang.String source, java.lang.String dest) {
        System.out.println("public static int copy(java.lang.String source, java.lang.String dest)");
        return 1;
    }
    
    public static int debugWrite(java.lang.String fileName, java.lang.String infoString) {
        System.out.println("public static int debugWrite(java.lang.String fileName, java.lang.String infoString)");
        return 1;
    }
    
    public static int delete(java.lang.String fileName) {
        //System.out.println("public static int delete(java.lang.String "+fileName+") ");
        data.remove(fileName);
        return 1;
    }
    
    public static int exists(java.lang.String fileName) {
        //System.out.println("public static int exists(java.lang.String "+fileName+") "+data.containsKey(fileName));
        if (data.containsKey(fileName.intern())||
        MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(fileName)!=null
        ) return 1; else return -1;
    }
    
    public static boolean isDirectory(java.lang.String pathName) {
        System.out.println("public static boolean isDirectory(java.lang.String pathName)");
        return false;
    }
    
    public int length(int fileDescriptor) {
        //System.out.println("public int length(int fileDescriptor)");
        return ((FileInfo)data.get(files.elementAt(fileDescriptor))).data.length;
        
    }
    
    public static java.lang.String[] list(java.lang.String pathName) {
        System.out.println(" public static java.lang.String[] list(java.lang.String pathName)");
        return null;
    }
    
    public int open(java.lang.String fileName){
        //System.out.println("public int open(java.lang.String "+fileName+")");
        
        
        files.addElement(fileName.intern());
        byte[] s=new byte[0];
        if (!data.containsKey(fileName)) {
            data.put(fileName.intern(), new FileInfo(s));
            try {
                InputStream is=MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(fileName);

                if(is!=null) {
                    BufferedInputStream bis=new BufferedInputStream(is);
                    
                    int l;
                    byte[] b=new byte[1024];
                    while((l=bis.read(b))!=-1) {
                        byte[] t=new byte[l+s.length];
                        System.arraycopy(s, 0, t, 0, s.length);
                        System.arraycopy(b, 0, t, s.length, l);
                        s=t;
                    }
                    data.put(fileName.intern(), new FileInfo(s));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return files.indexOf(fileName);
    }
    
    public int read(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        //System.out.println("public int read(int fileDescriptor, byte[] buf, int offset, int numBytes)"+files.elementAt(fileDescriptor));
        FileInfo f=((FileInfo)data.get(files.elementAt(fileDescriptor)));
        
        if(numBytes>(f.data.length-f.seek)) numBytes= f.data.length-f.seek;
        
        System.arraycopy(f.data, f.seek, buf, offset, numBytes);
        f.seek+=numBytes;
        return numBytes;
    }
    
    public static int rename(java.lang.String source, java.lang.String dest) {
        System.out.println("public static int rename(java.lang.String source, java.lang.String dest)");
        return 1;
    }
    
    public int seek(int fileDescriptor, int seekpos) {
        //System.out.println("public int seek(int fileDescriptor, int seekpos)");
        ((FileInfo)data.get(files.elementAt(fileDescriptor))).seek=seekpos;
        return 1;
    }
    
    public static int spaceAvailable() {
        //System.out.println("public static int spaceAvailable()");
        return 1000000;
    }
    
    public static void truncate(int fileDescriptor, int size) {
        System.out.println("public static void truncate(int fileDescriptor, int size)");
    }
    
    public int write(int fileDescriptor, byte[] buf, int offset, int numBytes) {
        //System.out.println("public int write(int fileDescriptor, byte[] buf, int offset, int numBytes)"+files.elementAt(fileDescriptor));
        FileInfo f=((FileInfo)data.get(files.elementAt(fileDescriptor)));
        
        if(numBytes>(f.data.length+f.seek)) {
            byte[] t=new byte[f.data.length+numBytes];
            System.arraycopy(f.data, 0, t, 0, f.data.length);
            f.data=t;
        }
        
        System.arraycopy(buf,offset,f.data,f.seek,numBytes);
        f.seek+=numBytes;
        
        /*for (int i=0;i<f.data.length;i++)
        {
            System.out.print((char)f.data[i]);
        }
        System.out.println();*/
        
        return 1;
    }
    
    
    class FileInfo {
        byte[] data;
        int seek;
        
        FileInfo(byte[] data) {
            this.data=data;
            this.seek=0;
        }
    }
}