package com.leadway.service;

import com.leadway.dto.response.CalIntegracaoResponseDTO;
import com.leadway.entity.CalIntegracao;
import com.leadway.repository.CalIntegracaoRepository;
import com.leadway.repository.VendedorRepository;
import com.leadway.exception.CalException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CalIntegracaoService {

    private final CalIntegracaoRepository calIntegracaoRepository;
    private final VendedorRepository vendedorRepository;
    private final CalApiService calApiService;

    public CalIntegracaoService(CalIntegracaoRepository calIntegracaoRepository,
                                VendedorRepository vendedorRepository,
                                CalApiService calApiService) {
        this.calIntegracaoRepository = calIntegracaoRepository;
        this.vendedorRepository = vendedorRepository;
        this.calApiService = calApiService;
    }

    public boolean estaConectado(Integer vendedorId) {
        return calIntegracaoRepository.existsById(vendedorId);
    }

    public CalIntegracaoResponseDTO status(Integer vendedorId) {
        Optional<CalIntegracao> integracaoOpt = calIntegracaoRepository.findById(vendedorId);
        if (integracaoOpt.isEmpty()) {
            return montarResposta(null);
        }
        return montarResposta(integracaoOpt.get());
    }

    public CalIntegracaoResponseDTO conectar(Integer vendedorId, String apiKey) {
        if (vendedorId == null || !vendedorRepository.existsById(vendedorId)) {
            throw CalException.naoEncontrado("Vendedor não encontrado.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw CalException.invalido("Informe a API key do Cal.com.");
        }
        String chave = apiKey.trim();
        if (!chave.startsWith("cal_")) {
            throw CalException.invalido(
                    "A API key do Cal.com começa com \"cal_\". Copie-a em Settings > Developer > API Keys.");
        }

        String username = calApiService.buscarUsername(chave);
        if (username == null) {
            throw CalException.invalido("Não foi possível identificar a conta dessa API key.");
        }

        Optional<CalIntegracao> existente = calIntegracaoRepository.findById(vendedorId);
        CalIntegracao integracao;
        if (existente.isPresent()) {
            integracao = existente.get();
        } else {
            integracao = new CalIntegracao();
            integracao.setVendedorId(vendedorId);
        }
        integracao.setCalUsername(username);
        integracao.setCalApiKey(chave);
        integracao.setConectadoEm(LocalDateTime.now());
        calIntegracaoRepository.save(integracao);

        return montarResposta(integracao);
    }

    public void desconectar(Integer vendedorId) {
        if (calIntegracaoRepository.existsById(vendedorId)) {
            calIntegracaoRepository.deleteById(vendedorId);
        }
    }

    public CalIntegracao buscarCredencial(Integer vendedorId) {
        Optional<CalIntegracao> integracaoOpt = calIntegracaoRepository.findById(vendedorId);
        if (integracaoOpt.isEmpty()) {
            throw CalException.naoConectado();
        }
        return integracaoOpt.get();
    }

    private CalIntegracaoResponseDTO montarResposta(CalIntegracao integracao) {
        CalIntegracaoResponseDTO dto = new CalIntegracaoResponseDTO();
        if (integracao == null) {
            dto.setConectado(false);
            return dto;
        }
        dto.setConectado(true);
        dto.setUsername(integracao.getCalUsername());
        dto.setConectadoEm(integracao.getConectadoEm());
        return dto;
    }
}
