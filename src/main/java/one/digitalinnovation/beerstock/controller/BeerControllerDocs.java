package one.digitalinnovation.beerstock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;

@Api("Manages beer stock")
public interface BeerControllerDocs {

    @ApiOperation(value = "Hello world!!")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns a simple hello world")
    })
    BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;
}
