package ba.unsa.etf.rma.rma2020_16570.View;

import java.util.ArrayList;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ICheckLimits {
    void saveAlertDialog(ArrayList<Transaction> transactions);
    void editAlertDialog(ArrayList<Transaction> transactions);
}
