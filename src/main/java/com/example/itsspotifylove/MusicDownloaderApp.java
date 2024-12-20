package com.example.itsspotifylove;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MusicDownloaderApp extends Application {

    private static final String APIKEY = "AIzaSyBzVE5k6xoxz6_56f5Mj-t8PAnZL8Y5Q2Q";
    private TableView<String> resultsTable;
    private TextField searchField;
    private List<String> videoIds;
    private ObservableList<String> downloadedSongs;
    private VBox sidebar;
    private ProgressBar progressBar;
    private boolean sidebarVisible = true;
    private ComboBox<String> formatComboBox;
    // private static final String DOWNLOAD_FOLDER = "downloads/%(title)s.%(ext)s";
    private final ArrayList<String[]> credentials = new ArrayList<>();

    private final File credentialsFile = new File("credentials.txt");

    public void start(Stage primaryStage) {

        loadCredentialsFromFile();
        BorderPane bp = new BorderPane();

        Image image = new Image("file:D:\\Dawood\\Books\\COMsats\\2nd sem (Temporary ab say )\\OOP\\GUI\\itsspotifylove22\\itsspotifylove\\image.png"); // Update path if necessary
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(370);
        imageView.setFitHeight(364);
        imageView.setPreserveRatio(false);

        bp.setTop(imageView);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);



        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(new Font("Arial",  25));
        usernameLabel.setStyle(" -fx-text-fill:#FFD700 ; -fx-font-weight: bold;");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle(" -fx-text-fill:#FFD700 ; -fx-font-weight: bold;");
        passwordLabel.setFont(new Font("Arial",  25));
        PasswordField passwordField = new PasswordField();


        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold ;");
        loginButton.setPrefHeight(300);
        loginButton.setPrefWidth(150);

        Button saveButton = new Button("Sign up");
        saveButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        saveButton.setPrefHeight(300);
        saveButton.setPrefWidth(100);

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: black; -fx-font-weight: bold;");
        exitButton.setPrefHeight(300);
        exitButton.setPrefWidth(100);

        Label notificationLabel = new Label();
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);



        GridPane buttonPane = new GridPane();
        buttonPane.setHgap(30);
        buttonPane.setVgap(50);
        buttonPane.add(loginButton, 0, 2);
        buttonPane.add(saveButton, 1, 2);
        buttonPane.add(exitButton, 2, 2);
        gridPane.add(buttonPane, 0, 2, 2, 1);


        gridPane.add(notificationLabel, 0, 3, 2, 1);
        notificationLabel.setAlignment(Pos.CENTER);



        bp.setCenter(gridPane);
        bp.setStyle("-fx-background-color: #000000;");




        saveButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                saveCredentialsToFile(username, password);
                notificationLabel.setText("Credentials saved successfully!");
            } else {
                notificationLabel.setText("Username and password cannot be empty!");
            }
        });
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateCredentials(username, password)) {
                notificationLabel.setText("Login successful!");

                openMain(primaryStage);

            } else {
                notificationLabel.setText("Invalid username or password!");
            }
        });

        exitButton.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(bp, 800, 600);

        primaryStage.setTitle("Login Windowww");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    private void loadCredentialsFromFile() {
        try {
            if (!credentialsFile.exists()) {
                credentialsFile.createNewFile();
            }


            Scanner scanner = new Scanner(credentialsFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    credentials.add(parts);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading credentials: " + e.getMessage());
        }
    }

    private boolean validateCredentials(String username, String password) {

        for (String[] pair : credentials) {
            if (pair[0].equals(username) && pair[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void saveCredentialsToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            credentials.add(new String[]{username, password}); // Update in-memory list as well
        } catch (IOException e) {
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }




    public void openMain(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #121212;");
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #333333;");

        searchField = new TextField();
        searchField.setPromptText("Enter song name...");
        searchField.setStyle("-fx-background-color: #222222; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        searchField.setPrefWidth(450);


        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        searchButton.setOnAction(e -> searchSongs());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchSongs();
            }
        });

        progressBar = new ProgressBar(0); // Initialize progress bar
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-accent: #FFD700;");
        progressBar.setVisible(false); // Hidden by default

        topBar.getChildren().addAll(searchField, searchButton);


        resultsTable = new TableView<>();
        resultsTable.setPlaceholder(new Label("No results for now."));
        resultsTable.setStyle("-fx-background-color: white; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-color: #FFD700;");

        TableColumn<String, String> titleColumn = new TableColumn<>("Searched Results:");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        titleColumn.setPrefWidth(550);

        resultsTable.getColumns().add(titleColumn);


        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #282828; -fx-border-color: #FFD700;");
        sidebar.setPrefWidth(350);

        Label sidebarTitle = new Label("Downloaded Songs");
        sidebarTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFD700; -fx-font-weight: bold;");

        ListView<String> downloadedList = new ListView<>();

        downloadedSongs = FXCollections.observableArrayList(); // observable list
        downloadedList.setItems(downloadedSongs);

        downloadedList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedFilePath = downloadedList.getSelectionModel().getSelectedItem();
                if (selectedFilePath != null) {

                    File file = new File(selectedFilePath.replace("file:///", ""));
                    if (file.exists()) {

                        Media media = new Media(file.toURI().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        MediaView mediaView = new MediaView(mediaPlayer);

                        Button playButton = new Button("Play");
                        Button pauseButton = new Button("Pause");
                        Button stopButton = new Button("Stop");
                        playButton.setStyle("#FFD700");
                        pauseButton.setStyle("#FFD700");
                        stopButton.setStyle("#FFD700");

                        Slider progressSlider = new Slider();
                        progressSlider.setMin(0);
                        progressSlider.setValue(0);
                        progressSlider.setStyle("#FFD700");

                        Label timeLabel = new Label("0:00 / 0:00");


                        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                            double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                            progressSlider.setValue(progress * 100);

                            int currentMinutes = (int) newTime.toMinutes();
                            int currentSeconds = (int) (newTime.toSeconds() % 60);
                            int totalMinutes = (int) mediaPlayer.getTotalDuration().toMinutes();
                            int totalSeconds = (int) (mediaPlayer.getTotalDuration().toSeconds() % 60);
                            timeLabel.setText(String.format("%d:%02d / %d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds));
                        });


                        progressSlider.setOnMouseReleased(e -> {
                            double seekTime = progressSlider.getValue() / 100 * mediaPlayer.getTotalDuration().toSeconds();
                            mediaPlayer.seek(Duration.seconds(seekTime));
                        });

                        playButton.setOnAction(e -> mediaPlayer.play());
                        pauseButton.setOnAction(e -> mediaPlayer.pause());
                        stopButton.setOnAction(e -> mediaPlayer.stop());

                        HBox controls = new HBox(10, playButton, pauseButton, stopButton, progressSlider, timeLabel);
                        controls.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-alignment: center;");

                        BorderPane playerPane = new BorderPane();
                        playerPane.setCenter(mediaView);
                        playerPane.setBottom(controls);
                        playerPane.setStyle("-fx-background-color: black;");


                        Stage playerStage = new Stage();
                        Scene playerScene = new Scene(playerPane, 700, 500);
                        playerStage.setTitle("Playing: " + file.getName());
                        playerStage.setScene(playerScene);
                        playerStage.show();

                        mediaPlayer.play();

                        playerStage.setOnCloseRequest(e -> mediaPlayer.stop());
                    } else {
                        showAlert("Error", "Kuch Masla hai with this file " + file.getAbsolutePath(), Alert.AlertType.ERROR);

                    }
                }
            }
        });



        sidebar.getChildren().addAll(sidebarTitle, downloadedList);
        sidebar.setVisible(true);




        formatComboBox = new ComboBox<>();
        formatComboBox.getItems().addAll("MP3 (Audio)", "MP4 (Video)", "WAV");
        formatComboBox.setValue("MP3 (Audio)");
        formatComboBox.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        formatComboBox.setPrefWidth(150);

        Button downloadButton = new Button("Download Selected");
        downloadButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-weight: bold;");
        downloadButton.setOnAction(e -> downloadSelectedSong());

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: black; -fx-font-weight: bold;");
        exitButton.setOnAction(e -> stage.close());




        HBox bottomBar = new HBox(10, formatComboBox, downloadButton, exitButton , progressBar);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setStyle("-fx-background-color: #333333;");








        // Set layout sections
        root.setTop(topBar);
        root.setCenter(resultsTable);
        root.setBottom(bottomBar);
        root.setLeft(sidebar);

        loadDownloadedSongs();
        // Scene and stage setup
        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Music Downloader");
        stage.setScene(scene);
        stage.show();
    }

    private void loadDownloadedSongs() {
        File downloadDir = new File("downloads");

        // Filter out non-media files
        File[] files = downloadDir.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".mp4") || name.endsWith(".wav"));
        if (files != null) {
            for (File file : files) {
                downloadedSongs.add("file:///" + file.getAbsolutePath());
            }
        }
    }

    private String extractVideoIdFromUrl(String url) {
        try {
            if (url.contains("youtube.com/watch?v=")) {
                return url.split("v=")[1].split("&")[0];
            } else if (url.contains("youtu.be/")) {
                return url.split("youtu.be/")[1].split("[?&]")[0];
            }
        } catch (Exception e) {
            System.out.println("Invalid URL format: " + e.getMessage());
        }
        return null;
    }
    private void searchSongs() {
        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            showAlert("Error", "Search box cannot be empty.", Alert.AlertType.ERROR);
            return;
        }
        try {
            String searchUrl;
            String videoId = extractVideoIdFromUrl(search);
            if (videoId != null) {
                searchUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + videoId + "&key=" + APIKEY;
            } else {
                searchUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=50&q="
                        + URLEncoder.encode(search, "UTF-8") + "&key=" + APIKEY;
            }
            URL url = new URL(searchUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("GET");

            InputStreamReader isr= new InputStreamReader(connection.getInputStream()); //getting the binary data
            BufferedReader in = new BufferedReader(isr); //converting binary into characters
            String response = in.lines().collect(Collectors.joining()); //ye collecting all character into a single string
            //this response is actually wo wala JASON shapar, it looks like that screenshot
            in.close();
            List<String> results = parseYouTubeResponse(response);//wo jo items wali list return ho rhi hai, wo result wali list mn store ho rhi hai
            resultsTable.setItems(FXCollections.observableArrayList(results));

        } catch (Exception e) {
            showAlert("Error", "Failed to fetch results: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private List<String> parseYouTubeResponse(String jsonResponse) { // method is returning a string type ki list.
        videoIds = new ArrayList<>();
        List<String> titles = new ArrayList<>();// yehi to hmara function return kray ga

        JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        if (responseObject.has("items")) {
            JsonArray items = responseObject.getAsJsonArray("items");

            for (JsonElement item : items) {
                JsonObject itemObject = item.getAsJsonObject();

                String videoId;
                String title;

                if (itemObject.has("id") && itemObject.get("id").isJsonObject()) {
                    videoId = itemObject.getAsJsonObject("id").get("videoId").getAsString();
                } else {
                    videoId = itemObject.get("id").getAsString();
                }

                title = itemObject.getAsJsonObject("snippet").get("title").getAsString();

                videoIds.add(videoId);
                titles.add(title);
            }
        }


/*
// ab us jason object mn say we getting ITEMS wali array. jis mn objects of VIDEO ID and Title/discription etc sab kuchhhh!!.
        JsonArray items = responseObject.getAsJsonArray("items"); //items has the JASON type ki array
        for (JsonElement item : items) {//JasonEelement is generic, it can store jason type arrays objects etc


            // this converts item array into item object
            JsonObject itemObject = item.getAsJsonObject();

            // now finally we can extract the string type data from the jason objects.

            String videoId = itemObject.getAsJsonObject("id").get("videoId").getAsString();
            String title = itemObject.getAsJsonObject("snippet").get("title").getAsString();



            videoIds.add(videoId);
            titles.add(title);

        }

        // we get raw JASON string
        // we convert that raw JASON string into Jason object
        // then we convert that JASON object to JASON type Array
        // us JASON type array k andar hain mazeeeeeedd Jason type k objects i.e videoID and title
        // we convert those JASON type k objects i.e videID and title into Strings.

*/
        return titles;
    }



    private void downloadSelectedSong() {
        String selectedSong = resultsTable.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            showAlert("No Selection", "Please select a song to download.", Alert.AlertType.WARNING);
            return;
        }

        String selectedFormat = formatComboBox.getValue();
        if (selectedFormat == null) {
            showAlert("No Format", "Please select a format to download.", Alert.AlertType.WARNING);
            return;
        }

        progressBar.setVisible(true); // Show progress bar when download starts
        progressBar.setProgress(0);

        // Resolve format arguments for yt-dlp
        String formatFlag;
        String formatOption;
        String fileExtension;

        if (selectedFormat.equals("MP3 (Audio)")) {
            formatFlag = "-x"; // Extract audio
            formatOption = "--audio-format=mp3";
            fileExtension = "mp3";
        } else if (selectedFormat.equals("WAV")) {
            formatFlag = "-x";
            formatOption = "--audio-format=wav";
            fileExtension = "wav";
        } else if (selectedFormat.equals("MP4 (Video)")) {
            formatFlag = "-f";
            formatOption = "mp4";
            fileExtension = "mp4";
        } else {
            showAlert("Invalid Format", "Unsupported format selected.", Alert.AlertType.ERROR);
            return;
        }
        int selectedIndex = resultsTable.getSelectionModel().getSelectedIndex();
        String videoId = videoIds.get(selectedIndex);

        String outputFolder = "downloads/";
        String outputFilePath = outputFolder + selectedSong + "." + fileExtension;

        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                new File(outputFolder).mkdirs();

                ProcessBuilder processBuilder = new ProcessBuilder(
                        "yt-dlp",
                        formatFlag,
                        formatOption,
                        "--ffmpeg-location", "C:\\Program Files (x86)\\ffmpeg-7.1-full_build\\bin\\ffmpeg.exe",
                        "https://www.youtube.com/watch?v=" + videoId,
                        "-o", outputFilePath
                );

                Map<String, String> env = processBuilder.environment();
                String currentPath = env.get("PATH");
                env.put("PATH", currentPath + ";C:\\Program Files (x86)\\yt-dlp");

                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("%")) {
                            double progress = parseProgress(line);
                            updateProgress(progress, 100); // Update Task progress
                        }
                        System.out.println(line); // Debug output
                    }
                }

                process.waitFor(); // Wait until process completes
                return null;
            }

            @Override
            protected void succeeded() {
                downloadedSongs.add("file:///" + new File("downloads/" + selectedSong + "." + selectedFormat.split(" ")[0].toLowerCase()).getAbsolutePath());
                progressBar.progressProperty().unbind();
                progressBar.setVisible(false);
                showAlert("Success", "Downloaded: " + selectedSong + " in " + selectedFormat, Alert.AlertType.INFORMATION);
            }

            @Override
            protected void failed() {
                progressBar.setVisible(false);
                progressBar.progressProperty().unbind();
                showAlert("Error", "Failed to download the song.", Alert.AlertType.ERROR);
            }
        };

        // Bind progress bar
        progressBar.progressProperty().bind(downloadTask.progressProperty());

        // Run task in a background thread
        Thread downloadThread = new Thread(downloadTask);
        downloadThread.setDaemon(true);
        downloadThread.start();
    }

    // Helper method to parse progress percentage from yt-dlp output
    private double parseProgress(String line) {
        try {
            int percentIndex = line.indexOf('%');
            if (percentIndex > 0) {
                String progressStr = line.substring(percentIndex - 4, percentIndex).trim(); // Extract progress
                return Double.parseDouble(progressStr);
            }
        } catch (Exception e) {
            System.out.println("Error parsing progress: " + e.getMessage());
        }
        return 0.0;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
