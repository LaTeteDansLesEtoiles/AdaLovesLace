package org.alienlabs.adaloveslace.domain;

import com.google.gson.annotations.Expose;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.io.Serializable;

/**
 * A Picture.
 */
@XmlType(name = "Knot")
@XmlAccessorType(XmlAccessType.FIELD)
public class Picture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose
    private String picture;

    @Expose
    private String pictureContentType;

    @Expose
    private String showcase;

    @Expose
    private Long preview;

    public String getPicture() {
        return this.picture;
    }

    public Picture picture(String picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return this.pictureContentType;
    }

    public Picture pictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
        return this;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public String getShowcase() {
        return this.showcase;
    }

    public Picture showcase(String showcase) {
        this.setShowcase(showcase);
        return this;
    }

    public void setShowcase(String showcase) {
        this.showcase = showcase;
    }

    public Long getPreview() {
        return this.preview;
    }

    public void setPreview(Long diagram) {
        this.preview = diagram;
    }

    public Picture preview(Long diagram) {
        this.setPreview(diagram);
        return this;
    }

    @Override
    public String toString() {
        return "Picture{" +
            ", pictureContentType='" + getPictureContentType() + "'" +
            ", showcase='" + getShowcase() + "'" +
            "}";
    }
}
