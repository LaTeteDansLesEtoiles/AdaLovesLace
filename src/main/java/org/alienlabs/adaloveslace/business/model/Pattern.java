package org.alienlabs.adaloveslace.business.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.alienlabs.adaloveslace.view.window.ToolboxWindow;

/**
 * An image, or at least the one represented by a Pattern filename, which can be put at will on a Canvas and chosen
 * in the ToolboxWindow.
 * <p>
 * Initially, the Patterns are loaded from a certain package of the classpath, according to the expression:
 *
 * @see org.alienlabs.adaloveslace.util.FileUtil#CLASSPATH_RESOURCES_PATH
 * <p>
 * @see ToolboxWindow#loadPatternsResourcesFiles
 *
 * */
@XmlType(name = "Pattern")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pattern {

  private String filename;

  public Pattern(String filename) {
    this.filename = filename;
  }

  public Pattern() {
    this(null);
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

}
