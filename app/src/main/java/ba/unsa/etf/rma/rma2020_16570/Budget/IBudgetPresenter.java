package ba.unsa.etf.rma.rma2020_16570.Budget;

import ba.unsa.etf.rma.rma2020_16570.Model.Account;

public interface IBudgetPresenter {
    void setAccount(Account account);
    Account getAccount();
}
