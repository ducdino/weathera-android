package io.github.kbiakov.weathera.models.result;

import java.io.Serializable;


public class CloudsData implements Serializable {

    private int all;

    public int getCloudiness() {
        return all;
    }

}
