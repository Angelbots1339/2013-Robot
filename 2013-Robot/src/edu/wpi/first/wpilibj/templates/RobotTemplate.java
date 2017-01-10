/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends SimpleRobot {
    /*
     * creates a Joystick stick object that is the first joystick out of four 
     * possible. This is used to acces all of the inputs on the joystick.
     */
    Joystick stick = new Joystick(1);
    
    /*
     * creates a Shooter object that controls all of the functions of the
     * shooter. I controls the solenoids and the shooter motors. The first 
     * two arguments are the ports for the two shooter motors on the digital
     * sidecar. the second pair of arguments are for the 2 solenoid ports in the
     * pnuematic bumper on the crio.
     */
    Shooter shooter = new Shooter(5,6,3,4);
    
    /*
     * creates a Compressor object that controls all of the compressor functions.
     * It calls for the GPIO (general purpose IO) port on the digital sidecar
     * for the pressure sensor and the relay port for the spike that controls 
     * the compressor.
     */
    Compressor compress = new Compressor(1, 1);
    
    /**
     * creates a DriveTrain object which controls the wheels of the robot.
     * the first four numbers are the ports on the digital sidecar for the motors
     * in this order: front left motor, front right motor, back left motor, back 
     * right motor. Then the next parameter is for the joystick used to drive it. 
     * Then the two axis used for tank drive, left then right side.
     */
    DriveTrain shooterBot = new DriveTrain(3, 1, 4, 2, stick, 3, 5);
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        System.out.println("Starting Autonomous Code");
        
        compress.start();
        /*
         * uses the fire2 method in order to fire two frisbees.
         */
        shooter.fire3();
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        /*
         * Joystick Map:
         * 0 -
         * 1 -
         * 2 -
         * 3 - 
         * 4 -
         * 5 - 
         * 6 -
         * 7 - 
         * 8 - 
         * 9 -
         */
        System.out.println("Under Operator control");
        compress.start();
        
        
        /*
         * this creates a new thread fo the drive code and starts it.
         */
        Thread driveCode = new Thread(shooterBot);
        driveCode.start();
        
        
    
        Solenoid firstSolenoid = new Solenoid(1);
        Solenoid secondSolenoid = new Solenoid(2);
        
        /*
         * while loop that runs during teleoperation mode that runs seperatly
         * from the drive code thread.
         */
        while(isEnabled()&&isOperatorControl())
        {   
            /*
             * this code is used to turn on the shooter motors and fire the 
             * piston. The shooter motors are set to toggle when the 8th button
             * is pressed.
             */
            if(stick.getRawButton(8) && !shooter.areMotorsOn()) {
                Timer.delay(.3);
                shooter.motorsOn();
            }
            
            if(stick.getRawButton(8) && shooter.areMotorsOn()){
                Timer.delay(.3);
                shooter.motorsOff();
            }
            
            
            if(stick.getRawButton(6)){
                shooter.activateSolenoids();
            }
            
            
            if(stick.getRawButton(1)){
                secondSolenoid.set(false);
                firstSolenoid.set(true);
                Timer.delay(.5);
                firstSolenoid.set(false);
            }
            
            if(stick.getRawButton(2)){
                firstSolenoid.set(false);
                secondSolenoid.set(true);
                Timer.delay(.5);
                secondSolenoid.set(false);
            }
            
            
            
        }
        
        /*
         * this line stops the drive code thread so multiple threads are not 
         * created and so the robot will not move after being disable.
         */
        shooterBot.stopDriveCode();
    }
    
    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {
        System.out.println("Test Mode");
    } 
}
