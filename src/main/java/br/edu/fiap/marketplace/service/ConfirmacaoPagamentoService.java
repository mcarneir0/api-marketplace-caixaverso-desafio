package br.edu.fiap.marketplace.service;

import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoRequest;
import br.edu.fiap.marketplace.dto.ConfirmacaoPagamentoResponse;
import br.edu.fiap.marketplace.entity.Carrinho;
import br.edu.fiap.marketplace.entity.ConfirmacaoPagamento;
import br.edu.fiap.marketplace.entity.StatusCarrinho;
import br.edu.fiap.marketplace.entity.Usuario;
import br.edu.fiap.marketplace.exception.ConflitoNegocioException;
import br.edu.fiap.marketplace.exception.RecursoNaoEncontradoException;
import br.edu.fiap.marketplace.exception.RegraNegocioException;
import br.edu.fiap.marketplace.repository.CarrinhoRepository;
import br.edu.fiap.marketplace.repository.ConfirmacaoPagamentoRepository;
import br.edu.fiap.marketplace.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO implementar criação, aprovação, recusa e consulta do pagamento.
 */
@Service
public class ConfirmacaoPagamentoService {
    private final ConfirmacaoPagamentoRepository pagamentoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConfirmacaoPagamentoService(ConfirmacaoPagamentoRepository pagamentoRepository, CarrinhoRepository carrinhoRepository, UsuarioRepository usuarioRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ConfirmacaoPagamentoResponse criar(ConfirmacaoPagamentoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        Carrinho carrinho = carrinhoRepository.findById(request.carrinhoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho não encontrado."));

        if (!usuario.isAtivo())
            throw new RegraNegocioException("Usuário inativo não pode pagar.");
        if (!carrinho.getUsuario().getId().equals(usuario.getId()))
            throw new RegraNegocioException("O carrinho não pertence ao usuário informado.");
        if (carrinho.getStatus() != StatusCarrinho.ABERTO)
            throw new RegraNegocioException("Somente carrinho aberto pode receber pagamento.");
        if (pagamentoRepository.existsByCarrinhoId(carrinho.getId()))
            throw new ConflitoNegocioException("O carrinho já possui confirmação de pagamento.");
        if (pagamentoRepository.existsByIdPagamento(request.idPagamento()))
            throw new ConflitoNegocioException("O idPagamento já foi utilizado.");

        ConfirmacaoPagamento pagamento = new ConfirmacaoPagamento(
                carrinho, usuario, request.idPagamento(), carrinho.calcularTotal());
        return ConfirmacaoPagamentoResponse.de(pagamentoRepository.save(pagamento));
    }

    @Transactional   // fronteira transacional exigida pelo PDF
    public ConfirmacaoPagamentoResponse aprovar(Long id) {
        ConfirmacaoPagamento pagamento = buscar(id);
        pagamento.aprovar();                                   // valida PENDENTE, finaliza carrinho, marca PAGO
        Carrinho carrinho = pagamento.getCarrinho();
        carrinho.getProduto().baixarEstoque(carrinho.getQuantidade());  // falha => rollback de tudo
        return ConfirmacaoPagamentoResponse.de(pagamento);
    }

    @Transactional
    public ConfirmacaoPagamentoResponse recusar(Long id) {
        ConfirmacaoPagamento pagamento = buscar(id);
        pagamento.recusar();
        return ConfirmacaoPagamentoResponse.de(pagamento);
    }

    @Transactional(readOnly = true)
    public ConfirmacaoPagamentoResponse buscarPorId(Long id) {
        return ConfirmacaoPagamentoResponse.de(buscar(id));
    }

    private ConfirmacaoPagamento buscar(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado."));
    }

}
