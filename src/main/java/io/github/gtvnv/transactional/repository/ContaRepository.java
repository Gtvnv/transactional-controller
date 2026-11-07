package io.github.gtvnv.transactional.repository;

import io.github.gtvnv.transactional.domain.Conta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio que simula a persistencia de contas em memoria.
 *
 * <p>Demonstra o padrao de repositorio e o reuso de codigo: toda a aplicacao
 * acessa as contas por esta unica porta de entrada.</p>
 */
public class ContaRepository {

    private final List<Conta> contas = new ArrayList<>();

    public Conta salvar(Conta conta) {
        contas.add(conta);
        return conta;
    }

    public Optional<Conta> buscarPorNumero(long numero) {
        return contas.stream()
                .filter(conta -> conta.getNumero() == numero)
                .findFirst();
    }

    public List<Conta> listarTodas() {
        return List.copyOf(contas);
    }

    public boolean vazio() {
        return contas.isEmpty();
    }

    public int quantidade() {
        return contas.size();
    }
}
