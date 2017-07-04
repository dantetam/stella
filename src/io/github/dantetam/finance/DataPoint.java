package io.github.dantetam.finance;

//Represents the data at a single point in time, i.e. a day

public class DataPoint {

	public double price; //Closing price as well
	public int volume;
	
	public double high, low;
	
	public DataPoint(double price, int volume, double high, double low) {
		this.price = price;
		this.volume = volume;
		this.high = high;
		this.low = low;
	}
	
	public double tp() {
		return (high + low + price) / 3.0;
	}
	
}
