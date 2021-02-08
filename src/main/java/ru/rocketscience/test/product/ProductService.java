package ru.rocketscience.test.product;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.rocketscience.test.ValidateException;
import ru.rocketscience.test.common.IdNameDto;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlace;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlaceRepository;
import ru.rocketscience.test.productOnStockPlace.ProductOnStockPlaceSpecification;
import ru.rocketscience.test.stock.Stock;
import ru.rocketscience.test.stock.StockRepository;
import ru.rocketscience.test.stockPlace.StockPlace;
import ru.rocketscience.test.stockPlace.StockPlaceFilterDto;
import ru.rocketscience.test.stockPlace.StockPlaceRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Product prodId = getProductEntityById(id);
        Product productToUpd
                = productMapper.toEntityWithId(prodId.getId(), productRequestDto);
        productRepository.save(productToUpd);
    }

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
            trowValidateException(stockPlaceId, capacityStockPlace, quantityProductFromDTO, quantityProductOnStockPlace);
        }
        ProductOnStockPlace productOnStockPlaceEntity = getProductOnStockPlace(productEntity, stockPlace, totalProductQuantity);
        productOnStockPlaceRepository.save(productOnStockPlaceEntity);
    }

    //перемещение товара со складского места одного склада на складское место другого склада
    @Transactional
    ProductMovementResponseDto movementProductsBetweenStocks(ProductMovementRequestDto productMovementRequestDto) {
        long productId = productMovementRequestDto.getProductId();
        Product productEntity = getProductEntityById(productId);
        StockPlace stockPlaceEntityFrom = getStockPlaceEntityById(productMovementRequestDto.getStockPlaceIdFrom());
        StockPlace stockPlaceEntityTo = getStockPlaceEntityById(productMovementRequestDto.getStockPlaceIdTo());
        Long stockEntityToId = stockPlaceEntityTo.getStock().getId();
        Stock stockEntityTo = getStockEntityFromId(stockEntityToId);
        Long stockPlaceEntityToId = stockPlaceEntityTo.getId(); //id нового складского места, куда добавляется товар
        long capacityStockPlace = stockPlaceEntityTo.getCapacity(); //вместимость полки куда хотим переместить
        long productQuantityToMove = productMovementRequestDto.getProductQuantityToMove(); //количество товара для перемещения
        long quantityProductOnStockPlace = productRepository.getSumQuantityProductByStockPlaceId(stockPlaceEntityToId);//сколько товара уже лежит

        StockPlace stockPlace = getStockPlaceBuilder(stockPlaceEntityTo, stockEntityTo);
        /*if (quantityProductOnStockPlace.equals(null)) {
            ProductOnStockPlace productOnStockPlaceEntity = productOnStockPlaceRepository.getByStockPlaceId(stockPlace.getId()).orElseThrow(()
                    -> new ValidateException("Ошибка! Неправильные данные!"));
            productOnStockPlaceEntity.setQuantityProduct(0L);
        }*/
        long totalProductQuantityFinal = productQuantityToMove + quantityProductOnStockPlace; //общее количество товара на полке

        ProductOnStockPlace productOnStockPlaceByStockPlaceAndProduct //ProductOnStockPlace By StockPlace id + product id
                = productOnStockPlaceRepository.getProductOnStockPlaceByStockPlaceAndProduct(stockPlaceEntityFrom.getId(), productId);
        long sumQuantityProductByStockPlaceAndProduct //количество конкретного продукта на конкретной полке
                = productOnStockPlaceByStockPlaceAndProduct.getQuantityProduct();
        if (capacityStockPlace < totalProductQuantityFinal) {
            trowValidateException(stockPlaceEntityToId, capacityStockPlace, productQuantityToMove, quantityProductOnStockPlace);
        }

        //update,если к-во перемещаемого товара меньше того, сколько остается на полке, с которой перемещаем
        if (productQuantityToMove < sumQuantityProductByStockPlaceAndProduct) {
            ProductOnStockPlace productOnStockPlaceEntity
                    = getProductOnStockPlace(productEntity, stockPlaceEntityTo, quantityProductOnStockPlace);

            productOnStockPlaceEntity.setQuantityProduct(quantityProductOnStockPlace - productQuantityToMove); //обновляем количество товара на полке
            productOnStockPlaceRepository.save(productOnStockPlaceEntity);
            stockPlaceRepository.save(stockPlace);
            return productMovementResponseDto(productId, stockPlaceEntityTo, stockEntityTo);
        }//если равно, то не остаётся товара на полке - delete
        stockPlaceRepository.save(stockPlace);
        productOnStockPlaceRepository.deleteByProductId(productId);
        return productMovementResponseDto(productId, stockPlaceEntityTo, stockEntityTo);
    }

    //динамический фильтр по критериям по товару
    @Transactional
    ProductFilterResponseDto filterProductByParam(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<ProductResponseDto> productListByParam = productRepository.findAll(
                productSpecification.findByNameAndPrice(name, minPrice, maxPrice))
                .stream()
                .map(productMapper::fromEntity)
                .collect(Collectors.toList());
        return new ProductFilterResponseDto(productListByParam);
    }

      /*  @Transactional
        List<FilterResultDto> resultCriteriaFilter(String city, String product) {
            List<FilterResultDto> result = new ArrayList<>();
            FilterResultDto current = FilterResultDto.builder()
                    .product(new IdNameDto(-1L, ""))
                    .stockDto(new FilterResultDto.StockDto(-1L, "")).build();
            IdNameDto.IdNameDtoBuilder idNameBuilder = IdNameDto.builder();
            List<ProductOnStockPlace> pspList
                    = productOnStockPlaceRepository.findAll(productOnStockPlaceSpecification.findByCityProduct(city, product));
            for (ProductOnStockPlace pSp : pspList) {
                if (!current.getProduct().getId().equals(pSp.getProduct().getId())
                        || !current.getStockDto().getId().equals(pSp.getStockPlace().getStock().getId())) {
                    IdNameDto.IdNameDtoBuilder productBuilder = idNameBuilder
                            .id(pSp.getProduct().getId())
                            .name(pSp.getProduct().getName());
                    //  FilterResultDto.FilterResultDtoBuilder filterBuilder = FilterResultDto.builder();
                    FilterResultDto.StockDto stockDtoObj = new FilterResultDto.StockDto(
                            pSp.getStockPlace().getStock().getId(), pSp.getStockPlace().getStock().getName());
                    current = new FilterResultDto(productBuilder.build(), stockDtoObj, new ArrayList<>()); //сфотрмировать product + stockDto
                    result.add(current);
                } //формируем StockPlace и пихаем его в лист
                StockPlace stockPlace = pSp.getStockPlace();
                StockPlaceFilterDto.StockPlaceFilterDtoBuilder spBuilder
                        = StockPlaceFilterDto.builder()
                        .id(stockPlace.getId())
                        .row(stockPlace.getRow())
                        .shelf(stockPlace.getShelf())
                        .quantity(pSp.getQuantityProduct());
                current.getStockPlaceFilterDto().add(spBuilder.build()); //сформировать stockPlaceDto + взять оттуда quantity сложить
                long quantity = 0L;
                quantity += pSp.getQuantityProduct();
                current.getStockDto().setQuantity(quantity);
                result.add(current); //?
            }*/

    //получение списка из динамического запроса
    //если создаем отдельный mapStruct, то в target : stockId, source stock.id
    @Transactional
    List<FilterResultDto> resultCriteriaFilter(String city, String product) {
        List<FilterResultDto> result = new ArrayList<>();
        List<ProductOnStockPlace> resultPSP
                = productOnStockPlaceRepository.findAll(productOnStockPlaceSpecification.findByCityProduct(city, product)
        );
        for (ProductOnStockPlace ignored : resultPSP) {
            result.add(converter(resultPSP));
        }
        return result;
    }

    //конвертер для динамического фильтра ProductOnStockPlace -> FilterResult
    private FilterResultDto converter(List<ProductOnStockPlace> pspList) {
        FilterResultDto.FilterResultDtoBuilder result = FilterResultDto.builder();
        List<FilterResultDto.StockDto> listStockDto = new ArrayList<>(); //список StockDto
        IdNameDto.IdNameDtoBuilder idNameBuilder = IdNameDto.builder();
        for (ProductOnStockPlace psp : pspList) {
            idNameBuilder
                    .id(psp.getProduct().getId())
                    .name(psp.getProduct().getName());

            Map<Long, List<StockPlaceFilterDto>> stockPlaceMap = convertToMap(pspList);
            Map<Long, Long> stockPlaceMapQuantity = convertToMapQuantity(pspList);
            FilterResultDto.StockDto stockDto //полностью собранный StockDto
                    = getStockDto(stockPlaceMap, stockPlaceMapQuantity, psp);
            listStockDto.add(stockDto);
        }

        result.product(idNameBuilder.build());
        result.stockDto(listStockDto);
        return result.build();
    }

    //сборка StockDto
    private FilterResultDto.StockDto getStockDto(Map<Long, List<StockPlaceFilterDto>> stockPlaceMap,
                                                 Map<Long, Long> stockPlaceMapQuantity,
                                                 ProductOnStockPlace psp) {
        Long stockId = psp.getStockPlace().getStock().getId();
        FilterResultDto.StockDto stockDto = new FilterResultDto.StockDto(
                stockId, psp.getStockPlace().getStock().getName());

        stockDto.setCity(psp.getStockPlace().getStock().getCity()); //имя города в StockDto
        for (Map.Entry<Long, List<StockPlaceFilterDto>> entry : stockPlaceMap.entrySet()) {
            if (stockId.equals(entry.getKey())) {
                stockDto.setStockPlaceFilterDto(entry.getValue()); //List<StockPlaceFilterDto> принадлежащий Stock
            }
        }
        for (Map.Entry<Long, Long> entry : stockPlaceMapQuantity.entrySet()) {
            if (stockId.equals(entry.getKey())) {
                stockDto.setQuantity(entry.getValue()); //добавляем quantity в конкретный склад
            }
        }
        return stockDto;
    }

    //Map<stockId, List<StockPlaceFilterDto>>
    private Map<Long, List<StockPlaceFilterDto>> convertToMap(List<ProductOnStockPlace> list) {
        List<StockPlaceFilterDto> stockPlaceList = new ArrayList<>();
        Map<Long, List<StockPlaceFilterDto>> result = new HashMap<>();
        StockPlaceFilterDto.StockPlaceFilterDtoBuilder filterBuilder = StockPlaceFilterDto.builder();
        for (ProductOnStockPlace psp : list) {
            Long id = psp.getStockPlace().getStock().getId();
            //проверяем входящие id на совпадение с теми, которые есть в map
            for (Map.Entry<Long, List<StockPlaceFilterDto>> entry : result.entrySet()) {
                if (!id.equals(entry.getKey())) { //если не совпадает, то пишем k, v
                    getStockPlaceFilterDtoBuilder(filterBuilder, psp); //собираем builder с готовой StPlFilterDto
                    stockPlaceList.add(filterBuilder.build());
                    result.put(id, stockPlaceList);
                } //если совпадает, то добавляем в List<value>
                List<StockPlaceFilterDto> listValues = entry.getValue();
                getStockPlaceFilterDtoBuilder(filterBuilder, psp);
                listValues.add(filterBuilder.build());
                entry.setValue(listValues);
            }
        }
        return result;
    }

    //Map<stockId, quantityStock>
    private Map<Long, Long> convertToMapQuantity(List<ProductOnStockPlace> list) {
        Map<Long, Long> result = new HashMap<>();
        for (ProductOnStockPlace psp : list) {
            Long stockId = psp.getStockPlace().getStock().getId();
            long quantity = psp.getQuantityProduct();
            for (Map.Entry<Long, Long> entry : result.entrySet()) {
                if (!stockId.equals(entry.getKey())) { //если stockId не совпадает, то просто добавляем id - quantity
                    result.put(stockId, quantity);
                } //если совпадает, то += quantity
                quantity += psp.getQuantityProduct();
                entry.setValue(quantity);
            }
        }
        return result;
    }

    //конвертер для динамического фильтра Product -> FilterResult
    /*private FilterResultDto converter(Product product) {
        IdNameDto productIdName = IdNameDto.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
        long quantity = 0L;

                Set<ProductOnStockPlace> stockPlaceSet = product.getProductOnStockPlaceSet();
        List<StockPlaceFilterDto> stockPlaceList = new ArrayList<>();
        for (ProductOnStockPlace ps : stockPlaceSet) {
            Long stockId = ps.getStockPlace().getStock().getId();
            StockPlaceFilterDto stockPlaceFilterDto = StockPlaceFilterDto.builder()
                    .id(ps.getStockPlace().getId())
                    .row(ps.getStockPlace().getRow())
                    .shelf(ps.getStockPlace().getShelf())
                    .quantity(ps.getQuantityProduct())
                    .build();
            quantity += ps.getQuantityProduct();
            stockPlaceList.add(stockPlaceFilterDto);
        }
        List<FilterResultDto.StockDto> stockDtoList = new ArrayList<>();
        for (ProductOnStockPlace ps : stockPlaceSet) {
            Stock stock = ps.getStockPlace().getStock();
            Long id = ps.getStockPlace().getStock().getId();
            String name = stock.getName();
            String city = stock.getCity();
            FilterResultDto.StockDto stockDto = new FilterResultDto.StockDto(id, name);
            stockDto.setQuantity(quantity);
            stockDto.setCity(city);
            stockDto.setStockPlaceFilterDto(stockPlaceList);
            stockDtoList.add(stockDto);
        }
        return FilterResultDto.builder()
                .product(productIdName)
                .stockDto(stockDtoList)
                .build();
    }*/
    /*private FilterResultDto converter(Product product) {
        IdNameDto productIdName = IdNameDto.builder()
                .id(product.getId())
                .name(product.getName())
                .build();

        List<FilterResultDto.StockDto> filterStockDtoList = new ArrayList<>();
        List<StockPlaceFilterDto> stockPlaceList = new ArrayList<>();
        Set<ProductOnStockPlace> stockPlaceSet = product.getProductOnStockPlaceSet(); //set psp по товару
        for (ProductOnStockPlace psp : stockPlaceSet) {
            long quantity = 0L;
            Stock stock = psp.getStockPlace().getStock();
            Long stockId = stock.getId(); //берем stockId
            Set<ProductOnStockPlace> byStockId
                    = stockPlaceSet
                    .stream()
                    .filter(each -> //фильтр соответствующих stock place по stock id
                            each.getStockPlace().getStock().getId().equals(stockId)).collect(Collectors.toSet());
            for (ProductOnStockPlace ps : byStockId) { //заполнение листа stock place, в соответствии со stock id
                StockPlaceFilterDto stockPlaceFilterDto = StockPlaceFilterDto.builder()
                        .id(ps.getStockPlace().getId())
                        .row(ps.getStockPlace().getRow())
                        .shelf(ps.getStockPlace().getShelf())
                        .quantity(ps.getQuantityProduct())
                        .build();
                quantity += ps.getQuantityProduct();
                stockPlaceList.add(stockPlaceFilterDto);
            } //заполнение stock dto исходя из определенного stockId
            String name = stock.getName();
            String city = stock.getCity();
            FilterResultDto.StockDto stockDto = new FilterResultDto.StockDto(stockId, name);
            stockDto.setQuantity(quantity);
            stockDto.setCity(city);
            stockDto.setStockPlaceFilterDto(stockPlaceList);
            filterStockDtoList.add(stockDto);
        }
        return FilterResultDto.builder()
                .product(productIdName)
                .stockDto(filterStockDtoList)
                .build();
    }*/

    //builder с готовой StockPlaceFilterDto
    private void getStockPlaceFilterDtoBuilder(StockPlaceFilterDto.StockPlaceFilterDtoBuilder filterBuilder, ProductOnStockPlace psp) {
        StockPlace stockPlace = psp.getStockPlace();
        filterBuilder.id(stockPlace.getId());
        filterBuilder.row(stockPlace.getRow());
        filterBuilder.shelf(stockPlace.getShelf());
        filterBuilder.quantity(psp.getQuantityProduct());
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
    private void trowValidateException(
            Long stockPlaceId, long capacityStockPlace, long productQuantity, long quantityProductOnStockPlace) {
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



