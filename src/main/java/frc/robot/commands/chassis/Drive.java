// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.chassis;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.chassis.ChassisConstants;
import frc.robot.subsystems.chassis.Chassis;
import frc.robot.utilities.UtilsGeneral;
import frc.robot.utilities.UtilsGeneral.ControllerSide;

/**
 * Drives the robot using the left stick for velocity and the triggers for
 * rotation.
 */
public class Drive extends CommandBase {
    private final Chassis chassis;
    private final XboxController controller;
    private double scaleVelocity = 2;
    private double scaleRotation = 2;
    private double vx;
    private double vy;
    private double omega;
    private Rotation2d angle;
    private boolean red;

    /**
     * Creates a new DriveVelocities.
     * 
     * @param chassis    The chassis to drive
     * @param controller The controller to get input from (left stick is used for
     *                   velocity, triggers or right stick for rotation)
     */
    public Drive(Chassis chassis, XboxController controller) {
        this.chassis = chassis;
        this.controller = controller;
        addRequirements(chassis);
    }

    public void updateValues(){
         red = UtilsGeneral.isRedAlliance();
        Translation2d xy = UtilsGeneral.getScaledStick(controller, ControllerSide.LEFT, scaleVelocity)
                .times(red ? -1 : 1);
         vx = -xy.getX() * ChassisConstants.MAX_DRIVE_SPEED;
         vy = -xy.getY() * ChassisConstants.MAX_DRIVE_SPEED;
         omega = UtilsGeneral.getScaledTriggerDiff(controller, ControllerSide.LEFT, scaleRotation)
                * ChassisConstants.MAX_ANGULAR_SPEED;
         angle = UtilsGeneral.getStickRotation(controller, ControllerSide.RIGHT);
    }

    @Override
    public void execute() {
        updateValues();
        if (angle == null){
        chassis.setVelocitiesAcceleration(vx, vy, omega);
        }
        else{
        chassis.setAngleVelocityWithAcceleration(vx, vy, angle.getRadians() + Math.PI / 2 * (red ? 1 : -1));
        }
    }

    @Override
    public void end(boolean interrupted) {
        chassis.stop();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Velocity Scale", () -> scaleVelocity, (s) -> scaleVelocity = s);
        builder.addDoubleProperty("Rotation Scale", () -> scaleRotation, (s) -> scaleRotation = s);
        builder.addDoubleProperty("DRIVE X", ()->{return vx;}, null);
        builder.addDoubleProperty("DRIVE Y", ()->{return vy;}, null);
        builder.addDoubleProperty("Angle", ()->{try{return angle.getRadians();}catch(Exception e){return 5635;}}, null);
        builder.addDoubleProperty("OMEGA", ()->{return omega;}, null);
    }
}
