package org.alienlabs.adaloveslace.domain.xmladapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.paint.Color;

import java.util.Optional;

public class OptionalColorAdapter extends XmlAdapter<String, Optional<Color>> {

    @Override
    public Optional<Color> unmarshal(String v) {
        if (v == null || v.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(Color.web(v));
    }

    @Override
    public String marshal(Optional<Color> v) {
        if (v == null || v.isEmpty()) return "";
        Color color = v.get();
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255)
        );
    }

}
