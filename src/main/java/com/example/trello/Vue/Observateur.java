package com.example.trello.Vue;
import com.example.trello.Modele.Sujet;

public interface Observateur {
    public void actualiser(Sujet s);
}
