package com.mwu.productService.service;

import com.mwu.productService.DTO.CategoryDTO;
import com.mwu.productService.DTO.CategoryResponse;
import com.mwu.productService.config.MapperStructConfig;
import com.mwu.productService.entity.Category;
import com.mwu.productService.exception.APIException;
import com.mwu.productService.exception.ResourceNotFoundException;
import com.mwu.productService.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private MapperStructConfig mapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageadetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepo.findAll(pageadetails);

        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            throw new RuntimeException("No categories found");
        }

        List<CategoryDTO> categoryDto = categories.stream().map(mapper::toCategoryDTO).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDto);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {

        Category category = mapper.toCategory(categoryDto);
        Category categoryName = categoryRepo.findByCategoryName(categoryDto.getCategoryName());

        if (categoryName != null) {
            throw new APIException("Category with the name: " + categoryDto.getCategoryName() + " already exsits");
        }
        Category savedCategory = categoryRepo.save(category);
        CategoryDTO savedCategoryDto = mapper.toCategoryDTO(savedCategory);

        return savedCategoryDto;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category deletedCategory = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepo.delete(deletedCategory);

        return mapper.toCategoryDTO(deletedCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDto, Long categoryId) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Update the existing category object with new values
        category.setCategoryName(categoryDto.getCategoryName());
        // Update other fields as necessary

        // Save the updated category object
        Category updatedCategory = categoryRepo.save(category);

        // Map the updated category object to CategoryDTO
        CategoryDTO catDto = mapper.toCategoryDTO(updatedCategory);

        return catDto;
    }
}