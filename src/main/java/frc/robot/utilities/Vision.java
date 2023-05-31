// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utilities;

import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.function.Consumer;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import frc.robot.Constants;
import frc.robot.Constants.VisionConstants;
import frc.robot.utilities.VisionUtils.LimeLight;

/** Add your docs here. */
public class Vision {
    private NetworkTable visionTable;
    private LimeLight limeLight;

    private Pair<Pose2d, Double> lastValue;

    private int windowSize = 5;
    private int index = 0;
    private Pose2d value;
    private Pose2d sum = new Pose2d(new Translation2d(Constants.FIELD_WIDTH/2, Constants.FIELD_HEIGHT/2), Rotation2d.fromDegrees(90));
    private Pose2d[] readings = new Pose2d[windowSize];
    private Pose2d averaged;

    private Vision(NetworkTable visionTable, LimeLight limeLight){
        this.visionTable = visionTable;
        this.limeLight = limeLight;
        setupListenerLL2(this::updateFilter);
        for(int i = 0; i<5; i++){
            readings[i] = new Pose2d(new Translation2d(Constants.FIELD_WIDTH/2, Constants.FIELD_HEIGHT/2), Rotation2d.fromDegrees(90));
        }
    }

    private  void setupListenerLL2(Consumer<Pair<Pose2d, Double>> listener) {
        NetworkTableInstance.getDefault().addListener(VisionConstants.ROBOT_POSE_ENTRY,
                EnumSet.of(Kind.kValueAll), (event) -> {
                    Pair<Pose2d, Double> pose = VisionUtils.getVisionPoseFiltered(visionTable, limeLight);
                    if (pose != null) {
                        try {
                            listener.accept(pose);
                        } catch (ConcurrentModificationException e) {
                            System.out.println("ConcurrentModificationException");
                        }
                    }
                });
    }

    private void updateFilter(Pair<Pose2d, Double> pair){
        lastValue = pair;
        
        // Remove the oldest entry from the sum:
        sum = new Pose2d(new Translation2d(sum.getX() - readings[index].getX(), sum.getY() - readings[index].getY())
        , Rotation2d.fromDegrees(sum.getRotation().getDegrees() - readings[index].getRotation().getDegrees()));    
        
        // Read the next sensor value:
        value = pair.getFirst();     
        
        // Add the newest reading to the window:
        readings[index] = value;           

        // Add the newest reading to the sum:
        sum = new Pose2d(new Translation2d(sum.getX() + value.getX(), sum.getY() + value.getY())
            , Rotation2d.fromDegrees(sum.getRotation().getDegrees() + value.getRotation().getDegrees()));    
        
        // Increment the index, and wrap to 0 if it exceeds the window size
        index = (index+1) % windowSize;   
      
        // Divide the sum of the window by the window size for the result
        averaged = new Pose2d(new Translation2d(sum.getX()/windowSize, sum.getY()/windowSize)
        , Rotation2d.fromDegrees(sum.getRotation().getDegrees()/windowSize));    
      
    }

    public Pose2d GetPose(){
        return averaged;
    }

    public double GetTimeStamp(){
        return lastValue.getSecond();
    }

    public Pair<Pose2d, Double> GetPair(){
        return lastValue;
    }

    public static Vision LimeLight2 = new Vision(VisionConstants.LIMELIGHT_TABLE1, LimeLight.LimeLight2);
    public static Vision LimeLight3 = new Vision(VisionConstants.LIMELIGHT3_TABLE, LimeLight.LimeLight3);

}
