package com.mwu.productService.service;

import com.mwu.productService.DTO.ProductDTO;
import com.mwu.productService.DTO.ProductResponse;
import com.mwu.productService.config.MapperStructConfig;
import com.mwu.productService.entity.Category;
import com.mwu.productService.entity.Products;
import com.mwu.productService.exception.APIException;
import com.mwu.productService.exception.ResourceNotFoundException;
import com.mwu.productService.repository.CategoryRepository;
import com.mwu.productService.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private ProductsRepository prodRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private MapperStructConfig mapper;

    @Value("${image-service.url:http://image-service}")
    private String imageServiceUrl;

    // Helper method to map Product to ProductDTO with image URL
    private ProductDTO mapToDTO(Products product) {
        ProductDTO productDTO = mapper.toProductDTO(product);

        // Set image URL if imageId exists
        if (product.getImageId() != null && !product.getImageId().isEmpty()) {
            productDTO.setImageUrl(imageServiceUrl + "/api/" + product.getImageId());
        }

        return productDTO;
    }

    @Override
    public ProductDTO addProducts(Long categoryId, ProductDTO productDto) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Products> prods = category.getProducts();
        for (Products value : prods) {
            if (value.getProductName().equals(productDto.getProductName())) {
                isProductNotPresent = true;
                break;
            }
        }
        if (isProductNotPresent) {
            Products products = mapper.toProduct(productDto);
            products.setCategory(category);
            double specialPrice = products.getPrice() - ((products.getDiscount() * 0.01) * products.getPrice());
            products.setSpecialPrice(specialPrice);

            Products savedProduct = prodRepo.save(products);
            return mapToDTO(savedProduct); // Use mapToDTO instead of direct mapping
        } else {
            throw new APIException("Product already exist!!");
        }
    }

    @Override
    public ProductResponse viewAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Products> spec = Specification.where(null);

        if(keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" +  keyword.toLowerCase() + "%"));
        }
        if(category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"), category));
        }

        Page<Products> pageProducts = prodRepo.findAll(spec, pageDetails);

        List<Products> products = pageProducts.getContent();
        List<ProductDTO> productsDto = products.stream()
                .map(this::mapToDTO) // Use mapToDTO for consistent image URL handling
                .toList();

        ProductResponse prodResponse = new ProductResponse();
        prodResponse.setContent(productsDto);
        prodResponse.setPageNumber(pageProducts.getNumber());
        prodResponse.setPageSize(pageProducts.getSize());
        prodResponse.setTotalElements(pageProducts.getTotalElements());
        prodResponse.setTotalPages(pageProducts.getTotalPages());
        prodResponse.setLastPage(pageProducts.isLast());
        return prodResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                            String sortOrder) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Products> pageProducts = prodRepo.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Products> products = pageProducts.getContent();
        List<ProductDTO> productsDto = products.stream()
                .map(this::mapToDTO) // Use mapToDTO for consistent image URL handling
                .toList();

        ProductResponse prodResponse = new ProductResponse();
        prodResponse.setContent(productsDto);
        prodResponse.setPageNumber(pageProducts.getNumber());
        prodResponse.setPageSize(pageProducts.getSize());
        prodResponse.setTotalElements(pageProducts.getTotalElements());
        prodResponse.setTotalPages(pageProducts.getTotalPages());
        prodResponse.setLastPage(pageProducts.isLast());
        return prodResponse;
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
                                                   String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Products> pageProducts = prodRepo.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Products> products = pageProducts.getContent();
        List<ProductDTO> productsDto = products.stream()
                .map(this::mapToDTO) // Use mapToDTO for consistent image URL handling
                .toList();

        ProductResponse prodResponse = new ProductResponse();
        prodResponse.setContent(productsDto);
        prodResponse.setPageNumber(pageProducts.getNumber());
        prodResponse.setPageSize(pageProducts.getSize());
        prodResponse.setTotalElements(pageProducts.getTotalElements());
        prodResponse.setTotalPages(pageProducts.getTotalPages());
        prodResponse.setLastPage(pageProducts.isLast());
        return prodResponse;
    }

    // Get Products by ID
    @Override
    public ProductDTO viewProductsById(Long productId) {
        Products prod = prodRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        return mapToDTO(prod); // Use mapToDTO instead of direct mapping
    }

    @Override
    public ProductDTO updateProducts(ProductDTO productDto, Long productId) {
        Products existingProduct = prodRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Preserve imageId when updating other fields
        String existingImageId = existingProduct.getImageId();

        Products products = modelMapper.map(productDto, Products.class);
        existingProduct.setProductName(products.getProductName());
        existingProduct.setDescription(products.getDescription());
        existingProduct.setQuantity(products.getQuantity());
        existingProduct.setPrice(products.getPrice());
        existingProduct.setDiscount(products.getDiscount());

        // Keep existing imageId if not explicitly changed
        if (existingImageId != null && !existingImageId.isEmpty()) {
            existingProduct.setImageId(existingImageId);
        }

        double specialPrice = existingProduct.getPrice() - ((existingProduct.getDiscount() * 0.01) * existingProduct.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        Products savedProduct = prodRepo.save(existingProduct);

        return mapToDTO(savedProduct); // Use mapToDTO for consistent image URL handling
    }

    @Override
    public ProductDTO deleteProducts(Long productId) {
        Products product = prodRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Get the product DTO before deleting
        ProductDTO productDTO = mapToDTO(product);

        prodRepo.delete(product);
        return productDTO;
    }

    @Override
    public ProductDTO updateProductImageReference(Long productId, String imageId) {
        Products existingProduct = prodRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        existingProduct.setImageId(imageId);
        Products updatedProduct = prodRepo.save(existingProduct);

        return mapToDTO(updatedProduct); // Use mapToDTO for consistent image URL handling
    }

    @Override
    public String getProductImageId(Long productId) {
        Products product = prodRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        return product.getImageId();
    }
}
