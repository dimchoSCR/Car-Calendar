package model.Vehicle;

import java.io.Serializable;
import java.util.Date;

import model.Stickers.IVignette;

/**
 * Created by DevM on 12/19/2016.
 */

public class Car extends Vehicle implements Serializable {

    private String engineType;
    private String carType;
    private String kmRange;
    private double vehicleTaxAmmount;
    private int image;

    public Car(){
        super();
    }

//    public Car(int productionYear, String registrationPlate, IVignette vignette) {
//        super(productionYear, registrationPlate, vignette);
//    }

    public Car(Car x){
        super(x.getBrand(),x.getModel(),x.getProductionYear(),x.getRegistrationPlate(),x.getVignette());
        this.engineType = x.engineType;
        this.carType = x.carType;
        this.kmRange = x.kmRange;
        this.vehicleTaxAmmount = x.vehicleTaxAmmount;
        this.image = x.image;
    }

    public String getEngineType() {
        return engineType;
    }

//    public void setYear(int year){
//        super.setProductionYear(year);
//    }

    public void setEngineType(String engineType) {
        if(engineType != null && !engineType.isEmpty()) {
            this.engineType = engineType;
        }
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {

        if(carType != null && !carType.isEmpty()) {
            this.carType = carType;
        }
    }

    public String getKmRange() {
        return kmRange;
    }

    public void setKmRange(String kmRange) {
        if(kmRange != null && !kmRange.isEmpty()) {
            this.kmRange = kmRange;
        }
    }

    public double getVehicleTaxAmmount() {
        return vehicleTaxAmmount;
    }

    public void setVehicleTaxAmmount(double vehicleTaxAmmount) {
        if(vehicleTaxAmmount >= 0) {
            this.vehicleTaxAmmount = vehicleTaxAmmount;
        }
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}