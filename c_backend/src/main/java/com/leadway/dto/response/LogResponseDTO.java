package com.leadway.dto.response;

/**
 * Item do painel de notificações.
 * O contrato segue o AppNotification usado pelo front:
 * { id, icon, title, description, tone, createdAt, read }.
 *
 * A tabela log não tem coluna de leitura, então "read" é sempre false.
 */
public class LogResponseDTO {

    private String id;
    private String icon;
    private String title;
    private String description;
    private String tone;
    private String createdAt;
    private Boolean read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
