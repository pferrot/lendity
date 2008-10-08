package com.pferrot.loancalc.beans;

public class RepaySchedule {
	private int paymentNo;
	private double principal;
	private double interest;
	private double outstanding;

	public int getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(int paymentNo) {
		this.paymentNo = paymentNo;
	}

	public double getPrincipal() {
		return principal;
	}

	public void setPrincipal(double principal) {
		this.principal = principal;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(double outstanding) {
		this.outstanding = outstanding;
	}

	public RepaySchedule(int paymentNo, double principal, double interest,
			double outstanding) {
		setPaymentNo(paymentNo);
		setPrincipal(principal);
		setInterest(interest);
		setOutstanding(outstanding);
	}
}
