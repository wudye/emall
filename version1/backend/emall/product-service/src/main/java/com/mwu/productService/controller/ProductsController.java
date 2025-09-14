package com.mwu.productService.controller;


import com.mwu.productService.DTO.ImageResponse;
import com.mwu.productService.DTO.ProductDTO;
import com.mwu.productService.DTO.ProductResponse;
import com.mwu.productService.config.AppConstants;
import com.mwu.productService.feignClient.CartClient;
import com.mwu.productService.feignClient.ImageClient;
import com.mwu.productService.feignClient.UserProfileClient;
import com.mwu.productService.service.ProductsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductsController {

    @Autowired
    private ProductsService prodService;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private UserProfileClient userProfileClient;

    @Autowired
    private ImageClient imageClient;


    // Add new Products
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addNewProducts(@Valid @RequestBody ProductDTO productDto,
                                                     @PathVariable Long categoryId) {

        ProductDTO savedProductDto = prodService.addProducts(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    // View all Products
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        ProductResponse products = prodService.viewAllProducts(pageNumber, pageSize, sortBy, sortOrder, keyword, category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // get all products by Category
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = prodService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }

    // Get All Products using Keyword
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = prodService.searchProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }

    // Get Products by Id
    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDTO> getProductsById(@PathVariable Long productId) {
        ProductDTO productdto = prodService.viewProductsById(productId);
        return new ResponseEntity<>(productdto, HttpStatus.OK);
    }

    // Update Products by Id
    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProducts(@RequestBody ProductDTO productDto, @PathVariable Long productId) {
        ProductDTO updatedProductsDto = prodService.updateProducts(productDto, productId);
        return new ResponseEntity<>(updatedProductsDto, HttpStatus.OK);
    }

    // Delete Products by Id
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        prodService.deleteProducts(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Add Products to the Cart
    @PostMapping("/cart")
    public ResponseEntity<Void> addProductToCart(@RequestParam String email, @RequestParam Long productId,
                                                 @RequestParam int quantity) {
        int userId = userProfileClient.getUserId(email);
        ProductDTO product = prodService.viewProductsById(productId);
        cartClient.addProductsToCart(userId, productId, product.getProductName(), product.getImageId(),
                product.getPrice(), quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // delete products from cart
    @DeleteMapping("/cart")
    public ResponseEntity<Void> deleteProductFromCart(@RequestParam String email, @RequestParam Long productId) {
        int userId = userProfileClient.getUserId(email);
        cartClient.deleteProductFromCart(userId, productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Upload Product image
    @PostMapping(value = "/admin/product/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) {

        // First, upload image to image-service
        ResponseEntity<ImageResponse> imageResponse = imageClient.uploadImage(image, "product");

        // Get the image ID from the response
        String imageId = imageResponse.getBody().getId();

        // Update product with image reference
        ProductDTO updatedProduct = prodService.updateProductImageReference(productId, imageId);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // Method to get product image
    @GetMapping(value = "/public/product/{productId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        // Get image ID from product
        String imageId = prodService.getProductImageId(productId);

        // Get image data from image-service
        return imageClient.getImage(imageId);
    }

    // Method to delete product image
    @DeleteMapping("/admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> deleteProductImage(@PathVariable Long productId) {
        // Get image ID from product
        String imageId = prodService.getProductImageId(productId);

        // Delete image from image-service
        imageClient.deleteImage(imageId);

        // Update product to remove image reference
        ProductDTO updatedProduct = prodService.updateProductImageReference(productId, null);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
