/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.org.winters.android.androidthingspca9685;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.common.base.Joiner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nz.org.winters.android.libpca9685.PCA9685Servo;


/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 */

@EActivity(R.layout.main_activity)
public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Pref
  AppPrefs_ appPrefs;

  @ViewById(R.id.seekBar)
  RangeBar rangeBar;

  @ViewById(R.id.textView)
  TextView textView;

  @ViewById(R.id.spinnerChannel)
  Spinner spinnerChannel;

  private static final int SERVO_MIN = 145;
  private static final int SERVO_MAX = 580;
  private int usingChannel = 0;

  PCA9685Servo servo;


  private class RangeBarChangeListener implements RangeBar.OnRangeBarChangeListener {

    @Override
    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
      try {
        setChannelLeftAngle(usingChannel, leftPinIndex);
        setChannelRightAngle(usingChannel, rightPinIndex);
        updateText();
      } catch (Exception e) { // NOSONAR
        Log.d("ERROR", "Exception: " + e.getMessage());
      }

    }
  }

  private List<Integer> channelLeftAngles = new ArrayList<>(16);
  private List<Integer> channelRightAngles = new ArrayList<>(16);

  @AfterViews
  protected void onAfterViews() {
    channelLeftAngles.clear();
    if (!appPrefs.channelAnglesLeft().get().isEmpty()) {
      TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter('|');

      // Once per string to split
      splitter.setString(appPrefs.channelAnglesLeft().get());
      for (String s : splitter) {
        channelLeftAngles.add(Integer.parseInt(s));
      }
    } else {
      for (int i = 0; i < 16; i++) {
        channelLeftAngles.add(20);
      }
    }

    if (!appPrefs.channelAnglesRight().get().isEmpty()) {
      channelRightAngles.clear();
      TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter('|');

      // Once per string to split
      splitter.setString(appPrefs.channelAnglesRight().get());
      for (String s : splitter) {
        channelRightAngles.add(Integer.parseInt(s));
      }
    } else {
      for (int i = 0; i < 16; i++) {
        channelRightAngles.add(40);
      }
    }

    rangeBar.setOnRangeBarChangeListener(new RangeBarChangeListener());
    spinnerChannel.setSelection(appPrefs.selectedChannel().get());

    try {
      PeripheralManagerService peripheralManagerService = new PeripheralManagerService();


      servo = new PCA9685Servo(PCA9685Servo.PCA9685_ADDRESS, peripheralManagerService);
      servo.setServoMinMaxPwm(0, 180, SERVO_MIN, SERVO_MAX);

    } catch (Exception e) { // NOSONAR
      Log.d("ERROR", "Exception: " + e.getMessage());
    }
  }


  @Override
  public void onResume() {
    super.onResume();


  }

  @Override
  public void onPause() {
    super.onPause();

  }

  @Click(R.id.buttonSetLeft)
  void onButtonSetLeftClick() {
    try {
      
      if(servo !=null) {
        servo.setServoAngle(usingChannel, getChannelLeftAngle(usingChannel));
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Left Click: " + e.getMessage());
    }
  }

  @Click(R.id.buttonSetRight)
  void onButtonSetRightClick() {
    try {
      if(servo !=null) {
        servo.setServoAngle(usingChannel, getChannelRightAngle(usingChannel));
      }
    } catch (IOException e) { // NOSONAR - logged with android.
      Log.d(TAG,"Exception on Right Click: " + e.getMessage());
    }
  }

  @ItemSelect(R.id.spinnerChannel)
  void onItemSelect(boolean selected, int position) {
    if(selected) {
      usingChannel = position;
      rangeBar.setRangePinsByIndices(getChannelLeftAngle(usingChannel),getChannelRightAngle(usingChannel));
      appPrefs.selectedChannel().put(position);
      updateText();
    }
  }

  private void updateText() {
    textView.setText(String.format(Locale.getDefault(), "Channel %d Angle Left %d Angle Right %d",
        usingChannel, rangeBar.getLeftIndex(), rangeBar.getRightIndex()));

  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

  private void setChannelLeftAngle(int channel, int angle) {
    channelLeftAngles.set(channel, angle);
      appPrefs.channelAnglesLeft().put(Joiner.on('|').join(channelLeftAngles));
  }

  private void setChannelRightAngle(int channel, int angle) {
    channelRightAngles.set(channel, angle);
       appPrefs.channelAnglesRight().put(Joiner.on('|').join(channelRightAngles));
  }

  int getChannelLeftAngle(int channel) {
    return channelLeftAngles.get(channel);
  }

  int getChannelRightAngle(int channel) {
    return channelRightAngles.get(channel);
  }

}
