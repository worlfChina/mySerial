/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * 给串口权限，并创建相应的流
 */
public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd; // 这个不要改，注释的意思jni用到了
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
		if(device != null && device.exists()){
			/* Check access permission */
			if (!device.canRead() || !device.canWrite()) {
				try {
				/* Missing read/write permission, trying to chmod the file */
					Process su;
					su = Runtime.getRuntime().exec("/system/xbin/su"); // su的目录也可能是 /system/bin/su
					// 给串口赋予超级管理员权限
					String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
							+ "exit\n";
					su.getOutputStream().write(cmd.getBytes());

					// 777 权限的赋予。串口设备的读写权限
					String command = "chmod 777 " + device.getAbsolutePath() + "\n"
							+ "exit\n";
					Process proc = Runtime.getRuntime().exec("/system/xbin/su");
					proc.getOutputStream().write(command.getBytes());

					if ((su.waitFor() != 0) || !device.canRead()
							|| !device.canWrite()) {
						Log.d(TAG, "SerialPort: throw new SecurityException();");
						throw new SecurityException();
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new SecurityException();
				}
			}
		}

		// 调用JNI打开串口，创建流
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
