package io.github.gtvnv.transactional.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta poupanca que permite aplicar um rendimento percentual sobre o saldo.
 * Especializa o comportamento base demonstrando polimorfismo.
 */
public class ContaPoupanca extends Conta {

    /** Taxa de rendimento padrao aplicada sobre o saldo (0,5%). */
    private static final BigDecimal TAXA_RENDIMENTO = new BigDecimal("0.005");

    public ContaPoupanca(Cliente titular) {
        super(titular);
    }

    @Override
    public TipoConta getTipo() {
        return TipoConta.POUPANCA;
    }

    @Override
    public boolean permiteRendimento() {
        return true;
    }

    /**
     * Aplica o rendimento sobre o saldo atual e credita o valor calculado.
     *
     * @return o valor creditado como rendimento.
     */
    public BigDecimal aplicarRendimento() {
        BigDecimal rendimento = getSaldo()
                .multiply(TAXA_RENDIMENTO)
                .setScale(2, RoundingMode.HALF_EVEN);
        if (rendimento.signum() > 0) {
            creditar(rendimento, TipoTransacao.RENDIMENTO,
                    "Rendimento de poupanca (" + TAXA_RENDIMENTO + ")");
        }
        return rendimento;
    }
}
