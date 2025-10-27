package one.digitalinnovation.beerstock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.mockito.Mockito;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2L;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenGETIsCalledWithValidNameThenBeerIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder()
                .id(VALID_BEER_ID)
                .name("Skol")
                .brand("Ambev")
                .max(50)
                .quantity(10)
                .build();

        when(beerService.findByName("Skol")).thenReturn(beerDTO);

        mockMvc.perform(get(BEER_API_URL_PATH + "/Skol")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Skol")))
                .andExpect(jsonPath("$.brand", is("Ambev")))
                .andExpect(jsonPath("$.max", is(50)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }
    @Test
    void whenPOSTIsCalledThenBeerIsCreated() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder()
                .name("Heineken")
                .brand("Heineken")
                .max(100)
                .quantity(20)
                .build();

        String beerJson = objectMapper.writeValueAsString(beerDTO);

        when(beerService.createBeer(any(BeerDTO.class))).thenReturn(beerDTO);

        mockMvc.perform(post(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Heineken")))
                .andExpect(jsonPath("$.brand", is("Heineken")))
                .andExpect(jsonPath("$.max", is(100)))
                .andExpect(jsonPath("$.quantity", is(20)));
    }


}