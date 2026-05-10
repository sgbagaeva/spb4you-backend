package com.example.spb4you_backend.models.links;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "route_additional_info")
public class RouteAdditionalInfo {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("route_id")
    private Integer routeId;

    @JsonProperty("additional_info_id")
    private Integer additionalInfoId;

    @JsonProperty("sort_order")
    private Integer sortOrder = 0;

    public RouteAdditionalInfo(Integer routeId, Integer additionalInfoId) {
        this.routeId = routeId;
        this.additionalInfoId = additionalInfoId;
    }

    public RouteAdditionalInfo(Integer routeId, Integer additionalInfoId, Integer sortOrder) {
        this.routeId = routeId;
        this.additionalInfoId = additionalInfoId;
        this.sortOrder = sortOrder;
    }

    @Nonnull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public void setAdditionalInfoId(Integer additionalInfoId) {
        this.additionalInfoId = additionalInfoId;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteAdditionalInfo routeAdditInfo = (RouteAdditionalInfo) o;
        return Objects.equals(this.id, routeAdditInfo.id) &&
                Objects.equals(this.routeId, routeAdditInfo.routeId) &&
                Objects.equals(this.additionalInfoId, routeAdditInfo.additionalInfoId) &&
                Objects.equals(this.sortOrder, routeAdditInfo.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, routeId, additionalInfoId, sortOrder);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tag {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" routeId: ").append(toIndentedString(routeId)).append("\n");
        sb.append(" additionalInfoId: ").append(toIndentedString(additionalInfoId)).append("\n");
        sb.append(" sortOrder: ").append(toIndentedString(sortOrder)).append("\n");
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

