// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.chassis.utils;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Gyro extends SubsystemBase {
  private double angle;
  private double speedDS;
  private Double timeStamp  = Timer.getFPGATimestamp();
  /** Creates a new Gyro. */
  public Gyro() {}

  public void setAngle(double angle){
    this.angle = angle;
  }

  public double GetAngle(){
    return angle;
  }

  public void setSpeedDS(double d){
    speedDS = d;
  }

  @Override
  public void periodic() {
    angle += speedDS * (Timer.getFPGATimestamp() - timeStamp);
    timeStamp = Timer.getFPGATimestamp();
  }
}
