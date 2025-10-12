package com.dio.beerstock.service;

import com.dio.beerstock.dto.BeerDTO;
import com.dio.beerstock.entity.Beer;
import com.dio.beerstock.exception.BeerAlreadyRegisteredException;
import com.dio.beerstock.exception.BeerNotFoundException;
import com.dio.beerstock.exception.BeerStockExceededException;
import com.dio.beerstock.mapper.BeerMapper;
import com.dio.beerstock.repository.BeerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    // Configuração do Ambiente de Teste:
    
    // Injeta o mock do Repository no Service (o objeto a ser testado)
    @InjectMocks
    private BeerService beerService;

    // Cria um objeto mockado (duplicata) do BeerRepository
    @Mock
    private BeerRepository beerRepository;

    // Instância do Mapper para conversão (necessário para os testes)
    private BeerMapper beerMapper = BeerMapper.INSTANCE;


    // =========================================================================
    // CASOS DE TESTE para CREATE (Criação)
    // =========================================================================

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTO.builder().id(1L).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

        // WHEN
        // 1. Simula que a cerveja não existe no repositório
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        // 2. Simula que o repositório salva e retorna a cerveja
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        // THEN
        BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredBeerIsGivenThenAnExceptionShouldBeThrown() {
        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTO.builder().id(1L).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();
        Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

        // WHEN
        // Simula que a cerveja JÁ existe no repositório
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        // THEN
        // Verifica que a exceção correta é lançada
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));

        // Verifica que o método save NUNCA foi chamado (com Mockito.never())
        verify(beerRepository, never()).save(any(Beer.class));
    }
    
    // =========================================================================
    // CASOS DE TESTE para FIND (Busca)
    // =========================================================================

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // GIVEN
        BeerDTO expectedBeerDTO = BeerDTO.builder().id(1L).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();
        Beer expectedFoundBeer = beerMapper.toModel(expectedBeerDTO);

        // WHEN
        // Simula que a busca por nome retorna a cerveja
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // THEN
        BeerDTO foundBeerDTO = beerService.findByName(expectedBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedBeerDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // GIVEN
        String notFoundBeerName = "Brahma";

        // WHEN
        // Simula que a busca por nome retorna Optional.empty()
        when(beerRepository.findByName(notFoundBeerName)).thenReturn(Optional.empty());

        // THEN
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(notFoundBeerName));
    }
    
    // =========================================================================
    // CASOS DE TESTE para DELETE (Exclusão)
    // =========================================================================

    @Test
    void whenValidIdIsGivenThenABeerShouldBeDeleted() throws BeerNotFoundException {
        // GIVEN
        Long validId = 1L;
        Beer expectedDeletedBeer = Beer.builder().id(validId).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();

        // WHEN
        // 1. Simula que a cerveja EXISTE
        when(beerRepository.findById(validId)).thenReturn(Optional.of(expectedDeletedBeer));
        // 2. Executa o método de exclusão
        beerService.deleteById(validId);

        // THEN
        // Verifica se o método de exclusão do repositório foi chamado EXATAMENTE 1 vez.
        verify(beerRepository, times(1)).deleteById(validId);
    }
    
    @Test
    void whenInvalidIdIsGivenThenAnExceptionShouldBeThrown() {
        // GIVEN
        Long invalidId = 2L;

        // WHEN
        // Simula que a cerveja NÃO existe
        when(beerRepository.findById(invalidId)).thenReturn(Optional.empty());

        // THEN
        // Verifica que a exceção BeerNotFoundException é lançada
        assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(invalidId));
        
        // Verifica que o método de exclusão NUNCA foi chamado
        verify(beerRepository, never()).deleteById(invalidId);
    }

    // =========================================================================
    // CASOS DE TESTE para INCREMENT (Incremento de Estoque)
    // =========================================================================
    
    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        // GIVEN
        Long validId = 1L;
        int quantityToIncrement = 10;
        
        BeerDTO expectedBeerDTO = BeerDTO.builder().id(validId).name("Brahma").brand("Ambev").quantity(20).max(50).type("Lager").build();
        
        // Cerveja atual (antes do incremento)
        Beer beerToUpdate = Beer.builder().id(validId).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();
        // Cerveja esperada (após o incremento: 10 + 10 = 20)
        Beer incrementedBeer = Beer.builder().id(validId).name("Brahma").brand("Ambev").quantity(20).max(50).type("Lager").build();


        // WHEN
        when(beerRepository.findById(validId)).thenReturn(Optional.of(beerToUpdate));
        when(beerRepository.save(incrementedBeer)).thenReturn(incrementedBeer); // Simula o save do objeto atualizado

        // THEN
        BeerDTO incrementedBeerDTO = beerService.increment(validId, quantityToIncrement);
        
        assertThat(incrementedBeerDTO.getQuantity(), is(equalTo(20)));
        
        // Verifica se o método save foi chamado EXATAMENTE 1 vez.
        verify(beerRepository, times(1)).save(incrementedBeer); 
    }
    
    @Test
    void whenIncrementIsCalledExceedsMaxThenThrowException() {
        // GIVEN
        Long validId = 1L;
        int quantityToIncrement = 50; // Tentar adicionar 50
        
        // Estoque atual: 10. Max: 50. 10 + 50 = 60 (Excede o limite)
        Beer beerToUpdate = Beer.builder().id(validId).name("Brahma").brand("Ambev").quantity(10).max(50).type("Lager").build();

        // WHEN
        when(beerRepository.findById(validId)).thenReturn(Optional.of(beerToUpdate));

        // THEN
        // Verifica que a exceção de estoque excedido é lançada
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(validId, quantityToIncrement));
        
        // Verifica que o método save NUNCA foi chamado
        verify(beerRepository, never()).save(any(Beer.class)); 
    }
}
