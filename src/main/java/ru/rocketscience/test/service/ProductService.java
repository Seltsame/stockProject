package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.dto.request.ProductRequestDto;
import ru.rocketscience.test.mapper.ProductMapper;
import ru.rocketscience.test.model.Product;
import ru.rocketscience.test.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDto getProductById(Long id) {
        Optional<Product> entityToGet = productRepository.findById(id);
        if (!entityToGet.isPresent()) {
            throw new ValidateException("Товара с id = " + id + " не существует!");
        }
        return productMapper.fromEntity(entityToGet.get());
    }

    public Long addProduct(ProductRequestDto productRequestDto) {
        Product productToSave = productRepository.save(productMapper.toEntity(productRequestDto));
        return productToSave.getId();
    }
}
