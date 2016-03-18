package com.kotcrab.vis.editor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class CrashReporter extends Application {
	private static final String PATH = "http://apps.kotcrab.com/vis/crashes/report.php";

	private static String restartCommand;
	private static File reportFile;
	private static String report;

	@FXML private TextArea detailsTextArea;
	@FXML private HBox buttonsBox;
	@FXML private HBox progressBox;

	public static void main (String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Invalid args, exiting.");
			System.exit(0);
		}

		restartCommand = args[0].replace("%", "\"");
		reportFile = new File(args[1]);
		if (reportFile.exists() == false) {
			System.out.println("Report file does not exist: " + args[1]);
			System.exit(0);
		}

		report = new String(Files.readAllBytes(reportFile.toPath()));

		launch(args);
	}

	@Override
	public void start (Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("crash-reporter-layout.fxml"));

		Scene scene = new Scene(root);

		stage.setTitle("VisEditor Crash Reporter");
		stage.getIcons().add(new Image(CrashReporter.class.getResourceAsStream("icon.png")));
		stage.setScene(scene);
		stage.setResizable(false);
		stage.sizeToScene();
		stage.show();
	}

	@FXML
	private void handleSend (ActionEvent event) {
		buttonsBox.setDisable(true);
		sendReport();
	}

	@FXML
	private void handleRestart (ActionEvent event) {
		buttonsBox.setDisable(true);
		restart();
		Platform.exit();
	}

	@FXML
	private void handleSendAndRestart (ActionEvent event) {
		buttonsBox.setDisable(true);
		sendReport();
		restart();
	}

	@FXML
	private void handleShowReport (ActionEvent event) throws IOException {
		new Thread(() -> {
			try {
				Desktop.getDesktop().open(reportFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void restart () {
		new Thread(() -> {
			try {
				CommandLine cmdLine = CommandLine.parse(restartCommand);
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void sendReport () {
		progressBox.setVisible(true);

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call () {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(PATH + "?filename=" + reportFile.getName()).openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("POST");
					OutputStream os = connection.getOutputStream();

					os.write(report.getBytes());
					os.write(("Details: " + detailsTextArea.getText()).getBytes());
					os.flush();
					os.close();

					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

					String s;
					while ((s = in.readLine()) != null)
						System.out.println("Server response: " + s);
					in.close();

					System.out.println("Crash report sent");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded () {
				Platform.exit();
			}

			@Override
			protected void failed () {
				Platform.exit();
			}
		};
		new Thread(task).start();
	}
}
