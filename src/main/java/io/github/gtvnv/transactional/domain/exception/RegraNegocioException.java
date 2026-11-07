package io.github.gtvnv.transactional.domain.exception;

/**
 * Lancada quando uma operacao viola uma regra de negocio do sistema,
 * como saldo insuficiente ou valor invalido.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
