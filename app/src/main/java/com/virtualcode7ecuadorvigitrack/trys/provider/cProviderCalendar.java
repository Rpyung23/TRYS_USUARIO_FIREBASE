package com.virtualcode7ecuadorvigitrack.trys.provider;

import java.util.Calendar;
import java.util.Date;

public class cProviderCalendar
{
    private Calendar mCalendar;
    private int year;
    private int mes;
    private int day;
    public cProviderCalendar()
    {
        this.mCalendar = Calendar.getInstance();
        this.day = mCalendar.get(Calendar.DATE);
        this.mes = mCalendar.get(Calendar.MONTH);
        this.year = mCalendar.get(Calendar.YEAR);
    }

    public String getFecha()
    {
        String dayString="";
        String mesString="";
        if (day<10)
        {
            dayString = "0"+String.valueOf(day);
        }else
            {
                dayString = String.valueOf(day);
            }
        if (mes<10)
        {
            mesString = "0"+mes;
        }else
            {
                mesString = String.valueOf(mes);
            }
        return String.valueOf(year)+"-"+mesString+"-"+dayString;
    }
}
