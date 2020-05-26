// Declare dependencies.
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

// "Main" class declaration.
public class Main extends Application
{

	// Global instance declarations.
	private static final int WINDOW_WIDTH = 512;
	private static final int WINDOW_HEIGHT = 266;
	String rawAngleReading;

	// JavaFX "start" method.
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		SerialPort sp = SerialPort.getCommPort("COM17");
		sp.setComPortParameters(9600,8,1,0);
		sp.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		if(sp.openPort())
		{
			System.out.println("Success.");
		}
		else
		{
			System.out.println("Failure.");
			return;
		}
		Scanner scanner = new Scanner(sp.getInputStream());

		// Declare 3D objects
		Box base = new Box(32,35,28);
		Box pivotingPiece = new Box(32,35,28);
		Cylinder pivotPoint = new Cylinder(25,35);
		base.translateYProperty().setValue(35);
		pivotingPiece.translateYProperty().setValue(-35);
		pivotPoint.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		// Declare 2D objects.
		Rectangle textBox = new Rectangle();
		Rectangle jointBox = new Rectangle();
		Rectangle backgroundText = new Rectangle();
		Text titleText = new Text(40,25,"INFORMATON");
		Text rawSensorText = new Text(35,50,"RAW SENSOR READING: ");
		Text angleText = new Text(35,70,"CURRENT ANGLE: ");

		textBox.setStroke(WHITE);
		textBox.setStrokeWidth(2);
		textBox.setX(20);
		textBox.setY(20);
		textBox.setWidth(226);
		textBox.setHeight(226);
		textBox.setArcWidth(20);
		textBox.setArcHeight(20);
		jointBox.setStroke(WHITE);
		jointBox.setStrokeWidth(2);
		jointBox.setX(266);
		jointBox.setY(20);
		jointBox.setWidth(226);
		jointBox.setHeight(226);
		jointBox.setArcWidth(20);
		jointBox.setArcHeight(20);
		backgroundText.setFill(BLACK);
		backgroundText.setX(38);
		backgroundText.setY(15);
		backgroundText.setWidth(92);
		backgroundText.setHeight(10);
		titleText.setFont(Font.font("hp simplified", 16));
		titleText.setFill(WHITE);
		rawSensorText.setFont(Font.font("hp simplified", 14));
		rawSensorText.setFill(WHITE);
		angleText.setFont(Font.font("hp simplified", 14));
		angleText.setFill(WHITE);

		// Declare groups.
		Group objects2D = new Group();
		objects2D.getChildren().add(textBox);
		objects2D.getChildren().add(jointBox);
		objects2D.getChildren().add(backgroundText);
		objects2D.getChildren().add(titleText);
		objects2D.getChildren().add(rawSensorText);
		objects2D.getChildren().add(angleText);
		Group objects3D = new Group();
		Group main = new Group();
		main.getChildren().add(objects2D);
		main.getChildren().add(objects3D);
		Scene scene = new Scene(main, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.setFill(BLACK);

		// Transforms.
		objects3D.getChildren().add(base);
		objects3D.getChildren().add(pivotPoint);
		objects3D.getChildren().add(pivotingPiece);
		objects3D.getTransforms().add(new Rotate(45, Rotate.X_AXIS));
		objects3D.getTransforms().add(new Rotate(45, Rotate.Y_AXIS));
		objects3D.translateXProperty().setValue(380);
		objects3D.translateYProperty().setValue(WINDOW_HEIGHT/2);


		// Window setup.
		primaryStage.setTitle("Joint Simulator");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

		new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				rawAngleReading = scanner.nextLine();
				double angle = (Double.parseDouble(scanner.nextLine()) > 200)?Math.round((Double.parseDouble(scanner.nextLine())-200)*0.225):0;
				angleText.setText("CURRENT ANGLE: " + angle);
				rawSensorText.setText("RAW SENSOR READING: " + rawAngleReading);
				pivotingPiece.setLayoutX(35*Math.cos(Math.toRadians(angle-90)));
				pivotingPiece.setLayoutY(35*Math.sin(Math.toRadians(angle-90))+35);
				pivotingPiece.rotateProperty().set(angle);
			}
		}.start();
	}

	// The "main" method.
	public static void main(String[] args) throws IOException
	{
		launch(args);
	}
}
