package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.AtualizarQuantidadeRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoRequest;
import br.edu.fiap.marketplace.dto.CatalogoProdutoResponse;
import br.edu.fiap.marketplace.dto.MovimentacaoRequest;
import br.edu.fiap.marketplace.entity.CatalogoProduto;
import br.edu.fiap.marketplace.exception.ProdutoCadastradoException;
import br.edu.fiap.marketplace.exception.ProdutoNaoEncontradoException;
import br.edu.fiap.marketplace.repository.CatalogoProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** TODO implementar cadastro, listagem, busca e alterações do catálogo. */
@Service
public class CatalogoProdutoService {

    private final CatalogoProdutoRepository catalogoProdutoRepository;

    public CatalogoProdutoService(CatalogoProdutoRepository catalogoProdutoRepository) {
        this.catalogoProdutoRepository = catalogoProdutoRepository;
    }

    @Transactional
    public CatalogoProdutoResponse cadastrarProduto( CatalogoProdutoRequest request ) {

        Optional<CatalogoProduto> produtoEncontrado = catalogoProdutoRepository.findByNome( request.nome() );

        if( produtoEncontrado.isPresent() ) {
            throw new ProdutoCadastradoException();
        }

        CatalogoProduto novoProduto =new CatalogoProduto( request.nome(), request.descricao(), request.preco(),
                request.estoque(), request.ativo() );

        CatalogoProduto produtoSalvo = catalogoProdutoRepository.save( novoProduto );

        return CatalogoProdutoResponse.de( produtoSalvo );
    }

    @Transactional( readOnly = true )
    public List<CatalogoProdutoResponse> listar() {
        List<CatalogoProduto> listar = catalogoProdutoRepository.findAll();

        List<CatalogoProdutoResponse> response = listar.stream()
                .map( CatalogoProdutoResponse::de )
                .toList();

        return response;
    }

    @Transactional( readOnly = true )
    public CatalogoProdutoResponse buscarPorId( Long id ) {

        CatalogoProduto produto = catalogoProdutoRepository.findById( id ).
                orElseThrow( () -> new ProdutoNaoEncontradoException( id ) );

        CatalogoProdutoResponse produtoEncontrado = CatalogoProdutoResponse.de( produto );

        return produtoEncontrado;
    }

    @Transactional
    public CatalogoProdutoResponse atualizar( Long id, CatalogoProdutoRequest request ) {
        CatalogoProduto produto = buscar( id );

        produto.atualizar( request.nome(), request.descricao() );

        CatalogoProduto produtoSalvo = catalogoProdutoRepository.save( produto );

        CatalogoProdutoResponse produtoAtualizado = CatalogoProdutoResponse.de( produtoSalvo );

        return produtoAtualizado;
    }

    @Transactional
    public CatalogoProdutoResponse atualizarPreco(Long id, MovimentacaoRequest request ) {
        CatalogoProduto produto = buscar( id );

        produto.alterarPreco( request.valor() );

        CatalogoProduto produtoSalvo = catalogoProdutoRepository.save( produto );

        CatalogoProdutoResponse response = CatalogoProdutoResponse.de( produtoSalvo );

        return response;
    }

    @Transactional
    public CatalogoProdutoResponse baixarEstoque(Long id, AtualizarQuantidadeRequest request) {
        CatalogoProduto produto = buscar( id );

        produto.baixarEstoque( request.quantidade() );

        CatalogoProduto produtoSalvo = catalogoProdutoRepository.save( produto );

        CatalogoProdutoResponse response = CatalogoProdutoResponse.de( produtoSalvo );

        return response;
    }

    @Transactional
    public CatalogoProdutoResponse reporEstoque(Long id, AtualizarQuantidadeRequest request) {
        CatalogoProduto produto = buscar( id );

        produto.reporEstoque( request.quantidade() );

        CatalogoProduto produtoSalvo = catalogoProdutoRepository.save( produto );

        CatalogoProdutoResponse response = CatalogoProdutoResponse.de( produtoSalvo );

        return response;
    }

    @Transactional
    public void excluirProduto( Long id ) {
        CatalogoProduto produto = buscar( id );

        catalogoProdutoRepository.delete( produto );
    }

    private CatalogoProduto buscar( Long id) {
        return catalogoProdutoRepository.findById( id )
                .orElseThrow( () -> new ProdutoNaoEncontradoException( id ) );

    }
}