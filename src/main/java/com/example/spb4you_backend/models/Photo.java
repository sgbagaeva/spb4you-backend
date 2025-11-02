package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

/**
 * Photo
 */
@Table(name = "photos")
public class Photo {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("url")
    private String url; // Временная signed URL

    @JsonProperty("name")
    private String name;

    public Photo id(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Уникальный идентификатор фотографии
     * @return id
     */
    @Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Photo url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Временная signed URL фотографии
     * @return url
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Photo name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Имя фотографии
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Photo photo = (Photo) o;
        return Objects.equals(this.id, photo.id) &&
                Objects.equals(this.url, photo.url) &&
                Objects.equals(this.name, photo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Photo {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" url: ").append(toIndentedString(url)).append("\n");
        sb.append(" name: ").append(toIndentedString(name)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n ");
    }
}

