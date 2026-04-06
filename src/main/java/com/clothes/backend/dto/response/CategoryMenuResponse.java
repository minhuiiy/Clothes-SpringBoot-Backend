package com.clothes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryMenuResponse {
    private List<CategoryNode> nam;
    private List<CategoryNode> nu;
    private List<CategoryNode> phuKien;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryNode {
        private Long id;
        private String name;
        private String slug;
        private String imageUrl;
        private List<CategoryNode> children;
    }
}
