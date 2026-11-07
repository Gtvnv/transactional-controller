package io.github.gtvnv.transactional.domain;

/**
 * Conta corrente comum. Nao rende e serve como conta operacional padrao.
 */
public class ContaCorrente extends Conta {

    public ContaCorrente(Cliente titular) {
        super(titular);
    }

    @Override
    public TipoConta getTipo() {
        return TipoConta.CORRENTE;
    }

    @Override
    public boolean permiteRendimento() {
        return false;
    }
}
