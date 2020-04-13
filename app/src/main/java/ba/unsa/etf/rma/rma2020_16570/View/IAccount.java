package ba.unsa.etf.rma.rma2020_16570.View;

public interface IAccount {
    void setBudget(Double budget);
    void setTotalLimit(Double totalLimit);
    void setMonthLimit(Double monthLimit);
    Double getBudget();
    Double getTotalLimit();
    Double getMonthLimit();
}
