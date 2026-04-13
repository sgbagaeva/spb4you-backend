package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(name = "photos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Photo {

    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("file_key")
    private String fileKey;

    @Transient
    @JsonProperty("url")
    private String url;

    public Photo() {}

    public Photo(String filename, Long size, String mimeType, String fileKey) {
        this.filename = filename;
        this.size = size;
        this.mimeType = mimeType;
        this.fileKey = fileKey;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getFilename() { return filename; }
    public Long getSize() { return size; }
    public String getMimeType() { return mimeType; }
    public String getFileKey() { return fileKey; }
    public String getUrl() { return url; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setSize(Long size) { this.size = size; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setFileKey(String fileKey) { this.fileKey = fileKey; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id) &&
                Objects.equals(filename, photo.filename) &&
                Objects.equals(size, photo.size) &&
                Objects.equals(mimeType, photo.mimeType) &&
                Objects.equals(fileKey, photo.fileKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename, size, mimeType, fileKey);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                ", mimeType='" + mimeType + '\'' +
                ", fileKey='" + fileKey + '\'' +
                '}';
    }
}