package com.example.trello.Modele;

import java.io.Serializable;

public class Date implements Serializable {
    private static final long serialVersionUID = 1L;

    public Jour jour;
    public Heure heure;

    public Date(Heure h, Jour j) {
        this.heure = h;
        this.jour = j;
    }

    public Date(Heure h) {
        this.jour = new Jour();
        this.heure = h;
    }

    public Date(Jour j) {
        this.jour = j;
        this.heure = new Heure();
    }

    public Date() {
        this.jour = new Jour();
        this.heure = new Heure();
    }



    public Jour getJour() {
        return this.jour;
    }

    public void setJour(String jour) {
        this.jour.setJour(jour);
    }

    public void setJour(Jour jour) {
        this.jour = jour;
    }



    public Heure getHeure() {
        return this.heure;
    }

    public void setHeure(int heure) {
        this.heure.setHeure(heure);
    }

    public void setHeure(Heure heure) {
        this.heure = heure;
    }

    

}
