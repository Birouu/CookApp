package com.example.kouizine;


import com.google.firebase.firestore.DocumentId;

public class Items {
    String restaurantName ,itemsP,itemdescriP ,priceP,IDC, docId,restoID,ClientID;;


    public Items(){}

    public Items(String restaurantName,String itemsP, String itemdescriP, String priceP) {
        this.itemsP = itemsP;
        this.itemdescriP = itemdescriP;
        this.priceP = priceP;
        this.restaurantName = restaurantName;
        this.docId = docId;
    }
    public Items(String restaurantName,String itemsP, String itemdescriP, String priceP,String restoID) {
        this.itemsP = itemsP;
        this.itemdescriP = itemdescriP;
        this.priceP = priceP;
        this.restaurantName = restaurantName;
        this.docId = docId;
        this.restoID = restoID;

    }
    public Items(String restaurantName,String itemsP, String itemdescriP, String priceP,String restoID,String ClientID) {
        this.itemsP = itemsP;
        this.itemdescriP = itemdescriP;
        this.priceP = priceP;
        this.restaurantName = restaurantName;
        this.docId = docId;
        this.restoID = restoID;
        this.ClientID = ClientID;

    }
    public String getRestaurantName() {return restaurantName;}

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getItemsP() {return itemsP;}

    public void setItemsP(String itemsP) {
        this.itemsP = itemsP;
    }

    public String getItemdescriP() {
        return itemdescriP;
    }

    public void setItemdescriP(String itemdescriP) {
        this.itemdescriP = itemdescriP;
    }

    public String getPriceP() {
        return priceP;
    }

    public void setPriceP(String priceP) {
        this.priceP = priceP;
    }

    public String getRestoID() {
        return restoID;
    }

    public void setRestoID(String restoID) {
        this.restoID = restoID;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String ClientID) {
        this.ClientID = ClientID;
    }


    @DocumentId
    public String getDocId() {
        return docId;
    }

    @DocumentId
    public void setDocId(String docId) {
        this.docId = docId;
    }


}
