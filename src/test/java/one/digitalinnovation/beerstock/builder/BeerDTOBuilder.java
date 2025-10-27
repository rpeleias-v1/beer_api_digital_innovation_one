package one.digitalinnovation.beerstock.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.enums.BeerType;

@Getter
@Setter
@Builder
public class BeerDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private BeerType type = BeerType.LAGER;

    public BeerDTO toBeerDTO() {
        return BeerDTO.builder()
                .id(id)
                .name(name)
                .brand(brand)
                .max(max)
                .quantity(quantity)
                .type(type)
                .build();
    }
}