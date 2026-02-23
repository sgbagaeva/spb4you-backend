package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

/**
 * LocationPoint
 */
@Table("location_points")
public class LocationPoint {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("point_id")
    private Integer pointId;

    /**
     * Уникальный идентификатор
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
     * ID локации
     * @return locationId
     */

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * ID точки
     * @return pointId
     */

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationPoint locationPoint = (LocationPoint) o;
        return Objects.equals(this.id, locationPoint.id) &&
                Objects.equals(this.locationId, locationPoint.locationId) &&
                Objects.equals(this.pointId, locationPoint.pointId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, pointId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tag {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" locationId: ").append(toIndentedString(locationId)).append("\n");
        sb.append(" pointId: ").append(toIndentedString(pointId)).append("\n");
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
