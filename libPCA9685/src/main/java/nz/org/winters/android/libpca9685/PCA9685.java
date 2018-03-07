package nz.org.winters.android.libpca9685;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Created by mathew on 18/01/17.
 * Copyright 2017 Mathew Winters
 */
@SuppressWarnings({"WeakerAccess", "unused","squid:S00115", "squid:S1068"})
public class PCA9685 implements Closeable{
  public static final byte PCA9685_ADDRESS = 0x40;
  private static final int MODE1 = 0x00;
  private static final int MODE2 = 0x01;
  private static final int SUBADR1 = 0x02; // NOSONAR
  private static final int SUBADR2 = 0x03; // NOSONAR
  private static final int SUBADR3 = 0x04; // NOSONAR
  private static final int PRESCALE = 0xFE;
  private static final int LED0_ON_L = 0x06;
  private static final int LED0_ON_H = 0x07;
  private static final int LED0_OFF_L = 0x08;
  private static final int LED0_OFF_H = 0x09;
  private static final int ALL_LED_ON_L = 0xFA;
  private static final int ALL_LED_ON_H = 0xFB;
  private static final int ALL_LED_OFF_L = 0xFC;
  private static final int ALL_LED_OFF_H = 0xFD;
  // Bits
  private static final int RESTART = 0x80; // NOSONAR
  private static final int SLEEP = 0x10;
  private static final int ALLCALL = 0x01;
  private static final int INVRT = 0x10; // NOSONAR
  private static final int OUTDRV = 0x04;

  private static final String TAG = PCA9685.class.getName();

  private I2cDevice i2cDevice;

  public PCA9685(byte address, @NonNull PeripheralManager manager) throws IOException, InterruptedException {

    List<String> deviceList = manager.getI2cBusList();
    if (deviceList.isEmpty()) {
      i2cDevice = null;
      Log.i(TAG, "No I2C bus available on this device.");
    } else {
      Log.i(TAG, "List of available devices: " + deviceList);
      try {
        i2cDevice = manager.openI2cDevice(deviceList.get(0), address);
        if (i2cDevice != null) {

          setAllPwm(0, 0);
          i2cDevice.writeRegByte(MODE2, (byte) OUTDRV);
          i2cDevice.writeRegByte(MODE1, (byte) ALLCALL);
          Thread.sleep(5); // #wait for oscillator
          byte mode1 = i2cDevice.readRegByte(MODE1);
          mode1 = (byte) (mode1 & ~SLEEP); //#wake up (reset sleep)
          i2cDevice.writeRegByte(MODE1, mode1);
          Thread.sleep(5); //#wait for oscillator


          setPwmFreq(50); // good default.
        }
      } catch (IOException e) {
        Log.d(TAG, "IO Error " + e.getMessage());
        e.printStackTrace(); // NOSONAR
        throw e;
      } catch (InterruptedException e) {
        Log.d(TAG, "Error in sleep " + e.getMessage());
        e.printStackTrace(); // NOSONAR
        throw e;
      }
    }
  }

  public void setPwmFreq(int freqHz) throws IOException {
    try {
      double prescaleval = 25000000.0;    //# 25MHz
      prescaleval /= 4096.0;       //# 12-bit
      prescaleval /= freqHz;
      prescaleval -= 1.0;

      Log.d(TAG, String.format("Setting PWM frequency to %d Hz", freqHz));
      Log.d(TAG, String.format("Estimated pre-scale: %.4f", prescaleval));
      int prescale = (int) Math.floor(prescaleval + 0.5);
      Log.d(TAG, String.format("Final pre-scale: %d", prescale));
      byte oldmode = i2cDevice.readRegByte(MODE1);
      byte newmode = (byte) ((oldmode & 0x7F) | 0x10); //#sleep
      i2cDevice.writeRegByte(MODE1, newmode); //#go to sleep
      i2cDevice.writeRegByte(PRESCALE, (byte) prescale);
      i2cDevice.writeRegByte(MODE1, oldmode);

      Thread.sleep(5);

      i2cDevice.writeRegByte(MODE1, (byte) (oldmode | 0x80));
    } catch (IOException e) {
      Log.d(TAG, "IO Error " + e.getMessage());
      e.printStackTrace(); // NOSONAR
      throw e;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void setPwm(int channel, int on, int off) throws IOException {
    if (i2cDevice != null && channel >= 0 && channel < 16) {
      try {
        i2cDevice.writeRegByte(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        i2cDevice.writeRegByte(LED0_ON_H + 4 * channel, (byte) (on >> 8));
        i2cDevice.writeRegByte(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
        i2cDevice.writeRegByte(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
      } catch (IOException e) {
        e.printStackTrace(); // NOSONAR
        throw e;
      }
    }

  }

  public void setAllPwm(int on, int off) throws IOException {
    if (i2cDevice != null) {
      try {
        i2cDevice.writeRegByte(ALL_LED_ON_L, (byte) (on & 0xFF));
        i2cDevice.writeRegByte(ALL_LED_ON_H, (byte) (on >> 8));
        i2cDevice.writeRegByte(ALL_LED_OFF_L, (byte) (off & 0xFF));
        i2cDevice.writeRegByte(ALL_LED_OFF_H, (byte) (off >> 8));
      } catch (IOException e) {
        e.printStackTrace(); // NOSONAR
        throw e;
      }
    }
  }
  @Override
  public void close() throws IOException {
    if (i2cDevice != null) {
      i2cDevice.close();
    }
  }
}
