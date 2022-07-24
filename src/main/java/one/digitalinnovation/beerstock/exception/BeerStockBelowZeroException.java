package one.digitalinnovation.beerstock.exception;

public class BeerStockBelowZeroException extends Exception {
    public BeerStockBelowZeroException(Long id, int quantityToDecrement) {
        super(String.format("Beers with %s ID to decrement informed exceeds the min stock capacity: %s", id, quantityToDecrement));
    }
}
