package com.pferrot.loancalc.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.pferrot.loancalc.beans.LoanInfo;


public class LoanCalcValidator implements Validator
{
	public boolean supports(Class aClass)
	{
		return aClass.equals(LoanInfo.class);
	}

	public void validate(Object o, Errors errors)
	{
		LoanInfo li = (LoanInfo) o;

		if (Double.compare(li.getPrincipal(), 0.0) <= 0)
			errors.rejectValue("principal", "error.invalid.principal", "Principal invalid");
		if (Double.compare(li.getApr(), 0.0) <= 0)
			errors.rejectValue("apr", "error.invalid.apr", "APR invalid");
		if (li.getYears() <= 0)
			errors.rejectValue("years", "error.invalid.years", "Number of years invalid");
		if (li.getPeriodPerYear() <= 0)
			errors.rejectValue("periodPerYear", "error.invalid.periodPerYear", "Period invalid");
	}
}

