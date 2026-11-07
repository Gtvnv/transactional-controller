package io.github.gtvnv.transactional.domain;

import io.github.gtvnv.transactional.domain.exception.RegraNegocioException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Representa uma aplicacao financeira vinculada a uma conta de investimento.
 * Demonstra composicao: a {@link ContaInvestimento} e composta por varios
 * {@code Investimento}.
 */
@Getter
public class Investimento {

    private final String nome;
    private final BigDecimal taxaRendimento;
    private final LocalDateTime dataAplicacao;
    private BigDecimal valorAplicado;

    public Investimento(String nome, BigDecimal valorAplicado, BigDecimal taxaRendimento) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O investimento precisa de um nome.");
        }
        if (valorAplicado == null || valorAplicado.signum() <= 0) {
            throw new RegraNegocioException("O valor aplicado deve ser maior que zero.");
        }
        if (taxaRendimento == null || taxaRendimento.signum() < 0) {
            throw new RegraNegocioException("A taxa de rendimento nao pode ser negativa.");
        }
        this.nome = nome;
        this.valorAplicado = valorAplicado;
        this.taxaRendimento = taxaRendimento;
        this.dataAplicacao = LocalDateTime.now();
    }

    /**
     * Calcula o rendimento sobre o valor aplicado e o incorpora ao montante.
     *
     * @return o rendimento gerado.
     */
    public BigDecimal renderJuros() {
        BigDecimal rendimento = valorAplicado
                .multiply(taxaRendimento)
                .setScale(2, RoundingMode.HALF_EVEN);
        this.valorAplicado = this.valorAplicado.add(rendimento);
        return rendimento;
    }

    @Override
    public String toString() {
        return "%s | aplicado: %s | taxa: %s".formatted(nome, valorAplicado, taxaRendimento);
    }
}
