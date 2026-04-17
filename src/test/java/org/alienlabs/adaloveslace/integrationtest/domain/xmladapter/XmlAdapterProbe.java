package org.alienlabs.adaloveslace.integrationtest.domain.xmladapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.paint.Color;
import org.alienlabs.adaloveslace.domain.Pattern;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalColorAdapter;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalPatternAdapter;
import org.alienlabs.adaloveslace.domain.xmladapter.OptionalStringAdapter;

import java.util.Optional;

/**
 * Minimal JAXB root used only to exercise the production {@link XmlJavaTypeAdapter} types under
 * {@link org.alienlabs.adaloveslace.domain.xmladapter}.
 */
@XmlRootElement(name = "adapterProbe")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlAdapterProbe {

  @XmlElement
  @XmlJavaTypeAdapter(OptionalColorAdapter.class)
  private Optional<Color> color = Optional.empty();

  @XmlElement
  @XmlJavaTypeAdapter(OptionalStringAdapter.class)
  private Optional<String> text = Optional.empty();

  @XmlElement
  @XmlJavaTypeAdapter(OptionalPatternAdapter.class)
  private Optional<Pattern> pattern = Optional.empty();

  public XmlAdapterProbe() {
  }

  public static XmlAdapterProbe sample() {
    XmlAdapterProbe p = new XmlAdapterProbe();
    p.color = Optional.of(Color.web("#AABBCCDD"));
    p.text = Optional.of("hello-xml");
    Pattern pat = new Pattern();
    pat.setFilename("fixture.jpg");
    pat.setAbsoluteFilename("/tmp/fixture.jpg");
    pat.setWidth(12d);
    pat.setHeight(34d);
    p.pattern = Optional.of(pat);
    return p;
  }

  public Optional<Color> getColor() {
    return color;
  }

  public Optional<String> getText() {
    return text;
  }

  public Optional<Pattern> getPattern() {
    return pattern;
  }
}
