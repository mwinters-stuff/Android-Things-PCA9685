package nz.org.winters.android.libpca9685;


import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * @author MWinters
 */
@RunWith(MockitoJUnitRunner.class)
public class PCA9685Test {
  private static final int MODE1 = 0x00;
  private static final int MODE2 = 0x01;
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
  private static final int ALLCALL = 0x01;
  private static final int OUTDRV = 0x04;


  @Mock
  public PeripheralManagerService peripheralManagerServiceMock;

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
  public void creation() throws Exception {
    InOrder inOrder = inOrder(i2cDeviceMock);

    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685 device = new PCA9685((byte)0x40, peripheralManagerServiceMock);

    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_L, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_H, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_L, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_H, (byte) (0));

    inOrder.verify(i2cDeviceMock).writeRegByte(MODE2, (byte) OUTDRV);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) ALLCALL);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) (0));

    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte)16); //#go to sleep
    inOrder.verify(i2cDeviceMock).writeRegByte(PRESCALE, (byte)121);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) 0);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) (0x80));

    device.setPwm(1,10,0);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4, (byte) (10 & 0xFF));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4, (byte) (10 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4, (byte) (0));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4, (byte) (0));
    inOrder.verifyNoMoreInteractions();
  }



  @Test
  public void setPwmFreq() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685 device = new PCA9685((byte)0x40, peripheralManagerServiceMock);

    InOrder inOrder = inOrder(i2cDeviceMock);

    device.setPwmFreq(30);

    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte)16); //#go to sleep
    inOrder.verify(i2cDeviceMock).writeRegByte(PRESCALE, (byte)202);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) 0);
    inOrder.verify(i2cDeviceMock).writeRegByte(MODE1, (byte) (0x80));

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setPwm() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685 device = new PCA9685((byte)0x40, peripheralManagerServiceMock);

    InOrder inOrder = inOrder(i2cDeviceMock);

    device.setPwm(5,10,20);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4 * 5, (byte) (10 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4 * 5, (byte) (10 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4 * 5, (byte) (20 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4 * 5, (byte) (20 >> 8));

    device.setPwm(7,1000,2123);

    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_L + 4 * 7, (byte) (1000 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_ON_H + 4 * 7, (byte) (1000 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_L + 4 * 7, (byte) (2123 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(LED0_OFF_H + 4 * 7, (byte) (2123 >> 8));

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setAllPwm() throws Exception {
    when(peripheralManagerServiceMock.getI2cBusList()).thenReturn(i2cDevices);
    when(peripheralManagerServiceMock.openI2cDevice("I2C1",0x40)).thenReturn(i2cDeviceMock);

    when(i2cDeviceMock.readRegByte(MODE1)).thenReturn((byte)0x0);

    PCA9685 device = new PCA9685((byte)0x40, peripheralManagerServiceMock);

    InOrder inOrder = inOrder(i2cDeviceMock);

    device.setAllPwm(10,20);

    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_L ,(byte) (10 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_H ,(byte) (10 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_L, (byte) (20 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_H, (byte) (20 >> 8));

    device.setAllPwm(1000,2123);

    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_L ,(byte) (1000 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_ON_H ,(byte) (1000 >> 8));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_L, (byte) (2123 & 0xff));
    inOrder.verify(i2cDeviceMock).writeRegByte(ALL_LED_OFF_H, (byte) (2123 >> 8));

    inOrder.verifyNoMoreInteractions();

  }



}