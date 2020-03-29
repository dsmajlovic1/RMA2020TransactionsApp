package ba.unsa.etf.rma.rma2020_16570.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransactionsModel {
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>(){
        {
            add(new Transaction("25.03.2020", 20.0, "Rata za nesto", Transaction.Type.REGULARPAYMENT, "Peta rata za nesto", 30, "25.10.2020"));
            add(new Transaction("22.03.2020", 15.0, "Nesto drugo", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
        }
    };
}
