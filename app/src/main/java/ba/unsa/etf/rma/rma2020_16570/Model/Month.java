package ba.unsa.etf.rma.rma2020_16570.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Month {
    private Date date;

    public Month(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String toString(){
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("MMM");
        SimpleDateFormat simpleYearFormat = new SimpleDateFormat(("yyyy"));
        return simpleMonthFormat.format(date)+" "+simpleYearFormat.format(date);
    }
    public String getMonthNumberString(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.MONTH)+1);
    }
    public String getYearNumberString(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.YEAR));
    }
    public void nextMonth(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        date = cal.getTime();
    }
    public void previousMonth(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        date = cal.getTime();
    }
}
