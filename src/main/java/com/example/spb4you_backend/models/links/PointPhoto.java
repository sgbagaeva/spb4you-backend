package com.example.spb4you_backend.models.links;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("point_photos")
public class PointPhoto {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("point_id")
    private Integer pointId;

    @JsonProperty("photo_id")
    private Integer photoId;

    @JsonProperty("sort_order")
    private Integer sortOrder;

    public PointPhoto(Integer pointId, Integer photoId, Integer sortOrder) {
        this.pointId = pointId;
        this.photoId = photoId;
        this.sortOrder = sortOrder;
    }

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
     * ID фотографии
     * @return photoId
     */

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
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
        PointPhoto pointPhoto = (PointPhoto) o;
        return Objects.equals(this.id, pointPhoto.id) &&
                Objects.equals(this.pointId, pointPhoto.pointId) &&
                Objects.equals(this.photoId, pointPhoto.photoId) &&
                Objects.equals(this.sortOrder, pointPhoto.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pointId, photoId, sortOrder);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Tag {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" pointId: ").append(toIndentedString(pointId)).append("\n");
        sb.append(" photoId: ").append(toIndentedString(photoId)).append("\n");
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
