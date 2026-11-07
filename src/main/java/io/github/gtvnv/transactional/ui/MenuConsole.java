package io.github.gtvnv.transactional.ui;

import io.github.gtvnv.transactional.domain.Cliente;
import io.github.gtvnv.transactional.domain.Conta;
import io.github.gtvnv.transactional.domain.ContaInvestimento;
import io.github.gtvnv.transactional.domain.Investimento;
import io.github.gtvnv.transactional.domain.TipoConta;
import io.github.gtvnv.transactional.domain.Transacao;
import io.github.gtvnv.transactional.domain.exception.RegraNegocioException;
import io.github.gtvnv.transactional.service.ContaService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Responsavel pela interacao com o usuario via console: exibe menus, le
 * entradas e aciona o {@link ContaService}.
 */
public class MenuConsole {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ContaService service;
    private final Scanner scanner;

    public MenuConsole(ContaService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("==============================================");
        System.out.println("   CONTROLE DE TRANSACOES FINANCEIRAS (POO)   ");
        System.out.println("==============================================");

        boolean executando = true;
        while (executando) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();
            try {
                executando = tratarOpcao(opcao);
            } catch (RegraNegocioException e) {
                System.out.println("[ERRO] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Valor numerico invalido.");
            }
            System.out.println();
        }
        System.out.println("Encerrando. Ate logo!");
    }

    private void exibirMenu() {
        System.out.println("""
                ----------------------------------------------
                 1 - Abrir conta
                 2 - Depositar
                 3 - Sacar
                 4 - Transferir via PIX
                 5 - Aplicar investimento
                 6 - Aplicar rendimentos
                 7 - Ver extrato / historico
                 8 - Listar contas
                 0 - Sair
                ----------------------------------------------""");
        System.out.print("Escolha uma opcao: ");
    }

    private boolean tratarOpcao(String opcao) {
        switch (opcao) {
            case "1" -> abrirConta();
            case "2" -> depositar();
            case "3" -> sacar();
            case "4" -> transferirPix();
            case "5" -> aplicarInvestimento();
            case "6" -> aplicarRendimentos();
            case "7" -> verExtrato();
            case "8" -> listarContas();
            case "0" -> {
                return false;
            }
            default -> System.out.println("[ERRO] Opcao invalida.");
        }
        return true;
    }

    private void abrirConta() {
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine().trim();
        System.out.print("CPF do titular: ");
        String cpf = scanner.nextLine().trim();

        System.out.println("Tipo de conta: 1 - Corrente | 2 - Poupanca | 3 - Investimento");
        System.out.print("Escolha o tipo: ");
        TipoConta tipo = switch (scanner.nextLine().trim()) {
            case "1" -> TipoConta.CORRENTE;
            case "2" -> TipoConta.POUPANCA;
            case "3" -> TipoConta.INVESTIMENTO;
            default -> throw new RegraNegocioException("Tipo de conta invalido.");
        };

        Conta conta = service.abrirConta(tipo, new Cliente(nome, cpf));
        System.out.println("[OK] Conta criada: " + conta);
    }

    private void depositar() {
        long numero = lerNumeroConta();
        BigDecimal valor = lerValor("Valor do deposito: ");
        service.depositar(numero, valor);
        System.out.println("[OK] Deposito realizado. Saldo atual: "
                + service.buscar(numero).getSaldo());
    }

    private void sacar() {
        long numero = lerNumeroConta();
        BigDecimal valor = lerValor("Valor do saque: ");
        service.sacar(numero, valor);
        System.out.println("[OK] Saque realizado. Saldo atual: "
                + service.buscar(numero).getSaldo());
    }

    private void transferirPix() {
        System.out.print("Conta de origem: ");
        long origem = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Conta de destino: ");
        long destino = Long.parseLong(scanner.nextLine().trim());
        BigDecimal valor = lerValor("Valor do PIX: ");
        service.transferirPix(origem, destino, valor);
        System.out.println("[OK] PIX realizado com sucesso.");
    }

    private void aplicarInvestimento() {
        long numero = lerNumeroConta();
        System.out.print("Nome do investimento: ");
        String nome = scanner.nextLine().trim();
        BigDecimal valor = lerValor("Valor a aplicar: ");
        BigDecimal taxa = lerValor("Taxa de rendimento (ex: 0.10 para 10%): ");
        Investimento investimento = service.aplicarInvestimento(numero, nome, valor, taxa);
        System.out.println("[OK] Investimento aplicado: " + investimento);
    }

    private void aplicarRendimentos() {
        long numero = lerNumeroConta();
        BigDecimal rendimento = service.aplicarRendimentos(numero);
        System.out.println("[OK] Rendimento aplicado: " + rendimento);
    }

    private void verExtrato() {
        long numero = lerNumeroConta();
        Conta conta = service.buscar(numero);
        System.out.println("---------- EXTRATO ----------");
        System.out.println(conta);

        if (conta instanceof ContaInvestimento investimento && !investimento.getCarteira().isEmpty()) {
            System.out.println("Carteira de investimentos:");
            for (Investimento inv : investimento.getCarteira()) {
                System.out.println("  - " + inv);
            }
        }

        List<Transacao> historico = conta.getHistorico();
        if (historico.isEmpty()) {
            System.out.println("Nenhuma transacao registrada.");
        } else {
            System.out.println("Historico de transacoes:");
            for (Transacao t : historico) {
                System.out.printf("  [%s] %-28s R$ %s%n",
                        t.dataHora().format(FORMATO_DATA),
                        t.descricao(),
                        t.valor());
            }
        }
        System.out.println("-----------------------------");
    }

    private void listarContas() {
        List<Conta> contas = service.listar();
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }
        System.out.println("Contas cadastradas:");
        contas.forEach(conta -> System.out.println("  - " + conta));
    }

    private long lerNumeroConta() {
        System.out.print("Numero da conta: ");
        return Long.parseLong(scanner.nextLine().trim());
    }

    private BigDecimal lerValor(String rotulo) {
        System.out.print(rotulo);
        return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
    }
}
