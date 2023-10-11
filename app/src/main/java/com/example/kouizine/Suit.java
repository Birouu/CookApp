package com.example.kouizine;


import com.google.firebase.firestore.DocumentId;

public class Suit {
    String cuisinier , plaintes ,IDC, docId;;


    public Suit(){}

    public Suit(String cuisinier, String plaintes,String IDC) {
        this.cuisinier = cuisinier;
        this.plaintes = plaintes;
        this.IDC = IDC;
        this.docId = docId;
    }

    public String getCuisinier() {
        return cuisinier;
    }

    public void setCuisinier(String cuisinier) {
        this.cuisinier = cuisinier;
    }

    public String getPlaintes() {
        return plaintes;
    }

    public void setPlaintes(String plaintes) {
        this.plaintes = plaintes;
    }

    public String getIDC() {
        return IDC;
    }

    public void setIDC(String cuisinier) {
        this.IDC = IDC;
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
