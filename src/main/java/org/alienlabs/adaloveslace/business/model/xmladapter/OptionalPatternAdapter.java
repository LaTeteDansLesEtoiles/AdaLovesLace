package org.alienlabs.adaloveslace.business.model.xmladapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.alienlabs.adaloveslace.business.model.Pattern;

import java.util.Optional;

public class OptionalPatternAdapter extends XmlAdapter<Pattern, Optional<Pattern>> {

    @Override
    public Optional<Pattern> unmarshal(Pattern v) {
        return Optional.ofNullable(v);
    }

    @Override
    public Pattern marshal(Optional<Pattern> v) {
        return v.orElse(null);
    }
}
