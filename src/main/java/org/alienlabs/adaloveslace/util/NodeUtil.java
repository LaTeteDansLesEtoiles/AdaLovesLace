package org.alienlabs.adaloveslace.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.alienlabs.adaloveslace.App;
import org.alienlabs.adaloveslace.domain.Diagram;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.enumeration.MouseMode;
import org.alienlabs.adaloveslace.view.component.GridUtil;
import org.alienlabs.adaloveslace.view.component.grid.PatternImageCache;
import org.alienlabs.adaloveslace.view.window.event.GridEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.alienlabs.adaloveslace.App.CANVAS_TEXT_FONT_SIZE;
import static org.alienlabs.adaloveslace.App.PATTERNS_DIRECTORY_NAME;
import static org.alienlabs.adaloveslace.domain.Diagram.isNewText;
import static org.alienlabs.adaloveslace.domain.Knot.NEW_TEXT;
import static org.alienlabs.adaloveslace.util.FileUtil.APP_FOLDER_IN_USER_HOME;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.app;
import static org.alienlabs.adaloveslace.view.window.event.GridEvents.keyHandler;

public class NodeUtil {

    public static final int HANDLE_SIZE   = 25;

    private static final Logger logger = LoggerFactory.getLogger(NodeUtil.class);

    public NodeUtil() {
        // Nothing to do here, that's just to avoid an all-static class
    }

    public boolean isMouseOverKnot(Knot knot) {
        return (knot.getImageView().isHover())
                || ((knot.getHovered() != null) && (knot.getHovered().isHover()))
                || ((knot.getSelection() != null) && (knot.getSelection().isHover()));
    }

    public Knot copyKnot(Knot knot) {
        Knot copy = new Knot(
                knot.getX(),
                knot.getY(),
                knot.getPattern(),
                knot.getText(),
                knot.getColor(),
                knot.getImageView()
        );
        copy(knot, copy);

        // Le rectangle de sélection sera créé dans drawHoveredOverOrSelectedDecorations()
        // pour garantir qu'il est correctement ajouté au pane avec les bonnes propriétés

        return copy;
    }

    public Knot copyKnotCloningImageView(Knot knot) {
        Knot copy = new Knot(
                knot.getX(),
                knot.getY(),
                knot.getPattern(),
                knot.getText(),
                knot.getColor(),
                new ImageView(knot.getImageView().getImage())
        );
        copy(knot, copy);
        copy.getImageView().addEventHandler(MouseEvent.MOUSE_MOVED, GridEvents.getGridHoverEventHandler(app));
        copy.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, GridEvents.getMouseClickEventHandler(app));

        return copy;
    }

    private static void copy(Knot knot, Knot copy) {
        copy.setRotationAngle(knot.getRotationAngle());
        copy.setZoomFactor(knot.getZoomFactor());
        copy.setVisible(knot.isVisible());
        copy.setSelectable(knot.isSelectable());
        copy.setFlippedVertically(knot.isFlippedVertically());
        copy.setFlippedHorizontally(knot.isFlippedHorizontally());
        copy.setTextId(knot.getTextId());
        // Créer une copie profonde du StringBuilder pour éviter la mutation partagée
        if (knot.getTypedText() != null) {
            copy.setTypedText(new StringBuilder(knot.getTypedText()));
        } else {
            copy.setTypedText(null);
        }
        copy.setText(knot.getText());

        if (knot.getHovered() != null) {
            copy.setHovered(knot.getHovered());
        }
        // Ne pas copier la référence du rectangle de sélection pour éviter que plusieurs nœuds
        // partagent le même rectangle. Le rectangle sera créé dans drawHoveredOverOrSelectedDecorations()
        // si nécessaire. Pour les copies créées lors du déplacement avec les flèches, cela garantit
        // que chaque nœud a son propre rectangle qui sera mis à jour correctement.
        copy.setSelection(null);
        if (knot.getHandle() != null) {
            copy.setHandle(knot.getHandle());
        }
    }

    public ImageView drawPattern(double x, double y, Pattern currentPattern) {
        if (null == currentPattern) {
            return null;
        }

        ImageView iv = null;

        try (FileInputStream fis = new FileInputStream(new File(APP_FOLDER_IN_USER_HOME + PATTERNS_DIRECTORY_NAME, currentPattern.getFilename()))) {
            Image image = replaceColoredPixels(new Image(fis), app.getOptionalDotGrid().getDiagram().getCurrentColor());
            iv = new ImageView(image);

            iv.setLayoutX(x);
            iv.setLayoutY(y);
            iv.setRotate(0d);

            logger.info("Top left corner of the knot {} is ({},{})", currentPattern.getFilename(), x, y);
        } catch (IOException e) {
            logger.error("Problem with pattern resource file!", e);
        }

        return iv;
    }

    public Image replaceColoredPixels(Image image, Color replacementColor, Color... backgroundColor) {
        if (replacementColor == null) {
            return image;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                if (backgroundColor.length == 0) {
                    // Mode par défaut: remplacer le NOIR par la couleur choisie
                    if (isBlack(color)) {
                        writer.setColor(x, y, replacementColor);
                    } else {
                        writer.setColor(x, y, color);
                    }
                } else {
                    // Mode avec couleur de fond spécifiée: remplacer cette couleur
                    if (isBackgroundColor(backgroundColor[0], color)) {
                        writer.setColor(x, y, replacementColor);
                    } else {
                        writer.setColor(x, y, color);
                    }
                }
            }
        }
        

        return result;
    }

    private boolean isBlack(Color color) {
        // Tolérance pour détecter les nuances de noir (RGB proche de 0)
        double tolerance = 0.3;

        return color.getRed() <= tolerance &&
                         color.getGreen() <= tolerance &&
                         color.getBlue() <= tolerance &&
                         color.getOpacity() > (1d - tolerance);
    }
    

    private boolean isBackgroundColor(Color backgroundColor, Color color) {
        return color.getRed() == backgroundColor.getRed() &&
                color.getGreen() == backgroundColor.getGreen() &&
                color.getBlue() == backgroundColor.getBlue() &&
                color.getOpacity() == backgroundColor.getOpacity();
    }

    public void colorizeKnot(App app, Knot copiedKnot) {
        // Vérifier si le bouton "Back to Black" est sélectionné
        if (app.getToolboxWindow().getBackInBlackButton().isSelected()) {
            // Mode "Back to Black": restaurer le nœud à sa couleur originale (noir)
            copiedKnot.setColor(Optional.empty());
            if (copiedKnot.getPattern().isPresent()) {
                // Pour les patterns, restaurer l'image originale depuis le cache
                PatternImageCache.updateKnotImageView(copiedKnot);
            } else {
                // Pour le texte, recréer l'image en noir
                app.getOptionalDotGrid().drawTextImageView(
                    copiedKnot,
                    copiedKnot.getX(),
                    copiedKnot.getY()
                );
            }
        } else if (app.getOptionalDotGrid().getDiagram().getCurrentColor() != null) {
            // Mode couleur: appliquer la couleur choisie
            Color newColor = app.getOptionalDotGrid().getDiagram().getCurrentColor();

            // Toujours remplacer le noir, peu importe l'ancienne couleur
            copiedKnot.setColor(Optional.of(newColor));
            if (copiedKnot.getPattern().isPresent()) {
                // Pour les patterns, utiliser le cache pour obtenir l'image colorisée
                PatternImageCache.updateKnotImageView(copiedKnot);
            } else {
                // Pour le texte, recréer l'image avec la nouvelle couleur
                app.getOptionalDotGrid().drawTextImageView(
                    copiedKnot,
                    copiedKnot.getX(),
                    copiedKnot.getY()
                );
            }
        }
    }

    public void drawText(Diagram diagram, double x, double y) {
        diagram.setX(x);
        diagram.setY(y);

        app.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
        app.getOptionalDotGrid().getDiagram()           .setCurrentMode(MouseMode.SELECTION);
        // Les boutons sont maintenant dans ToolboxWindow
        // app.getGeometryWindow().getDrawingButton()      .setSelected(false);
        // app.getGeometryWindow().getSelectionButton()    .setSelected(true);
        // app.getGeometryWindow().getDeletionButton()     .setSelected(false);
        // app.getGeometryWindow().getDuplicationButton()  .setSelected(false);

        app.getOptionalDotGrid().getDiagram().setCurrentKnot(null);
        diagram.getUpdateImage().run();
    }

    public void clearTechnicalElements(App app) {
        app.getOptionalDotGrid().clearSelections();
        app.getOptionalDotGrid().clearAllGuideLines();
        app.getOptionalDotGrid().clearHovered();
        app.getOptionalDotGrid().clearHandles();
        app.getOptionalDotGrid().layoutChildren();
    }


    public Knot newKnot(double x, double y, ImageView imageView, Pattern pattern, Knot currentKnot, Color currentColor) {
        Knot knot = new Knot(
                isNewText || currentKnot == null || pattern != null ? x : currentKnot.getX(),
                isNewText || currentKnot == null || pattern != null ? y : currentKnot.getY(),
                pattern == null ? Optional.empty() : Optional.of(pattern),
                pattern == null && !isNewText
                        ? Optional.of(currentKnot == null ? NEW_TEXT.toString() : currentKnot.getTypedText().toString())
                        : Optional.of(NEW_TEXT.toString()),
                currentColor == null ? Optional.empty() : Optional.of(currentColor),
                null
        );

        if (knot.getPattern().isPresent()) {
            knot.getPattern().get().setCenterX(x);
            knot.getPattern().get().setCenterY(y);
            org.alienlabs.adaloveslace.view.component.grid.PatternImageCache.updateKnotImageView(knot);
        } else if (imageView != null) {
            knot.setImageView(imageView);
        } else {
            knot.setImageView(new ImageView());
        }

        return knot;
    }

    public ImageView createText(double x, double y, ImageView imageView, Knot currentKnot, Color currentColor) {
        if (null == imageView) {
            imageView = new ImageView();
        }

        final Text text = new Text();
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        text.setText(currentKnot == null || currentKnot.getTypedText() == null 
                ? NEW_TEXT.toString() 
                : currentKnot.getTypedText().toString());
        text.setFont(new Font(CANVAS_TEXT_FONT_SIZE));
        text.setFill(currentColor);
        text.setScaleX(currentKnot == null ? 1d : new GridUtil(app.getMovablePane()).computeZoomFactor(currentKnot.getZoomFactor()));
        text.setScaleY(currentKnot == null ? 1d : new GridUtil(app.getMovablePane()).computeZoomFactor(currentKnot.getZoomFactor()));
        text.setRotate(currentKnot == null ? 0d : currentKnot.getRotationAngle());
        text.setLayoutX(currentKnot == null ? x : currentKnot.getX());
        text.setLayoutY(currentKnot == null ? y : currentKnot.getY());

        WritableImage s = text.snapshot(params, null);
        imageView.setImage(s);
        
        return imageView;
    }

}
