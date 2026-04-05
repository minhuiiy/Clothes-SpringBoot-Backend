package com.clothes.backend.controller;

import com.clothes.backend.entity.Category;
import com.clothes.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.clothes.backend.dto.response.CategoryMenuResponse;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/menu")
    public ResponseEntity<CategoryMenuResponse> getMenuCategories() {
        List<Category> categories = categoryRepository.findAll();
        Map<Long, CategoryMenuResponse.CategoryNode> nodesById = new HashMap<>();
        Map<Long, Long> parentIdById = new HashMap<>();

        for (Category c : categories) {
            CategoryMenuResponse.CategoryNode node = CategoryMenuResponse.CategoryNode.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .slug(c.getSlug())
                    .imageUrl(c.getImageUrl())
                    .children(new ArrayList<>())
                    .build();
            nodesById.put(c.getId(), node);
            parentIdById.put(c.getId(), c.getParentId());
        }

        for (Map.Entry<Long, Long> e : parentIdById.entrySet()) {
            Long id = e.getKey();
            Long parentId = e.getValue();
            if (parentId == null) continue;
            CategoryMenuResponse.CategoryNode parent = nodesById.get(parentId);
            CategoryMenuResponse.CategoryNode child = nodesById.get(id);
            if (parent != null && child != null) {
                parent.getChildren().add(child);
            }
        }

        List<CategoryMenuResponse.CategoryNode> nam = selectByPredicate(nodesById, node -> isNamSlug(node.getSlug()));
        List<CategoryMenuResponse.CategoryNode> nu = selectByPredicate(nodesById, node -> isNuSlug(node.getSlug()));
        List<CategoryMenuResponse.CategoryNode> phuKien = selectByPredicate(nodesById, node -> isPhuKienSlug(node.getSlug()) && !isNamSlug(node.getSlug()) && !isNuSlug(node.getSlug()));

        return ResponseEntity.ok(CategoryMenuResponse.builder()
                .nam(nam)
                .nu(nu)
                .phuKien(phuKien)
                .build());
    }

    private interface NodePredicate {
        boolean test(CategoryMenuResponse.CategoryNode node);
    }

    private List<CategoryMenuResponse.CategoryNode> selectByPredicate(
            Map<Long, CategoryMenuResponse.CategoryNode> nodesById,
            NodePredicate predicate
    ) {
        Set<Long> selected = new HashSet<>();
        for (CategoryMenuResponse.CategoryNode node : nodesById.values()) {
            if (predicate.test(node) && node.getId() != null) {
                selected.add(node.getId());
            }
        }

        List<CategoryMenuResponse.CategoryNode> result = new ArrayList<>();
        for (Long id : selected) {
            CategoryMenuResponse.CategoryNode node = nodesById.get(id);
            if (node != null) {
                result.add(node);
            }
        }

        Set<Long> childIds = new HashSet<>();
        for (CategoryMenuResponse.CategoryNode n : result) {
            collectChildrenIds(n, childIds);
        }
        result.removeIf(n -> n.getId() != null && childIds.contains(n.getId()));

        result.sort((a, b) -> {
            String as = a.getSlug() == null ? "" : a.getSlug();
            String bs = b.getSlug() == null ? "" : b.getSlug();
            return as.compareToIgnoreCase(bs);
        });

        return result;
    }

    private void collectChildrenIds(CategoryMenuResponse.CategoryNode node, Set<Long> out) {
        if (node.getChildren() == null) return;
        for (CategoryMenuResponse.CategoryNode c : node.getChildren()) {
            if (c.getId() != null) out.add(c.getId());
            collectChildrenIds(c, out);
        }
    }

    private boolean isNamSlug(String slug) {
        String s = normalizeSlug(slug);
        return s.equals("nam") || s.endsWith("-nam");
    }

    private boolean isNuSlug(String slug) {
        String s = normalizeSlug(slug);
        return s.equals("nu") || s.endsWith("-nu");
    }

    private boolean isPhuKienSlug(String slug) {
        String s = normalizeSlug(slug);
        return s.contains("phu-kien");
    }

    private String normalizeSlug(String slug) {
        if (slug == null) return "";
        return slug.toLowerCase(Locale.ROOT).trim();
    }
}
