package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Location
 */
@Table(name = "points")
public class Point {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * Уникальный идентификатор точки
     * @return id
     */
    @Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Название точки
     * @return name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Описание точки
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Широта точки
     * @return latitude
     */

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Долгота точки
     * @return longitude
     */

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Время создания точки
     * @return createdAt
     */

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        return Objects.equals(this.id, point.id) &&
                Objects.equals(this.name, point.name) &&
                Objects.equals(this.description, point.description) &&
                Objects.equals(this.latitude, point.latitude) &&
                Objects.equals(this.longitude, point.longitude) &&
                Objects.equals(this.createdAt, point.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, latitude, longitude, createdAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Location {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" name: ").append(toIndentedString(name)).append("\n");
        sb.append(" description: ").append(toIndentedString(description)).append("\n");
        sb.append(" latitude: ").append(toIndentedString(latitude)).append("\n");
        sb.append(" longitude: ").append(toIndentedString(longitude)).append("\n");
        sb.append(" createdAt: ").append(toIndentedString(createdAt)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n ");
    }
}

