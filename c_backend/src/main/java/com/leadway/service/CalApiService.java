package com.leadway.service;

import com.leadway.exception.CalException;

import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalApiService {

    private static final String URL_BASE = "https://api.cal.com/v2";

    private static final String VERSAO_PADRAO = "2024-08-13";
    private static final String VERSAO_LISTA_BOOKINGS = "2026-05-01";
    private static final String VERSAO_SLOTS = "2024-09-04";

    private static final DateTimeFormatter FORMATO_UTC = DateTimeFormatter.ISO_INSTANT;

    private final RestClient restClient;

    public CalApiService() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpClient);
        fabrica.setReadTimeout(Duration.ofSeconds(15));

        this.restClient = RestClient.builder()
                .baseUrl(URL_BASE)
                .requestFactory(fabrica)
                .build();
    }

    public String buscarUsername(String apiKey) {
        Map<String, Object> resposta = get(apiKey, VERSAO_PADRAO, "/me", new HashMap<>());
        Map<String, Object> dados = comoObjeto(resposta.get("data"));
        Object username = dados.get("username");
        return username == null ? null : username.toString();
    }

    public List<String> buscarHorariosLivres(String apiKey, String username, String slugEvento,
                                             Instant inicio, Instant fim, String fusoHorario) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("slug", slugEvento);
        variaveis.put("usuario", username);
        variaveis.put("inicio", FORMATO_UTC.format(inicio));
        variaveis.put("fim", FORMATO_UTC.format(fim));
        variaveis.put("fuso", fusoHorario);

        Map<String, Object> resposta = get(apiKey, VERSAO_SLOTS,
                "/slots?eventTypeSlug={slug}&username={usuario}&start={inicio}&end={fim}&timeZone={fuso}",
                variaveis);

        List<String> horarios = new ArrayList<>();
        Map<String, Object> dados = comoObjeto(resposta.get("data"));
        for (Object listaDoDia : dados.values()) {
            for (Object item : comoLista(listaDoDia)) {
                Object start = comoObjeto(item).get("start");
                if (start != null) {
                    horarios.add(start.toString());
                }
            }
        }
        return horarios;
    }

    public Map<String, Object> criarBooking(String apiKey, String username, String slugEvento, Instant inicio,
                                            String nomeConvidado, String emailConvidado, String fusoHorario,
                                            Map<String, String> metadata, String notas) {
        Map<String, Object> convidado = new HashMap<>();
        convidado.put("name", nomeConvidado);
        convidado.put("email", emailConvidado);
        convidado.put("timeZone", fusoHorario);
        convidado.put("language", "pt-BR");

        Map<String, Object> corpo = new HashMap<>();
        corpo.put("start", FORMATO_UTC.format(inicio));
        corpo.put("eventTypeSlug", slugEvento);
        corpo.put("username", username);
        corpo.put("attendee", convidado);
        corpo.put("metadata", metadata);   // nosso "post-it": id da empresa, do contato e o tipo
        if (notas != null && !notas.isBlank()) {
            Map<String, Object> campos = new HashMap<>();
            campos.put("notes", notas);
            corpo.put("bookingFieldsResponses", campos);
        }

        Map<String, Object> resposta = post(apiKey, VERSAO_PADRAO, "/bookings", new HashMap<>(), corpo);
        return comoObjeto(resposta.get("data"));
    }

    public List<Map<String, Object>> listarBookings(String apiKey, Instant de, Instant ate) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("de", FORMATO_UTC.format(de));
        variaveis.put("ate", FORMATO_UTC.format(ate));

        Map<String, Object> resposta = get(apiKey, VERSAO_LISTA_BOOKINGS,
                "/bookings?afterStart={de}&beforeEnd={ate}&limit=100", variaveis);

        List<Map<String, Object>> bookings = new ArrayList<>();
        for (Object item : comoLista(resposta.get("data"))) {
            bookings.add(comoObjeto(item));
        }
        return bookings;
    }

    public void cancelarBooking(String apiKey, String uid, String motivo) {
        Map<String, Object> corpo = new HashMap<>();
        if (motivo != null && !motivo.isBlank()) {
            corpo.put("cancellationReason", motivo);
        }
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("uid", uid);
        post(apiKey, VERSAO_PADRAO, "/bookings/{uid}/cancel", variaveis, corpo);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String apiKey, String versao, String caminho, Map<String, Object> variaveis) {
        try {
            Map<String, Object> resposta = restClient.get()
                    .uri(caminho, variaveis)
                    .header("Authorization", "Bearer " + apiKey)   // a API key vai aqui
                    .header("cal-api-version", versao)
                    .retrieve()
                    .body(Map.class);                              // JSON -> Map
            return resposta == null ? new HashMap<>() : resposta;
        } catch (RestClientResponseException e) {
            throw traduzirErro(e);                 // o Cal.com respondeu com erro (400, 401, 500...)
        } catch (RestClientException e) {
            throw CalException.indisponivel();  // nem chegou resposta (internet, timeout...)
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String apiKey, String versao, String caminho,
                                     Map<String, Object> variaveis, Object corpo) {
        try {
            Map<String, Object> resposta = restClient.post()
                    .uri(caminho, variaveis)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("cal-api-version", versao)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)                                   // Map -> JSON
                    .retrieve()
                    .body(Map.class);
            return resposta == null ? new HashMap<>() : resposta;
        } catch (RestClientResponseException e) {
            throw traduzirErro(e);
        } catch (RestClientException e) {
            throw CalException.indisponivel();
        }
    }

    private RuntimeException traduzirErro(RestClientResponseException e) {
        int status = e.getStatusCode().value();

        if (status == 401 || status == 403) {
            return CalException.recusado(
                    "O Cal.com recusou a API key. Gere uma nova em Settings > Developer > API Keys.");
        }
        if (status == 429 || status >= 500) {
            return CalException.indisponivel();
        }
        return CalException.recusado(lerMensagemDoErro(e));
    }

    @SuppressWarnings("unchecked")
    private String lerMensagemDoErro(RestClientResponseException e) {
        try {
            Map<String, Object> corpo = e.getResponseBodyAs(Map.class);
            if (corpo != null) {
                Map<String, Object> erro = comoObjeto(corpo.get("error"));
                if (erro.get("message") != null) {
                    return erro.get("message").toString();
                }
                if (corpo.get("message") != null) {
                    return corpo.get("message").toString();
                }
            }
        } catch (RuntimeException ignorado) {
            // se o corpo não for um JSON legível, usamos a mensagem genérica abaixo
        }
        return "O Cal.com recusou a operação (erro " + e.getStatusCode().value() + ").";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> comoObjeto(Object valor) {
        if (valor instanceof Map) {
            return (Map<String, Object>) valor;
        }
        return new HashMap<>();
    }

    private List<Object> comoLista(Object valor) {
        List<Object> resultado = new ArrayList<>();
        if (valor instanceof List) {
            for (Object item : (List<?>) valor) {
                resultado.add(item);
            }
        }
        return resultado;
    }
}
