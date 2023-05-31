// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.chassis.Drive;
import frc.robot.commands.chassis.GotoCommunity;
import frc.robot.commands.chassis.GotoNodes;
import frc.robot.subsystems.chassis.Chassis;
import frc.robot.subsystems.gripper.Gripper;
import frc.robot.subsystems.gripper.GripperConstants;
import frc.robot.subsystems.parallelogram.Parallelogram;
import frc.robot.utilities.UtilsGeneral;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

  private static RobotContainer instance;
    private final CommandXboxController main = new CommandXboxController(0);
    public final CommandXboxController secondary = new CommandXboxController(1);
    private final Chassis chassis;
    private final Parallelogram parallelogram;
    private final Gripper gripper;
    private GotoNodes gotoNodes;
    private GotoCommunity gotoCommunity;

  // The robot's subsystems and commands are defined here...

  // Replace with CommandPS4Controller or CommandJoystick if needed

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    UtilsGeneral.initializeDeafultAllianceChooser();
        chassis = Chassis.CreateInstance();
        parallelogram = new Parallelogram();
        chassis.setDefaultCommand(new Drive(chassis, main.getHID()));
        SmartDashboard.putData((Sendable) chassis.getDefaultCommand());
        SmartDashboard.putData(chassis);
        gripper = new Gripper(GripperConstants.MOTOR_ID);
        gotoNodes = new GotoNodes(chassis, main, parallelogram);
        SmartDashboard.putData("gotonoeew", (Sendable)gotoNodes);
        gotoCommunity = new GotoCommunity(chassis);
        SmartDashboard.putData(gripper);
        configureButtonBindings();

        SmartDashboard.putData(CommandScheduler.getInstance());
        SmartDashboard.putBoolean("is left led", false);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureButtonBindings() {

    Command unload = gotoCommunity.GetCommand()
            .andThen(gotoNodes.GetCommand());

    unload = unload.until(() -> UtilsGeneral.hasInput(main.getHID()))
           /*.andThen(new InstantCommand(() -> parallelogram.getGoBackCommand().schedule()))*/;

    main.leftBumper().onTrue(new InstantCommand(() -> gripper.getCloseCommand().schedule()));
    main.rightBumper().onTrue(new InstantCommand(() -> gripper.getOpenCommand().schedule()));


    unload.setName("Unload");

    //Change X from auto place to Go to manual angle parallelogram
    main.a().onTrue(new InstantCommand(()->{
      System.out.println("CPMMAND COMMAND COAMMND");
      gotoNodes.GetCommand().schedule();
  }));

    main.start().onTrue(new InstantCommand(()->chassis.setAngleTo180DependsOnAlliance()).ignoringDisable(true));
    main.back().onTrue(new InstantCommand(()->chassis.setAngleTo0DependsOnAlliance()).ignoringDisable(true));

}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return new InstantCommand(()->{chassis.setAngleVelocityWithAcceleration(100, 0, 0);});
  }
}
