// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.chassis.utils;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.chassis.ChassisConstants;

public class SimulateMotor extends SubsystemBase {
  private double timeStamp = Timer.getFPGATimestamp();
  private double speed;
  private double encoder = 0;
  /** Creates a new SimulateMotor. */
  public SimulateMotor() {}

  @Override
  public void periodic() {
    encoder += (speed*(Timer.getFPGATimestamp() - timeStamp)) * ChassisConstants.SwerveModuleConstants.PULSE_PER_METER;
    timeStamp = Timer.getFPGATimestamp();
  }

  public void setVelocity(double speed){
    this.speed = speed;
  }

  public void setAngle(double angle){
    encoder = angle;
  }

  public double getSelectedSensorPosition(){
    return encoder;
  }

  public double getSelectedSensorVelocity(){
    return speed * ChassisConstants.SwerveModuleConstants.PULSE_PER_METER;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("SPD", ()->{return speed;}, null);
    builder.addDoubleProperty("ENC", ()->{return encoder;}, null);
  }

}
