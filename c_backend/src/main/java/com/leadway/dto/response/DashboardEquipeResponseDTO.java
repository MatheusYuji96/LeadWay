package com.leadway.dto.response;

import java.util.List;

public record DashboardEquipeResponseDTO(
        int representantesAtivos,
        int leadsEmCarteira,
        int contatosRealizados,
        int pendenciasEquipe,
        List<DesempenhoVendedorDTO> desempenho,
        List<RepresentanteResumoDTO> representantes,
        List<LeadParadoDTO> leadsParados
) {
    public record DesempenhoVendedorDTO(Integer id, String nome, int leads, int contatos, int propostas, int pendencias) {}
    public record RepresentanteResumoDTO(Integer id, String nome, String codigo, String email, int pendencias) {}
    public record LeadParadoDTO(Integer id, String nome, String responsavel, String etapa, long diasParado) {}
}
