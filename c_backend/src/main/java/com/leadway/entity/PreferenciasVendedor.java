package com.leadway.entity;

import jakarta.persistence.*;

@Entity
public class PreferenciasVendedor {
    @Id private Integer vendedorId;
    @Column(nullable = false) private Boolean newLeads = true;
    @Column(nullable = false) private Boolean appointmentReminders = true;
    @Column(nullable = false) private Boolean lateCadenceAlerts = true;
    @Column(nullable = false) private Boolean weeklySummaryEmail = false;
    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer id) { this.vendedorId = id; }
    public Boolean getNewLeads() { return newLeads; }
    public void setNewLeads(Boolean value) { newLeads = value; }
    public Boolean getAppointmentReminders() { return appointmentReminders; }
    public void setAppointmentReminders(Boolean value) { appointmentReminders = value; }
    public Boolean getLateCadenceAlerts() { return lateCadenceAlerts; }
    public void setLateCadenceAlerts(Boolean value) { lateCadenceAlerts = value; }
    public Boolean getWeeklySummaryEmail() { return weeklySummaryEmail; }
    public void setWeeklySummaryEmail(Boolean value) { weeklySummaryEmail = value; }
}
