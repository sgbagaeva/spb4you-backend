package com.example.spb4you_backend.dto;

import java.util.List;

public class CategoryItemsUpdateDto {
    private List<Integer> itemIds; // Список ID элементов, которые должны быть в категории

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
