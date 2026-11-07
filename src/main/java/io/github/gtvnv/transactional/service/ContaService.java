package io.github.gtvnv.transactional.service;

import io.github.gtvnv.transactional.domain.Cliente;
import io.github.gtvnv.transactional.domain.Conta;
import io.github.gtvnv.transactional.domain.ContaCorrente;
import io.github.gtvnv.transactional.domain.ContaInvestimento;
import io.github.gtvnv.transactional.domain.ContaPoupanca;
import io.github.gtvnv.transactional.domain.Investimento;
import io.github.gtvnv.transactional.domain.TipoConta;
import io.github.gtvnv.transactional.domain.TipoTransacao;
import io.github.gtvnv.transactional.domain.exception.RegraNegocioException;
import io.github.gtvnv.transactional.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orquestra os casos de uso do sistema, aplicando as regras de negocio e
 * delegando a persistencia ao {@link ContaRepository}.
 */
public class ContaService {

    private final ContaRepository repository;

    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }

    public Conta abrirConta(TipoConta tipo, Cliente cliente) {
        Conta conta = switch (tipo) {
            case CORRENTE -> new ContaCorrente(cliente);
            case POUPANCA -> new ContaPoupanca(cliente);
            case INVESTIMENTO -> new ContaInvestimento(cliente);
        };
        return repository.salvar(conta);
    }

    public Conta buscar(long numero) {
        return repository.buscarPorNumero(numero)
                .orElseThrow(() -> new RegraNegocioException(
                        "Conta nao encontrada para o numero " + numero + "."));
    }

    public List<Conta> listar() {
        return repository.listarTodas();
    }

    public void depositar(long numero, BigDecimal valor) {
        buscar(numero).depositar(valor);
    }

    public void sacar(long numero, BigDecimal valor) {
        buscar(numero).sacar(valor);
    }

    /**
     * Transfere via PIX de uma conta para outra de forma atomica do ponto de
     * vista das regras: debita a origem e credita o destino.
     */
    public void transferirPix(long origem, long destino, BigDecimal valor) {
        if (origem == destino) {
            throw new RegraNegocioException("A conta de origem e destino nao podem ser a mesma.");
        }
        Conta contaOrigem = buscar(origem);
        Conta contaDestino = buscar(destino);

        contaOrigem.debitar(valor, TipoTransacao.PIX_ENVIADO,
                "PIX enviado para conta " + destino);
        contaDestino.creditar(valor, TipoTransacao.PIX_RECEBIDO,
                "PIX recebido da conta " + origem);
    }

    public Investimento aplicarInvestimento(long numero, String nome,
                                            BigDecimal valor, BigDecimal taxa) {
        Conta conta = buscar(numero);
        if (!(conta instanceof ContaInvestimento contaInvestimento)) {
            throw new RegraNegocioException(
                    "A conta " + numero + " nao e uma conta de investimento.");
        }
        return contaInvestimento.aplicar(nome, valor, taxa);
    }

    public BigDecimal aplicarRendimentos(long numero) {
        Conta conta = buscar(numero);
        if (conta instanceof ContaPoupanca poupanca) {
            return poupanca.aplicarRendimento();
        }
        if (conta instanceof ContaInvestimento investimento) {
            return investimento.renderCarteira();
        }
        throw new RegraNegocioException(
                "A conta " + numero + " nao rende.");
    }
}
