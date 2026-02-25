package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Tag
 */
@Table(name = "categories")
public class Category {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name; // Название категории

    @JsonProperty("description")
    private String description; // Описание категории

    @JsonProperty("type")
    private String type; // Тип категории ('Локации'/'Маршруты')

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * Уникальный идентификатор категории
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
     * Название категории
     * @return name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Описание категории
     * @return description
     */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Тип категории
     * @return type
     */
    @Nonnull
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Время создания категории
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
        Category category = (Category) o;
        return Objects.equals(this.id, category.id) &&
                Objects.equals(this.name, category.name) &&
                Objects.equals(this.description, category.description) &&
                Objects.equals(this.type, category.type) &&
                Objects.equals(this.createdAt, category.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, createdAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Category {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" name: ").append(toIndentedString(name)).append("\n");
        sb.append(" description: ").append(toIndentedString(description)).append("\n");
        sb.append(" type: ").append(toIndentedString(type)).append("\n");
        sb.append(" createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

