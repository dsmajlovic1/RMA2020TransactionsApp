package ba.unsa.etf.rma.rma2020_16570.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class Transaction implements Parcelable {

    public enum Type {INDIVIDUALPAYMENT, REGULARPAYMENT, PURCHASE, INDIVIDUALINCOME, REGULARINCOME}

    private Date date;
    private Double amount;
    private String tittle;
    private Type type;
    private String itemDescription;
    private Integer transactionInterval;
    private Date endDate;

    public Transaction(){
        this.date = null;
        this.amount = 0.0;
        this.tittle = "";
        this.type = Type.INDIVIDUALPAYMENT;
        this.itemDescription = "";
        this.endDate = null;
    }

    public Transaction(String date, Double amount, String tittle, Type type, String itemDescription, Integer transactionInterval, String endDate) {
        try {
            this.date = new SimpleDateFormat("dd.MM.yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.amount = amount;
        this.tittle = tittle;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        if(endDate != null){
            try {
                this.endDate = new SimpleDateFormat("dd.MM.yyyy").parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public static Comparator<Transaction> compareByAmount = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getAmount().compareTo(t2.getAmount());
        }
    };
    public static Comparator<Transaction> compareByTitle = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getTittle().compareTo(t2.getTittle());
        }
    };
    public static Comparator<Transaction> compareByDate = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getDate().compareTo(t2.getDate());
        }
    };

    protected Transaction(Parcel in) {
        try{
            date = (Date)in.readSerializable();
        }
        catch (Exception e){
            date = null;
        }
        amount = in.readDouble();
        tittle = in.readString();
        type = Transaction.Type.valueOf(in.readString());
        try{
            itemDescription = in.readString();
        }
        catch (Exception e){
            itemDescription = null;
        }
        try{
            transactionInterval = in.readInt();
        }
        catch (Exception e){
            transactionInterval = null;
        }
        try{
            endDate = (Date) in.readSerializable();
        }
        catch (Exception e){
            endDate = null;
        }
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(date != null) dest.writeSerializable(date);
        dest.writeDouble(amount);
        dest.writeString(tittle);
        dest.writeString(type.toString());
        if(itemDescription != null) dest.writeString(itemDescription);
        if(transactionInterval != null) dest.writeInt(transactionInterval);
        if(endDate != null) dest.writeSerializable(endDate);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(Integer transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}
