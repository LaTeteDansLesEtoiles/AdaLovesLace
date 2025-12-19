package org.alienlabs.adaloveslace.domain.dto;

import com.google.gson.annotations.Expose;
import org.alienlabs.adaloveslace.domain.enumeration.Language;
import org.alienlabs.adaloveslace.domain.enumeration.SubTechnique;
import org.alienlabs.adaloveslace.domain.enumeration.Technique;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Diagram on the network.
 */
public class DiagramDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Expose
  private UUID uuid;

  @Expose
  private String name;

  @Expose
  private String showcase;

  @Expose
  private String previewContentType;

  @Expose
  private Technique technique;

  @Expose
  private SubTechnique subTechnique;

  @Expose
  private Language language;

  @Expose
  private String diagram;

  @Expose
  private String diagramContentType;

  @Expose
  private String username;

  @Expose
  private UUID clientId;

  @Expose
  private UUID clientSecret;

  @Expose
  private String diagramPreview;

  @Expose
  private List<String> previews = new ArrayList<>();

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

  public String getShowcase() {
    return this.showcase;
  }

  public DiagramDTO showcase(String preview) {
    this.setShowcase(preview);
    return this;
  }

  public void setShowcase(String showcase) {
    this.showcase = showcase;
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

  public String getDiagram() {
    return this.diagram;
  }

  public DiagramDTO diagram(String diagram) {
    this.setDiagram(diagram);
    return this;
  }

  public void setDiagram(String diagram) {
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

  public String getDiagramPreview() {
    return this.diagramPreview;
  }

  public void setDiagramPreview(String diagramPreview) {
    this.diagramPreview = diagramPreview;
  }

  public DiagramDTO diagramPreview(String diagramPreview) {
      this.diagramPreview = diagramPreview;
      return this;
  }

  public List<String> getPreviews() {
    return previews;
  }

  public void setPreviews(List<String> previews) {
    this.previews = previews;
  }

  public DiagramDTO previews(List<String> previews) {
    this.setPreviews(previews);
    return this;
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
