package io.github.dantetam.finance;

import java.util.ArrayList;
import java.util.List;

//A class for storing and collecting stock data and technical indicators for a certain period of time
//http://cs229.stanford.edu/proj2014/Xinjie%20Di,%20Stock%20Trend%20Prediction%20with%20Technical%20Indicators%20using%20SVM.pdf

public class DataPeriod {

	private List<DataPoint> dataPoints;
	
	public DataPeriod() {
		dataPoints = new ArrayList<>();
	}
	
	public void addDataPoint(DataPoint point) {
		dataPoints.add(point);
	}
	
	public DataPoint getDataPointFromDay(int dayNumber) {
		if (dayNumber < 0 || dayNumber >= dataPoints.size()) {
			throw new IllegalArgumentException("Data point from day index out of range");
		}
		return dataPoints.get(dayNumber);
	}
	
	public int getLastDay() {
		if (dataPoints.size() == 0) {
			throw new IllegalArgumentException("Trying to find index of last day of an empty data set");
		}
		return dataPoints.size() - 1;
	}
	
	public double highestPrice() {
		double maxPrice = -1;
		for (DataPoint dataPoint: dataPoints) {
			if (dataPoint.price > maxPrice || maxPrice == -1) {
				maxPrice = dataPoint.price;
			}
		}
		return maxPrice;
	}
	
	public double lowestPrice() {
		double minPrice = -1;
		for (DataPoint dataPoint: dataPoints) {
			if (dataPoint.price < minPrice || minPrice == -1) {
				minPrice = dataPoint.price;
			}
		}
		return minPrice;
	}
	
	public double closingPrice() {
		if (dataPoints.size() == 0) {
			throw new IllegalArgumentException("Trying to find closing price of an empty data set");
		}
		return dataPoints.get(getLastDay()).price;
	}
	
	public double WilliamsR() {
		return (highestPrice() - closingPrice()) / (highestPrice() - lowestPrice()) * 100.0;
	}
	
	//Where t is the current time and n is the length of the study period
	public double rateOfChange(int t, int n) {
		if (n > t) {
			throw new IllegalArgumentException("Attempt to create a study period longer than entire history");
		}
		double priceT = getDataPointFromDay(t).price;
		double priceTSubN = getDataPointFromDay(t - n).price;
		return (priceT / priceTSubN) * 100.0;
	}
	
	public double momentum(int t, int n) {
		if (n > t) {
			throw new IllegalArgumentException("Attempt to create a study period longer than entire history");
		}
		double priceT = getDataPointFromDay(t).price;
		double priceTSubN = getDataPointFromDay(t - n).price;
		return priceT - priceTSubN;
	}
	
	public double relativeStrengthIndex(int t, int n) {
		if (n > t) {
			throw new IllegalArgumentException("Attempt to create a study period longer than entire history");
		}
		double avgPriceUp = 0, avgPriceDown = 0;
		double upDays = 0, downDays = 0;
		for (int i = t - n; i < t; i++) {
			double before = getDataPointFromDay(i).price;
			double after = getDataPointFromDay(i + 1).price;
			if (after > before) {
				avgPriceUp += after - before;
				upDays++;
			}
			else {
				avgPriceDown += before - after;
				downDays++;
			}
		}
		avgPriceUp /= upDays;
		avgPriceDown /= downDays;
		return avgPriceUp / (avgPriceUp + avgPriceDown) * 100.0;
	}
	
	public double tpAvg(int t, int n) {
		if (n > t) {
			throw new IllegalArgumentException("Attempt to create a study period longer than entire history");
		}
		double avg = 0;
		for (int i = t - n; i < t; i++) {
			double tp = getDataPointFromDay(i).price;
			avg += tp;
		}
		return avg / n;
	}
	public double commodityChannelIndex(int t, int n) {
		
	}
	
}
