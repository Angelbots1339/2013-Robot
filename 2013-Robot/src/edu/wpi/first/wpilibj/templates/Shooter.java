/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Solenoid;


/**
 *
 * @author Mouse
 */
public class Shooter {
    
    /*
     * creates instance variables to hold values to be used throughout the class.
     */
    Solenoid solenoidOut, solenoidIn;    
    Talon shooterMotor1, shooterMotor2;
    private boolean shooterOn;
    
    /**
     * The constructor assigns the parameters received to instance variables.
     * 
     * @param motor1Port
     * @param motor2Port
     * @param solenoidOutPort
     * @param solenoidInPort 
     */
    public Shooter(int motor1Port, int motor2Port, int solenoidOutPort, int solenoidInPort){
        
        solenoidOut = new Solenoid(solenoidOutPort);
        solenoidIn = new Solenoid(solenoidInPort);
        
        shooterMotor1 = new Talon(motor1Port);
        shooterMotor2 = new Talon(motor2Port);
    }
    
    /**
     * This block of code is to be used for autonomous mode. The methods 
     * activate the motors and solenoids in order to fire either one, two, three
     * or four Frisbees.
     */
    public void fire1(){    //fires one shot 
        motorsOn();
        Timer.delay(3);
        activateSolenoids();
        motorsOff();
    }
    public void fire2(){    //fires two shots
        motorsOn();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(1);
        motorsOff();
    }
    public void fire3(){    //fires three shots
        motorsOn();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(1);
        motorsOff();
    }
    public void fire4(){    //Fires four shots
        motorsOn();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        Timer.delay(3);
        activateSolenoids();
        motorsOff();
    }
    
    /*
     * These methods turn the shooter motors on and off. They are used both
     * within this method and in the RobotTemplate method.
     */
    public void motorsOn(){
        shooterMotor1.set(-1);
        shooterMotor2.set(-1);
        shooterOn = true;
    }
    public void motorsOff(){
        shooterMotor1.set(0);
        shooterMotor2.set(0);
        shooterOn = false;
    }
    
    /*
     * This method activates the solenoids in order to push out the piston and
     * push the frisbee into the shooter motors. The command will only run if
     * the shooter motors are already on.
     */
    public void activateSolenoids(){
        if(!areMotorsOn()){
            return;
        }
        solenoidOut.set(true);
        Tim/*
 * DRV8825 - Stepper Motor Driver Driver
 * Indexer mode only.

 * Copyright (C)2015 Laurentiu Badea
 *
 * This file may be redistributed under the terms of the MIT license.
 * A copy of this license has been included with this distribution in the file LICENSE.
 */
#include "DRV8825.h"

/*
 * Microstepping resolution truth table (Page 13 of DRV8825 pdf)
 * 0bMODE2,MODE1,MODE0 for 1,2,4,