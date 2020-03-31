package ba.unsa.etf.rma.rma2020_16570.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransactionsModel {
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>(){
        {
            add(new Transaction("25.03.2020", 20.0, "Regular payment March", Transaction.Type.REGULARPAYMENT, "Peta rata za nesto", 30, "25.10.2020"));
            add(new Transaction("22.03.2020", 15.0, "Individual payment March", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("22.04.2020", 18.0, "Individual payment April", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("18.04.2020", 50.0, "Regular income April", Transaction.Type.REGULARINCOME, null, 30, "30.01.2023"));
            add(new Transaction("22.02.2020", 15.0, "Individual payment February", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("14.03.2020", 35.0, "Purchase match", Transaction.Type.PURCHASE, "Item", null, null));
            add(new Transaction("15.02.2020", 15.0, "Regular income February", Transaction.Type.REGULARINCOME, "Income", 25, "06.06.2021"));
            add(new Transaction("05.03.2020", 25.0, "Individual income March", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("22.03.2020", 15.0, "Nesto drugo", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("22.03.2020", 15.0, "Nesto drugo", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("22.03.2020", 15.0, "Nesto drugo", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
            add(new Transaction("22.03.2020", 15.0, "Nesto drugo", Transaction.Type.INDIVIDUALPAYMENT, "Peta rata za nesto", null, null));
        }
    };
}
