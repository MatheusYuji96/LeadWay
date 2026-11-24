package com.leadway.dto.response;

import java.time.LocalDateTime;

public record InteracaoResponseDTO(
        Integer id, Integer leadId, Integer contatoId, String contatoNome,
        String tipo, LocalDateTime dataHora, String descricao,
        LocalDateTime criadoEm, LocalDateTime atualizadoEm
) {}
