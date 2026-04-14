package com.example.amazonlite.service;


import com.example.amazonlite.entity.Category;
import com.example.amazonlite.exceptions.AlreadyExistsException;
import com.example.amazonlite.exceptions.ResourceNotFoundException;
import com.example.amazonlite.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(String categoryName){
        if(categoryRepository.existsByCategoryName(categoryName))
        {
            throw new AlreadyExistsException("Category Already Exists");
        }
        Category category = Category.builder()
                .categoryName(categoryName)
                .build();
        return categoryRepository.save(category);
    }

    public Category getCategoryById(String id){
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
    }

    public void deleteCategory(String id){
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    public Category updateCategory(String id, String newName){
        Category category = getCategoryById(id);
        if(categoryRepository.existsByCategoryName(newName)){
            throw new AlreadyExistsException("Category Name Already Exists");
        }
        category.setCategoryName(newName);
        return categoryRepository.save(category);
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
