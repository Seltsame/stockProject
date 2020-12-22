package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    ProductResponseDto getById(Long id) {
        Product productToGet = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        return productMapper.fromEntity(productToGet);
    }

    Long add(ProductRequestDto productRequestDto) {
        Product productToSave = productRepository.save(productMapper.toEntity(productRequestDto));
        return productToSave.getId();
    }

    @Transactional
    void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidateException("Товара с id = " + id + " не существует!");
        }
    }

    @Transactional
    void update(Long id, ProductRequestDto productRequestDto) {
        Product productToUpd = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        productToUpd.setName(productRequestDto.getName());
        productToUpd.setPrice(productRequestDto.getPrice());
        productRepository.save(productToUpd);
    }
}
