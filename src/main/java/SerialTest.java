import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Random;

public class SerialTest {

    public static void main(String[] args) {
        SerialPort serialPort = new SerialPort("COM3");
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
           // serialPort.writeString("2\n");
//            serialPort.writeString("0\n");

//            serialPort.notifyAll();
            Random random = new Random();
            while (true){
                if(random.nextBoolean()){
                    System.out.println("net");
                    serialPort.writeBytes("2\n".getBytes());
                }else {
                    System.out.println("well");
                    serialPort.writeBytes("8\n".getBytes());
                }
                Thread.sleep(1000);
            }
//            Thread.sleep(8000);
//         serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}