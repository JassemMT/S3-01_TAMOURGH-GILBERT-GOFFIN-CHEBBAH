package com.example.trello.Modele;

import java.io.Serializable;

public class Heure implements Serializable {
    private static final long serialVersionUID = 1L;

    public final static int NB_HEURE_PAR_JOUR = 8;
    public int heure;

    public Heure(int heure) {
        if (heure <= NB_HEURE_PAR_JOUR && heure >= 0) this.heure = heure;
        else this.heure = 0;
    }

    public Heure() {
        this.heure = 0;
    }

    public int getHeure() {
        return heure;
    }

    public void setHeure(int heure) {
        if (heure <= NB_HEURE_PAR_JOUR && heure >= 0) this.heure = heure;
    }
}
