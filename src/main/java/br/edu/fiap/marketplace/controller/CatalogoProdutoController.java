package br.edu.fiap.marketplace.controller;

import br.edu.fiap.marketplace.controller.mapper.GenericController;
import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.dto.MovimentacaoRequest;
import br.edu.fiap.marketplace.service.CatalogoProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** TODO criar as rotas públicas de leitura e protegidas de alteração do catálogo. */
@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Catálogo")
public class CatalogoProdutoController implements GenericController {

    private final CatalogoProdutoService catalogoProdutoService;

    public CatalogoProdutoController(CatalogoProdutoService catalogoProdutoService) {
        this.catalogoProdutoService = catalogoProdutoService;
    }

    @PostMapping
    @SecurityRequirement( name = "bearerAuth" )
    @Operation( summary = "Cadastrar produto." )
    @ApiResponses({
            @ApiResponse( responseCode = "201", description = "Produto Cadastrado"),
            @ApiResponse( responseCode = "400", description = "Dados inválidos"),
            @ApiResponse( responseCode = "409", description = "Produto já cadastrado." )

    })
    public ResponseEntity<CatalogoProdutoResponse> cadastrar( @Valid @RequestBody CatalogoProdutoRequest request ) {
        CatalogoProdutoResponse response = catalogoProdutoService.cadastrarProduto( request );

        URI localizacao = gerarHeaderLocation( request.nome() );

        return ResponseEntity.created( localizacao ).body( response );
    }

    @GetMapping
    @Operation( summary = "Listar todos os produtos" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista criada"),

    })
    public ResponseEntity<List<CatalogoProdutoResponse>> listaProdutos() {

        List<CatalogoProdutoResponse> catalogo = catalogoProdutoService.listar();

        return ResponseEntity.ok( catalogo );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Listar por Id" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<CatalogoProdutoResponse> buscarPorId( @PathVariable Long id ) {

        CatalogoProdutoResponse buscarProduto = catalogoProdutoService.buscarPorId( id );

        return ResponseEntity.ok( buscarProduto );
    }

    @PutMapping ( "/{id}" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation( summary = "Atualizar Produto" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados válidos" ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<CatalogoProdutoResponse> atualizarProduto(
            @Valid @PathVariable Long id, @Valid @RequestBody CatalogoProdutoRequest request ) {

        CatalogoProdutoResponse response = catalogoProdutoService.atualizar( id, request );

        return ResponseEntity.ok( response );

    }

    @PatchMapping("/{id}/alterarPreco")
    @SecurityRequirement( name = "bearerAuth" )
    @Operation( summary = "Alterar o preço de um produto",
            description = "Atualiza o valor do produto. O produto não pode estar inativo e o novo preço deve ser maior que zero." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido ou formato incorreto"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<CatalogoProdutoResponse> atualizarPreco(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoRequest request) {
        return ResponseEntity.ok( catalogoProdutoService.atualizarPreco(id, request));
    }

    @PatchMapping("/{id}/estoqueBaixa")
    @SecurityRequirement( name = "bearerAuth" )
    @Operation( summary = "Dar baixa no estoque",
            description = "Reduz a quantidade em estoque do produto. A quantidade deve ser positiva e não pode ser maior que o estoque atual." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida ou estoque insuficiente"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<CatalogoProdutoResponse> baixarEstoque(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        return ResponseEntity.ok( catalogoProdutoService.baixarEstoque(id, request));
    }

    @PatchMapping("/{id}/estoqueRepor")
    @SecurityRequirement( name = "bearerAuth" )
    @Operation( summary = "Repor estoque",
            description = "Adiciona uma quantidade positiva ao estoque do produto." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque reposto com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida (deve ser maior que zero)"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<CatalogoProdutoResponse> resporEstoque(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        return ResponseEntity.ok( catalogoProdutoService.reporEstoque(id, request));
    }

    @DeleteMapping( "/{id}" )
    @SecurityRequirement( name = "bearerAuth" )
    @Operation(summary = "Excluir produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> exlcuirProduto( @PathVariable Long id ) {

        catalogoProdutoService.excluirProduto( id );

        return ResponseEntity.noContent().build();
    }
}