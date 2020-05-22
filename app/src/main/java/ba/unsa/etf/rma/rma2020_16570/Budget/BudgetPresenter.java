package ba.unsa.etf.rma.rma2020_16570.Budget;

import android.content.Context;

import ba.unsa.etf.rma.rma2020_16570.Model.Account;

public class BudgetPresenter implements IBudgetPresenter, BudgetInteractor.OnAccountFetched {
    private ISetBudget budgetFragmentAccount;
    private Context context;

    public BudgetPresenter(Context context, ISetBudget budgetFragmentAccount) {
        this.context = context;
        this.budgetFragmentAccount = budgetFragmentAccount;
    }

    @Override
    public void setAccount(Account account) {
        new BudgetInteractor(context, "POST", this, account).execute();
    }

    @Override
    public void getAccount() {
        new BudgetInteractor(context, "GET", this, null).execute();
    }

    @Override
    public void onDone(Account account) {
        budgetFragmentAccount.setAccount(account);
    }
}
