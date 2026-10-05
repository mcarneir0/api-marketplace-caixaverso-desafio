package br.edu.fiap.marketplace.entity;

import br.edu.fiap.marketplace.exception.CatalogoInativoException;
import br.edu.fiap.marketplace.exception.NovoPrecoInvalidoException;
import br.edu.fiap.marketplace.exception.ProdutoNaoPodeSerAtivoException;
import br.edu.fiap.marketplace.exception.QuantidadeInvalidaException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * TODO implementar um teste unitário da regra de estoque ou preço.
 * Não iniciar o Spring e não usar repository neste arquivo.
 */
class CatalogoProdutoTest {

    @Nested
    class ProdutoValido {
        @Test
        @DisplayName( "Deve criar um produto válido" )
        void deveCriarUmprodutoValido() {
            CatalogoProduto produto = new CatalogoProduto(
                    "Teclado",
                    "Teclado com cabo",
                    new BigDecimal( "39.90" ),
                    10,
                    true
            );

            assertThat( produto.getNome() ).isEqualTo( "Teclado" );
            assertThat( produto.getPreco() ).isEqualByComparingTo( "39.90" );
            assertThat( produto.getEstoque() ).isEqualTo( 10 );
            assertThat( produto.isAtivo() ).isTrue();
            assertThat( produto.getId() ).isNull();
        }
    }

    @Nested
    class Preco {
        @Test
        @DisplayName( "Deve alterar preco com sucesso" )
        void deveAlterarPrecoQuandoAtivo() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 2, true
            );
            produto.alterarPreco( new BigDecimal( "20.00" ) );

            assertThat( produto.getPreco() ).isEqualByComparingTo( "20.00" );
        }

        @Test
        @DisplayName( "Não deve alterar preco de produto inativo" )
        void NaoDeveAlterarPrecoQuandoAtivo() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 2, false
            );
            assertThatThrownBy( () -> produto.alterarPreco( new BigDecimal( "20.00" ) ) )
                    .isInstanceOf(CatalogoInativoException.class);

            assertThat( produto.getPreco() ).isEqualByComparingTo( "10.00" );
        }

        @Test
        @DisplayName( "Deve lançar exceção ao criar produto com preço nulo")
        void deveLancarExceptionPrecoNulo() {

            assertThatThrownBy(() -> new CatalogoProduto( "nome", "desc", null,
                    5, true )).isInstanceOf( NovoPrecoInvalidoException.class );

        }

        @Test
        @DisplayName( "Deve lançar exceção ao criar produto com preço zero ou negativo")
        void deveLancarExceptionPrecoZeroOuNegativo() {
            assertThatThrownBy( () -> new CatalogoProduto( "nome", "desc", BigDecimal.ZERO,
                    10, true)).isInstanceOf( NovoPrecoInvalidoException.class );

            assertThatThrownBy( () -> new CatalogoProduto( "nome", "desc", new BigDecimal( "-50.00"),
                    5, true)).isInstanceOf( NovoPrecoInvalidoException.class );
        }
    }

    @Nested
    class Estoque{
        @Test
        @DisplayName( "Deve lançar exceção quando o estoque é nulo ou negativo" )
        void deveLancarExcecaoEstoqueInvalido() {

            assertThatThrownBy( () -> new CatalogoProduto( "nome", "desc", new BigDecimal( "50.00"),
                    null, true)).isInstanceOf( QuantidadeInvalidaException.class );

            assertThatThrownBy( () -> new CatalogoProduto( "nome", "desc", new BigDecimal( "30.00"),
                    -3, true)).isInstanceOf( QuantidadeInvalidaException.class );

            QuantidadeInvalidaException erro = Assertions.catchThrowableOfType(
                    () -> new CatalogoProduto( "nome", "desc", new BigDecimal( "30.00"),
                            -3, true), QuantidadeInvalidaException.class );

            assertThat( erro ).isNotNull();
            assertThat( erro.getMessage() ).isEqualTo( "A quantidade digitada é inválida" );

        }

        @Test
        @DisplayName( "Deve baixar o estoque com sucesso" )
        void deveBaixarEstoque() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 2, true
            );

            produto.baixarEstoque( 1 );

            assertThat( produto.getEstoque() ).isEqualTo( 1 );
        }

        @Test
        @DisplayName( "Deve lançar exceção quando quantidade maior que estoque" )
        void deveLancarExceptionQuantidadeMaiorEstoque() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 10, true );

            QuantidadeInvalidaException erro = Assertions.catchThrowableOfType(
                    () -> produto.baixarEstoque( 20 ), QuantidadeInvalidaException.class );

            assertThat( erro ).isNotNull();
            assertThat( erro.getMessage() ).isEqualTo( "A quantidade digitada é inválida" );
        }

        @Test
        @DisplayName( "Deve repor o estoque com sucesso" )
        void deveReporEstoque() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 2, true
            );

            produto.reporEstoque( 3 );

            assertThat( produto.getEstoque() ).isEqualTo( 5 );
        }

        @Test
        @DisplayName( "Deve retornar uma exceção quando tentar repor quantidade negativa" )
        void deveLancarExceptionReporQuantidadeNegativa() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome", "desc", new BigDecimal( "10.00"), 2, true
            );

            QuantidadeInvalidaException erro = Assertions.catchThrowableOfType(
                    () -> produto.reporEstoque( -50 ), QuantidadeInvalidaException.class
            );

            assertThat( erro ).isNotNull();
            assertThat( erro.getMessage() ).isEqualTo( "A quantidade digitada é inválida" );

            assertThat( produto.getEstoque() ).isEqualTo( 2);
        }


    }

    @Nested
    class Ativacao {
        @Test
        @DisplayName( "Deve ativar produto com estoque positivo" )
        void ativarProdutoComEstoque() {
            CatalogoProduto produto = new CatalogoProduto( "nome", "desc", new BigDecimal( "100"),
                    5, false );

            produto.ativar();

            assertThat( produto.isAtivo() ).isTrue();
        }

        @Test
        @DisplayName( "Não deve ativar produto sem estoque" )
        void ativarProdutoSemEstoque() {
            CatalogoProduto produto = new CatalogoProduto( "nome", "desc", new BigDecimal( "100"),
                    0, false );

            assertThatThrownBy( () -> produto.ativar() ).isInstanceOf(ProdutoNaoPodeSerAtivoException.class);

            assertThat( produto.isAtivo() ).isFalse();
        }

        @Test
        @DisplayName( "Deve desativar produto ativo" )
        void desativarProdutoAtivo() {
            CatalogoProduto produto = new CatalogoProduto( "nome", "desc", new BigDecimal( "100"),
                    5, true );

            produto.desativar();

            assertThat( produto.isAtivo() ).isFalse();
        }
    }

    @Nested
    class Atualizacao {
        @Test
        @DisplayName( "Deve atualizar um produto " )
        void deveAtualizarProduto() {
            CatalogoProduto produto = new CatalogoProduto(
                    "nome antigo", "desc antiga", new BigDecimal("10.00"), 5, true
            );

            produto.atualizar( "novoNome", "novaDesc" );

            assertThat( produto.getNome() ).isEqualTo( "novoNome" );
            assertThat( produto.getDescricao() ).isEqualTo( "novaDesc" );
        }
    }

}