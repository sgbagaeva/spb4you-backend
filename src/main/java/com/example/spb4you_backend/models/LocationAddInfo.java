package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("location_additional_info")
public class LocationAddInfo {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("sort_order")
    private Integer sortOrder;

    /**
     * Уникальный идентификатор
     * @return id
     */

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
     * Заголовок
     * @return title
     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Описание
     * @return description
     */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Порядок сортировки
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
        LocationAddInfo addInfo = (LocationAddInfo) o;
        return Objects.equals(this.id, addInfo.id) &&
                Objects.equals(this.locationId, addInfo.locationId) &&
                Objects.equals(this.title, addInfo.title) &&
                Objects.equals(this.description, addInfo.description) &&
                Objects.equals(this.sortOrder, addInfo.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, title, description, sortOrder);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tag {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" locationId: ").append(toIndentedString(locationId)).append("\n");
        sb.append(" title: ").append(toIndentedString(title)).append("\n");
        sb.append(" description: ").append(toIndentedString(description)).append("\n");
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
