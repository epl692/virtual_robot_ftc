package virtual_robot.robots.classes;

import com.qualcomm.robotcore.hardware.*;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import virtual_robot.controller.BotConfig;

/**
 * Team R.O.B.O.T.I.C.S. 2026 robot: a mecanum drive base using the hardware names from the team's
 * FTC2026 TeamCode, plus the pickup motor, shooter motor and three continuous-rotation index servos.
 *
 * The extras only spin visually; there are no game pieces to pick up or shoot.
 *
 * RoboticsBot2026 is the controller class for the "robotics_2026.fxml" markup file.
 */
@BotConfig(name = "Team R.O.B.O.T.I.C.S. 2026", filename = "robotics_2026")
public class RoboticsBot2026 extends MecanumPhysicsBase {

    // The drive motors are registered by MecanumPhysicsBase under these names; the team's code
    // uses the *_drive names instead, so each motor is also registered under its team name.
    private static final String[] SIM_DRIVE_NAMES =
            {"back_left_motor", "front_left_motor", "front_right_motor", "back_right_motor"};
    private static final String[] TEAM_DRIVE_NAMES =
            {"back_left_drive", "front_left_drive", "front_right_drive", "back_right_drive"};

    private static final String[] INDEX_SERVO_NAMES = {"index_servo_1", "index_servo_2", "index_servo_3"};
    private static final double INDEX_SERVO_DEGREES_PER_SECOND = 720;

    private DcMotorExImpl pickupMotor = null;
    private DcMotorExImpl shooterMotor = null;
    private CRServoImpl[] indexServos = new CRServoImpl[3];

    // These are instantiated during loading via fx:id properties.
    @FXML Rectangle pickupRoller;
    @FXML Rectangle shooterWheel;
    @FXML Rectangle indexBar1;
    @FXML Rectangle indexBar2;
    @FXML Rectangle indexBar3;

    public RoboticsBot2026(){
        super();
    }

    public void initialize(){
        super.initialize();
        hardwareMap.setActive(true);
        pickupMotor = (DcMotorExImpl)hardwareMap.get(DcMotorEx.class, "pickup_motor");
        shooterMotor = (DcMotorExImpl)hardwareMap.get(DcMotorEx.class, "shooter_motor");
        for (int i=0; i<3; i++) indexServos[i] = (CRServoImpl)hardwareMap.crservo.get(INDEX_SERVO_NAMES[i]);
        hardwareMap.setActive(false);

        // Pivots are the centers of each shape, in the 75x75 FXML coordinate space
        pickupRoller.getTransforms().add(new Rotate(0, 37.5, 17));
        shooterWheel.getTransforms().add(new Rotate(0, 37.5, 55));
        indexBar1.getTransforms().add(new Rotate(0, 15, 0.5));
        indexBar2.getTransforms().add(new Rotate(0, 60.5, 0.5));
        indexBar3.getTransforms().add(new Rotate(0, 55, 29.5));
    }

    protected void createHardwareMap(){
        super.createHardwareMap();
        // Same motor objects under the team's names, so the base class's physics wiring is unchanged
        // (the map only hands out devices while active)
        hardwareMap.setActive(true);
        for (int i=0; i<4; i++){
            hardwareMap.put(TEAM_DRIVE_NAMES[i], hardwareMap.get(DcMotor.class, SIM_DRIVE_NAMES[i]));
        }
        hardwareMap.setActive(false);
        hardwareMap.put("pickup_motor", new DcMotorExImpl(MOTOR_TYPE, motorController1, 0));
        hardwareMap.put("shooter_motor", new DcMotorExImpl(MOTOR_TYPE, motorController1, 1));
        for (String name : INDEX_SERVO_NAMES) {
            hardwareMap.put(name, new CRServoImpl(INDEX_SERVO_DEGREES_PER_SECOND));
        }
    }

    public synchronized void updateStateAndSensors(double millis){
        super.updateStateAndSensors(millis);
        pickupMotor.update(millis);
        shooterMotor.update(millis);
        for (CRServoImpl servo : indexServos) servo.updatePositionDegrees(millis);
    }

    public synchronized void updateDisplay(){
        super.updateDisplay();
        setAngle(pickupRoller, motorDegrees(pickupMotor));
        setAngle(shooterWheel, motorDegrees(shooterMotor));
        setAngle(indexBar1, indexServos[0].getPositionDegrees());
        setAngle(indexBar2, indexServos[1].getPositionDegrees());
        setAngle(indexBar3, indexServos[2].getPositionDegrees());
    }

    public void powerDownAndReset(){
        super.powerDownAndReset();
        pickupMotor.stopAndReset();
        shooterMotor.stopAndReset();
    }

    private double motorDegrees(DcMotorExImpl motor){
        return motor.getActualPosition() * 360.0 / MOTOR_TYPE.TICKS_PER_ROTATION;
    }

    private static void setAngle(Rectangle node, double degrees){
        ((Rotate)node.getTransforms().get(0)).setAngle(degrees);
    }
}
