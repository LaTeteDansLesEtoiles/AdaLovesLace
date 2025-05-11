package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.util.Optional;

public class OptionalStringAdapter  extends XmlAdapter<String, Optional<String>> {

    @Override
    public Optional<String> unmarshal(String v) {
        return v.isEmpty() ? Optional.empty() : Optional.of(v);
    }

    @Override
    public String marshal(Optional<String> v) {
        return v.orElse(null);
    }
}
