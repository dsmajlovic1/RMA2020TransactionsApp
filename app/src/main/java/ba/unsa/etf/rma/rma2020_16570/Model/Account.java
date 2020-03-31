package ba.unsa.etf.rma.rma2020_16570.Model;

public class Account {
    private Double budget;
    private Double totalLimit;
    private  Double monthLimit;

    public Account(Double budget, Double totalLimit, Double monthLimit) {
        this.budget = budget;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
    }


    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(Double monthLimit) {
        this.monthLimit = monthLimit;
    }
}
