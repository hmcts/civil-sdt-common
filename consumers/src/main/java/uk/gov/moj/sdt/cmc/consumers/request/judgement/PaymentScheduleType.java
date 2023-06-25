package uk.gov.moj.sdt.cmc.consumers.request.judgement;

public enum PaymentScheduleType {
    IN_FULL("inFull"),
    INSTALLMENT("installment"),
    IMMEDIATE("immediate");

    private String scheduleType;

    PaymentScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleType() {
        return scheduleType;
    }
}
