package io.github.gtvnv.transactional.domain;

/**
 * Diferencia os tipos de conta suportados pelo sistema.
 */
public enum TipoConta {
    CORRENTE("Conta Corrente"),
    POUPANCA("Conta Poupanca"),
    INVESTIMENTO("Conta Investimento");

    private final String descricao;

    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
