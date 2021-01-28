package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rocketscience.test.stockPlace.StockPlaceRepository;

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

    //добавление нескольких однотипных товаров в конкретный склад - место c проверкой на вместимость
    //добавь проверку на существование товара типа прогнать name по set/list из товаров, если нет, то предложить создать новое
    @Transactional
    void addProductsToStockPlace(ProductPlacementDto productPlacementDto) {
        long productId = productPlacementDto.getProductId();//id товара
        Product productEntity = productRepository.getById(productId).orElseThrow(()
                -> new ValidateException("Товара с id = " + productId + " не существует!"));
        long stockPlaceId = productPlacementDto.getStockPlaceId(); //id места
        StockPlace stockPlace = stockPlaceRepository.getById(stockPlaceId).orElseThrow(()
                -> new ValidateException("Такого места с id: " + stockPlaceId + " не существует!"));

        long capacityStockPlace = stockPlace.getCapacity(); //вместимость полки
        long quantityProductFromDTO = productPlacementDto.getQuantityProduct(); //количесвто продукта, которое хотим добавить
        long quantityProductOnStockPlace = productRepository.getSumQuantityProductByStockPlaceId(stockPlaceId);//сколько товара уже лежит
        long totalProductQuantity = quantityProductFromDTO + quantityProductOnStockPlace; //общее количество товара на полке
        if (capacityStockPlace < totalProductQuantity) {
            throw new ValidateException("Такое количество товара: " + quantityProductFromDTO + " не поместится на выбранное складское место с id: "
                    + stockPlaceId + ". Место имеет вместимость:" + capacityStockPlace + ". " +
                    "На нем уже лежит: " + quantityProductOnStockPlace + ", выберите другое место!");
        }
        ProductOnStockPlace productOnStockPlaceEntity = productOnStockPlaceRepository.getByStockPlaceId(stockPlaceId).orElseThrow(()
                -> new ValidateException("Ошибка! Неправильные данные!"));
        productOnStockPlaceEntity.setProduct(productEntity);
        productOnStockPlaceEntity.setStockPlace(stockPlace);
        productOnStockPlaceEntity.setQuantityProduct(totalProductQuantity); //обновляем количество товара на полке
        productOnStockPlaceRepository.save(productOnStockPlaceEntity);
    }
}



