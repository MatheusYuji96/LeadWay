package com.leadway.dto.response;

import java.util.List;

public class SlotsResponseDTO {

    private String date;
    private List<SlotDTO> slots;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<SlotDTO> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotDTO> slots) {
        this.slots = slots;
    }
}
