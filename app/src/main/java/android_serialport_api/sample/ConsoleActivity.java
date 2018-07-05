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

package android_serialport_api.sample;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 用于接收和发送的 Activity
 */
public class ConsoleActivity extends SerialPortActivity {

	private static final String TAG = "ConsoleActivity";

	EditText mReception;
	EditText Emission;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);

		mReception = (EditText) findViewById(R.id.EditTextReception);
		Emission = (EditText) findViewById(R.id.EditTextEmission);

		final Button buttonsend = (Button) findViewById(R.id.ButtonSent1);
		buttonsend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				CharSequence t = Emission.getText();
				String t = Emission.getText().toString().trim(); // 自己改的
				Log.d(TAG, "onClick: " + t);
				if ("".equals(t)) {
					Log.d(TAG, "onClick: t is null");
					Toast.makeText(getApplicationContext(), "请输入发送的内容：",
							Toast.LENGTH_SHORT).show();
					return;
				}
				char[] text = new char[t.length()];
				for (int i = 0; i < t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					mOutputStream.write(new String(text).getBytes());
					mOutputStream.write('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				Log.d(TAG, "run: receive some data");
				if (mReception != null) {
					mReception.append(new String(buffer, 0, size)); // 转换的格式要不要写？
					mReception.append("\n");
				}
			}
		});
	}
}
