package com.example.spb4you_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Admin
 */
@Table(name = "admins")
public class Admin {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name; // Имя администратора

    @JsonProperty("password")
    private String password; // Пароль администратора

    @JsonProperty("created_at")
    private LocalDateTime createdAt; // Дата создания администратора

    /**
     * Уникальный идентификатор пользователя
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
     * Имя администратора
     * @return name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Пароль aдминистратора
     * @return password
     */
    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Дата регистрации администратора
     * @return сreatedAt
     */
    @Nonnull
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Admin admin = (Admin) o;
        return Objects.equals(this.id, admin.id) &&
                Objects.equals(this.name, admin.name) &&
                Objects.equals(this.password, admin.password) &&
                Objects.equals(this.createdAt, admin.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, createdAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Admin {\n");
        sb.append(" id: ").append(toIndentedString(id)).append("\n");
        sb.append(" name: ").append(toIndentedString(name)).append("\n");
        sb.append(" password: ").append(toIndentedString(password)).append("\n");
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
