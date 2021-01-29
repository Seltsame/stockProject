package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rosketscience.test.ValidateException;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rosketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rosketscience.test.stockPlace.StockPlace;
import ru.rosketscience.test.stockPlace.StockPlaceRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductMapper productMapper;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockPlaceRepository stockPlaceRepository;
    private final ProductSpecification productSpecification;
    private final ProductOnStockPlaceRepository productOnStockPlaceRepository;
    private final ProductOnStockPlaceSpecification productOnStockPlaceSpecification;

    ProductResponseDto getById(Long id) {
        return productMapper.fromEntity(getProductEntityById(id));
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
        Product productToUpd = getProductEntityById(id);
        productToUpd.setName(productRequestDto.getName());
        productToUpd.setPrice(productRequestDto.getPrice());
        productRepository.save(productToUpd);
    }

    //добавление нескольких однотипных товаров в конкретный склад - место c проверкой на вместимость
       @Transactional
    void addProductsToStockPlace(ProductPlacementDto productPlacementDto) {
        long productId = productPlacementDto.getProductId();//id товара
        Product productEntity = getProductEntityById(productId);
        long stockPlaceId = productPlacementDto.getStockPlaceId(); //id места
        StockPlace stockPlace = getStockPlaceEntityById(stockPlaceId);

        long capacityStockPlace = stockPlace.getCapacity(); //вместимость полки
        long quantityProductFromDTO = productPlacementDto.getQuantityProduct(); //количесвто продукта, которое хотим добавить
        long quantityProductOnStockPlace = productRepository.getSumQuantityProductByStockPlaceId(stockPlaceId);//сколько товара уже лежит
        long totalProductQuantity = quantityProductFromDTO + quantityProductOnStockPlace; //общее количество товара на полке
        if (capacityStockPlace < totalProductQuantity) {
            /*throw new ValidateException("Такое количество товара: " + quantityProductFromDTO + " не поместится на выбранное складское место с id: "
                    + stockPlaceId + ". Место имеет вместимость:" + capacityStockPlace + ". " +
                    "На нем уже лежит: " + quantityProductOnStockPlace + ", выберите другое место!");*/
            validateException(stockPlaceId, capacityStockPlace, quantityProductFromDTO, quantityProductOnStockPlace);
        }
        ProductOnStockPlace productOnStockPlaceEntity = getProductOnStockPlace(productEntity, stockPlace, totalProductQuantity);
        productOnStockPlaceRepository.save(productOnStockPlaceEntity);
    }

    //перемещение товара со складского места одного склада на складское место другого склада
    @Transactional
    ProductMovementResponseDto movementProductsBetweenStocks(ProductMovementRequestDto productMovementRequestDto) {
        long productId = productMovementRequestDto.getProductId();
        Product productEntity = getProductEntityById(productId);
        StockPlace stockPlaceEntity = getStockPlaceEntityById(productMovementRequestDto.getStockPlaceIdFrom());
        StockPlace stockPlaceEntityFinal = getStockPlaceEntityById(productMovementRequestDto.getFinalStockPlaceId());
        Long stockEntityFinalId = stockPlaceEntityFinal.getStock().getId();
        Stock stockEntityFinal = getStockEntityFromId(stockEntityFinalId);
        Long stockPlaceEntityFinalId = stockPlaceEntityFinal.getId(); //id нового складского места, куда добавляется товар
        long capacityStockPlace = stockPlaceEntityFinal.getCapacity(); //вместимость полки куда хотим переместить
        long productQuantityToMove = productMovementRequestDto.getProductQuantityToMove(); //количество товара для перемещения
        long quantityProductOnStockPlace = productRepository.getSumQuantityProductByStockPlaceId(stockPlaceEntityFinalId);//сколько товара уже лежит

        StockPlace stockPlace = getStockPlaceBuilder(stockPlaceEntityFinal, stockEntityFinal);
        /*if (quantityProductOnStockPlace.equals(null)) {
            ProductOnStockPlace productOnStockPlaceEntity = productOnStockPlaceRepository.getByStockPlaceId(stockPlace.getId()).orElseThrow(()
                    -> new ValidateException("Ошибка! Неправильные данные!"));
            productOnStockPlaceEntity.setQuantityProduct(0L);
        }*/
        long totalProductQuantityFinal = productQuantityToMove + quantityProductOnStockPlace; //общее количество товара на полке

        ProductOnStockPlace productOnStockPlaceByStockPlaceAndProduct //ProductOnStockPlace By StockPlace id + product id
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockPlaceAndProduct(stockPlaceEntity.getId(), productId);
        long sumQuantityProductByStockPlaceAndProduct //количество конкретного продукта на конкретной полке
                = productOnStockPlaceByStockPlaceAndProduct.getQuantityProduct();
        if (capacityStockPlace < totalProductQuantityFinal) {
            validateException(stockPlaceEntityFinalId, capacityStockPlace, productQuantityToMove, quantityProductOnStockPlace);
        }

   //     StockPlace stockPlace = getStockPlaceBuilder(stockPlaceEntityFinal, stockEntityFinal);

        if (!(productQuantityToMove == sumQuantityProductByStockPlaceAndProduct)) { //update,если не равно к-во
            ProductOnStockPlace productOnStockPlaceEntity
                    = getProductOnStockPlace(productEntity, stockPlaceEntityFinal, quantityProductOnStockPlace);

            productOnStockPlaceEntity.setQuantityProduct(quantityProductOnStockPlace - productQuantityToMove); //обновляем количество товара на полке
            productOnStockPlaceRepository.save(productOnStockPlaceEntity);
            stockPlaceRepository.save(stockPlace);
            return productMovementResponseDto(productId, stockPlaceEntityFinal, stockEntityFinal);
        }
        stockPlaceRepository.save(stockPlace);
        productOnStockPlaceRepository.deleteByProductId(productId);
        return productMovementResponseDto(productId, stockPlaceEntityFinal, stockEntityFinal);
    }

    //динамический фильтр по критериям по товару
    @Transactional
    ProductFilterResponseDto filterProductByParam(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<ProductResponseDto> productListByParam = productRepository.findAll(
                productSpecification.findByNameAndPrice(name, minPrice, maxPrice))
                .stream()
                .map(productMapper::fromEntity)
                .collect(Collectors.toList());
        return ProductFilterResponseDto.builder()
                .productList(productListByParam)
                .build();
    }

    //получение списка из динамического запроса
    //если создаем отдельный mapStruct, то в target : stockId, source stock.id
    @Transactional
    List<ProductCriteriaFilterResponseDto> criteriaFilterByParam(String city, String product) {
        ProductCriteriaFilterResponseDto.ProductCriteriaFilterResponseDtoBuilder builder = ProductCriteriaFilterResponseDto.builder();
        List<ProductCriteriaFilterResponseDto> resultList = new ArrayList<>();
        productOnStockPlaceRepository.findAll(productOnStockPlaceSpecification.findByCityProduct(city, product))
                .forEach(productOnStockPlace -> {
                    builder.productId(productOnStockPlace.getId());
                    builder.productName(productOnStockPlace.getProduct().getName());
                    builder.stockId(productOnStockPlace.getStockPlace().getId());
                    builder.quantityProduct(productOnStockPlace.getQuantityProduct());
                    resultList.add(builder.build());
                });
        return resultList;
    }

    //Response Dto
    private ProductMovementResponseDto productMovementResponseDto(long productId, StockPlace stockPlaceEntityFinal, Stock stockEntityFinal) {
        return ProductMovementResponseDto.builder()
                .productId(productId)
                .stockId(stockEntityFinal.getId())
                .stockplaceId(stockPlaceEntityFinal.getId())
                .build();
    }

    //создание StockPlace
    private StockPlace getStockPlaceBuilder(StockPlace stockPlaceEntityFinal, Stock stockEntityFinal) {
        return StockPlace.builder()
                .row(stockPlaceEntityFinal.getRow())
                .shelf(stockPlaceEntityFinal.getShelf())
                .capacity(stockPlaceEntityFinal.getCapacity())
                .stock(stockEntityFinal)
                .build();
    }

    //ошибка при добавлении большого количества товара
    private void validateException(Long stockPlaceId, long capacityStockPlace, long productQuantity, long quantityProductOnStockPlace) {
        throw new ValidateException("Такое количество товара: " + productQuantity + " не поместится на выбранное складское место с id: "
                + stockPlaceId + ". Место имеет вместимость:" + capacityStockPlace +
                ". На нем уже лежит: " + quantityProductOnStockPlace + ", выберите другое место!");
    }

    //метод для получения EntityById + Validate
    private Product getProductEntityById(Long productId) {
        return productRepository.getById(productId).orElseThrow(()
                -> new ValidateException("Товара с id = " + productId + " не существует!"));
    }

    //метод для получения EntityById + Validate
    private StockPlace getStockPlaceEntityById(long stockPlaceId) {
        return stockPlaceRepository.getById(stockPlaceId).orElseThrow(()
                -> new ValidateException("Такого места с id: " + stockPlaceId + " не существует!"));
    }

    //метод для получения EntityById + Validate
    private Stock getStockEntityFromId(Long stockId) {
        return stockRepository.getById(stockId).orElseThrow(()
                -> new ValidateException("Такого склада с id: " + stockId + " не существует!"));
    }

    //готовая ProductOnStockPlace updated Entity
    private ProductOnStockPlace getProductOnStockPlace(Product productEntity, StockPlace stockPlace, long totalProductQuantity) {
        ProductOnStockPlace productOnStockPlaceEntity = productOnStockPlaceRepository.getByStockPlaceId(stockPlace.getId()).orElseThrow(()
                -> new ValidateException("Ошибка! Неправильные данные!"));
        productOnStockPlaceEntity.setProduct(productEntity);
        productOnStockPlaceEntity.setStockPlace(stockPlace);
        productOnStockPlaceEntity.setQuantityProduct(totalProductQuantity); //обновляем количество товара на полке
        return productOnStockPlaceEntity;
    }
}



