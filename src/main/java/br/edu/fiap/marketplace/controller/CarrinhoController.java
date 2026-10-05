package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.CarrinhoRequest;
import br.edu.fiap.marketplace.dto.CarrinhoResponse;
import br.edu.fiap.marketplace.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/carrinhos")
@Tag(name = "Carrinhos")
@SecurityRequirement(name = "bearerAuth")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo carrinho.")
    @ApiResponse(responseCode = "201", description = "Carrinho criado.")
    public ResponseEntity<CarrinhoResponse> criarCarrinho(@Valid @RequestBody CarrinhoRequest request) {
        CarrinhoResponse carrrinho = carrinhoService.novoCarrinho(request);
        return ResponseEntity.created(URI.create(carrrinho.id().toString())).body(carrrinho);
    }

    @GetMapping("/{carrinhoId}")
    @Operation(summary = "Busca um carrinho pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado."),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado.")
    })
    public ResponseEntity<CarrinhoResponse> buscarCarrinhoporId(@PathVariable Long carrinhoId) {
        return ResponseEntity.ok(carrinhoService.buscarCarrinhoPorId(carrinhoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Busca todos os carrinhos de um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho(s) encontrado(s)."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<List<CarrinhoResponse>> buscarCarrinhosPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carrinhoService.buscarCarrinhoPorUsuarioId(usuarioId));
    }

    @PatchMapping("/{carrinhoId}/quantidade")
    @Operation(summary = "Atualiza a quantidade de produtos de um carrinho pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "409", description = "Carrinho cancelado.")
    })
    public ResponseEntity<CarrinhoResponse> atualizarCarrinho(@PathVariable Long carrinhoId, @Valid @RequestBody CarrinhoRequest request) {
        return ResponseEntity.ok(carrinhoService.atualizarQuantidadePorCarrinhoId(carrinhoId, request.quantidade()));
    }

    @DeleteMapping("/{carrinhoId}")
    @Operation(summary = "Cancela um carrinho.")
    @ApiResponse(responseCode = "204", description = "Carrinho cancelado.")
    public ResponseEntity<Void> cancelarCarrinho(@PathVariable Long carrinhoId) {
        carrinhoService.cancelarCarrinho(carrinhoId);
        return ResponseEntity.noContent().build();
    }
}
