package demo;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RobotDemo {

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        //robot.setAutoDelay(1000);
//        robot.mouseMove(0, 0);
//        robot.mouseMove(800, 100);
//        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);



        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        // CTRL+Z is now pressed (receiving application should see a "key down" event.)

//        robot.delay(2000);
//
//        robot.keyPress(KeyEvent.VK_CONTROL);
//        robot.keyPress(KeyEvent.VK_ESCAPE);
//        robot.keyRelease(KeyEvent.VK_ESCAPE);
//        robot.keyRelease(KeyEvent.VK_CONTROL);
        // CTRL+Z is now released (receiving application should now see a "key up" event - as well as a "key pressed" event).


    }
}
