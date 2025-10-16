package org.alienlabs.adaloveslace.view.component.grid;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Knot;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PatternImageCache {
    private static final Map<PatternColorKey, ImageView> cache = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PatternImageCache.class);
    
    private static class PatternColorKey {
        private final Pattern pattern;
        private final Color color;
        
        public PatternColorKey(Pattern pattern, Color color) {
            this.pattern = pattern;
            this.color = color;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PatternColorKey)) return false;
            PatternColorKey that = (PatternColorKey) o;
            return Objects.equals(pattern, that.pattern) && 
                   Objects.equals(color, that.color);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(pattern, color);
        }
    }
    
    public static ImageView getImageView(Pattern pattern, Optional<Color> color) {
        PatternColorKey key = new PatternColorKey(pattern, color.orElse(null));
        return cache.computeIfAbsent(key, __ -> createImageView(pattern, color));
    }

    public static void updateKnotImageView(Knot knot) {
        if (!knot.getPattern().isPresent()) {
            return;
        }

        ImageView cachedView = getImageView(knot.getPattern().get(), knot.getColor());
        
        if (knot.getImageView() == null) {
            // Première initialisation
            knot.setImageView(new ImageView(cachedView.getImage()));
            knot.getImageView().setEffect(cachedView.getEffect());
        } else {
            // Mise à jour d'une ImageView existante
            knot.getImageView().setImage(cachedView.getImage());
            knot.getImageView().setEffect(cachedView.getEffect());
        }
    }
    
    private static ImageView createImageView(Pattern pattern, Optional<Color> color) {
        try {
            Image image = new Image(new FileInputStream(pattern.getAbsoluteFilename()));
            ImageView imageView = new ImageView(image);
            
            // Si une couleur est spécifiée, l'appliquer
            if (color.isPresent()) {
                // Créer un effet de coloration
                ColorAdjust monochrome = new ColorAdjust();
                monochrome.setSaturation(-1.0);
                
                Blend colorize = new Blend(
                    BlendMode.MULTIPLY,
                    monochrome,
                    new ColorInput(
                        0,
                        0,
                        image.getWidth(),
                        image.getHeight(),
                        color.get()
                    )
                );
                
                imageView.setEffect(colorize);
            }
            
            return imageView;
        } catch (FileNotFoundException e) {
            logger.error("Could not load pattern image: " + pattern.getAbsoluteFilename(), e);
            return new ImageView();
        }
    }
    
    public static void clear() {
        cache.clear();
    }
}
