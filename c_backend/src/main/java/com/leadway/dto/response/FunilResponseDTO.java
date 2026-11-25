package com.leadway.dto.response;

/**
 * Posição do lead no funil. Os nomes JSON seguem o contrato do front (funnel).
 */
public class FunilResponseDTO {

    private String currentStage;
    private Integer daysInCurrentStage;
    private String enteredStageAt;

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public Integer getDaysInCurrentStage() {
        return daysInCurrentStage;
    }

    public void setDaysInCurrentStage(Integer daysInCurrentStage) {
        this.daysInCurrentStage = daysInCurrentStage;
    }

    public String getEnteredStageAt() {
        return enteredStageAt;
    }

    public void setEnteredStageAt(String enteredStageAt) {
        this.enteredStageAt = enteredStageAt;
    }
}
