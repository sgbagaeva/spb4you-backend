package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route - Сущность маршрута
 */
@Table(name = "routes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Route {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @Getter
    @JsonProperty("description")
    private String description;

    @Getter
    @JsonProperty("likes")
    private Integer likes;

    @JsonProperty("tag_ids")
    private List<Integer> tagIds = new ArrayList<>();

    @JsonProperty("category_ids")
    private List<Integer> categoryIds = new ArrayList<>();

    @JsonProperty("photo_ids")
    private List<Integer> photoIds = new ArrayList<>();

    @Getter
    @JsonProperty("main_photo_id")
    private Integer mainPhotoId;

    @Getter
    @JsonProperty("distance")
    private Double distance;

    @Getter
    @JsonProperty("time")
    private Integer time;

    @Getter
    @JsonProperty("steps")
    private Integer steps;

    /**
     * Транзиентные поля (не сохраняются в БД)
     */
    @Transient
    @JsonProperty("photos")
    private List<Photo> photos = new ArrayList<>();

    @Transient
    @JsonProperty("points")
    private List<Point> points = new ArrayList<>();

    @Transient
    @JsonProperty("additionalInfo")
    private List<AdditionalInfo> additionalInfo = new ArrayList<>();

    @Getter
    @Transient
    @JsonProperty("main_photo_url")
    private String mainPhotoUrl;

    // ===== Геттеры и сеттеры =====

    @Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public List<Integer> getTagIds() {
        return tagIds != null ? tagIds : new ArrayList<>();
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds != null ? tagIds : new ArrayList<>();
    }

    public List<Integer> getCategoryIds() {
        return categoryIds != null ? categoryIds : new ArrayList<>();
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds != null ? categoryIds : new ArrayList<>();
    }
    public List<Integer> getPhotoIds() { return photoIds != null ? photoIds : new ArrayList<>(); }
    public void setPhotoIds(List<Integer> photoIds) { this.photoIds = photoIds != null ? photoIds : new ArrayList<>(); }
    public void addPhotoId(Integer photoId) { this.photoIds.add(photoId); }

    public void setMainPhotoId(Integer mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setMainPhotoUrl(String mainPhotoUrl) {
        this.mainPhotoUrl = mainPhotoUrl;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    // Транзиентные поля

    public List<Photo> getPhotos() {
        return photos != null ? photos : new ArrayList<>();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points != null ? points : new ArrayList<>();
    }

    public void setPoints(List<Point> points) {
        this.points = points != null ? points : new ArrayList<>();
    }

    public List<AdditionalInfo> getAdditionalInfo() { return additionalInfo != null ? additionalInfo : new ArrayList<>(); }
    public void setAdditionalInfo(List<AdditionalInfo> additionalInfo) { this.additionalInfo = additionalInfo != null ? additionalInfo : new ArrayList<>(); }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id) &&
                Objects.equals(name, route.name) &&
                Objects.equals(description, route.description) &&
                Objects.equals(likes, route.likes) &&
                Objects.equals(tagIds, route.tagIds) &&
                Objects.equals(categoryIds, route.categoryIds) &&
                Objects.equals(mainPhotoId, route.mainPhotoId) &&
                Objects.equals(distance, route.distance) &&
                Objects.equals(time, route.time) &&
                Objects.equals(steps, route.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, likes, tagIds, categoryIds, mainPhotoId, distance, time, steps);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", likes=" + likes +
                ", tagIds=" + tagIds +
                ", categoryIds=" + categoryIds +
                ", mainPhotoId=" + mainPhotoId +
                ", distance=" + distance +
                ", time=" + time +
                ", steps=" + steps +
                '}';
    }
}