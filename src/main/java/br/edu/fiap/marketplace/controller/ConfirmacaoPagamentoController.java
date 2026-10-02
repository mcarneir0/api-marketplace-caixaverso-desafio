package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.service.ConfirmacaoPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/** TODO criar as rotas protegidas de confirmação e consulta do pagamento. */
@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class ConfirmacaoPagamentoController {
    private final ConfirmacaoPagamentoService service;   // construtor

    public ConfirmacaoPagamentoController(ConfirmacaoPagamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cria confirmação de pagamento pendente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário ou carrinho inexistente"),
            @ApiResponse(responseCode = "409", description = "Conflito de negócio")
    })
    public ResponseEntity<ConfirmacaoPagamentoResponse> criar(
            @Valid @RequestBody ConfirmacaoPagamentoRequest request) {
        ConfirmacaoPagamentoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")                   // 200 ou 404
    public ConfirmacaoPagamentoResponse buscar(@PathVariable Long id) { return service.buscarPorId(id); }

    @PatchMapping("/{id}/aprovar")         // 200, 404 ou 409
    public ConfirmacaoPagamentoResponse aprovar(@PathVariable Long id) { return service.aprovar(id); }

    @PatchMapping("/{id}/recusar")         // 200, 404 ou 409
    public ConfirmacaoPagamentoResponse recusar(@PathVariable Long id) { return service.recusar(id); }

}
