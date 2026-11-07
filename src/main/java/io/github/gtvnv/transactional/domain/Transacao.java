package io.github.gtvnv.transactional.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um lancamento imutavel no historico de uma conta.
 *
 * <p>Uso de {@code record} para modelar um dado imutavel de transporte,
 * conforme boas praticas sugeridas no desafio.</p>
 */
public record Transacao(
        TipoTransacao tipo,
        BigDecimal valor,
        LocalDateTime dataHora,
        String descricao
) {

    public Transacao {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da transacao e obrigatorio.");
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException("O valor da transacao nao pode ser negativo.");
        }
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
        if (descricao == null || descricao.isBlank()) {
            descricao = tipo.getDescricao();
        }
    }

    public static Transacao de(TipoTransacao tipo, BigDecimal valor, String descricao) {
        return new Transacao(tipo, valor, LocalDateTime.now(), descricao);
    }
}
