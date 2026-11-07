package io.github.gtvnv.transactional.domain;

/**
 * Representa a natureza de uma transacao financeira registrada no historico.
 */
public enum TipoTransacao {
    DEPOSITO("Deposito"),
    SAQUE("Saque"),
    PIX_ENVIADO("PIX enviado"),
    PIX_RECEBIDO("PIX recebido"),
    APLICACAO_INVESTIMENTO("Aplicacao em investimento"),
    RESGATE_INVESTIMENTO("Resgate de investimento"),
    RENDIMENTO("Rendimento");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
