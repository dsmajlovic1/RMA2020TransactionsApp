package ba.unsa.etf.rma.rma2020_16570.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private Double budget;
    private Double totalLimit;
    private  Double monthLimit;

    public Account(Double budget, Double totalLimit, Double monthLimit) {
        this.budget = budget;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
    }


    protected Account(Parcel in) {
        if (in.readByte() == 0) {
            budget = null;
        } else {
            budget = in.readDouble();
        }
        if (in.readByte() == 0) {
            totalLimit = null;
        } else {
            totalLimit = in.readDouble();
        }
        if (in.readByte() == 0) {
            monthLimit = null;
        } else {
            monthLimit = in.readDouble();
        }
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(budget);
        dest.writeDouble(totalLimit);
        dest.writeDouble(monthLimit);
    }
}
