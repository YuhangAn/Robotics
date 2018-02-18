package ca.mcgill.ecse211.controller;

import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.display.Display;
import ca.mcgill.ecse211.model.*;

import ca.mcgill.ecse211.ultrasonic.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class Lab5 {

	public static Odometer odometer;
	public static Display odometryDisplay;

	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;

		// Odometer related objects
		odometer = Odometer.getOdometer(Robot.leftMotor, Robot.rightMotor, Robot.TRACK, Robot.WHEEL_RAD);
		odometryDisplay = new Display(Robot.lcd);
		
		System.out.println("Console output directed to terminal");
		
		do {
			// clear the display
			Robot.lcd.clear();
			// ask the user whether the motors should drive with obstacle avoidance
			Robot.lcd.drawString("< Left | Right >", 0, 0);
			Robot.lcd.drawString("L: Color test, ", 0, 1);
			Robot.lcd.drawString("R: Search, ", 0, 2);
			Robot.lcd.drawString("Please select", 0, 3);
			// Record choice (left or right press)
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_LEFT) {
			System.out.println("Start Color test");
			
		} else {
			// start all threads
			Thread odoThread = new Thread(odometer);
			odoThread.start();
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();
			UltrasonicLocalizer usLocal = new UltrasonicLocalizer(odometer);
			UltrasonicPoller usPoller = new UltrasonicPoller(Robot.usDistance, Robot.usData, usLocal);
			
			// run ultrasonic thread last
			usPoller.start();
			// update the robot model
			Robot.loc=Robot.LocalizationCategory.FALLING_EDGE;
			System.out.println("US localization: "+Robot.loc);
			// do something with localizer
			System.out.println("Start localization method");
			usLocal.localize();
			// beep twice when the localization finishes
			Sound.twoBeeps();
			System.out.println("Complete Ultrasonic Loc");
			
			// wait for light sensor input
			// ToDo: this line should be removed before final submission
			Robot.lcd.drawString("Press any Light Loc", 0, 1);
			buttonChoice = Button.waitForAnyPress();
			
			LightLocalizer newLightLoc=new LightLocalizer(odometer);
			newLightLoc.localize();
			Sound.twoBeeps();
			System.out.println("Complete Light Loc");
			// ToDo: implement search method
			
			Robot.lcd.drawString("Finished", 0, 1);
			
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}