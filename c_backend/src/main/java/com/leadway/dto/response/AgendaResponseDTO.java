package com.leadway.dto.response;

import java.util.List;


public class AgendaResponseDTO {

    private boolean connected;
    private List<CompromissoResponseDTO> items;
    private List<OverviewItemDTO> overview;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<CompromissoResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<CompromissoResponseDTO> items) {
        this.items = items;
    }

    public List<OverviewItemDTO> getOverview() {
        return overview;
    }

    public void setOverview(List<OverviewItemDTO> overview) {
        this.overview = overview;
    }
}
