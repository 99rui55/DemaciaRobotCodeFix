package frc.robot.commands.chassis;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.chassis.Chassis;
import frc.robot.subsystems.chassis.utils.ChassisCommands;
import frc.robot.subsystems.chassis.utils.TrajectoryGenerator;
import frc.robot.utilities.UtilsGeneral;
import frc.robot.utilities.UtilsGeneral.Zone;

/**
 * Drives the robot semi autonomously to the community zone.
 */
public class GotoCommunity {

    private final Chassis chassis;
    private Command command;

    /**
     * Constructs a new GotoCommunity command.
     * 
     * @param chassis    The chassis subsystem
     * @param controller The controller to use for input
     */
    public GotoCommunity(Chassis chassis) {
        this.chassis = chassis;
    }


    public Command GetCommand(){
        command = new InstantCommand();
        TrajectoryGenerator generator = new TrajectoryGenerator(Alliance.Blue);

        Zone zone = Zone.fromRobotLocation(chassis.getPose().getTranslation());
        if (zone == Zone.COMMUNITY_BOTTOM || zone == Zone.COMMUNITY_TOP || zone == Zone.COMMUNITY_MIDDLE) {
            return command;
        }

        if (chassis.getPose().getY() > 1.51) {
            // Enter through the top
            switch (zone) {
                case LOADING_ZONE:
                    generator.add(new Pose2d(new Translation2d(10.98, 7), Rotation2d.fromDegrees(180)),
                            Rotation2d.fromDegrees(180));
                case OPEN_AREA:
                    generator.add(new Pose2d(new Translation2d(5.65, 4.735), Rotation2d.fromDegrees(180)),
                            Rotation2d.fromDegrees(180));
                default:
                    generator.add(new Pose2d(new Translation2d(2.17, 4.735), Rotation2d.fromDegrees(180)),
                            Rotation2d.fromDegrees(180));
            }
        } else {
            // Enter through the bottom
            generator.add(new Pose2d(new Translation2d(2.17, 0.755), Rotation2d.fromDegrees(180)),
                    Rotation2d.fromDegrees(180));
        }

        command = ChassisCommands.createPathFollowingCommand(false, generator.generate(chassis.getPose()));
        command = command.until(()->{return UtilsGeneral.isRedAlliance() ? chassis.getPose().getX() > 16.54 - 2.6
            : chassis.getPose().getX() < 2.6;});

        return command;
    }
}
