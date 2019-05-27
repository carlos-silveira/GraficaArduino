package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.animation.AnimationTimer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Main extends Application {
    public static String message;
    private static final int MAX_DATA_POINTS = 20;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private ExecutorService executor;
    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
    private SerialPort comPort;
    private int voltage = 0;
    private NumberAxis xAxis;


    //termometro
    Gauge gauge2;
    private int randomN;
    private Gauge gauge;
    private long lastTimerCall;
    private AnimationTimer timer;
    Circle circle1=new Circle();
    Text t1=new Text();
    Circle circle2=new Circle();
    Text t2=new Text();
    Button b1= new Button();
    boolean on=false;

    private void init(Stage primaryStage) throws URISyntaxException {

        xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        NumberAxis yAxis = new NumberAxis(0, 100, 10);

        // Create a LineChart
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };

        lineChart.setTitle("Potenciometro");
        lineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        series1.setName("Porcentaje de Voltaje");

        // Add Chart Series
        lineChart.getData().addAll(series1);


//        comPort = SerialPort.getCommPorts()[0];
//        comPort.openPort();
//        comPort.addDataListener(new SerialPortDataListener() {
//            @Override
//            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
//            @Override
//            public void serialEvent(SerialPortEvent event)
//            {
//                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
//                    return;
//                InputStream in = comPort.getInputStream();
//                byte[] buffer = new byte[3];
//                String message = "";
//                int len = 0;
//                try {
//                    len = in.read(buffer);
//                } catch (IOException e) {}
//                if (len > 0) {
//                    message = new String(buffer);
//                }
//                try {
//                    voltage = Integer.parseInt(message.trim());
////                    System.out.println(voltage);
//                } catch (NumberFormatException ignored) {}
//            }
//        });
        ExampleClient c = new ExampleClient( new URI( "ws://192.168.43.57:8000/ws/chat/arduino/" )); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        c.connect();
        /*Thermometer */
        Gauge.SkinType skin = Gauge.SkinType.LINEAR;

        gauge = GaugeBuilder.create()
                .skinType(skin)
                .title("Tanquesote")
                .titleColor(Color.RED)
                .barColor(Color.web("#A31621"))
                .minValue(0)
                .maxValue(10)
                .value(0)
                .prefWidth(700)
                .lcdVisible(true)
                .unit("\u00B0C")
                .build();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {

            private int counter = 0;
            private boolean changed = false;

            @Override
            public void handle( long now ) {
                if ( now > lastTimerCall  ) {
//                    randomN= (int) (100 * Math.random() - 5);

                    voltage = Integer.parseInt(message.trim());
                    gauge.setValue(voltage);
                    System.out.println(voltage);
                    gauge2.setValue(voltage);

                    if(voltage>50){
                        circle1.setFill(Color.web("#A31621"));
                        circle2.setFill(Color.web("#CED3DC"));
                    } if(voltage<50){
                        circle2.setFill(Color.web("#1098F7"));
                        circle1.setFill(Color.web("#CED3DC"));
                    }
                    if ( counter++ >= 2 ) {
                        if ( !changed ) {
                            changed = true;
                            gauge.setOrientation(Orientation.VERTICAL);
                        }
                    }

                    lastTimerCall = now;

                }
            }
        };

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

//        grid.add(lineChart, 0, 1);
        StackPane pane = new StackPane(gauge);
        b1.setText("Ledcito");
        b1.setOnAction(event -> {
                comPort.writeBytes("Hola".getBytes(), 4);
        });
        t1.setText("Mayor a 50°C");
        circle1.setCenterX(300.0f);
        circle1.setCenterY(135.0f);
        circle1.setFill(Color.web("#CED3DC"));
        circle1.setRadius(10.0f);
        t2.setText("Menor a 50°C");
        circle2.setCenterX(300.0f);
        circle2.setCenterY(135.0f);
        circle2.setFill(Color.web("#CED3DC"));
        circle2.setRadius(10.0f);
        pane.setPadding(new Insets(20));
        pane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        grid.add(pane, 1, 1);
        gauge2 = GaugeBuilder.create()
                .title("Gauge")
                .subTitle("")
                .minValue(0)
                .maxValue(10)
                .value(0)
                .unit("Litros")
                .build();


        grid.add(gauge2,2,1);
//        grid.add(circle1,2,1);
//        grid.add(t1,3,1);
//        grid.add(circle2,4,1);
//        grid.add(t2,5,1);
//        grid.add(b1,6,1);


        primaryStage.setScene(new Scene(grid));
    }

    @Override
    public void start(Stage stage) throws URISyntaxException {
        stage.setTitle("Potenciometro");
        init(stage);
        stage.show();
        timer.start();
        executor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        AddToQueue addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }

    private class AddToQueue implements Runnable {
        public void run() {
            try {
                dataQ1.add(voltage);
//           dataQ1.add(randomN);

                Thread.sleep(300);
                executor.execute(this);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() {
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ1.isEmpty()) break;
            series1.getData().add(new XYChart.Data<>(xSeriesData++, dataQ1.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series1.getData().size() > MAX_DATA_POINTS) {
            series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS);
        }
        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);
    }

    @Override
    public void stop() {
//        comPort.closePort();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
