import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.scene.canvas.*; 
import javafx.scene.effect.*;
import javafx.animation.AnimationTimer; 
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;

public class Application
{

    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;

    static Set<KeyCode> keysPressed = new HashSet<>();

    private static double xMan = 512;
    private static double yMan = 450;
    private static double vMan = 0;
    private static boolean reverseMan = false;
    private static double worldScroll = 0;

    private static double xBullet[];
    private static double yBullet[];
    private static int noOfBullets = 0;
    private static double countdown = 1;

    private static int highScore = 0;    

    public static void main(String args[])
    {       
        JFXPanel panel = new JFXPanel();        
        Platform.runLater(() -> start());               
    }

    private static void restart() {

        xMan = 512;
        yMan = 450;
        vMan = 0;
        reverseMan = false;
        worldScroll = 0;
        noOfBullets = 0;
        countdown = 1;

    }

    private static void start() 
    {
        try
        {         
            System.out.println("Application Starting...");

            MediaPlayer mp = new MediaPlayer(new Media(Application.class.getResource("epic.mp3").toString()));
            mp.play();

            FrameRegulator fr = new FrameRegulator();
            Random rnd = new Random(System.currentTimeMillis());

            Group root = new Group();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            Canvas canvas = new Canvas();

            stage.setTitle("JavaFX Canvas Demo");
            stage.setResizable(false);
            stage.setScene(scene);                        
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        System.out.println("Close button was clicked!");
                        Application.terminate();
                    }
                });
            stage.show(); 
            stage.setWidth(WINDOW_WIDTH);
            stage.setHeight(WINDOW_HEIGHT);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> keysPressed.add(event.getCode()));
            scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> keysPressed.remove(event.getCode()));

            canvas.setWidth(WINDOW_WIDTH);
            canvas.setHeight(WINDOW_HEIGHT);            
            root.getChildren().add(canvas);

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.WHITE);
            gc.setFont(new Font("Arial", 24));  

            Image leftMan = new Image("left.png");
            Image rightMan = new Image("right.png");

            Image floor = new Image("floor.png");
            Image wall = new Image("wall.png");

            Image bullet = new Image("bullet.png");

            xBullet = new double[1000];
            yBullet = new double[1000];

            new AnimationTimer() {
                @Override
                public void handle(long now) {

                    /* INPUT */

                    for(KeyCode k : keysPressed)
                    {
                        if (k == KeyCode.ESCAPE) Application.terminate();                        

                        if (k == KeyCode.D) 
                        {   
                            xMan += 500 * fr.getFrameLength();
                            reverseMan = false;
                        }

                        if (k == KeyCode.A) 
                        {
                            xMan -= 500 * fr.getFrameLength();
                            reverseMan = true;
                        }

                        if (k == KeyCode.W && yMan == 450) 
                        {
                            vMan = -1500;
                        }

                    }
                    /* PROCESS */

                    worldScroll += 100 * fr.getFrameLength();                 
                    xMan -= 100 * fr.getFrameLength();

                    if (xMan < 0) xMan = 0;
                    if (xMan > WINDOW_WIDTH) xMan = WINDOW_WIDTH;

                    if (yMan <= 450) {
                        yMan += vMan * fr.getFrameLength();
                        vMan += 5000 * fr.getFrameLength();

                        if (yMan >= 450) {
                            yMan = 450;
                            vMan = 0;
                        }

                    }

                    countdown -= fr.getFrameLength();
                    if (countdown < 0) {
                        xBullet[noOfBullets] = WINDOW_WIDTH + 100;
                        yBullet[noOfBullets] = rnd.nextInt(400) + 50;
                        noOfBullets += 1;
                        countdown = 2;
                        if (noOfBullets > highScore) highScore = noOfBullets;
                    }

                    for (int b = 0; b < noOfBullets; b++) {
                        xBullet[b] -= 200 * fr.getFrameLength();
                        double d = Math.sqrt(Math.pow(xMan - xBullet[b], 2) + Math.pow(yMan - yBullet[b], 2));
                        if (d < 75)  restart();                    

                    }

                    /* OUTPUT */
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

                    for (int x = 0; x < WINDOW_WIDTH + 100; x += 100) {
                        for (int y = 0; y < WINDOW_HEIGHT; y += 100) {
                            gc.drawImage(y < 500 ? wall : floor, x - worldScroll % 100, y);
                        }
                    }

                    gc.drawImage(reverseMan ? leftMan : rightMan, xMan - 50, yMan - 50);

                    for (int b = 0; b < noOfBullets; b++) {
                        if (xBullet[b] < -100) continue;
                        gc.drawImage(bullet, xBullet[b] - 50, yBullet[b] - 50);
                    }

                    gc.strokeText("HIGH SCORE: " + highScore, 10, 20);

                    fr.updateFPS(now, gc);
                }
            }.start();

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            terminate();
        }
    }

    public static void terminate()
    {
        System.out.println("Terminating Application...");
        System.exit(0);
    }

}