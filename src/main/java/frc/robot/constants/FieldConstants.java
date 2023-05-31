package frc.robot.constants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FieldConstants {
    public static final double
            FIELD_LENGTH_METERS = 16.54175,
            FIELD_WIDTH_METERS = 8.02;
    private static final double BUMPERS_LENGTH_METERS = 0.86;
    private static final double BUMPERS_WIDTH_METERS = 0.86;
    public static final Pose2d ENDGAME_OBJECT_POSITION = new Pose2d(4, 5, Rotation2d.fromDegrees(90));
    //......
    public enum PositionAlignment {
        POSITION_ONE(new Pose2d(1,1,Rotation2d.fromDegrees(0))),
        POSITION_TWO(new Pose2d(2,2,Rotation2d.fromDegrees(0))),
        POSITION_THREE(new Pose2d(3,1,Rotation2d.fromDegrees(0))),
        POSITION_FOUR(new Pose2d(4,1,Rotation2d.fromDegrees(180))),
        POSITION_FIVE(new Pose2d(5,1,Rotation2d.fromDegrees(0)));
        //......
        public final Pose2d inFrontOfGridPose;
        public final int gridNumber;
        public final int columnNumber;

        PositionAlignment(Pose2d inFrontOfGridPose) {
            this.inFrontOfGridPose = inFrontOfGridPose;
            final int index = ordinal();

            gridNumber = index / 3 + 1;
            columnNumber = index % 3 + 1;
        }

        public static PositionAlignment getGridAlignment(int gridNumber, int columnNumber) {
            return values()[(gridNumber - 1) * 3 + columnNumber - 1];
        }
    }
}
