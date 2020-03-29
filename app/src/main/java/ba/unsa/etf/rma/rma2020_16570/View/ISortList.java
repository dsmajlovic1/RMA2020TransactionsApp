package ba.unsa.etf.rma.rma2020_16570.View;

import java.util.List;

import ba.unsa.etf.rma.rma2020_16570.Model.Transaction;

public interface ISortList {
    List<Transaction> sortByPrice(List<Transaction> transactionList);
    List<Transaction> sortByName(List<Transaction> transactionList);
    List<Transaction> sortByDate(List<Transaction> transactionList);
    List<Transaction> filterByCategory(List<Transaction> transactionList);
    List<Transaction> filterByMonth(List<Transaction> transactionList);
}
