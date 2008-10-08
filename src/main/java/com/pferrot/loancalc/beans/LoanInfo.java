package com.pferrot.loancalc.beans;

import java.util.List;

public class LoanInfo {
	// IN
	private double principal;
	private double apr; // annual percentage rate
	private int years;
	private int periodPerYear;
	// OUT
	private double payment; // periodic payment amount
	private List<RepaySchedule> schedule; // repayment schedule

	public double getPrincipal() {
		return principal;
	}

	public void setPrincipal(double principal) {
		this.principal = principal;
	}

	public double getApr() {
		return apr;
	}

	public void setApr(double apr) {
		this.apr = apr;
	}

	public int getYears() {
		return years;
	}

	public void setYears(int years) {
		this.years = years;
	}

	public int getPeriodPerYear() {
		return periodPerYear;
	}

	public void setPeriodPerYear(int periodPerYear) {
		this.periodPerYear = periodPerYear;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	public List<RepaySchedule> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<RepaySchedule> schedule) {
		this.schedule = schedule;
	}
}
