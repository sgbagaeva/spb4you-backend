package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route
 */
@Table(name = "routes")
public class Route {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("likes")
    private Integer likes;

    @JsonProperty("tags")
    private List<Integer> tags = new ArrayList<>();

    @JsonProperty("categories")
    private List<Integer> categories = new ArrayList<>();

    @JsonProperty("distance")
    private Double distance;

    @JsonProperty("time")
    private Integer time;

    @JsonProperty("steps")
    private Integer steps;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Уникальный идентификатор маршрута
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
     * Название маршрута
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
     * Описание маршрута
     * @return description
     */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Количество лайков маршрута
     * @return likes
     */
    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * Теги маршрута
     * @return tags
     */
    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    /**
     * Категории маршрута
     * @return categories
     */

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    /**
     * Время создания локации
     * @return createdAt
     */

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Время обновления локации
     * @return updatedAt
     */

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Route route = (Route) o;
        return Objects.equals(this.id, route.id) &&
                Objects.equals(this.name, route.name) &&
                Objects.equals(this.description, route.description) &&
                Objects.equals(this.likes, route.likes) &&
                Objects.equals(this.tags, route.tags) &&
                Objects.equals(this.categories, route.categories) &&
                Objects.equals(this.distance, route.distance) &&
                Objects.equals(this.time, route.time) &&
                Objects.equals(this.steps, route.steps) &&
                Objects.equals(this.createdAt, route.createdAt) &&
                Objects.equals(this.updatedAt, route.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, likes, tags, categories,
                distance, time, steps, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Route {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" name: ").append(toIndentedString(name)).append("\n");
        sb.append(" description: ").append(toIndentedString(description)).append("\n");
        sb.append(" likes: ").append(toIndentedString(likes)).append("\n");
        sb.append(" tags: ").append(toIndentedString(tags)).append("\n");
        sb.append(" categories: ").append(toIndentedString(categories)).append("\n");
        sb.append(" distance: ").append(toIndentedString(distance)).append("\n");
        sb.append(" time: ").append(toIndentedString(time)).append("\n");
        sb.append(" steps: ").append(toIndentedString(steps)).append("\n");
        sb.append(" createdAt: ").append(toIndentedString(createdAt)).append("\n");
        sb.append(" updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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
