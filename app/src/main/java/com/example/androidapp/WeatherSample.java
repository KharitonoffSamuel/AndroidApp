package com.example.androidapp;

public class WeatherSample {
    private String month;
    private double rainfall;
    private int sumHours;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }

    public int getSumHours() {
        return sumHours;
    }

    public void setSumHours(int sumHours) {
        this.sumHours = sumHours;
    }

    @Override
    public String toString() {
        return "WeatherSample{" +
                "month='" + month + '\'' +
                ", rainfall=" + rainfall +
                ", sumHours=" + sumHours +
                '}';
    }
}