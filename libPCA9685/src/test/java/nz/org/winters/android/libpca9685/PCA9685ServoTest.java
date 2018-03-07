package nz.org.winters.android.libpca9685;


import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * @author MWinters
 */
@RunWith(MockitoJUnitRunner.class)
public class PCA9685ServoTest {
  private static final int MODE1 = 0x00;
  private static final int LED0_ON_L = 0x06;
  private static final int LED0_ON_H = 0x07;
  private static final int LED0_OFF_L = 0x08;
  private static final int LED0_OFF_H = 0x09;



  @Mock
  public PeripheralManager peripheralManagerServiceMock;

  @Mock
  public I2cDevice i2cDeviceMock;

  private List<String> i2cDevices = new ArrayList<>();

  @Before
  public void setUp() throws Exception {

    i2cDevices.add("I2C1");

  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void setServoAngle() throws Exception {


    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685Servo device = new PCA9685Servo((byte)0x40, peripheralManagerServiceMock);

    InOrder inOrder = inOrder(i2cDeviceMock);

    device.setServoMinMaxPwm(0,180,10,210);

    device.setServoAngle(2,90);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4 * 2, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4 * 2, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4 * 2, (byte) (110 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4 * 2, (byte) (110 >> 8));

    device.setServoAngle(2,45);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4 * 2, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4 * 2, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4 * 2, (byte) (60 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4 * 2, (byte) (60 >> 8));

    inOrder.verifyNoMoreInteractions();

  }


  @Test
  public void map() throws Exception {
    PCA9685Servo device = new PCA9685Servo((byte)0x40, peripheralManagerServiceMock);

    assertEquals(20,device.map(0,10,20,30,40));
    assertEquals(21,device.map(1,10,20,30,40));
    assertEquals(22,device.map(2,10,20,30,40));
    assertEquals(23,device.map(3,10,20,30,40));
    assertEquals(24,device.map(4,10,20,30,40));
    assertEquals(25,device.map(5,10,20,30,40));
    assertEquals(26,device.map(6,10,20,30,40));
    assertEquals(27,device.map(7,10,20,30,40));
    assertEquals(28,device.map(8,10,20,30,40));
    assertEquals(29,device.map(9,10,20,30,40));


  }



}