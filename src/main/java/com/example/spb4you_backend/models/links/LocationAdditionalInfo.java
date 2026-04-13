package com.example.spb4you_backend.models.links;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "location_additional_info")
public class LocationAdditionalInfo {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("additional_info_id")
    private Integer additionalInfoId;

    @JsonProperty("sort_order")
    private Integer sortOrder = 0;

    public LocationAdditionalInfo(Integer locationId, Integer additionalInfoId, Integer sortOrder) {
        this.locationId = locationId;
        this.additionalInfoId = additionalInfoId;
        this.sortOrder = sortOrder;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setAdditionalInfoId(Integer additionalInfoId) {
        this.additionalInfoId = additionalInfoId;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationAdditionalInfo that = (LocationAdditionalInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(locationId, that.locationId) &&
                Objects.equals(additionalInfoId, that.additionalInfoId) &&
                Objects.equals(sortOrder, that.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, additionalInfoId, sortOrder);
    }

    @Override
    public String toString() {
        return "LocationAdditInfo{" +
                "id=" + id +
                ", locationId=" + locationId +
                ", additInfoId=" + additionalInfoId +
                ", sortOrder=" + sortOrder +
                '}';
    }
}