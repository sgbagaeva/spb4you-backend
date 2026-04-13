package com.example.spb4you_backend.dto;

import java.util.List;

public class TagItemsUpdateDto {
    private List<Integer> itemIds; // Список ID элементов, которые должны быть в теге

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
