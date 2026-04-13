package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "points")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Point {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @Transient
    @JsonProperty("photos")
    private List<Photo> photos = new ArrayList<>();

    public Point() {}

    public Point(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Nonnull
    public Integer getId() { return id; }

    @Nonnull
    public String getName() { return name; }

    public String getDescription() { return description; }

    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }

    public List<Photo> getPhotos() { return photos != null ? photos : new ArrayList<>(); }

    public void setId(Integer id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public void addPhoto(Photo photo) {
        if (this.photos == null) this.photos = new ArrayList<>();
        this.photos.add(photo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(id, point.id) &&
                Objects.equals(name, point.name) &&
                Objects.equals(description, point.description) &&
                Objects.equals(latitude, point.latitude) &&
                Objects.equals(longitude, point.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}