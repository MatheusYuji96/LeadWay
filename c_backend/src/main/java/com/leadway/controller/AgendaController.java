package com.leadway.controller;

import com.leadway.dto.request.AgendamentoRequestDTO;
import com.leadway.dto.response.AgendaResponseDTO;
import com.leadway.dto.response.CompromissoResponseDTO;
import com.leadway.dto.response.SlotsResponseDTO;
import com.leadway.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agenda", description = "Agenda comercial integrada ao Cal.com (as reuniões ficam no Cal.com, não no banco do LeadWay)")
@RestController
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @Operation(summary = "Compromissos do mês",
            description = "Lê os compromissos do vendedor no Cal.com. Sem Cal.com conectado, devolve connected=false.")
    @GetMapping("/agenda")
    public ResponseEntity<AgendaResponseDTO> listar(@RequestParam Integer vendedorId,
                                                    @RequestParam(required = false) String month) {
        return ResponseEntity.ok(agendaService.listar(vendedorId, month));
    }

    @Operation(summary = "Horários livres de um dia",
            description = "Consulta no Cal.com os horários realmente livres do vendedor na data informada (AAAA-MM-DD).")
    @GetMapping("/agenda/slots")
    public ResponseEntity<SlotsResponseDTO> buscarHorariosLivres(@RequestParam Integer vendedorId,
                                                                 @RequestParam String date) {
        return ResponseEntity.ok(agendaService.buscarHorariosLivres(vendedorId, date));
    }

    @Operation(summary = "Cria um compromisso",
            description = "Cria a reunião no Cal.com, que grava no calendário do vendedor e envia o convite por e-mail ao contato.")
    @PostMapping("/appointments")
    public ResponseEntity<CompromissoResponseDTO> criar(@RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.criar(dto));
    }

    @Operation(summary = "Cancela um compromisso",
            description = "Cancela a reunião no Cal.com, que avisa o contato por e-mail.")
    @DeleteMapping("/appointments/{uid}")
    public ResponseEntity<Void> cancelar(@PathVariable String uid,
                                         @RequestParam Integer vendedorId,
                                         @RequestParam(required = false) String motivo) {
        agendaService.cancelar(vendedorId, uid, motivo);
        return ResponseEntity.noContent().build();
    }
}
