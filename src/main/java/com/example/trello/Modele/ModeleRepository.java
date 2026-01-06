package com.example.trello.Modele;

import java.io.*;
import java. nio.file.Files;
import java.nio.file.Path;
import java.nio.file. Paths;

public class ModeleRepository {
    private String filePath;

    /**
     * Constructeur du repository
     * @param filePath Chemin complet du fichier de sauvegarde
     */
    public ModeleRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Sauvegarde le modèle dans le fichier
     * @param modele Le modèle à sauvegarder
     */
    public void save(Modele modele) {
        try {
            // Créer les répertoires parents si nécessaire
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Sérialiser le modèle
            try (FileOutputStream fileOut = new FileOutputStream(filePath);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                out.writeObject(modele);
                System.out.println("Modèle sauvegardé dans :  " + filePath); // output pour le debug
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du modèle");
            e.printStackTrace();
            throw new RuntimeException("Impossible de sauvegarder le modèle", e);
        }
    }

    /**
     * Charge le modèle depuis le fichier
     * @return Le modèle chargé, ou un nouveau modèle si le fichier n'existe pas
     */
    public Modele load() {
        // 1. Si le fichier n'existe pas
        if (!exists()) {
            System.out.println("Aucune sauvegarde trouvée.");
            return null; // Retourner null permet à AppTrello de créer les données de test
        }

        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            Modele modele = (Modele) in.readObject();
            System.out.println("Modèle chargé avec succès depuis : " + filePath);
            return modele;

        } catch (java.io.InvalidClassException e) {
            // C'est l'erreur spécifique que vous avez eue (changement de version de classe)
            System.out.println("Fichier de sauvegarde obsolète (incompatible avec la nouvelle version). Reset.");
            return null; // On renvoie null pour forcer la création de nouvelles données

        } catch (IOException | ClassNotFoundException e) {
            // Autres erreurs (fichier corrompu, etc.)
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return null;
        }
    }

    /**
     * Vérifie si le fichier de sauvegarde existe
     * @return true si le fichier existe, false sinon
     */
    public boolean exists() {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Supprime le fichier de sauvegarde
     */
    public void delete() {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out. println("Fichier supprimé : " + filePath);
            } else {
                System.err.println("Impossible de supprimer le fichier :  " + filePath);
            }
        } else {
            System.out.println("Aucun fichier à supprimer");
        }
    }

    /**
     * Obtient le chemin du fichier de sauvegarde
     * @return Le chemin du fichier
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Modifie le chemin du fichier de sauvegarde
     * @param filePath Le nouveau chemin
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}