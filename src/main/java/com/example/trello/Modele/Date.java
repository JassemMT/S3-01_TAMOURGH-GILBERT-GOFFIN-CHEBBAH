package com.example.trello.Modele;

import java.io.Serializable;

public class Date implements Serializable {
    private static final long serialVersionUID = 1L;

    public Jour jour;
    public int heure;

    public final static int NB_HEURES_PAR_JOUR = 8;

    public Date(Jour j, int h) {
        int jour_sup = 0;
        if (h <= NB_HEURES_PAR_JOUR && h >= 0) this.heure = heure;
        else {
            jour_sup = h / NB_HEURES_PAR_JOUR;
            this.heure = h % NB_HEURES_PAR_JOUR;
        }


        this.jour = j;
    }

    public Date(int h) {
        this.jour = new Jour();
        this.heure = h;
    }

    public Date(Jour j) {
        this.jour = j;
        this.heure = 0;
    }

    public Date() {
        this.jour = new Jour();
        this.heure = 0;
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



    public int getHeure() {
        return this.heure;
    }

    public void setHeure(int heure) {
        this.heure = heure;
    }



    public int compareTo(Date date) {
        if (date.getJour().getJour().equals(this.getJour().getJour())) {
            if (date.getHeure() == this.heure) return 0;
            else {
                if (date.getHeure() == this.getHeure()) return 0;
                else if (this.getHeure() > heure) return 1;
                else return -1;
            }
        } else return this.jour.compareTo(date.getJour());
    }

    public Date calculerJourFin(int heure) {
        int heureComplementJournee = this.NB_HEURES_PAR_JOUR - this.getHeure();
        heure = heure - heureComplementJournee;
        Jour jour = this.getJour().ajouterNbJours(1);

        return new Date(jour, heure); // calcule automatique dans le constructeur
    }

}
