package frc.robot.utilities;

import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.function.Consumer;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.VisionConstants;

/**
 * Utility class for vision
 */
public class VisionUtils {

    public enum LimeLight {
        LimeLight2,
        LimeLight3
    }

    /**
     * Sets up a listener for the vision system
     * 
     * @param listener The listener to call when a new pose is received
     */
    /**
     * Gets the pose of the robot from the vision system
     * 
     * @return The pose of the robot from the vision system, and the timestamp of
     *         the measurement, or null if no target is found
     */
    public static Pair<Pose2d, Double> getVisionPoseFiltered(NetworkTable limelightTable, LimeLight limelight) {
        double timeStamp = Timer.getFPGATimestamp();
        double hasTarget = limelightTable.getEntry("tv").getDouble(0);
        if (hasTarget == 0)
            return null;

        double[] robotPose = limelightTable.getEntry("botpose_wpiblue").getDoubleArray(new double[0]);
        if (robotPose.length != 7)
            return null;

        double latency = robotPose[6];

        Rotation2d robotRotation = Rotation2d.fromDegrees(robotPose[5]);
        Translation2d robotTranslation = new Translation2d(robotPose[0], robotPose[1]);

        double[] camTran = limelightTable.getEntry("targetpose_cameraspace").getDoubleArray(new double[0]);
        double distance = Math.hypot(camTran[0], camTran[2]);
        double ta = limelightTable.getEntry("ta").getDouble(0);
        double tx = Math.abs(limelightTable.getEntry("tx").getDouble(0));
        double horizontalLength = limelightTable.getEntry("thor").getDouble(0);
        double verticalLength = limelightTable.getEntry("tvert").getDouble(0);

        //Log
        SmartDashboard.putNumber("LL/Distance" + limelight.toString(), distance);
        SmartDashboard.putNumber("OurLimeLight/X" + limelight.toString(), robotTranslation.getX());
        SmartDashboard.putNumber("LL/Y" + limelight.toString(), robotTranslation.getY());
        
        //Limitations
        if (tx <= VisionConstants.VISION_TX_LIMIT
                && horizontalLength / verticalLength < VisionConstants.MAX_SIDES_RATIO) {
            // return null;
        }
        if (ta < VisionConstants.VISION_TA_LIMIT) {
            // return null;
        }
        if (distance > VisionConstants.MAX_DISTANCE_FOR_LIMELIGHT || distance == 0) {
            return null;
        }
        return new Pair<Pose2d, Double>(
                new Pose2d(robotTranslation, robotRotation),
                timeStamp - (latency / 1000));
    }
}
