package com.leadway.dto.response;

// Um horário livre. start = instante completo (volta no POST); label = só a hora (ex.: 09:30).

public class SlotDTO {

    private String start;
    private String label;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
