package com.hazelcast.examples.cdr;

import java.io.Serializable;

public class CDR implements Serializable {
    private String ID;
    private String callingNumber;
    private String calledNumber;
    private String destinationNumber;
    private String time;
    private String status;
    private String route="";

    //create a default constructor
    public CDR() {}
    //create a constructor with parameters
    public CDR(String ID, String callingNumber, String calledNumber, String destinationNumber, String time, String status) {
        this.ID = ID;
        this.callingNumber = callingNumber;
        this.calledNumber = calledNumber;
        this.destinationNumber = destinationNumber;
        this.time = time;
        this.status = status;
    }
    //create getters and setters
    public String getCallingNumber() {
        return callingNumber;
    }
    public void setCallingNumber(String callingNumber) {
        this.callingNumber = callingNumber;
    }
    public String getCalledNumber() {
        return calledNumber;
    }
    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }
    public String getDestinationNumber() {
        return destinationNumber;
    }
    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public void addToRoute(String destinationNumber) {
        if(this.route.equals("")){
            this.route = destinationNumber;
        }else{
            this.route = this.route + "," + destinationNumber;
        }
    }
    //create toString method
    @Override
    public String toString() {
        return "CDR{" +
                "ID='" + ID + '\'' +
                ", callingNumber='" + callingNumber + '\'' +
                ", calledNumber='" + calledNumber + '\'' +
                ", destinationNumber='" + destinationNumber + '\'' +
                ", route='" + route + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public String getRoute() {
        return route;
    }
}
