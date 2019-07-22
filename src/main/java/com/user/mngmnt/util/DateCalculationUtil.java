package com.user.mngmnt.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCalculationUtil {

  public static final String[] DATE_FORMATS = {"dd/MM/yyyy", "dd-MM-yyyy", "yyyy/MM/dd"};
//  public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

  public static Date stringToDate(String dateString) {
    for(String format: DATE_FORMATS){
      try {
        return new SimpleDateFormat(format).parse(dateString);
      }
      catch (Exception e){}
    }
    throw new IllegalArgumentException("Date is not parsable");
  }

	
	
}
