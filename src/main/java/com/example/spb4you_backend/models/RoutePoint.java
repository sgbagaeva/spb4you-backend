package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

/**
 * RoutePoint
 */
@Table("route_points")
public class RoutePoint {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("route_id")
    private Integer routeId;

    @JsonProperty("point_id")
    private Integer pointId;

    @JsonProperty("sort_order")
    private Integer sortOrder;

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
     * ID маршрута
     * @return routeId
     */

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
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

    /**
     * Позиция точки в маршруте
     * @return sortOrder
     */

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoutePoint routePoint = (RoutePoint) o;
        return Objects.equals(this.id, routePoint.id) &&
                Objects.equals(this.routeId, routePoint.routeId) &&
                Objects.equals(this.pointId, routePoint.pointId) &&
                Objects.equals(this.sortOrder, routePoint.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, routeId, pointId, sortOrder);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tag {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" routeId: ").append(toIndentedString(routeId)).append("\n");
        sb.append(" pointId: ").append(toIndentedString(pointId)).append("\n");
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

