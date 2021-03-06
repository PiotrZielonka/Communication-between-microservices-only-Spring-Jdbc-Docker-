package com.task.product.controller;

import com.task.product.controller.errors.IdInDtoExist;
import com.task.product.service.ProductService;
import com.task.product.service.dto.ProductDto;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  private final Logger log = LoggerFactory.getLogger(ProductController.class);

  private final ProductService productService;


  public ProductController(
      ProductService productService) {
    this.productService = productService;
  }

  @PostMapping("/products")
  public void createProduct(@Valid @RequestBody ProductDto productDto) {
    log.debug("REST request to save Product : {}", productDto);
    if (productDto.getId() != null) {
      throw new IdInDtoExist();
    }
    productService.save(productDto);
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<ProductDto> getProductByCreditId(@PathVariable Long id) {
    log.debug("REST request to get Product by CreditId");
    Optional<ProductDto> productDto = productService.findByCreditId(id);
    return ResponseEntity.of(productDto);
  }
}
