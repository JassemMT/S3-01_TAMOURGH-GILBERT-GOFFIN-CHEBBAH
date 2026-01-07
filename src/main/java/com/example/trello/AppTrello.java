package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.ModeleRepository;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;
import com.example.trello.Vue.VueArchives;
import com.example.trello.Vue.VueGantt;
import com.example.trello.Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.time.LocalDate; // <--- Import indispensable

public class AppTrello extends Application {

    private Modele modele;
    private ModeleRepository repository;

    @Override
    public void start(Stage primaryStage) {
        // 1. Chargement de la sauvegarde
        repository = new ModeleRepository("Sauvegarde/app.save");
        modele = repository.load();

        // 2. Si aucune sauvegarde n'existe (premier lancement), on crée des données de test
        if (modele == null || modele.getTaches().isEmpty()) {
            System.out.println("Sauvegarde absente ou vide, création des données de test.");

            // Si le repository a renvoyé null, on doit instancier.
            // S'il a renvoyé un objet vide, on peut le réutiliser ou en refaire un.
            if (modele == null) {
                modele = new Modele();
            }
            initDonneesTest(modele);
        }

        // Création des vues
        VueListe vueListe = new VueListe(modele);
        VueKanban vueKanban = new VueKanban(modele);
        VueGantt vueGantt = new VueGantt(modele);

        VueArchives vueArchives = new VueArchives(modele);

        // TabPane pour gérer les trois vues
        TabPane tabPane = new TabPane();

        Tab tabListe = new Tab("Vue Liste");
        tabListe.setContent(vueListe);
        tabListe.setClosable(false);

        Tab tabKanban = new Tab("Vue Kanban");
        tabKanban.setContent(vueKanban);
        tabKanban.setClosable(false);

        Tab tabGantt = new Tab("Vue Gantt");
        tabGantt.setContent(vueGantt);
        tabGantt.setClosable(false);

        Tab tabArchive = new Tab("Vue Archives");
        tabArchive.setContent(vueArchives);
        tabArchive.setClosable(false);


        tabPane.getTabs().addAll(tabListe, tabKanban, tabGantt, tabArchive);

        // Création de la scène
        Scene scene = new Scene(tabPane, 1200, 700);
        primaryStage.setTitle("Trello Clone - LocalDate & Composite");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Sauvegarde à la fermeture
        if (repository != null && modele != null) {
            modele.exit(repository);
        }
        super.stop();
    }

    // Fonction permettant de créer des données factices
    private void initDonneesTest(Modele modele) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate demain = aujourdhui.plusDays(1);
        // On supprime les colonnes par défaut du constructeur du Modele
        modele.supprimerColonne("Principal");
        modele.supprimerColonne("En cours");
        modele.supprimerColonne("Terminé");

        String[] colonnes = {"O", "P", "Q", "R", "T", "X", "Y", "Z"};
        for (String col : colonnes) {
            modele.ajouterColonne(col);
        }
        int annee = LocalDate.now().getYear();

        // =========================================================================
        // 2. COLONNE X : SCÉNARIO 10 taches
        // =========================================================================
        // --- ARBRE D (15/01, 2 semaines) ---
        TacheComposite D = new TacheComposite("Tache D", "Racine dépendance", LocalDate.of(annee, 1, 15), "X", 14);
        TacheSimple D1 = new TacheSimple("D1", "Sous-tache de D", LocalDate.of(annee, 1, 4), "X", 5);
        D.ajouterEnfant(D1);

        // --- TACHE C (01/02, 1 semaine) ---
        // C est une tache simple ici (elle n'a pas d'enfants mentionnés dans l'énoncé structurel)
        TacheSimple C = new TacheSimple("Tache C", "Dépend de D", LocalDate.of(annee, 2, 1), "X", 7);

        // --- ARBRE A (15/02, 3 semaines) ---
        TacheComposite A = new TacheComposite("Tache A", "Dépend de C", LocalDate.of(annee, 2, 15), "X", 21);
        TacheSimple A1 = new TacheSimple("A1", "Sous-tache A1", LocalDate.of(annee, 2, 1), "X", 7);
        TacheSimple A2 = new TacheSimple("A2", "Sous-tache A2", LocalDate.of(annee, 2, 2), "X", 7);
        A.ajouterEnfant(A1);
        A.ajouterEnfant(A2);

        // --- ARBRE B (22/02, 5 semaines) ---
        TacheComposite B = new TacheComposite("Tache B", "Gros Module", LocalDate.of(annee, 2, 22), "X", 35);

        // Sous-tache B1 (Celle à déplacer plus tard)
        TacheSimple B1 = new TacheSimple("B1", "A déplacer vers Q", LocalDate.of(annee, 2, 1), "X", 14);

        // Sous-tache B3
        TacheSimple B3 = new TacheSimple("B3", "Sous-tache B3", LocalDate.of(annee, 1, 5), "X", 21);

        // Sous-tache B2 (Qui est elle-même un Composite car elle a des enfants)
        TacheComposite B2 = new TacheComposite("B2", "Composite dans B", LocalDate.of(annee, 2, 12), "X", 7);
        TacheSimple B21 = new TacheSimple("B21", "Enfant de B2", LocalDate.of(annee, 1, 12), "X", 2);
        TacheSimple B23 = new TacheSimple("B23", "Enfant de B2", LocalDate.of(annee, 1, 14), "X", 2);

        B2.ajouterEnfant(B21);
        B2.ajouterEnfant(B23);

        // Construction de l'arbre B
        B.ajouterEnfant(B1);
        B.ajouterEnfant(B2);
        B.ajouterEnfant(B3);


        modele.ajouterTache(D);
        modele.ajouterTache(C);


        modele.ajouterTache(A);
        modele.ajouterTache(B);

        // Ajout d'une tâche de remplissage pour atteindre les 10 tâches dans X (4 parents + 10 enfants = 14 objets, mais visuellement on assure)
        // Les enfants sont ajoutés automatiquement via les parents dans certaines vues, mais pour être sûr que tout est chargé :
        modele.ajouterTache(D1);
        modele.ajouterTache(A1);
        modele.ajouterTache(A2);
        modele.ajouterTache(B1);
        modele.ajouterTache(B3);
        modele.ajouterTache(B2);
        modele.ajouterTache(B21);
        modele.ajouterTache(B23);
        String[] autresColonnes = {"O", "P", "Q", "R", "T", "Y", "Z"};

        for (String col : autresColonnes) {
            for (int i = 1; i <= 3; i++) {
                TacheSimple t = new TacheSimple(
                        "Tache " + col + "-" + i,
                        "Remplissage",
                        LocalDate.now().plusDays(i),
                        col,
                        2
                );
                modele.ajouterTache(t);
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}