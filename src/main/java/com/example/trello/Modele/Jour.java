package com.example.trello.Modele;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Jour implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jour;

    public static final List<String> JOURS_AUTORISES = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    public Jour(String jour) {
        if (this.JOURS_AUTORISES.contains(jour)) this.jour = jour;
        else this.jour = this.JOURS_AUTORISES.get(0);
    }

    public Jour() {
        this.jour = this.JOURS_AUTORISES.get(0);
    }

    public String getJour() {
        return jour;
    }

    public void setJour() {
        if (this.JOURS_AUTORISES.contains(jour)) this.jour = jour;
    }

    public int compareTo(Jour jour) {
        if (jour.getJour() == this.getJour()) return 0;
        else if(JOURS_AUTORISES.indexOf(this.getJour()) > JOURS_AUTORISES.indexOf(jour.getJour())) return 1;
        else return -1;
    }
}
