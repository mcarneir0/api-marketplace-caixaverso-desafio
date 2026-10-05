package br.edu.fiap.marketplace.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioTest {

    @Test
    @DisplayName("Deve cadastrar usuário")
    void deveCadastrarUsuario(){
        Usuario usuario = new Usuario(
                "Anderson Barbosa",
                "anderson@teste.com",
                "Senha@123");

        assertThat(usuario.getNome()).isEqualTo("Anderson Barbosa");
        assertThat(usuario.getEmail()).isEqualTo("anderson@teste.com");
        assertThat(usuario.getSenha()).isEqualTo("Senha@123");

    }
}
