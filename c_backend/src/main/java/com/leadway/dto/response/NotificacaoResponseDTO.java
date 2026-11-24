package com.leadway.dto.response;

import java.util.List;

/**
 * Resposta de GET /notifications, no formato consumido pelo cabeçalho:
 * { items: AppNotification[], unread: number }.
 */
public class NotificacaoResponseDTO {

    private List<LogResponseDTO> items;
    private Integer unread;

    public NotificacaoResponseDTO() {
    }

    public NotificacaoResponseDTO(List<LogResponseDTO> items, Integer unread) {
        this.items = items;
        this.unread = unread;
    }

    public List<LogResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<LogResponseDTO> items) {
        this.items = items;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }
}
