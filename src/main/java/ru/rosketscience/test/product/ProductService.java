package ru.rosketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rosketscience.test.stockPlace.StockPlace;
import ru.rosketscience.test.stockPlace.StockPlaceRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StockPlaceRepository stockPlaceRepository;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;

    ProductResponseDto getById(Long id) {
        Product productToGet = productRepository.findById(id).orElseThrow(()
                -> new ValidateException("Товара с id = " + id + " не существует!"));
        return productMapper.fromEntity(productToGet);
    }

    @Transactional
    Long add(ProductRequestDto productRequestDto) {
        Product productToSave = productRepository.save(productMapper.toEntity(productRequestDto));
        return productMapper.fromEntity(productToSave).getId();
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

    //Добавление товара в конкретный склад - место:
    //НУЖЕН ЛИ ВООБЩЕ ЭТОТ МЕТОД?
    @Transactional
    Long addToStockPlace(ProductRequestDto productRequestDto) {
        Product productEntity = productMapper.toEntity(productRequestDto);
        int stockPlaceId = productRequestDto.getStockPlaceId();
        StockPlace stockPlace = stockPlaceRepository.getById((long) stockPlaceId).orElseThrow(()
                -> new ValidateException("Такого места не существует!"));
        productRepository.save(productEntity);

        ProductOnStockPlace productOnStockPlace = ProductOnStockPlace.builder()
                .product(productEntity)
                .stockPlace(stockPlace)
                .build();
        ProductOnStockPlace savedEntity = productOnStockPlaceRepository.save(productOnStockPlace);
        // ProductResponseDto productResponseDto = new ProductResponseDto();
        //   productResponseDto.

        return null;//productResponseDto.getId();
    }

    //добавление нескольких однотипных товаров в конкретный склад - место c проверкой на вместимость
    //напиши тест!!!
    @Transactional
    Long addProductsToStockPlace(ProductBunchRequestDto productBunchRequestDto) {
        StockPlace stockPlace = stockPlaceRepository.getById((productBunchRequestDto.getStockPlaceId())).orElseThrow(()
                -> new ValidateException("Такого места не существует!"));
        long stockPlaceId = productBunchRequestDto.getStockPlaceId();
        int capacityStockPlace = stockPlace.getCapacity(); //вместимость полки
        int quantityProductFromDTO = productBunchRequestDto.getQuantityProduct(); //количесвто продукта, которое хотим добавить
        int maxQuantityProductOnStockPlace = productOnStockPlaceRepository.getMaxQuantityProduct(stockPlaceId);//сколько товара уже лежит
        int totalProductQuantity = quantityProductFromDTO + maxQuantityProductOnStockPlace; //общее количество товара на полке
        int freeSpace = capacityStockPlace - totalProductQuantity;
        if (freeSpace < totalProductQuantity) {
            throw new ValidateException("Такое количество товара не поместится на выборанное складское место с id: "
                    + stockPlaceId + ", выберите другое место!");
        } else {
            Product productEntity = Product.builder()
                    .name(productBunchRequestDto.getName())
                    .price(productBunchRequestDto.getPrice())
                    .build();
            productRepository.save(productEntity);

            ProductOnStockPlace productOnStockPlace = ProductOnStockPlace.builder()
                    .product(productEntity)
                    .stockPlace(stockPlace)
                    .quantityProduct(quantityProductFromDTO)
                    .build();
            ProductOnStockPlace savedEntity = productOnStockPlaceRepository.save(productOnStockPlace);
            ProductResponseDto productResponseDto = ProductResponseDto.builder()
                    .id(savedEntity.getId())
                    .build();
            return productResponseDto.getId();
        }
    }
}
        /*ProductOnStockPlace productOnStockPlace = new ProductOnStockPlace();
        productOnStockPlace.setProduct(productEntity);
        productOnStockPlace.setStockPlace(stockPlace);
        ProductOnStockPlace save = productOnStockPlaceRepository.save(productOnStockPlace);
          return save.getId();     */
/*
            Set<ProductOnStockPlace> allProductsByStockPlaceId
                = stockPlaceRepository.getAllProductsByStockPlaceId((long) stockPlaceId);
        stockPlace.setStockPlaceOnStockPlaceSet(allProductsByStockPlaceId);
*/

