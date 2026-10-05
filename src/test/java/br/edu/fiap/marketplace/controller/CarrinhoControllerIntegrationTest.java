package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.exception.GlobalExceptionHandler;
import br.edu.fiap.marketplace.service.CarrinhoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TODO implementar um @WebMvcTest com MockMvc e o CarrinhoService mockado.
 * O cenário mínimo deve comprovar status, JSON e validação HTTP.
 */
@WebMvcTest( CarrinhoController.class )
@Import( GlobalExceptionHandler.class )
class CarrinhoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarrinhoService carrinhoService;

    @Nested
    class criarCarrinho {
        @Test
        @DisplayName( "POST, deve criar um novo carrinho")
        void deveCadastrarUmNovoCarrinho() throws Exception{
            CarrinhoRequest request = new CarrinhoRequest( 2L, 3L, 5 );

            CarrinhoResponse response = new CarrinhoResponse(
                    1L, 2L, "nome", 3L, "produto",
                    new BigDecimal("100"), 2, new BigDecimal(200), StatusCarrinho.FINALIZADO, Instant.now()
            );

            when( carrinhoService.novoCarrinho( any( CarrinhoRequest.class ) ) ).thenReturn( response );


            mockMvc.perform( post( "/api/carrinhos" )
                    .contentType( MediaType.APPLICATION_JSON )
                    .content( objectMapper.writeValueAsBytes( request ) ) )
                    .andExpect( status().isCreated() );


        }
    }
}
