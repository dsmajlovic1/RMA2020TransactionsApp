package ba.unsa.etf.rma.rma2020_16570.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransactionsModel {
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>(){
        {
            add(new Transaction("25.03.2020", 20.0, "Regular payment March", Transaction.Type.REGULARPAYMENT, "Money received regularly", 30, "25.10.2020"));
            add(new Transaction("15.03.2020", 15.0, "Individual payment March", Transaction.Type.INDIVIDUALPAYMENT, "Item bought", null, null));
            add(new Transaction("22.03.2020", 19.0, "Second Individual payment March", Transaction.Type.INDIVIDUALPAYMENT, "Another item bought", null, null));
            add(new Transaction("05.03.2020", 25.0, "Individual income March", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("14.03.2020", 50.0, "Another Individual income March", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("20.03.2020", 35.0, "Purchase March", Transaction.Type.PURCHASE, "Item purchased", null, null));
            add(new Transaction("24.03.2020", 22.0, "Another Purchase March", Transaction.Type.PURCHASE, "Item purchased again", null, null));
            add(new Transaction("26.03.2020", 22.0, "Third Purchase March", Transaction.Type.PURCHASE, "Third time's the charm", null, null));
            add(new Transaction("17.03.2020", 50.0, "Regular income March", Transaction.Type.REGULARINCOME, null, 30, "25.10.2021"));

            add(new Transaction("22.04.2020", 18.0, "Individual payment April", Transaction.Type.INDIVIDUALPAYMENT, "Something payed", null, null));
            add(new Transaction("28.04.2020", 26.0, "Individual income April", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("12.04.2020", 40.0, "Purchase April", Transaction.Type.PURCHASE, "Item purchased", null, null));
            add(new Transaction("15.04.2020", 3.0, "Another Purchase April", Transaction.Type.PURCHASE, "Item purchased again", null, null));
            add(new Transaction("18.04.2020", 50.0, "Regular income April", Transaction.Type.REGULARINCOME, null, 30, "30.01.2023"));
            add(new Transaction("02.04.2020", 5.0, "Regular payment April", Transaction.Type.REGULARPAYMENT, "Paying regularly", 25, "02.04.2035"));

            add(new Transaction("22.02.2020", 26.0, "Individual payment February", Transaction.Type.INDIVIDUALPAYMENT, "Item bought", null, null));
            add(new Transaction("15.02.2020", 100.0, "Regular income February", Transaction.Type.REGULARINCOME, "Income", 25, "06.06.2021"));
            add(new Transaction("02.02.2020", 6.0, "Purchase February", Transaction.Type.PURCHASE, "Item purchased", null, null));
            add(new Transaction("28.02.2020", 7.0, "Regular income February", Transaction.Type.REGULARPAYMENT, "Regularly payed service", 30, "28.10.2020"));
            add(new Transaction("05.02.2020", 16.0, "Individual income February", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("10.02.2020", 15.0, "Another Individual income February", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("20.02.2020", 17.0, "Third Individual income February", Transaction.Type.INDIVIDUALINCOME, null, null, null));
            add(new Transaction("20.02.2019", 17.0, "Individual income February 2019", Transaction.Type.INDIVIDUALINCOME, null, null, null));
        }
    };
}
