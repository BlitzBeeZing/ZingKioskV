package app.zingnow.zingkiosk.model;

public class Delivery {
    public Delivery(String heading1, String isFree, String deliveryAddress, Boolean isSelected, String timeSlot) {
        this.heading1 = heading1;
        this.isFree = isFree;
        this.deliveryAddress = deliveryAddress;
        this.isSelected = isSelected;
        this.timeSlot = timeSlot;
    }

    String heading1;
    String isFree;

    public String getHeading1() {
        return heading1;
    }

    public void setHeading1(String heading1) {
        this.heading1 = heading1;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    String deliveryAddress;
    Boolean isSelected;
    String timeSlot;
}
