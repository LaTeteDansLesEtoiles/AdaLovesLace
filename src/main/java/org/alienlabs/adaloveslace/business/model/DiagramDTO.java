package org.alienlabs.adaloveslace.business.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * A Diagram on the network.
 */
public class DiagramDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID uuid;

  private String name;

  private byte[] preview;

  private String previewContentType;

  private Technique technique;

  private SubTechnique subTechnique;

  private Language language;

  private byte[] diagram;

  private String diagramContentType;

  private String username;

  private UUID clientId;

  private UUID clientSecret;

  public UUID getUuid() {
    return this.uuid;
  }

  public DiagramDTO uuid(UUID uuid) {
    this.setUuid(uuid);
    return this;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return this.name;
  }

  public DiagramDTO name(String name) {
    this.setName(name);
    return this;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getPreview() {
    return this.preview;
  }

  public DiagramDTO preview(byte[] preview) {
    this.setPreview(preview);
    return this;
  }

  public void setPreview(byte[] preview) {
    this.preview = preview;
  }

  public String getPreviewContentType() {
    return this.previewContentType;
  }

  public DiagramDTO previewContentType(String previewContentType) {
    this.previewContentType = previewContentType;
    return this;
  }

  public void setPreviewContentType(String previewContentType) {
    this.previewContentType = previewContentType;
  }
  public Technique getTechnique() {
    return this.technique;
  }

  public DiagramDTO technique(Technique technique) {
    this.setTechnique(technique);
    return this;
  }

  public void setTechnique(Technique technique) {
    this.technique = technique;
  }

  public SubTechnique getSubTechnique() {
    return this.subTechnique;
  }

  public DiagramDTO subTechnique(SubTechnique subTechnique) {
    this.setSubTechnique(subTechnique);
    return this;
  }

  public void setSubTechnique(SubTechnique subTechnique) {
    this.subTechnique = subTechnique;
  }

  public Language getLanguage() {
    return this.language;
  }

  public DiagramDTO language(Language language) {
    this.setLanguage(language);
    return this;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public byte[] getDiagram() {
    return this.diagram;
  }

  public DiagramDTO diagram(byte[] diagram) {
    this.setDiagram(diagram);
    return this;
  }

  public void setDiagram(byte[] diagram) {
    this.diagram = diagram;
  }

  public String getDiagramContentType() {
    return this.diagramContentType;
  }

  public DiagramDTO diagramContentType(String diagramContentType) {
    this.diagramContentType = diagramContentType;
    return this;
  }

  public void setDiagramContentType(String diagramContentType) {
    this.diagramContentType = diagramContentType;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public DiagramDTO username(String username) {
    this.setUsername(username);
    return this;
  }

  public UUID getClientId() {
    return clientId;
  }

  public void setClientId(UUID clientId) {
    this.clientId = clientId;
  }

  public DiagramDTO clientId(UUID clientId) {
    this.setClientId(clientId);
    return this;
  }

  public UUID getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(UUID clientSecret) {
    this.clientSecret = clientSecret;
  }

  public DiagramDTO clientSecret(UUID clientSecret) {
    this.setClientSecret(clientSecret);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DiagramDTO)) {
      return false;
    }
    return uuid != null && uuid.equals(((DiagramDTO) o).uuid);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "Diagram{" +
      "uuid='" + getUuid() + "'" +
      ", name='" + getName() + "'" +
      ", technique='" + getTechnique() + "'" +
      ", subTechnique='" + getSubTechnique() + "'" +
      ", language='" + getLanguage() + "'" +
      ", diagramContentType='" + getDiagramContentType() + "'" +
      ", previewContentType='" + getPreviewContentType() + "'" +
      "}";
  }
}
