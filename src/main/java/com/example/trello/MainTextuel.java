package com.example.trello;

import com.example.trello.Modele.Modele;
import com.example.trello.Modele.Tache;
import com.example.trello.Modele.TacheComposite;
import com.example.trello.Modele.TacheSimple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Application console pour gérer les tâches
 * Utilise uniquement les classes du package Modèle
 */
public class MainTextuel {
    private static Modele app;
    private static Scanner scanner;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        // Initialisation
        app = new Modele();
        scanner = new Scanner(System.in);

        // Ajoute des tâches de démonstration
        ajouterTachesDemo();

        // Affiche le menu principal
        afficherBienvenue();
        menuPrincipal();

        scanner.close();
    }

    /**
     * Affiche le message de bienvenue
     */
    private static void afficherBienvenue() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     GESTIONNAIRE DE TÂCHES - VERSION CONSOLE           ║");
        System.out.println("║     Pattern MVC + Observateur + Composite              ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Menu principal
     */
    private static void menuPrincipal() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("MENU PRINCIPAL");
            System.out.println("=".repeat(60));
            System.out.println("1. Afficher les tâches (Vue Kanban)");
            System.out.println("2. Afficher les tâches (Vue Liste)");
            System.out.println("3. Afficher les tâches (Vue Gantt)");
            System.out.println("4. Créer une nouvelle tâche simple");
            System.out.println("5. Créer une tâche composite");
            System.out.println("6. Ajouter une sous-tâche à une tâche composite");
            System.out.println("7. Déplacer une tâche");
            System.out.println("8. Afficher les dépendances d'une tâche");
            System.out.println("9. Archiver une tâche");
            System.out.println("10. Supprimer une tâche");
            System.out.println("11. Afficher les statistiques");
            System.out.println("0. Quitter");
            System.out.print("\nVotre choix : ");

            try {
                int choix = Integer.parseInt(scanner.nextLine());

                switch (choix) {
                    case 1:
                        afficherVueKanban();
                        break;
                    case 2:
                        afficherVueListe();
                        break;
                    case 3:
                        afficherVueGantt();
                        break;
                    case 4:
                        creerTacheSimple();
                        break;
                    case 5:
                        creerTacheComposite();
                        break;
                    case 6:
                        ajouterSousTache();
                        break;
                    case 7:
                        deplacerTache();
                        break;
                    case 8:
                        afficherDependances();
                        break;
                    case 9:
                        archiverTache();
                        break;
                    case 10:
                        supprimerTache();
                        break;
                    case 11:
                        afficherStatistiques();
                        break;
                    case 0:
                        continuer = false;
                        System.out.println("\n👋 Au revoir !");
                        break;
                    default:
                        System.out.println("❌ Choix invalide !");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide !");
            }
        }
    }

    /**
     * Affiche la vue Kanban (colonnes)
     */
    private static void afficherVueKanban() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("VUE KANBAN");
        System.out.println("=".repeat(60));

        Map<String, List<Tache>> colonnes = app.getColonnes();

        for (Map.Entry<String, List<Tache>> entry : colonnes.entrySet()) {
            System.out.println("\n┌─ " + entry.getKey().toUpperCase() + " (" + entry.getValue().size() + ") " + "─".repeat(40));

            if (entry.getValue().isEmpty()) {
                System.out.println("│ (vide)");
            } else {
                for (Tache tache : entry.getValue()) {
                    afficherCarteTache(tache);
                }
            }
            System.out.println("└" + "─".repeat(55));
        }
    }

    /**
     * Affiche une carte de tâche
     */
    private static void afficherCarteTache(Tache tache) {
        String type = tache instanceof TacheComposite ? "[COMPOSITE]" : "[SIMPLE]";
        System.out.println("│");
        System.out.println("│ ▪ " + type + " " + tache.getLibelle());
        System.out.println("│   État: " + tache.getEtat());

        if (!tache.getDateDebut().isEmpty() || !tache.getDateFin().isEmpty()) {
            System.out.println("│   Période: " + tache.getDateDebut() + " → " + tache.getDateFin());
        }

        if (tache.getCommentaire() != null && !tache.getCommentaire().isEmpty()) {
            System.out.println("│   Note: " + tache.getCommentaire());
        }

        if (tache instanceof TacheComposite) {
            TacheComposite composite = (TacheComposite) tache;
            int nbEnfants = composite.getEnfants().size();
            if (nbEnfants > 0) {
                System.out.println("│   Sous-tâches: " + nbEnfants);
                double progression = calculerProgression(composite);
                System.out.println("│   Progression: " + String.format("%.0f%%", progression));
            }
        }
    }

    /**
     * Calcule le pourcentage de progression d'une tâche composite
     */
    private static double calculerProgression(TacheComposite tache) {
        List<Tache> enfants = tache.getEnfants();
        if (enfants.isEmpty()) {
            return 0.0;
        }

        long terminees = enfants.stream()
                .filter(t -> "Terminé".equals(t.getColonne()))
                .count();

        return (double) terminees / enfants.size() * 100.0;
    }

    /**
     * Affiche la vue liste
     */
    private static void afficherVueListe() {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("VUE LISTE");
        System.out.println("=".repeat(120));

        List<Tache> taches = app.getTaches();

        if (taches.isEmpty()) {
            System.out.println("Aucune tâche à afficher.");
            return;
        }

        // En-tête
        System.out.printf("%-4s %-10s %-30s %-15s %-12s %-12s %-25s%n",
                "N°", "TYPE", "LIBELLÉ", "ÉTAT", "DATE DÉBUT", "DATE FIN", "COMMENTAIRE");
        System.out.println("-".repeat(120));

        // Lignes
        int index = 1;
        for (Tache tache : taches) {
            String type = tache instanceof TacheComposite ? "Composite" : "Simple";
            String libelle = tache.getLibelle().length() > 28 ?
                    tache.getLibelle().substring(0, 25) + "..." : tache.getLibelle();
            String commentaire = tache.getCommentaire() != null && tache.getCommentaire().length() > 23 ?
                    tache.getCommentaire().substring(0, 20) + "..." :
                    (tache.getCommentaire() != null ? tache.getCommentaire() : "");

            System.out.printf("%-4d %-10s %-30s %-15s %-12s %-12s %-25s%n",
                    index++, type, libelle, tache.getEtat(),
                    tache.getDateDebut(), tache.getDateFin(), commentaire);
        }
    }

    /**
     * Affiche la vue Gantt
     */
    private static void afficherVueGantt() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("VUE GANTT (Timeline)");
        System.out.println("=".repeat(100));

        List<Tache> taches = app.getTaches();

        if (taches.isEmpty()) {
            System.out.println("Aucune tâche à afficher.");
            return;
        }

        // Trouve les dates min et max
        LocalDate dateMin = null;
        LocalDate dateMax = null;

        for (Tache tache : taches) {
            LocalDate debut = tache.getDateDebutLocal();
            LocalDate fin = tache.getDateFinLocal();

            if (debut != null) {
                if (dateMin == null || debut.isBefore(dateMin)) dateMin = debut;
            }
            if (fin != null) {
                if (dateMax == null || fin.isAfter(dateMax)) dateMax = fin;
            }
        }

        if (dateMin == null || dateMax == null) {
            System.out.println("Les tâches n'ont pas de dates définies.");
            return;
        }

        System.out.println("Période: " + dateMin.format(FORMATTER) + " → " + dateMax.format(FORMATTER));
        System.out.println();

        for (Tache tache : taches) {
            afficherLigneGantt(tache, dateMin, dateMax);
        }
    }

    /**
     * Affiche une ligne du Gantt
     */
    private static void afficherLigneGantt(Tache tache, LocalDate dateMin, LocalDate dateMax) {
        LocalDate debut = tache.getDateDebutLocal();
        LocalDate fin = tache.getDateFinLocal();

        if (debut == null || fin == null) {
            return;
        }

        // Nom de la tâche (30 caractères)
        String nom = tache.getLibelle();
        if (nom.length() > 28) {
            nom = nom.substring(0, 25) + "...";
        }
        System.out.printf("%-30s │ ", nom);

        // Calcul de la timeline (60 caractères max)
        long totalJours = java.time.temporal.ChronoUnit.DAYS.between(dateMin, dateMax);
        long joursAvant = java.time.temporal.ChronoUnit.DAYS.between(dateMin, debut);
        long dureeTache = java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;

        int largeurTimeline = 60;
        int espacesAvant = (int) (joursAvant * largeurTimeline / (totalJours + 1));
        int largeurBarre = (int) (dureeTache * largeurTimeline / (totalJours + 1));
        if (largeurBarre < 1) largeurBarre = 1;

        // Affiche la barre
        for (int i = 0; i < espacesAvant; i++) {
            System.out.print(" ");
        }

        char symbole = '█';
        if ("Terminé".equals(tache.getColonne())) {
            symbole = '█'; // Plein
        } else if ("En cours".equals(tache.getColonne())) {
            symbole = '▓'; // Moyen
        } else {
            symbole = '░'; // Léger
        }

        for (int i = 0; i < largeurBarre; i++) {
            System.out.print(symbole);
        }

        System.out.println(" │ " + tache.getEtat());
    }

    /**
     * Crée une nouvelle tâche simple
     */
    private static void creerTacheSimple() {
        System.out.println("\n=== CRÉER UNE TÂCHE SIMPLE ===");

        System.out.print("Libellé : ");
        String libelle = scanner.nextLine();

        System.out.print("Commentaire : ");
        String commentaire = scanner.nextLine();

        System.out.print("Date de début (dd/MM/yyyy) ou vide : ");
        String dateDebutStr = scanner.nextLine();

        System.out.print("Date de fin (dd/MM/yyyy) ou vide : ");
        String dateFinStr = scanner.nextLine();

        System.out.print("Colonne (À faire/En cours/Terminé) [À faire] : ");
        String colonne = scanner.nextLine();
        if (colonne.isEmpty()) colonne = "À faire";

        System.out.print("Durée estimée (jours) [0] : ");
        String dureeStr = scanner.nextLine();
        int duree = dureeStr.isEmpty() ? 0 : Integer.parseInt(dureeStr);

        try {
            LocalDate dateDebut = parseDate(dateDebutStr);
            LocalDate dateFin = parseDate(dateFinStr);

            TacheSimple tache = new TacheSimple(libelle, commentaire, dateDebut, dateFin, colonne, duree);
            app.ajouterTache(tache);
            System.out.println("✅ Tâche créée avec succès !");
        } catch (DateTimeParseException e) {
            System.out.println("❌ Erreur de format de date !");
        }
    }

    /**
     * Crée une tâche composite
     */
    private static void creerTacheComposite() {
        System.out.println("\n=== CRÉER UNE TÂCHE COMPOSITE ===");

        System.out.print("Libellé : ");
        String libelle = scanner.nextLine();

        System.out.print("Commentaire : ");
        String commentaire = scanner.nextLine();

        System.out.print("Date de début (dd/MM/yyyy) ou vide : ");
        String dateDebutStr = scanner.nextLine();

        System.out.print("Date de fin (dd/MM/yyyy) ou vide : ");
        String dateFinStr = scanner.nextLine();

        System.out.print("Colonne (À faire/En cours/Terminé) [À faire] : ");
        String colonne = scanner.nextLine();
        if (colonne.isEmpty()) colonne = "À faire";

        System.out.print("Durée estimée (jours) [0] : ");
        String dureeStr = scanner.nextLine();
        int duree = dureeStr.isEmpty() ? 0 : Integer.parseInt(dureeStr);

        try {
            LocalDate dateDebut = parseDate(dateDebutStr);
            LocalDate dateFin = parseDate(dateFinStr);

            TacheComposite tache = new TacheComposite(libelle, commentaire, dateDebut, dateFin, colonne, duree);
            app.ajouterTache(tache);
            System.out.println("✅ Tâche composite créée avec succès !");
        } catch (DateTimeParseException e) {
            System.out.println("❌ Erreur de format de date !");
        }
    }

    /**
     * Parse une date au format dd/MM/yyyy
     */
    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, FORMATTER);
    }

    /**
     * Ajoute une sous-tâche à une tâche composite
     */
    private static void ajouterSousTache() {
        System.out.println("\n=== AJOUTER UNE SOUS-TÂCHE ===");

        // Affiche les tâches composites
        List<Tache> taches = app.getToutesToches();
        List<TacheComposite> composites = new java.util.ArrayList<>();

        System.out.println("\nTâches composites disponibles :");
        int index = 1;
        for (Tache t : taches) {
            if (t instanceof TacheComposite) {
                composites.add((TacheComposite) t);
                System.out.println(index + ". " + t.getLibelle());
                index++;
            }
        }

        if (composites.isEmpty()) {
            System.out.println("❌ Aucune tâche composite disponible.");
            return;
        }

        System.out.print("\nNuméro de la tâche composite : ");
        int numParent = Integer.parseInt(scanner.nextLine()) - 1;

        if (numParent < 0 || numParent >= composites.size()) {
            System.out.println("❌ Numéro invalide !");
            return;
        }

        TacheComposite parent = composites.get(numParent);

        // Crée la sous-tâche
        System.out.print("Libellé de la sous-tâche : ");
        String libelle = scanner.nextLine();

        System.out.print("Commentaire : ");
        String commentaire = scanner.nextLine();

        LocalDate aujourdHui = LocalDate.now();
        TacheSimple sousTache = new TacheSimple(
                libelle, commentaire, aujourdHui, aujourdHui.plusDays(7), "À faire", 7
        );

        parent.ajouterEnfant(sousTache);
        app.notifierObservateur();
        System.out.println("✅ Sous-tâche ajoutée avec succès !");
    }

    /**
     * Déplace une tâche
     */
    private static void deplacerTache() {
        afficherVueListe();

        System.out.print("\nNuméro de la tâche à déplacer : ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;

        List<Tache> taches = app.getTaches();
        if (num < 0 || num >= taches.size()) {
            System.out.println("❌ Numéro invalide !");
            return;
        }

        Tache tache = taches.get(num);

        System.out.println("\nColonnes disponibles :");
        System.out.println("1. À faire");
        System.out.println("2. En cours");
        System.out.println("3. Terminé");
        System.out.print("Choix : ");

        int choix = Integer.parseInt(scanner.nextLine());
        String colonne = "";

        switch (choix) {
            case 1: colonne = "À faire"; break;
            case 2: colonne = "En cours"; break;
            case 3: colonne = "Terminé"; break;
            default:
                System.out.println("❌ Choix invalide !");
                return;
        }

        app.deplacerTache(tache, colonne);
        System.out.println("✅ Tâche déplacée vers : " + colonne);
    }

    /**
     * Affiche les dépendances d'une tâche
     */
    private static void afficherDependances() {
        afficherVueListe();

        System.out.print("\nNuméro de la tâche : ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;

        List<Tache> taches = app.getTaches();
        if (num < 0 || num >= taches.size()) {
            System.out.println("❌ Numéro invalide !");
            return;
        }

        Tache tache = taches.get(num);
        LinkedList<Tache> dependances = app.getDependance(tache);

        System.out.println("\n=== DÉPENDANCES DE : " + tache.getLibelle() + " ===");

        if (dependances.isEmpty()) {
            System.out.println("Aucune dépendance.");
        } else {
            for (Tache dep : dependances) {
                System.out.println("  → " + dep.getLibelle() + " [" + dep.getEtat() + "]");
            }
        }
    }

    /**
     * Archive une tâche
     */
    private static void archiverTache() {
        afficherVueListe();

        System.out.print("\nNuméro de la tâche à archiver : ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;

        List<Tache> taches = app.getTaches();
        if (num < 0 || num >= taches.size()) {
            System.out.println("❌ Numéro invalide !");
            return;
        }

        Tache tache = taches.get(num);
        app.archiverTache(tache);
        System.out.println("✅ Tâche archivée !");
    }

    /**
     * Supprime une tâche
     */
    private static void supprimerTache() {
        afficherVueListe();

        System.out.print("\nNuméro de la tâche à supprimer : ");
        int num = Integer.parseInt(scanner.nextLine()) - 1;

        List<Tache> taches = app.getTaches();
        if (num < 0 || num >= taches.size()) {
            System.out.println("❌ Numéro invalide !");
            return;
        }

        Tache tache = taches.get(num);
        System.out.print("Confirmer la suppression de '" + tache.getLibelle() + "' ? (o/n) : ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("o")) {
            app.supprimerTache(tache);
            System.out.println("✅ Tâche supprimée !");
        } else {
            System.out.println("Suppression annulée.");
        }
    }

    /**
     * Affiche les statistiques
     */
    private static void afficherStatistiques() {
        List<Tache> taches = app.getToutesToches();

        int totalTaches = taches.size();
        int aFaire = 0, enCours = 0, terminees = 0, archivees = 0;
        int simples = 0, composites = 0;

        for (Tache t : taches) {
            // Type
            if (t instanceof TacheComposite) {
                composites++;
            } else {
                simples++;
            }

            // État
            String colonne = t.getColonne();
            if (t.isArchived()) {
                archivees++;
            } else if ("À faire".equals(colonne)) {
                aFaire++;
            } else if ("En cours".equals(colonne)) {
                enCours++;
            } else if ("Terminé".equals(colonne)) {
                terminees++;
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("STATISTIQUES");
        System.out.println("=".repeat(60));
        System.out.println("Total de tâches : " + totalTaches);
        System.out.println("\nPar type :");
        System.out.println("  • Tâches simples : " + simples);
        System.out.println("  • Tâches composites : " + composites);
        System.out.println("\nPar état :");
        System.out.println("  • À faire : " + aFaire);
        System.out.println("  • En cours : " + enCours);
        System.out.println("  • Terminées : " + terminees);
        System.out.println("  • Archivées : " + archivees);

        if (totalTaches > 0) {
            double tauxCompletion = (double) terminees / totalTaches * 100;
            System.out.println("\nTaux de complétion : " + String.format("%.1f%%", tauxCompletion));
        }
    }

    /**
     * Ajoute des tâches de démonstration
     */
    private static void ajouterTachesDemo() {
        LocalDate aujourdHui = LocalDate.now();

        TacheSimple tache1 = new TacheSimple(
                "Conception de l'interface",
                "Créer les maquettes et wireframes",
                aujourdHui.minusDays(5),
                aujourdHui.plusDays(2),
                "En cours",
                7
        );
        app.ajouterTache(tache1);

        TacheSimple tache2 = new TacheSimple(
                "Développement backend",
                "Implémenter l'API REST",
                aujourdHui,
                aujourdHui.plusDays(14),
                "À faire",
                14
        );
        app.ajouterTache(tache2);

        TacheSimple tache3 = new TacheSimple(
                "Tests unitaires",
                "Écrire et exécuter les tests",
                aujourdHui.minusDays(10),
                aujourdHui.minusDays(3),
                "Terminé",
                7
        );
        app.ajouterTache(tache3);

        TacheComposite tache4 = new TacheComposite(
                "Projet complet",
                "Développement d'une application web",
                aujourdHui.minusDays(10),
                aujourdHui.plusDays(30),
                "En cours",
                40
        );
        app.ajouterTache(tache4);

        // Ajoute des sous-tâches à la tâche composite
        TacheSimple sousTache1 = new TacheSimple(
                "Analyse des besoins",
                "Réunion avec le client",
                aujourdHui.minusDays(10),
                aujourdHui.minusDays(8),
                "Terminé",
                2
        );
        tache4.ajouterEnfant(sousTache1);

        TacheSimple sousTache2 = new TacheSimple(
                "Développement des fonctionnalités",
                "Coder les features principales",
                aujourdHui.minusDays(7),
                aujourdHui.plusDays(10),
                "En cours",
                17
        );
        tache4.ajouterEnfant(sousTache2);
    }
}