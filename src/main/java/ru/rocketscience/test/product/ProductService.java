package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StockPlaceRepository stockPlaceRepository;

    ProductResponseDto getById(Long id) {
        Product productToGet = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        return productMapper.fromEntity(productToGet);
    }

    /*Long add(ProductRequestDto productRequestDto) {
        Product productToSave = productRepository.save(productMapper.toEntity(productRequestDto));
        return productMapper.fromEntity(productToSave).getId();
    }*/
    @Transactional
    Long add(ProductRequestDto productRequestDto) {
        Product entity = productMapper.toEntity(productRequestDto);
        int lastQuantityProduct = productRepository.getMaxQuantityProduct(); //берем количество товара
        StockPlace stockPlace
                = stockPlaceRepository.getById((long)productRequestDto.getStockPlaceId()).orElseThrow(()
                -> new ValidateException("Такого товара не существует!"));
        entity.setStockPlace(stockPlace);
        entity.setQuantityProduct(lastQuantityProduct + 1); //в данном случае товар увеличивается на 1, тк добавление одного товара!!!
        Product addProduct = productRepository.save(entity);
        return productMapper.fromEntity(addProduct).getId();
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
