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

    @JsonProperty("link")
    private String link; // Ссылка на изображение (URL)

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

    public Photo link(String link) {
        this.link = link;
        return this;
    }

    /**
     * Ссылка на изображение
     * @return link
     */
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
                Objects.equals(this.link, photo.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Photo {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" link: ").append(toIndentedString(link)).append("\n");
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

