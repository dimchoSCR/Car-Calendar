package model.Stickers;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by DevM on 12/20/2016.
 */

public class MonthVignette implements IVignette {
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private Calendar today = Calendar.getInstance();
    private double price;

    private static final String TYPE = "Monthly";

    public MonthVignette(){}

    public MonthVignette(int startYear, int startMonth, int startDay,double price){
        startDate.set(startYear,startMonth,startDay);
        endDate.set(startYear,startMonth,startDay);
        endDate.add(Calendar.MONTH,+1);
        this.price = price;
    }

    public Calendar getStartDateObject() {
        return startDate;
    }

    public void setStartDate(int startYear, int startMonth, int startDay) {
        startDate.set(startYear,startMonth,startDay);
    }

    public Calendar getEndDateObject() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean isValid() {
        today.setTime(new Date());
        if(today.before(endDate)){
            return true;
        }
        else return false;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
