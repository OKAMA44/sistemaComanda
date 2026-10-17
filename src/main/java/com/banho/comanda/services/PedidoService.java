package com.banho.comanda.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.banho.comanda.pedido.Pedido;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.banho.comanda.conta.Conta;
import com.banho.comanda.conta.ContaId;
import com.banho.comanda.garcom.Garcom;
import com.banho.comanda.produto.Produto;
import com.banho.comanda.services.PedidoDTO.*;
import com.banho.comanda.repos.ContaRepo;
import com.banho.comanda.repos.GarcomRepo;
import com.banho.comanda.repos.PedidoRepo;
import com.banho.comanda.repos.ProdutoRepo;

@Service
public class PedidoService {

    // ---------- dependências ----------

    private final PedidoRepo pedidoRepo;
    private final ContaRepo contaRepo;
    private final ProdutoRepo produtoRepo;
    private final GarcomRepo garcomRepo;

    public PedidoService(PedidoRepo pedidoRepo, ContaRepo contaRepo, ProdutoRepo produtoRepo, GarcomRepo garcomRepo) {
        this.pedidoRepo = pedidoRepo;
        this.contaRepo = contaRepo;
        this.produtoRepo = produtoRepo;
        this.garcomRepo = garcomRepo;
    }

    /** Cria o pedido e os itens na mesma transação. O total é calculado aqui, nunca vem do cliente. */
    @Transactional
    public PedidoResponse criar(PedidoRequest req) {
        if (req.mesa() == null || req.mesa() <= 0) {
            throw erro("Informe o número da mesa.");
        }
        if (req.itens() == null || req.itens().isEmpty()) {
            throw erro("Adicione pelo menos um item ao pedido.");
        }

        Garcom garcom = null;
        if (req.garcomId() != null && !req.garcomId().isBlank()) {
            garcom = garcomRepo.findById(req.garcomId()).orElseThrow(() -> erro("Garçom não encontrado."));
        }

        Pedido pedido = new Pedido();
        pedido.setMesa(req.mesa());
        pedido.setStatus(true);
        pedido.setData(java.sql.Date.valueOf(LocalDate.now()));
        pedido.setGarcom(garcom);

        Map<String, Integer> qtdPorProduto = agruparItens(req.itens());

        double total = 0;
        List<Conta> itens = new ArrayList<>();
        for (Map.Entry<String, Integer> e : qtdPorProduto.entrySet()) {
            Produto produto = produtoRepo.findById(e.getKey())
                    .orElseThrow(() -> erro("Produto não encontrado: " + e.getKey()));
            double preco = produto.getPreco() == null ? 0 : produto.getPreco();

            Conta c = new Conta();
            c.setProduto(produto);
            c.setQtdProduto(e.getValue());
            c.setValorProduto(preco); // guarda o preço da hora da venda
            itens.add(c);
            total += preco * e.getValue();
        }
        pedido.setValorTotal(Math.round(total * 100.0) / 100.0);

        Pedido salvo = pedidoRepo.save(pedido);
        itens.forEach(c -> {
            c.setPedido(salvo);
            c.setId(new ContaId(c.getProduto().getId(), salvo.getId()));
        });
        contaRepo.saveAll(itens);
        return toResponse(salvo, itens);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepo.findAll(Sort.by(Sort.Direction.DESC, "data")).stream()
                .map(p -> toResponse(p, contaRepo.findByPedidoId(p.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(String id) {
        Pedido p = pedidoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        return toResponse(p, contaRepo.findByPedidoId(id));
    }

    @Transactional
    public PedidoResponse fechar(String id) {
        Pedido p = pedidoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        p.setStatus(false);
        return toResponse(pedidoRepo.save(p), contaRepo.findByPedidoId(id));
    }

    /**
     * Adiciona itens a um pedido que ainda está aberto.
     * - Produto que já está no pedido: soma a quantidade (mantém o preço da primeira venda).
     * - Produto novo: cria a linha com o preço atual.
     * O total é recalculado a partir de todos os itens do pedido.
     */
    @Transactional
    public PedidoResponse adicionarItens(String pedidoId, ItensRequest req) {
        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        if (!Boolean.TRUE.equals(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido já está fechado e não pode ser alterado.");
        }
        if (req == null || req.itens() == null || req.itens().isEmpty()) {
            throw erro("Adicione pelo menos um item.");
        }

        for (Map.Entry<String, Integer> e : agruparItens(req.itens()).entrySet()) {
            Produto produto = produtoRepo.findById(e.getKey())
                    .orElseThrow(() -> erro("Produto não encontrado: " + e.getKey()));

            ContaId id = new ContaId(produto.getId(), pedido.getId());
            Conta conta = contaRepo.findById(id).orElse(null);
            if (conta == null) {
                conta = new Conta();
                conta.setId(id);
                conta.setProduto(produto);
                conta.setPedido(pedido);
                conta.setQtdProduto(0);
                conta.setValorProduto(produto.getPreco() == null ? 0.0 : produto.getPreco());
            }
            conta.setQtdProduto(conta.getQtdProduto() + e.getValue());
            contaRepo.save(conta);
        }

        List<Conta> itens = contaRepo.findByPedidoId(pedidoId);
        double total = itens.stream().mapToDouble(c -> c.getValorProduto() * c.getQtdProduto()).sum();
        pedido.setValorTotal(Math.round(total * 100.0) / 100.0);
        return toResponse(pedidoRepo.save(pedido), itens);
    }

    /** Valida os itens e soma quantidades do mesmo produto (a PK de Conta não aceita repetido). */
    private Map<String, Integer> agruparItens(List<ItemRequest> itens) {
        Map<String, Integer> qtdPorProduto = new LinkedHashMap<>();
        for (ItemRequest it : itens) {
            if (it.produtoId() == null || it.produtoId().isBlank()) {
                throw erro("Item sem produto.");
            }
            if (it.quantidade() == null || it.quantidade() <= 0) {
                throw erro("A quantidade de cada item deve ser maior que zero.");
            }
            qtdPorProduto.merge(it.produtoId(), it.quantidade(), Integer::sum);
        }
        return qtdPorProduto;
    }

    private PedidoResponse toResponse(Pedido p, List<Conta> itens) {
        List<ItemResponse> its = itens.stream()
                .map(c -> new ItemResponse(c.getProduto().getId(), c.getProduto().getNome(),
                        c.getQtdProduto(), c.getValorProduto()))
                .toList();
        return new PedidoResponse(p.getId(), p.getMesa(), p.getStatus(), p.getValorTotal(), p.getData() == null ? null : p.getData().toLocalDate(),
                p.getGarcom() == null ? null : p.getGarcom().getNome(), its);
    }

    private ResponseStatusException erro(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
