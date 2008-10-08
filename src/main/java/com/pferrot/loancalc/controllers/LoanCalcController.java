package com.pferrot.loancalc.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.pferrot.loancalc.beans.LoanInfo;
import com.pferrot.loancalc.beans.RepaySchedule;


public class LoanCalcController extends SimpleFormController
{
	
	protected void doSubmitAction(Object o) throws Exception
	{
		LoanInfo li = (LoanInfo) o;

		double P = li.getPrincipal();
		double i = li.getApr() / (li.getPeriodPerYear() * 100.0);
		int n = li.getYears() * li.getPeriodPerYear();

		double A = P * i / (1 - Math.pow(1 + i, -n));

		li.setPayment(A);

		List<RepaySchedule> repayments = new ArrayList<RepaySchedule>();

		int pno = 1;
		while ((P - 0.0) > 0.001)
		{
			double B = P * i;
			double paidP = A - B;
			P = P - paidP;
			repayments.add(new RepaySchedule(pno, paidP, B, P));
			++pno;
		}

		li.setSchedule(repayments);
	}
}

