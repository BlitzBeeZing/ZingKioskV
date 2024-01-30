package app.zingnow.zingkiosk.model;

import java.io.Serializable;

public class Menu implements Serializable {

    public Menu(String _id, String nameOfItem, String imageURL, String itemDescription, double costPrice, double sellingPrice, String itemAttribute, String restaurantID, int cpType, int __v, int quantity, Boolean isSelected) {
        this._id = _id;
        this.nameOfItem = nameOfItem;
        this.imageURL = imageURL;
        this.itemDescription = itemDescription;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.itemAttribute = itemAttribute;
        this.restaurantID = restaurantID;
        this.cpType = cpType;
        this.__v = __v;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    private String _id;
        private String nameOfItem;
        private String imageURL;
        private String itemDescription;
        private double costPrice;
        private double sellingPrice;
        private String itemAttribute;
        private String restaurantID;
        private int cpType;
        private int __v;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    private int quantity;
        private Boolean isSelected;

        // Constructors, getters, and setters

        public Menu() {
        }



        // Add getters and setters for all fields

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getNameOfItem() {
            return nameOfItem;
        }

        public void setNameOfItem(String nameOfItem) {
            this.nameOfItem = nameOfItem;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }

        public double getCostPrice() {
            return costPrice;
        }

        public void setCostPrice(double costPrice) {
            this.costPrice = costPrice;
        }

        public double getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(double sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public String getItemAttribute() {
            return itemAttribute;
        }

        public void setItemAttribute(String itemAttribute) {
            this.itemAttribute = itemAttribute;
        }

        public String getRestaurantID() {
            return restaurantID;
        }

        public void setRestaurantID(String restaurantID) {
            this.restaurantID = restaurantID;
        }

        public int getCpType() {
            return cpType;
        }

        public void setCpType(int cpType) {
            this.cpType = cpType;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
    }


