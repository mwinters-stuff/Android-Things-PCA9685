package nz.org.winters.android.libpca9685;

import android.support.annotation.NonNull;

import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Created by mathew on 6/01/17.
 * Copyright 2017 Mathew Winters
 */

@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class PCA9685Servo extends PCA9685 {
  private int minPwm;
  private int maxPwm;
  private int minAngle;
  private int maxAngle;

  public PCA9685Servo(byte address, @NonNull PeripheralManager manager) throws IOException, InterruptedException {
    super(address, manager);
  }

  public void setServoMinMaxPwm(int minAngle, int maxAngle, int minPwm, int maxPwm) {
    this.maxPwm = maxPwm;
    this.minPwm = minPwm;
    this.minAngle = minAngle;
    this.maxAngle = maxAngle;
  }


  public void setServoAngle(int channel, int angle) throws IOException {
    setPwm(channel, 0, map(angle, minAngle, maxAngle, minPwm, maxPwm));
  }



  public int map(int x, int inMin, int inMax, int outMin, int outMax) {
    return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
  }

}
