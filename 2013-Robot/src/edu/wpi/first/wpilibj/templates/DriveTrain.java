/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * This class is used to control the four motors connected to the wheels and take care
 * of all of their functions.
 * @author Mouse
 */
public class DriveTrain implements Runnable
{
    
    /*
     * these are the objects for the Talon motor controlers on the robot.
     */
    Talon frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    
    /*
     * joystick object.
     */
    Joystick stick;
    
    /*
     * these are the reed swithces. they return false when the magnet passes by the switch
     * otherwise returns false.
     */
    DigitalInput leftWheel = new DigitalInput(9);
    DigitalInput rightWheel = new DigitalInput(6);
    
    /*
     * leftAxis and rightAxis are variables that hold the values for the joystick axis.
     * leftInverted and rightInverted are variables that are either 1 or -1 depending on
     * wheather we want the motors to be inverted due to orientation.
     */
    int leftAxis, rightAxis, leftInverted ,rightInverted;
    
    /*
     * these three doubles are used in the drive code in order to prevent the robot from
     * from accelerating too fast.  max change is the maximum amount of change in the speed 
     * of the robot eachg time the loop goes around. right and left speed are used to hold the values
     * that the motors are set to.
     */
    double maxChange = 0.05;
    double rightSpeed = 0;
    double leftSpeed = 0;
    
    //right side moves faster than left side so the right speed is multiplied by this double
    //in order to make both sides move at the same rate.
    double rightMultiplier = 1;
    
    /*
     * this multiplier is for teleop mode only. It is changed by pressing the shoulder button.
     * It is used so that the driver is able to control the speed and sensitivity of the robot.
     */
    double driveMultiplier;
    
    //used to run the while loop for the drive code.
    boolean runDriveCode;
    
    /**
     * Constructor takes variables given and sets them to instance variables to be used 
     * throughout the class.
     * 
     * @param motorFL - Front Left Motor for four motor drive
     * @param motorFR - Front Right Motor for four motor drive
     * @param motorBL - Back Left Motor for four motor drive
     * @param motorBR - Back Right Motor for four motor drive
     * @param stick - Joystick used to control the robot during teleoperation mode.
     * @param leftAxis - axis on joystick used to control two left wheels.
     * @param rightAxis - axis on joystick used to control two right wheels.
     */
    public DriveTrain(int motorFL, int motorFR, int motorBL, int motorBR, Joystick stick, int leftAxis, int rightAxis){
        frontLeftMotor = new Talon(motorFL);
        frontRightMotor = new Talon(motorFR);
        backLeftMotor = new Talon(motorBL);
        backRightMotor = new Talon(motorBR);  
        
        this.stick = stick;
        
        this.leftAxis = leftAxis;
        this.rightAxis = rightAxis;
    }
    
    /**
     * this method is used during teleoperation mode and a separate thread is made
     * this code runs alongside drive code.
     */
    public void run()
    {
        /*
         * some setup code that sets variables to the right state in order for the code to work 
         * properly
         */
        runDriveCode = true;
        invertLeft(true);
        invertRight(false);
        
        driveMultiplier = 1;
        System.out.println("Starting Drive Code");
        
        /*
         * The main body of the drive code runs in this while loop 
         */
        while(runDriveCode)
        {
            /*
             * gets the inputs off of the joystick, inverts the numbers if need
             * be, aplies multipliers needed due to inbalance of motors and 
             * saves it as a variable.
             */
            double leftInput = stick.getRawAxis(leftAxis)*leftInverted;
            double rightInput = stick.getRawAxis(rightAxis)*rightInverted*rightMultiplier;
            
            /*
             * This code is used to allow the driver to change the sensityivity
             * of the motors to make driving easier.
             */
            if(stick.getRawButton(7) && driveMultiplier >= .4){
                driveMultiplier -= .2;
                Timer.delay(.5);
            } else if(stick.getRawButton(5) && driveMultiplier <= .8){
                driveMultiplier += .2;
                Timer.delay(.5);
            }
            leftInput = driveMultiplier*leftInput;
            rightInput = driveMultiplier*rightInput;
            
            /*
             * This code is used to keep acceleration at a minimum and reduce 
             * jerk by only allowing the motors to increase or decrease thier 
             * speed in certain increments.
             */
            if(leftInput > (leftSpeed + maxChange)){
                leftSpeed = leftSpeed + maxChange;}
            else if(leftInput < (leftSpeed - maxChange)){
                leftSpeed = leftSpeed - maxChange;}
            else{
                leftSpeed = leftInput;}
            
            if(rightInput > (rightSpeed + maxChange)){
                rightSpeed = rightSpeed + maxChange;}
            else if(rightInput < (rightSpeed - maxChange)){
                rightSpeed = rightSpeed - maxChange;}
            else{
                rightSpeed = rightInput;}
            
            /**
             * Debug Code.
             */
            //System.out.println("Left Speed = " + leftSpeed);
            //System.out.println("Right Speed = " + rightSpeed);
            
            frontLeftMotor.set(leftSpeed);
            frontRightMotor.set(rightSpeed);
            backLeftMotor.set(leftSpeed);
            backRightMotor.set(rightSpeed);
            
            if(stick.getRawButton(10)){
                setZero();
            }
            
            /*
            * This delay is to prevent overloading processing and it doesn't 
            * need to run faster.
            */
            Timer.delay(0.01);
        }
        System.out.println("Stoping Drive Code");
    }
    
    public void stopDriveCode(){
        runDriveCode = false;
    }
    
    /*
     * changes the leftInverted or rightInverted int to either 1 or -1.
     */
    public void invertLeft(boolean alpha){
        if(alpha == true){
            leftInverted = -1;}
        else{
            leftInverted = 1;}
    }
    public void invertRight(boolean alpha){
        if(alpha == true){
            rightInverted = -1;}
        else{
            rightInverted = 1;}
    }
    
    //wheel circumfcance is 1' 5.7"
    /*
     * turns bot wheels until the magnets are lined up with the reed switches.
     */
    public void setZero(){
        int left = 0;
        int right = 0;
        allForward();
        Timer.delay(.1);
        while(left != 1 || right != 1){
            if(!leftWheel.get()){
                System.out.println("Left wheel read false. Actual value = " + leftWheel.get());
                left=1;
                leftOff();
            }
            if(!rightWheel.get()){
                System.out.println("Right wheel read false. Actual value = " + rightWheel.get());
                right=1;
                rightOff();
            }
        }
    }
    
    /*
     * these two methods haven't been updated. planned on being used to turn 90
     * degrees using reed swithes.
     */
    public void turnLeft(){
        rightBackward();
        leftForward();
        Timer.delay(1);
        rightOff();
        leftOff();
    }
    public void turnRight(){
        rightForward();
        leftBackward();
        Timer.delay(1);
        rightOff();
        leftOff();
    }
    
    /*
     * this command is for autonomous use. Moves the robot backwards for about 
     * 1.5ft*f. ie if the method is given the parameter 2 it will move 3 feet.
     */
    public void backward(int f){
        for(int i=0;i<f;i++){
            allBackward();
            int left = 0;
            int right = 0;
            Timer.delay(.1);
            if(!leftWheel.get()){
                left++;
                Timer.delay(.1);
            }
            System.out.println(!leftWheel.get());
            System.out.println(!rightWheel.get());
            if(!rightWheel.get()){
                right++;
                Timer.delay(.1);
             }
             if(right == 2){
                rightOff();
             }
             if(left == 2){
                leftOff();
            }
        }
    }
    
    /*
     * this command is for autonomous use. Moves the robot forwards for about 
     * 1.5ft*f. ie if the method is given the parameter 2 it will move 3 feet.
     */
    public void forward(int feet){
        for(int i=0;i<feet;i++){   //
            allForward();
            int left = 0;
            int right = 0;
            Timer.delay(.1);
            while(left != 2 || right != 2)
            {
                if(!leftWheel.get()){
                    left++;
                    Timer.delay(.05);
                }
                if(!rightWheel.get()){
                    right++;
                    Timer.delay(.05);
                }
                if(right == 2){
                    rightOff();
                }
                if(left == 2){
                    leftOff();
                }
            }
        }
    }
    
    /*
     * A private method that turns off all motors.
     */
    private void allOff(){
        frontLeftMotor.set(0);
        frontRightMotor.set(0);
        backLeftMotor.set(0);
        backRightMotor.set(0);
    }
    
    /*
     * A private method that turns all motors on and in the forward direction.
     */
    private void allForward(){
        frontLeftMotor.set(-.25*leftInverted);
        frontRightMotor.set(-.25*rightInverted*rightMultiplier);
        backLeftMotor.set(-.25*leftInverted);
        backRightMotor.set(-.25*rightInverted*rightMultiplier);
    }
    
    /*
     * A private method that turns all motors on and in the backward direction.
     */
    private void allBackward(){
        frontLeftMotor.set(.25*leftInverted);
        frontRightMotor.set(.25*rightInverted*rightMultiplier);
        backLeftMotor.set(.25*leftInverted);
        backRightMotor.set(.25*rightInverted*rightMultiplier);
    }
    
    /*
     * A private method that turns off the right motors.
     */
    private void rightOff(){
        frontRightMotor.set(0);
        backRightMotor.set(0);
    }
    
    /*
     * A private method that turns on the right motors in the forward direction.
     */
    private void rightForward(){
        frontRightMotor.set(.25*rightInverted*rightMultiplier);
        backRightMotor.set(.25*rightInverted*rightMultiplier);
    }
    
    /*
     * A private method that turns on the right motors in the backward direction.
     */
    private void rightBackward(){
        frontRightMotor.set(-.25*rightInverted*rightMultiplier);
        backRightMotor.set(-.25*rightInverted*rightMultiplier);
    }
    
    /*
     * A private method that turns off the left motors.
     */
    private void leftOff(){
        frontLeftMotor.set(0);
        frontRightMotor.set(0);
    }
    
    /*
     * A private method that turns on the left motors in the forward direction.
     */
    private void leftForward(){
        frontLeftMotor.set(.25*leftInverted);
        frontRightMotor.set(.25*leftInverted);
    }
    
    /*
     * A private method that turns on the left motors in the backward direction.
     */
    private void leftBackward(){
        frontLeftMotor.set(-.25*leftInverted);
        frontRightMotor.set(-.25*leftInverted);
    }
}
