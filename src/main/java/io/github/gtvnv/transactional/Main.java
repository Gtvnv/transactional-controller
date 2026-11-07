package io.github.gtvnv.transactional;

import io.github.gtvnv.transactional.repository.ContaRepository;
import io.github.gtvnv.transactional.service.ContaService;
import io.github.gtvnv.transactional.ui.MenuConsole;

/**
 * Ponto de entrada da aplicacao. Monta as dependencias (repositorio, servico e
 * interface de console) e inicia o menu interativo.
 */
public class Main {

    public static void main(String[] args) {
        ContaRepository repository = new ContaRepository();
        ContaService service = new ContaService(repository);
        MenuConsole menu = new MenuConsole(service);
        menu.iniciar();
    }
}
