package com.leadway.dto.response;

import java.util.List;

/**
 * Envelope devolvido por GET /vendedores/{id} (tela de detalhe da Equipe).
 * Os nomes de campos do JSON seguem o contrato já usado pelo front:
 * representative, notice, stats, evolution e leads.
 */
public record VendedorDetalheResponseDTO(
        RepresentanteDTO representative,
        NoticeDTO notice,
        List<StatDTO> stats,
        List<EvolutionDTO> evolution,
        List<LeadResponseDTO> leads
) {
    public record RepresentanteDTO(
            Integer id,
            String name,
            String code,
            String region,
            String email,
            String phone,
            int leads,
            int completed,
            int pending,
            Boolean active
    ) {}

    public record NoticeDTO(String title, String description) {}

    public record StatDTO(
            String key,
            String icon,
            String label,
            String value,
            String detail,
            String delta,
            String tone
    ) {}

    public record EvolutionDTO(String month, int value) {}
}
