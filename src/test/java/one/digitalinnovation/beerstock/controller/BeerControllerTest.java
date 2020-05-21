package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2l;

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOBuilder.asJsonString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithouRequiredFieldThenAnErrorIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setName(null);

        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(BeerDTOBuilder.asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        mockMvc.perform(get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithNotRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        mockMvc.perform(get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        when(beerService.listAll()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(beerService).deleteById(VALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + VALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService, times(1)).deleteById(VALID_BEER_ID);
    }

    @Test
    void whenDELETEIsCalledWithoutValidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
