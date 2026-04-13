package com.example.spb4you_backend.models.links;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(name = "location_points")
public class LocationPoint {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("point_id")
    private Integer pointId;

    // Конструктор по умолчанию
    public LocationPoint() {}

    // Конструктор с параметрами
    public LocationPoint(Integer locationId, Integer pointId) {
        this.locationId = locationId;
        this.pointId = pointId;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public Integer getPointId() {
        return pointId;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationPoint that = (LocationPoint) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(locationId, that.locationId) &&
                Objects.equals(pointId, that.pointId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, pointId);
    }

    @Override
    public String toString() {
        return "LocationPoint{" +
                "id=" + id +
                ", locationId=" + locationId +
                ", pointId=" + pointId +
                '}';
    }
}