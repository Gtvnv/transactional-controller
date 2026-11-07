package io.github.gtvnv.transactional.domain;

/**
 * Dados basicos do titular de uma conta. Modelado como {@code record} por ser
 * um dado imutavel que identifica o cliente.
 */
public record Cliente(String nome, String cpf) {

    public Cliente {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF do cliente e obrigatorio.");
        }
    }
}
