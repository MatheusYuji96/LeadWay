package com.leadway.service;

import com.leadway.dto.integration.ViaCepResponseDTO;
import com.leadway.dto.response.CepResponseDTO;
import com.leadway.exception.CepConsultaException;
import com.leadway.exception.CepInvalidoException;
import com.leadway.exception.CepNaoEncontradoException;
import com.leadway.mapper.CepMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CepService {

    private final RestClient restClient;
    private final CepMapper cepMapper;

    public CepService(RestClient.Builder restClientBuilder, @Value("${viacep.base-url:https://viacep.com.br/ws}") String baseUrl, CepMapper cepMapper){
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.cepMapper = cepMapper;
    }

    public CepResponseDTO consultar(String cep) {
        String cepNormal = normalizar(cep);

        if (!cepNormal.matches("\\d{8}")) {
            throw new CepInvalidoException("CEP inválido. Informe os 8 dígitos");
        }
        try {
            ViaCepResponseDTO viaCep = restClient.get()
                    .uri("/{cep}/json/", cepNormal)
                    .retrieve()
                    .body(ViaCepResponseDTO.class);
            if (viaCep == null || viaCep.isErro()) {
                throw new CepNaoEncontradoException("CEP não foi localizado");
            }

            return cepMapper.toResponse(viaCep, cepNormal);
        } catch (CepInvalidoException | CepNaoEncontradoException e) {
            throw e;
        } catch (RestClientException e) {
            throw new CepConsultaException("Não foi possível consultar o CEP no momento. Tente novamente.", e);
        } catch (Exception e) {
            throw new CepConsultaException("Falha ao consultar o CEP.", e);
        }
    }

    public String normalizar(String cep){
        return String.valueOf(cep == null ? "" : cep).replaceAll("\\D", "");
    }
}
