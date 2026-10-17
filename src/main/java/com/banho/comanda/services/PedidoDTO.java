package com.banho.comanda.services;

import java.time.LocalDate;
import java.util.List;

public class PedidoDTO {
    public record ItemRequest(String produtoId, Integer quantidade) {}
    public record ItensRequest(List<ItemRequest> itens) {}
    public record PedidoRequest(Integer mesa, String garcomId, List<ItemRequest> itens) {}
    public record ItemResponse(String produtoId, String produto, Integer quantidade, Double valorUnitario) {}
    public record PedidoResponse(String id, Integer mesa, Boolean status, Double valorTotal, LocalDate data, String garcom, List<ItemResponse> itens) {}
}
