package org.example;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXClock extends Application {
	VBox vBox;
	VBox root;
	Pane pane;
	HBox buttonPanel;
	HBox alarmPanel;
	HBox alarmButtonPanel;
	Canvas canvas;
	GraphicsContext gc;
	Scene scene;
	AnimationTimer widgetLoop;
	Timeline alarmTimer;
	Text text;
	Text digitText;
	LocalTime timeNow;
	Button startStop;
	Button alarmButton;
	Label alarmLabel;
	Label hoursLabel;
	Label minutesLabel;
	AudioClip alarm;
	ComboBox<Integer> hoursComboBox;
	ComboBox<Integer> minutesComboBox;
	List<Integer> chooseHours;
	List<Integer> chooseMinutes;
	String s;
	String[] clockDigits;
	private long lastUpdate = 0;
	int SPEED = 1000;
	int dotCount = 60;
	int dotCount1 = 12;
	int dotSize = 5;
	int divition = 0;
	int secs=660;
	int hoursChooser;
	int minutesChooser;
	double arrowAngle = Math.toRadians(-90);
	double minutesArrowAngle = Math.toRadians(-90);
	double hoursArrowAngle = Math.toRadians(-90);
	double alarmArrowAngle = Math.toRadians(-90);
	double arrowX;
	double arrowY;
	double stopAlarm;
	double accamulator = 0;
	double secondsAccum = 0;
	double stableSecond = 0;
	boolean dotFlag = false;
	boolean buttonFlag = false;
	boolean setTimer = false;
	boolean alarmTimerFlag = false;
	boolean smoothArrowMove = true;
	public void start(Stage primaryStage) {
		primaryStage.setTitle("LoadingFX");
		canvas = new Canvas(500,500);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	    chooseHours = new ArrayList<>();
		for(int i=0;i<24;i++) {
			chooseHours.add(i);
		}
		chooseMinutes = new ArrayList<>();
		for(int i=0;i<60;i++) {
			chooseMinutes.add(i);
			//System.out.println(chooseMinutes.get(i));
		}
		//System.out.println(chooseMinutes);
		clockDigits = new String[] {"12","1","2","3","4","5","6","7","8","9","10","11"};
//		vBox = new VBox(canvas);
//		vBox.setBackground(new Background(new BackgroundFill(Color.rgb(244, 244, 244),null,null)));
		//vBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0),null,null)));
		System.out.println(canvas.getWidth()+" "+canvas.getHeight());
		pane = new Pane(canvas);
		//pane.setPrefSize(500, 500);
		alarmLabel = new Label("");
		alarmLabel.setFont(Font.font("Arial",FontWeight.BOLD,20));
		alarmLabel.setTextFill(Color.RED);
		alarmTimer = new Timeline(new KeyFrame(Duration.millis(500),e->{
			if(!alarmTimerFlag) {
				alarmLabel.setText("Alarm!");
				alarmTimerFlag = true;
			}else {
				alarmLabel.setText("");
				alarmTimerFlag = false;
			}
		}));
		alarmTimer.setCycleCount(Animation.INDEFINITE);
		HBox.setMargin(alarmLabel, new Insets(0,0,10,0));
		startStop = new Button("Start");
		//startStop.setAlignment(Pos.CENTER_LEFT);
		startStop.setFont(Font.font("Arial",FontWeight.BOLD,20));
		//startStop.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0),null,null)));
		startStop.setOnAction(e->{
			if(!buttonFlag) {
				startStop.setText("Stop");
				buttonFlag = true;
				//lastUpdate = 0;
				widgetLoop.start();	
			}else {
				startStop.setText("Start");
				lastUpdate = 0;
				if(alarmTimer.getStatus()==Animation.Status.RUNNING) {
					alarmTimer.stop();
					alarmLabel.setText("");
				}
				widgetLoop.stop();
				
				buttonFlag = false;
			}
		});
		startStop.setFocusTraversable(false);
		
		//Label testLabel = new Label("testLabel");
		//testLabel.setFont(Font.font("Arial",FontWeight.BOLD,20));
		//testLabel.getStyleClass().add("button");
		hoursLabel = new Label("H");
		hoursLabel.setFont(Font.font("Arial",FontWeight.BOLD,20));
		hoursLabel.getStyleClass().add("button");
		hoursComboBox = new ComboBox<>();
		for(int i=0;i<24;i++) {
			hoursComboBox.getItems().add(chooseHours.get(i));
		}
		hoursComboBox.setCellFactory(list->new ListCell<>() {
			@Override
			protected void updateItem(Integer item,boolean empty) {
				super.updateItem(item,empty);
				if(empty||item == null) {
					setText(null);
				}else {
					setText(item.toString());
					setFont(Font.font("Arial",FontWeight.BOLD, 20));
					setAlignment(Pos.CENTER);
				}
			}
		});
		hoursComboBox.setButtonCell(new ListCell<>() {
			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item,empty);
				if(empty||item == null) {
					setText(null);
				}else {
					setText(item.toString());
					setFont(Font.font("Arial",FontWeight.BOLD, 20));
					setAlignment(Pos.CENTER);
					
				}
			}
		});
		//hoursComboBox.setPrefWidth(60);
		//hoursComboBox.setMaxWidth(60);
//		hoursComboBox.setMinWidth(Region.USE_COMPUTED_SIZE);
//		hoursComboBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
		hoursComboBox.setOnAction(e->{
			hoursChooser = hoursComboBox.getSelectionModel().getSelectedIndex();
			if(setTimer) {
				stopAlarm();
			}
			
		});
		hoursComboBox.getSelectionModel().select(0);
		hoursChooser = hoursComboBox.getSelectionModel().getSelectedIndex();
		hoursComboBox.setFocusTraversable(false);
		hoursComboBox.setVisibleRowCount(5);
		minutesLabel = new Label("M");
		minutesLabel.setFont(Font.font("Arial",FontWeight.BOLD,20));
		minutesLabel.getStyleClass().add("button");
		minutesComboBox = new ComboBox<>();
		for(int i=0;i<60;i++) {
			minutesComboBox.getItems().add(chooseMinutes.get(i));
		}
		minutesComboBox.setCellFactory(list->new ListCell<>() {
			@Override
			protected void updateItem(Integer item,boolean empty) {
				super.updateItem(item,empty);
				if(empty||item == null) {
					setText(null);
				}else {
					setText(item.toString());
					setFont(Font.font("Arial",FontWeight.BOLD, 20));
					setAlignment(Pos.CENTER);
				}
			}
		});
		minutesComboBox.setButtonCell(new ListCell<>() {
			@Override
			public void updateItem(Integer item, boolean empty) {
				super.updateItem(item,empty);
				if(empty||item == null) {
					setText(null);
				}else {
					setText(item.toString());
					setFont(Font.font("Arial",FontWeight.BOLD, 20));
					setAlignment(Pos.CENTER);
					
				}
			}
		});
		minutesComboBox.setOnAction(e->{
			minutesChooser = minutesComboBox.getSelectionModel().getSelectedIndex();
			if(setTimer) {
				stopAlarm();
			}
		});
		minutesComboBox.getSelectionModel().select(0);
		minutesChooser = minutesComboBox.getSelectionModel().getSelectedIndex();
		minutesComboBox.setFocusTraversable(false);
		minutesComboBox.setVisibleRowCount(5);
		alarmButton = new Button("Set Alarm");
		alarmButton.setFont(Font.font("Arial",FontWeight.BOLD,20));
		alarmButton.setOnAction(new EventHandler<ActionEvent>(){
			//boolean setTimer = false;
			@Override
			public void handle(ActionEvent e) {
				if(!setTimer) {
					alarmButton.setText("Stop Alarm");
					int hours = ((hoursChooser%12)*60)*60;
					int minutes = minutesChooser*60;
					int common = hours+minutes;
					alarmArrowAngle = Math.toRadians(-90)+common*(((2*Math.PI/60)/60)/12);
					stopAlarm = alarmArrowAngle+Math.toRadians(0.5);
					setTimer = true;
					paintMethod();
					
				}else {
					alarmButton.setText("Set Alarm");
					setTimer = false;
					if(alarmTimer.getStatus()==Animation.Status.RUNNING) {
						alarmTimer.stop();
						alarm.stop();
						alarmLabel.setText("");
					}
				}
			}
		});
		alarmButton.setFocusTraversable(false);
		alarmPanel = new HBox();
		alarmPanel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0),null,null)));
		alarmPanel.getChildren().add(alarmLabel);
		//alarmPanel.getChildren().add(testLabel);
		alarmPanel.setAlignment(Pos.CENTER);
		
		buttonPanel = new HBox();
		buttonPanel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0),null,null)));
		buttonPanel.getChildren().add(startStop);
		buttonPanel.setAlignment(Pos.CENTER);
		alarmButtonPanel = new HBox();
		alarmButtonPanel.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0),null,null)));
		alarmButtonPanel.getChildren().add(hoursLabel);
		alarmButtonPanel.getChildren().add(hoursComboBox);
		alarmButtonPanel.getChildren().add(minutesLabel);
		alarmButtonPanel.getChildren().add(minutesComboBox);
		alarmButtonPanel.getChildren().add(alarmButton);
		alarmButtonPanel.setAlignment(Pos.CENTER);
		root = new VBox();
		root.getChildren().add(pane);
		root.getChildren().add(alarmPanel);
		root.getChildren().add(alarmButtonPanel);
		root.getChildren().add(buttonPanel);
		VBox.setVgrow(pane, Priority.ALWAYS);
		root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0),null,null)));
		scene = new Scene(root,500,500);
		scene.setCamera(new PerspectiveCamera());
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		canvas.widthProperty().addListener((obs,oldVal,newVal)->{System.out.println(newVal);paintMethod();});
		canvas.heightProperty().addListener((obs,oldVal,newVal)->{System.out.println(newVal);paintMethod();});
		s = new String("Loading...");
		text = new Text(s);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		preloadAudio();
		System.out.println(canvas.getWidth()+" "+canvas.getHeight());
		LocalTime timeNow = LocalTime.now();
		double seconds = timeNow.getSecond();
		double minutes = timeNow.getMinute();
		double hour = timeNow.getHour()%12;
		if(hour == 0) hour=12;
		alarmArrowAngle += 10320*(((2*Math.PI/60)/60)/12);
		System.out.println("alarmArrowAngle "+Math.toDegrees(alarmArrowAngle));
		stopAlarm = alarmArrowAngle+Math.toRadians(0.5);
		//alarmArrowAngle += 720000*((((2*Math.PI/60)/60)/12)/1000);
		//alarmArrowAngle +=720000*(((2*Math.PI/60)/60)/60);
		System.out.println("Alarm arrow angle "+alarmArrowAngle);
		double alarmToDegrees = Math.toDegrees(alarmArrowAngle);
		System.out.println("Degrees "+alarmToDegrees);
		double var1 = 10.55;
		double var2 = 5.55;
		double summ = (var1+var2)%10;
		System.out.println(summ);
//		arrowAngle = Math.toRadians(-90+(seconds*6));
//		minutesArrowAngle = Math.toRadians(-90+(minutes+seconds/60)*6);
//		hoursArrowAngle = Math.toRadians(-90+(hour+minutes/60)*30);
//		paintMethod();
//		int time = (((LocalTime.now().getHour()%12)+(LocalTime.now().getMinute())/60)*30);
//		System.out.println(time);
		widgetLoop = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				//if(now-lastUpdate>=100*1_000_000) {
					if(lastUpdate == 0) {
						lastUpdate = now;
						return;
						
					}
					LocalTime timeNow = LocalTime.now();
					double nanoSeconds = timeNow.getNano();
					double seconds = timeNow.getSecond();
					double minutes = timeNow.getMinute();
					double hour = timeNow.getHour()%12;
					//if(hour == 0) hour=12;
					double deltaSeconds = (now-lastUpdate)/1_000_000_000.0;
					//System.out.println((now-lastUpdate)/1000000+" ms");
					//System.out.println(nanoSeconds/1000000000);
					double smoothSeconds = seconds+nanoSeconds/1000000000;
					//System.out.println(deltaSeconds);
					//System.out.println(seconds);
					/*arrowAngle = Math.toRadians(-90)+(seconds*(2*Math.PI/60));
					double forMinutes = seconds*((2*Math.PI/60)/60);
					minutesArrowAngle = Math.toRadians(-90)+minutes*(2*Math.PI/60)+forMinutes;
					double forHours = minutes*((2*Math.PI/60)/12);
					hoursArrowAngle = Math.toRadians(-90)+hour*(2*Math.PI/12)+forHours;*/
					arrowAngle = Math.toRadians(-90+(smoothSeconds*6));
					minutesArrowAngle = Math.toRadians(-90+(minutes+seconds/60)*6);
					hoursArrowAngle = Math.toRadians(-90+(hour+minutes/60)*30);
					//hoursArrowAngle = Math.toRadians(0);
					//System.out.println("Hours "+hour);
					//paintMethod();
					/*if(!smoothArrowMove) {
						accamulator+=deltaSeconds;
						//System.out.println(accamulator);
						if(accamulator>=1) {
							arrowAngle+=(2*Math.PI/60);
							accamulator-=1;
						}
					}else {
						arrowAngle+=((2*Math.PI/60)*deltaSeconds);
					}
					//System.out.println(Math.toDegrees(arrowAngle));
					arrowAngle%=(2*Math.PI);
//					if(arrowAngle >= Math.toRadians(270)) {
//						arrowAngle=Math.toRadians(-90);
//					}
					
					
					minutesArrowAngle+=((2*Math.PI/60)/60)*deltaSeconds;
					minutesArrowAngle%=(2*Math.PI);
//					if(minutesArrowAngle>= Math.toRadians(270)) {
//						minutesArrowAngle = Math.toRadians(-90);
//					}
					hoursArrowAngle+=(((2*Math.PI/60)/60)/12)*deltaSeconds;
					hoursArrowAngle%=(2*Math.PI);
//					if(hoursArrowAngle>= Math.toRadians(270)) {
//						hoursArrowAngle = Math.toRadians(-90);
//					}*/
					if(setTimer) {
//						System.out.println(Math.toDegrees(hoursArrowAngle)+" "+Math.toDegrees(alarmArrowAngle));
//						double EPSILON = Math.toRadians(0.25);
//						//System.out.println("Starts alarm");
//						if (hoursArrowAngle>=alarmArrowAngle&&hoursArrowAngle<stopAlarm) {
//							System.out.println("Running");
//							if(!alarm.isPlaying()) {
//								alarm.play();
//							}
//							if(alarmTimer.getStatus()!=Animation.Status.RUNNING) {
//								alarmTimer.play();
//							}
//						}else {
//							alarmLabel.setText("");
//							alarmTimer.stop();
//						}
						double current = (hoursArrowAngle + 2 * Math.PI) % (2 * Math.PI);
						double start = (alarmArrowAngle + 2 * Math.PI) % (2 * Math.PI);
						double end = (stopAlarm + 2 * Math.PI) % (2 * Math.PI);

						boolean alarmReached;

						if (start <= end) {
						    alarmReached = current >= start && current < end;
						} else {
						    alarmReached = current >= start || current < end;
						}
						if (alarmReached) {

					        if (!alarm.isPlaying()) {
					            alarm.play();
					        }

					        if (alarmTimer.getStatus() != Animation.Status.RUNNING) {
					            alarmTimer.play();
					        }

					    } else {
					        alarmLabel.setText("");
					        alarmTimer.stop();
					    }
						
					}
					paintMethod();
					lastUpdate = now;
				//}
			}
		};
		//widgetLoop.start();
		
		
	}
//	public void widgetLoop() {
//		
//	}
	public void preloadAudio() {
		alarm = new AudioClip(getClass().getResource("/My_Song.wav").toExternalForm());
		alarm.setVolume(0.0);
		alarm.play();
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(100),e->{
			alarm.stop();
			alarm.setVolume(0.1);
		}));
		timer.play();
	}
	private double normalizeAngle(double angle) {
	    return (angle % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI);
	}
	public void stopAlarm() {
		alarmButton.setText("Set Alarm");
		setTimer = false;
		if(alarmTimer.getStatus()==Animation.Status.RUNNING) {
			alarmTimer.stop();
			alarm.stop();
			alarmLabel.setText("");
		}

	}
	public void digits(double cx,double cy, double r) {
		Font digitFont = Font.font("Arial", FontWeight.BOLD, 20);
	    gc.setFont(digitFont);
	    gc.setFill(Color.WHITE);
		for (int i = 0;i<dotCount1;i++) {
			digitText = new Text(clockDigits[i]);
			digitText.setFont(digitFont);
			double textWidth = digitText.getLayoutBounds().getWidth();
			double ascent = -digitText.getLayoutBounds().getMinY();
			double dscent = digitText.getLayoutBounds().getMaxY();
			double textHeight = ascent+dscent;
			//double readDigitSize = Math.max(textWidth, textHeight);
			double correctAngle = Math.toRadians(-90)+(i*(2*Math.PI/dotCount1));
			double x = cx+Math.cos(correctAngle)*(r-15);
			double y = cy+Math.sin(correctAngle)*(r-15);
			//gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText(clockDigits[i], x-(textWidth/2.0), y+(ascent/2.0-dscent/2.0));
		}
	}
	public void minutesArrow(double cx,double cy,double r) {
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(4);
		double arrowX = cx+Math.cos(minutesArrowAngle)*r;
		double arrowY = cy+Math.sin(minutesArrowAngle)*r;
		gc.strokeLine(cx, cy, arrowX, arrowY);
		
	}
	public void hoursArrow(double cx,double cy,double r) {
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(6);
		double hoursRadius = r-15;
		double arrowX = cx+Math.cos(hoursArrowAngle)*hoursRadius;
		double arrowY = cy+Math.sin(hoursArrowAngle)*hoursRadius;
		gc.strokeLine(cx, cy, arrowX, arrowY);
	}
	public void alarmArrow(double cx, double cy, double r) {
		gc.setStroke(Color.YELLOW);
		gc.setLineWidth(2);
		double alarmRadius = r-15;
		double arrowX = cx+Math.cos(alarmArrowAngle)*alarmRadius;
		double arrowY = cy+Math.sin(alarmArrowAngle)*alarmRadius;
		gc.strokeLine(cx, cy, arrowX, arrowY);
	}
	public void paintMethod() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		text.setFont(Font.font("Arial",FontWeight.BOLD,20));
//		System.out.println(text.getLayoutBounds().getHeight());
		double ascent = -text.getLayoutBounds().getMinY();
		double dscent = text.getLayoutBounds().getMaxY();
		double textWidth = text.getLayoutBounds().getWidth();
		double textHeight = ascent+dscent;
//		System.out.println("ascent and dscent and textWidth and textHeight: "+ascent+" "+dscent+" "+textWidth+" "+textHeight);
		gc.setFill(Color.WHITE);
		gc.setFont(Font.font("Arial",FontWeight.BOLD,20));
		//gc.fillText(s, (canvas.getWidth()/2.0)-(textWidth/2.0), (canvas.getHeight()/2.0)+(ascent/2.0)-(dscent/2.0));
		double cx = canvas.getWidth()/2.0;
		double cy = canvas.getHeight()/2.0;
		double radius = Math.max(textHeight, textWidth);
		for(int i=0;i<dotCount;i++) {
			double correctAngle = Math.toRadians(-90)+(i*(2*Math.PI/dotCount));
//			System.out.println(Math.toRadians(15));
//			System.out.println("Angle, Cosinus and Sinus "+correctAngle+" "+Math.cos(correctAngle)+" "+Math.sin(correctAngle));
			double x = cx +(Math.cos(correctAngle)*radius);
			double y = cy +(Math.sin(correctAngle)*radius);
			if (!dotFlag) {
				dotSize = 10;
				dotFlag = !dotFlag;
			}else {
				dotSize = 10;
				dotFlag = !dotFlag;
			}
			//if(correctAngle<= Math.toRadians(180)) {
			if(divition==0) {
				gc.setFill(Color.rgb(0, 255, 0));
				gc.fillOval(x-dotSize/2, y-dotSize/2, dotSize, dotSize);
			}else {
				gc.setStroke(Color.WHITE);
				gc.setLineWidth(1);
				double x1 = cx + Math.cos(correctAngle) * (radius);
				double y1 = cy + Math.sin(correctAngle) * (radius);
				double x2 = cx +(Math.cos(correctAngle)*(radius+dotSize/2));
				double y2 = cy +(Math.sin(correctAngle)*(radius+dotSize/2));
				
				gc.strokeLine(x1, y1, x2, y2);
			//}
			}
			divition = (i+1)%5;
		}
		//dotFlag = !dotFlag;
		digits(cx,cy,radius);
		minutesArrow(cx,cy,radius);
		hoursArrow(cx,cy,radius);
		alarmArrow(cx,cy,radius);
		gc.setStroke(Color.rgb(0, 255, 0));
		gc.setLineWidth(1);
		//arrowAngle = Math.toRadians(-90);
		arrowX = cx+(Math.cos(arrowAngle)*radius);
		arrowY = cy+(Math.sin(arrowAngle)*radius);
		gc.strokeLine(cx, cy, arrowX, arrowY);
		
		
		
	}

	public static void main(String[] args) {
		FXClock.launch(args);

	}

}
