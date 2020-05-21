package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenNewBeerInformedThenShouldBeCreated() throws BeerAlreadyRegisteredException {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer = beerMapper.toModel(beerDTO);

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO createdBeerDTO = beerService.createBeer(beerDTO);

        assertEquals(beerDTO.getId(), createdBeerDTO.getId());
        assertEquals(beerDTO.getName(), createdBeerDTO.getName());
        assertEquals(beerDTO.getType(), createdBeerDTO.getType());
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(beerDTO);

        when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        assertThrows(BeerAlreadyRegisteredException.class,() -> beerService.createBeer(beerDTO)) ;
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));

        BeerDTO foundBeerDTO = beerService.findByName(expectedBeerDTO.getName());

        assertEquals(expectedBeerDTO, foundBeerDTO);
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class,() -> beerService.findByName(expectedBeerDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        List<BeerDTO> foundBeerDTO = beerService.listAll();

        assertFalse(foundBeerDTO.isEmpty());
        assertEquals(expectedBeerDTO, foundBeerDTO.get(0));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyList() {
        when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<BeerDTO> foundBeerDTO = beerService.listAll();

        assertTrue(foundBeerDTO.isEmpty());
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
        BeerDTO expectedExcludedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedExcludedBeer = beerMapper.toModel(expectedExcludedBeerDTO);

        when(beerRepository.findById(expectedExcludedBeerDTO.getId())).thenReturn(Optional.of(expectedExcludedBeer));
        doNothing().when(beerRepository).deleteById(expectedExcludedBeer.getId());

        beerService.deleteById(expectedExcludedBeerDTO.getId());

        verify(beerRepository, times(1)).findById(expectedExcludedBeerDTO.getId());
        verify(beerRepository, times(1)).deleteById(expectedExcludedBeerDTO.getId());
    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class,() -> beerService.deleteById(INVALID_BEER_ID));
    }
}
