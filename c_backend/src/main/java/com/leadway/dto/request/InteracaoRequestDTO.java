package com.leadway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record InteracaoRequestDTO(
        Integer contatoId,
        @NotBlank @Size(max = 45) String tipo,
        LocalDateTime dataHora,
        @NotBlank String descricao
) {}
