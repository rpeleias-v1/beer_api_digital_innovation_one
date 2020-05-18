package one.digitalinnovation.beerstock.repository;

import one.digitalinnovation.beerstock.entity.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    Beer findByName(String name);
}
