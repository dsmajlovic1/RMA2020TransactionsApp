package ba.unsa.etf.rma.rma2020_16570.View;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface IFragmentCommunication {
    void add();
    void save(Transaction transaction);
    void edit(Transaction changed, Transaction old);
    void delete(Transaction transaction);
}
