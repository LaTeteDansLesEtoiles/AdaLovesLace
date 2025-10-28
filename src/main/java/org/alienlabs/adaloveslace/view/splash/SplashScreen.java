package org.alienlabs.adaloveslace.view.splash;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.util.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javafx.animation.Animation.INDEFINITE;
import static org.alienlabs.adaloveslace.App.ASSETS_DIRECTORY;
import static org.alienlabs.adaloveslace.App.resourceBundle;

/**
 * Splash screen JavaFX sans FXML
 */
public class SplashScreen {
    
    private static final Logger logger = LoggerFactory.getLogger(SplashScreen.class);
    
    private Stage splashStage;
    private App mainApp;
    private ProgressBar progressBar;
    private Label statusLabel;
    private Circle dot1;
    private Circle dot2;
    private Circle dot3;

    public void show(App app) {
        this.mainApp = app;
        
        try {
            // Créer la scène du splash screen
            Scene splashScene = createSplashScene();
            
            // Créer la fenêtre du splash screen
            splashStage = new Stage();
            splashStage.initStyle(StageStyle.UNDECORATED);
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.setAlwaysOnTop(true);
            splashStage.show();
            
            // Démarrer les animations
            startAnimations();
            
            // Simuler le chargement de l'application
            simulateLoading();
            
        } catch (Exception e) {
            logger.error("Error creating splash screen", e);
            // En cas d'erreur, démarrer directement l'application
            app.showMainApplication();
        }
    }
    
    private Scene createSplashScene() {
        // Conteneur principal
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #667eea;");
        
        // Fond avec coins arrondis
        Rectangle background = new Rectangle(600, 400);
        background.setFill(createGradientBackground());
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setStroke(Color.rgb(255, 255, 255, 0.2));
        background.setStrokeWidth(2);
        
        // Contenu principal
        VBox content = createMainContent();
        
        // Animation des points de chargement
        VBox animationContainer = createLoadingAnimation();
        
        root.getChildren().addAll(background, content, animationContainer);
        
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.TRANSPARENT);
        
        return scene;
    }
    
    private LinearGradient createGradientBackground() {
        return new LinearGradient(
            0, 0, 1, 1, true, null,
            new Stop(0, Color.rgb(102, 126, 234)),
            new Stop(1, Color.rgb(118, 75, 162))
        );
    }
    
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        
        // Logo
        ImageView logoImageView = createLogo();
        
        // Titre
        Label titleLabel = createTitle();
        
        // Sous-titre
        Label subtitleLabel = createSubtitle();
        
        // Barre de progression et statut
        VBox progressContainer = createProgressContainer();
        
        // Informations de version
        HBox versionInfo = createVersionInfo();
        
        // Copyright
        Label copyrightLabel = createCopyright();
        
        content.getChildren().addAll(
            logoImageView,
            titleLabel,
            subtitleLabel,
            progressContainer,
            versionInfo,
            copyrightLabel
        );
        
        return content;
    }
    
    private ImageView createLogo() {
        ImageView logoImageView = new ImageView();
        try {
            Image logoImage = new Image(
                    getClass().getResourceAsStream(
                            ASSETS_DIRECTORY + "splashscreen.jpg"
                    )
            );
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            logger.warn("Could not load logo image, using fallback", e);
            // Créer un logo de remplacement simple
            logoImageView = createFallbackLogo();
        }
        
        logoImageView.setFitHeight(200);
        logoImageView.setFitWidth(200);
        logoImageView.setPreserveRatio(true);
        logoImageView.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 5);"
        );
        
        return logoImageView;
    }
    
    private ImageView createFallbackLogo() {
        // Créer une image simple avec un cercle
        ImageView fallbackLogo = new ImageView();
        
        // Créer un Canvas pour dessiner un logo simple
        Canvas canvas = new Canvas(120, 120);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Dessiner un cercle blanc avec bordure
        gc.setFill(Color.WHITE);
        gc.fillOval(15, 15, 100, 100);
        gc.setStroke(Color.rgb(102, 126, 234));
        gc.setLineWidth(3);
        gc.strokeOval(15, 15, 100, 100);
        
        // Dessiner les initiales "AL"
        gc.setFill(Color.rgb(102, 126, 234));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("ALL", 45, 70);
        
        // Convertir le Canvas en Image
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image fallbackImage = canvas.snapshot(params, null);
        fallbackLogo.setImage(fallbackImage);
        
        return fallbackLogo;
    }
    
    private Label createTitle() {
        Label titleLabel = new Label("AdaLovesLace");
        titleLabel.setFont(Font.font("Patrick Hand", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);"
        );
        
        return titleLabel;
    }
    
    private Label createSubtitle() {
        Label subtitleLabel = new Label();
        try {
            subtitleLabel.setText(resourceBundle.getString("SplashSubtitle"));
        } catch (Exception e) {
            subtitleLabel.setText("Application de création de diagrammes de dentelle");
        }
        
        subtitleLabel.setFont(Font.font("Patrick Hand", 16));
        subtitleLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));
        subtitleLabel.setStyle("-fx-font-style: italic;");
        
        return subtitleLabel;
    }
    
    private VBox createProgressContainer() {
        VBox progressContainer = new VBox(10);
        progressContainer.setAlignment(Pos.CENTER);
        
        // Barre de progression
        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(8);
        progressBar.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.3);" +
            "-fx-background-radius: 4;" +
            "-fx-border-radius: 4;" +
            "-fx-accent: #4CAF50;"
        );
        
        // Label de statut
        statusLabel = new Label();
        try {
            statusLabel.setText(resourceBundle.getString("LoadingApplication"));
        } catch (Exception e) {
            statusLabel.setText("Chargement de l'application...");
        }
        
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Patrick Hand", 14));
        
        progressContainer.getChildren().addAll(progressBar, statusLabel);
        
        return progressContainer;
    }
    
    private HBox createVersionInfo() {
        HBox versionInfo = new HBox(10);
        versionInfo.setAlignment(Pos.CENTER);
        
        Label versionLabel = new Label("Version 1.0.17");
        versionLabel.setTextFill(Color.rgb(255, 255, 255, 0.8));
        versionLabel.setFont(Font.font("Patrick Hand", 12));
        
        Label separator = new Label("|");
        separator.setTextFill(Color.rgb(255, 255, 255, 0.6));
        separator.setFont(Font.font("Patrick Hand", 12));
        
        Label javaVersionLabel = new Label("Java " + SystemInfo.javaVersion());
        javaVersionLabel.setTextFill(Color.rgb(255, 255, 255, 0.8));
        javaVersionLabel.setFont(Font.font("Patrick Hand", 12));
        
        versionInfo.getChildren().addAll(versionLabel, separator, javaVersionLabel);
        
        return versionInfo;
    }
    
    private Label createCopyright() {
        Label copyrightLabel = new Label();
        try {
            copyrightLabel.setText(resourceBundle.getString("Copyright"));
        } catch (Exception e) {
            copyrightLabel.setText("© 2023 - 2025 AlienLabs");
        }
        
        copyrightLabel.setTextFill(Color.rgb(255, 255, 255, 0.7));
        copyrightLabel.setFont(Font.font("Patrick Hand", 11));
        
        return copyrightLabel;
    }
    
    private VBox createLoadingAnimation() {
        VBox animationContainer = new VBox();
        animationContainer.setAlignment(Pos.BOTTOM_CENTER);
        animationContainer.setPadding(new Insets(20));
        
        HBox dotsContainer = new HBox(5);
        dotsContainer.setAlignment(Pos.CENTER);
        
        // Créer les points de chargement
        dot1 = new Circle(4, Color.WHITE);
        dot2 = new Circle(4, Color.WHITE);
        dot3 = new Circle(4, Color.WHITE);
        
        dot1.setOpacity(0.3);
        dot2.setOpacity(0.3);
        dot3.setOpacity(0.3);
        
        dotsContainer.getChildren().addAll(dot1, dot2, dot3);
        animationContainer.getChildren().add(dotsContainer);
        
        return animationContainer;
    }
    
    private void startAnimations() {
        // Animation du logo (fade in)
        if (splashStage != null && splashStage.getScene() != null) {
            ImageView logo = (ImageView) ((VBox) ((StackPane) splashStage.getScene().getRoot()).getChildren().get(1)).getChildren().getFirst();
            FadeTransition logoFade = new FadeTransition(Duration.seconds(1.5), logo);
            logoFade.setFromValue(0.0);
            logoFade.setToValue(1.0);
            logoFade.play();
        }
        
        // Animation des points de chargement
        startLoadingDotsAnimation();
    }
    
    private void startLoadingDotsAnimation() {
        Timeline dotsTimeline = new Timeline();
        
        // Point 1
        KeyFrame dot1Key1 = new KeyFrame(Duration.ZERO, new KeyValue(dot1.opacityProperty(), 0.3));
        KeyFrame dot1Key2 = new KeyFrame(Duration.seconds(0.5), new KeyValue(dot1.opacityProperty(), 1.0));
        KeyFrame dot1Key3 = new KeyFrame(Duration.seconds(1.0), new KeyValue(dot1.opacityProperty(), 0.3));
        
        // Point 2
        KeyFrame dot2Key1 = new KeyFrame(Duration.seconds(0.2), new KeyValue(dot2.opacityProperty(), 0.3));
        KeyFrame dot2Key2 = new KeyFrame(Duration.seconds(0.7), new KeyValue(dot2.opacityProperty(), 1.0));
        KeyFrame dot2Key3 = new KeyFrame(Duration.seconds(1.2), new KeyValue(dot2.opacityProperty(), 0.3));
        
        // Point 3
        KeyFrame dot3Key1 = new KeyFrame(Duration.seconds(0.4), new KeyValue(dot3.opacityProperty(), 0.3));
        KeyFrame dot3Key2 = new KeyFrame(Duration.seconds(0.9), new KeyValue(dot3.opacityProperty(), 1.0));
        KeyFrame dot3Key3 = new KeyFrame(Duration.seconds(1.4), new KeyValue(dot3.opacityProperty(), 0.3));
        
        dotsTimeline.getKeyFrames().addAll(
            dot1Key1, dot1Key2, dot1Key3,
            dot2Key1, dot2Key2, dot2Key3,
            dot3Key1, dot3Key2, dot3Key3
        );
        
        dotsTimeline.setCycleCount(INDEFINITE);
        dotsTimeline.play();
    }
    
    private void simulateLoading() {
        Task<Void> loadingTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String[] loadingSteps = {
                    "Initialisation...",
                    "Chargement des ressources...",
                    "Configuration de l'interface...",
                    "Préparation des outils...",
                    "Finalisation..."
                };
                
                for (int i = 0; i < loadingSteps.length; i++) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        statusLabel.setText(loadingSteps[finalI]);
                    });
                    
                    // Mettre à jour la barre de progression
                    double progress = (i + 1) / (double) loadingSteps.length;
                    Platform.runLater(() -> progressBar.setProgress(progress));
                    
                    // Attendre un peu
                    Thread.sleep(800);
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    logger.debug("Splash screen loading completed");
                    closeSplashScreen();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    logger.error("Splash screen loading failed", getException());
                    closeSplashScreen();
                });
            }
        };
        
        Thread loadingThread = new Thread(loadingTask);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }
    
    private void closeSplashScreen() {
        if (splashStage != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), splashStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                splashStage.close();
                if (mainApp != null) {
                    mainApp.showMainApplication();
                }
            });
            fadeOut.play();
        }
    }
}