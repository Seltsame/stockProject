package ru.rocketscience.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.dto.ProductResponseDto;
import ru.rocketscience.test.dto.request.ProductRequestDto;
import ru.rocketscience.test.mapper.ProductMapper;
import ru.rocketscience.test.model.Product;
import ru.rocketscience.test.repository.ProductRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDto getById(Long id) {
        Product productToGet = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        return productMapper.fromEntity(productToGet);
    }

    public Long add(ProductRequestDto productRequestDto) {
        Product productToSave = productRepository.save(productMapper.toEntity(productRequestDto));
        return productToSave.getId();
    }

    @Transactional
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidateException("Товара с id = " + id + " не существует!");
        }
    }

    @Transactional
    public void update(Long id, ProductRequestDto productRequestDto) {
        Product productToUpd = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        productToUpd.setName(productRequestDto.getName());
        productToUpd.setPrice(productRequestDto.getPrice());
        productRepository.save(productToUpd);
    }
}
