package io.github.kbiakov.weathera.models.result;

import java.io.Serializable;


public class WindData implements Serializable {

    private double speed;
    private double deg;

    public double getSpeed() {
        return speed;
    }

    public double getDegree() {
        return deg;
    }

}
