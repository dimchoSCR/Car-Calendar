package model.Vehicle;

import java.io.Serializable;

import model.Stickers.IVignette;

/**
 *  Abstract Vehicle class
 */

public abstract class Vehicle implements Serializable,Comparable<Vehicle> {
    private String brand;
    private String model;
    private int productionYear;
    private String registrationPlate;
    private IVignette vignette;
    private static int id=0;
    private int myId;

    public Vehicle(){
        myId = ++id;
    }

    public Vehicle(String brand, String model,int productionYear,String registrationPlate, IVignette vignette){
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.registrationPlate = registrationPlate;
        this.vignette = vignette;
        this.myId = ++id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        if(brand != null && !brand.isEmpty()) {
            this.brand = brand;
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if(model != null && !model.isEmpty()) {
            this.model = model;
        }
    }


    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public void setVignette(IVignette vignette) {
        this.vignette = vignette;
    }

    public int getId(){
        return myId;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public IVignette getVignette(){
        return vignette;
    }

    @Override
    public int compareTo(Vehicle vehicle) {
        if(this.brand.compareToIgnoreCase(vehicle.brand) == 0){
            if(this.model.compareToIgnoreCase(vehicle.model) == 0){
                if(this.myId == vehicle.myId) return 0;
                else return 1;
            }
            else{
                return  this.model.compareToIgnoreCase(vehicle.model);
            }
        }
        else return this.brand.compareToIgnoreCase(vehicle.brand);
    }
}
