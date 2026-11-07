package io.github.gtvnv.transactional.domain;

import io.github.gtvnv.transactional.domain.exception.RegraNegocioException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe abstrata base para todas as contas do sistema.
 *
 * <p>Demonstra os pilares da POO:</p>
 * <ul>
 *   <li><b>Abstracao:</b> define o contrato comum (deposito, saque, historico)
 *       sem conhecer o tipo concreto de conta.</li>
 *   <li><b>Encapsulamento:</b> o saldo e o historico sao protegidos e so
 *       mudam por metodos controlados.</li>
 *   <li><b>Polimorfismo:</b> metodos como {@link #getTipo()} e
 *       {@link #permiteRendimento()} sao especializados pelas subclasses.</li>
 * </ul>
 */
@Getter
public abstract class Conta {

    private static final AtomicLong GERADOR_NUMERO = new AtomicLong(1000);

    private final long numero;
    private final Cliente titular;
    private BigDecimal saldo;
    private final List<Transacao> historico;

    protected Conta(Cliente titular) {
        if (titular == null) {
            throw new IllegalArgumentException("A conta precisa de um titular.");
        }
        this.numero = GERADOR_NUMERO.incrementAndGet();
        this.titular = titular;
        this.saldo = BigDecimal.ZERO;
        this.historico = new ArrayList<>();
    }

    /** Cada subclasse informa seu proprio tipo (polimorfismo). */
    public abstract TipoConta getTipo();

    /** Indica se a conta aceita aplicacoes de investimento. */
    public abstract boolean permiteRendimento();

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor);
        registrar(TipoTransacao.DEPOSITO, valor, "Deposito em conta");
    }

    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        validarSaldoSuficiente(valor);
        this.saldo = this.saldo.subtract(valor);
        registrar(TipoTransacao.SAQUE, valor, "Saque em conta");
    }

    /**
     * Debita diretamente o saldo registrando a transacao informada. Usado por
     * operacoes compostas (PIX, investimento) que tem semantica propria.
     */
    public void debitar(BigDecimal valor, TipoTransacao tipo, String descricao) {
        validarValorPositivo(valor);
        validarSaldoSuficiente(valor);
        this.saldo = this.saldo.subtract(valor);
        registrar(tipo, valor, descricao);
    }

    /**
     * Credita diretamente o saldo registrando a transacao informada.
     */
    public void creditar(BigDecimal valor, TipoTransacao tipo, String descricao) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor);
        registrar(tipo, valor, descricao);
    }

    protected void registrar(TipoTransacao tipo, BigDecimal valor, String descricao) {
        this.historico.add(Transacao.de(tipo, valor, descricao));
    }

    /** Retorna uma visao imutavel do historico, preservando o encapsulamento. */
    public List<Transacao> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    protected void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new RegraNegocioException("O valor da operacao deve ser maior que zero.");
        }
    }

    protected void validarSaldoSuficiente(BigDecimal valor) {
        if (saldo.compareTo(valor) < 0) {
            throw new RegraNegocioException(
                    "Saldo insuficiente. Saldo atual: " + saldo + " / valor solicitado: " + valor);
        }
    }

    @Override
    public String toString() {
        return "%s n.%d | titular: %s | saldo: %s"
                .formatted(getTipo().getDescricao(), numero, titular.nome(), saldo);
    }
}
