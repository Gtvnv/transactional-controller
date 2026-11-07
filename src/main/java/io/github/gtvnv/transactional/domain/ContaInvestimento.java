package io.github.gtvnv.transactional.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conta de investimento. Alem do comportamento base de uma conta, mantem uma
 * carteira de {@link Investimento} (composicao) e permite aplicar e resgatar.
 */
public class ContaInvestimento extends Conta {

    private final List<Investimento> carteira;

    public ContaInvestimento(Cliente titular) {
        super(titular);
        this.carteira = new ArrayList<>();
    }

    @Override
    public TipoConta getTipo() {
        return TipoConta.INVESTIMENTO;
    }

    @Override
    public boolean permiteRendimento() {
        return true;
    }

    /**
     * Debita o saldo da conta e cria um novo investimento na carteira.
     *
     * @return o investimento criado.
     */
    public Investimento aplicar(String nome, BigDecimal valor, BigDecimal taxaRendimento) {
        debitar(valor, TipoTransacao.APLICACAO_INVESTIMENTO,
                "Aplicacao em investimento: " + nome);
        Investimento investimento = new Investimento(nome, valor, taxaRendimento);
        carteira.add(investimento);
        return investimento;
    }

    /**
     * Resgata um investimento da carteira, creditando o montante de volta ao
     * saldo da conta.
     *
     * @return o valor resgatado.
     */
    public BigDecimal resgatar(Investimento investimento) {
        if (!carteira.remove(investimento)) {
            throw new io.github.gtvnv.transactional.domain.exception.RegraNegocioException(
                    "Investimento nao encontrado na carteira.");
        }
        BigDecimal montante = investimento.getValorAplicado();
        creditar(montante, TipoTransacao.RESGATE_INVESTIMENTO,
                "Resgate de investimento: " + investimento.getNome());
        return montante;
    }

    /** Aplica o rendimento em todos os investimentos ativos da carteira. */
    public BigDecimal renderCarteira() {
        BigDecimal total = BigDecimal.ZERO;
        for (Investimento investimento : carteira) {
            total = total.add(investimento.renderJuros());
        }
        return total;
    }

    public List<Investimento> getCarteira() {
        return Collections.unmodifiableList(carteira);
    }
}
