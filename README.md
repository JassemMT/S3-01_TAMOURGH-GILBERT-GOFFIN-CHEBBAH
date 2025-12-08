# S3-01_TAMOURGH-GILBERT-GOFFIN-CHEBBAH
L’application à réaliser dans cette SAE est un organisateur de tâches. De nombreux sites proposent ce type d’outils (Trello, Asana, …) avec différentes approches. 
# SAE S3.01

# PARTIE ANALYSE

Semaine 49 et début 50 – 12h + 2h, dépôt d’un document d’étude préalable à la fin de la seconde
séance de SAE de la semaine 50 et présentation de l’étude préalable aux enseignants d’analyse
(avec éventuellement la présence de l’enseignant de Qualité de Développement du groupe) la
séance suivante :

Chaque groupe fera l’étude préalable du projet et rédigera un document dans lequel figureront : 

- [x]  La liste des fonctionnalités
- [x]  les cas d’utilisations de l’application
- [x]  les diagrammes de cas d’utilisation si cela est nécessaire
- [x]  des descriptions textuelles
- [x]  des DSS
- [x]  des scénarios pourront les compléter,
- [ ]  un diagramme d’activités de l’application
- [x]  un diagramme d’état de l’objet central de l’application (la tâche) ;
- [ ]  conception : un diagramme de classe en y précisant les patrons de conception et
d’architecture que vous prévoyez d’utiliser ;
- [ ]  une maquette de l’application ;
- [ ]  le planning des 6 itérations prévues et les objectifs de chacune (en terme de cas
d’utilisations) avec identification des risques.

Le trello du groupe devra être rempli selon le planning prévu.
Evaluation en analyse : soutenance de 10 minutes par groupe et évaluation du trello et du
document d’étude préalable.

- La liste des fonctionnalités ;
- les cas d’utilisation de l’application et les diagrammes de cas d’utilisation si cela est
nécessaire, des descriptions textuelles, des DSS et/ou scénarios pourront les compléter,
- un diagramme d’activités de l’application et un diagramme d’état de l’objet central de
l’application (la tâche) ;
- conception : un diagramme de classe en y précisant les patrons de conception et
d’architecture que vous prévoyez d’utiliser ;
- une maquette de l’application ;
- le planning des 6 itérations prévues et les objectifs de chacune (en terme de cas
d’utilisations) avec identification des risques.
Le trello du groupe devra être rempli selon le planning prévu.
Evaluation en analyse : soutenance de 10 minutes par groupe et évaluation du trello et du
document d’étude préalable.

# Liste des fonctionnalités

### **Principales :**

- **CRUD Tâche :** Créer, Modifier, Supprimer une tâche /coch
- **Gestion des Dépendances :** Lier une tâche A à une tâche B (B bloque A)
    
    ‌**Sous-tâches :** Gérer l'imbrication (Tâche mère > Tâches filles)
    
- **Vue Kanban :** Affichage en colonnes (To Do, In Progress, Done) avec Glisser-Déposer (Drag & Drop)
- **Vue Liste & Bureau :** Affichage hiérarchique
- **Vue Gantt :** Visualisation chronologique d'une sélection
- **Archivage :** Stocker les tâches terminées hors de la vue principale

### **Secondaires :**

- Catégories/Tags de couleurs (Urgent, Personnel, Travail)
- Tri et filtrage dans les listes

---

# Diagramme de Cas d’utilisation :

<img width="592" height="632" alt="image" src="https://github.com/user-attachments/assets/54173097-c3b5-4b4b-bea5-daf24f41049c" />


## User :

- CRUD des tâches
- archiver des tâche
- visualiser l’ensemble des tâches : bureau / liste
- générer le diagramme de gant

# Description Textuelle

## Créer une tâche

- **Acteur :** Utilisateur
- **Précondition :** L'application est lancée.
- **Scénario Nominal :**
    1. L'utilisateur demande la création d'une nouvelle tâche.
    2. Le système affiche le formulaire de création.
    3. L'utilisateur saisit les informations (Titre, Description, Date échéance, Catégorie/Tag).
    4. L'utilisateur valide.
    5. Le système vérifie la validité des données (ex: titre non vide).
    6. Le système enregistre la tâche avec l'état par défaut "To Do".
    7. Le système met à jour l'affichage courant.
- **Extensions :**
    - *Ajout de sous-tâches :* L'utilisateur peut ajouter directement des sous-tâches à la création.
    - *Données invalides :* Le système signale l'erreur, retour à l'étape 3.
      
<img width="1393" height="824" alt="image" src="https://github.com/user-attachments/assets/2b7e6476-4506-4d62-b76b-224dd6350d14" />

    

# Supprimer une tâche

Cette fonctionnalité permet à l’utilisateur de supprimer définitivement une tâche du système.

L’utilisateur sélectionne une tâche, clique sur l’option de suppression et confirme l’action.
Le système supprime alors la tâche de la base de données et met à jour l’interface pour que cette dernière ne soit plus affichée.
Un message informe l’utilisateur que la suppression a été effectuée avec succès.

## Diagramme de séquence

<img width="841" height="642" alt="image" src="https://github.com/user-attachments/assets/c99b4b19-f674-43ae-a26b-d168ede70c06" />


# Visualiser les tâches sous forme de bureau (vue Kanban)

Cette fonctionnalité permet à l’utilisateur de consulter ses tâches sous la forme d’un tableau Kanban composé de colonnes représentant les différents états (par exemple : À faire, En cours, Terminé).

Lorsque l’utilisateur sélectionne la vue « Bureau », le système récupère toutes les tâches avec leurs attributs et les affiche graphiquement dans leurs colonnes respectives.
L’interface présente alors une organisation visuelle intuitive ressemblant à un tableau de post-it.

## Diagramme de séquence

<img width="1038" height="450" alt="image" src="https://github.com/user-attachments/assets/f3930249-cbf8-4f62-968f-0be9e9203bf5" />

# Visualiser les tâches sous forme de liste

Cette fonctionnalité permet à l’utilisateur de consulter toutes les tâches sous forme d’une liste ordonnée, souvent plus adaptée à une lecture exhaustive ou à des recherches rapides.

En sélectionnant la vue « Liste », l’interface demande au système la liste complète des tâches.
Le système renvoie l’ensemble des données qui sont ensuite affichées sous forme de tableau ou de liste détaillée.
L’utilisateur peut ainsi voir toutes les tâches de manière compacte et triée.

## Diagramme de séquence

<img width="925" height="452" alt="image" src="https://github.com/user-attachments/assets/f6cefdf5-1cab-4942-8c1b-128ced50e5eb" />

# Déplacer une tâche (Kanban)

**Nom :** Déplacer une tâche

**Acteur principal :** Utilisateur

**But :** Changer la position ou l’état d’une tâche (ex : d’une colonne à une autre, ou d’un jour à un autre dans un calendrier).

**Résumé :**

L’utilisateur effectue un glisser-déposer ou une action équivalente pour déplacer une tâche vers une autre colonne, une autre section ou une autre période. Le système met à jour son état, son ordre ou ses dates en fonction du déplacement.

**Scénario principal :**

1. L’utilisateur sélectionne une tâche et la déplace.
2. Le système identifie la nouvelle position ou colonne cible.
3. Le système met à jour l’état, le groupe ou la date de la tâche.
4. Le système sauvegarde les nouvelles informations.
5. Le système met à jour l’affichage pour refléter le déplacement.

**Exception :** Si l'utilisateur dépose la tâche dans "Done", le système peut proposer d'archiver la tâche si l'option est activée.

## Diagramme de séquence

<img width="1520" height="883" alt="image" src="https://github.com/user-attachments/assets/00559af8-6230-49c9-bd3a-14fc20239b7b" />

# Modifier une tâche

**Nom :** Modifier une tâche

**Acteur principal :** Utilisateur

**But :** Mettre à jour les informations d’une tâche existante.

**Résumé :**

L’utilisateur ouvre une tâche, modifie son titre, sa description, ses dates, sa priorité ou tout autre attribut, puis sauvegarde. Le système applique les changements et met à jour l’affichage.

**Scénario principal :**

1. L’utilisateur sélectionne une tâche.
2. Le système affiche la fenêtre de détail.
3. L’utilisateur modifie les informations.
4. L’utilisateur clique sur « Enregistrer ».
5. Le système met à jour la tâche dans les données internes.
6. Le système actualise l’affichage pour refléter les modifications.

## Diagramme de séquence

<img width="1252" height="629" alt="image" src="https://github.com/user-attachments/assets/67d63011-bce4-4ab2-9f20-89c973e705d0" />

# Archiver une tâche

## Diagramme de Séquence

<img width="915" height="664" alt="image" src="https://github.com/user-attachments/assets/ec24ad23-992e-4363-a1de-787bce60dad6" />

# Créer une colonne

Cette fonctionnalité permet à l’utilisateur d’ajouter une nouvelle colonne au bureau Kanban, afin d’adapter le flux de travail à ses besoins.

L’utilisateur clique sur « Ajouter une colonne », saisit un nom puis valide.
Le système crée la nouvelle colonne dans sa structure interne et met à jour l’affichage pour intégrer cette nouvelle colonne dans le tableau.
La colonne apparaît immédiatement prête à accueillir des tâches.

## Diagramme de séquence

<img width="857" height="574" alt="image" src="https://github.com/user-attachments/assets/1d24ba87-1967-4adf-9753-dd78f0a2f812" />

# Modifier libellé d’une colonne

Cette fonctionnalité offre à l’utilisateur la possibilité de renommer une colonne du bureau Kanban (ex : renommer « En cours » en « Travail en cours »).

L’utilisateur sélectionne le libellé, saisit le nouveau nom et valide.
Le système met alors à jour le nom de la colonne dans sa structure interne, puis rafraîchit l’écran pour afficher la nouvelle appellation.
La modification est immédiatement visible pour l’utilisateur.

## Diagramme de séquence

<img width="1203" height="614" alt="image" src="https://github.com/user-attachments/assets/1866c6f0-8dc8-418f-9d19-560f9407e4af" />

# Supprimer une colonne

Cette fonctionnalité permet à l’utilisateur de retirer complètement une colonne du bureau Kanban.

L’utilisateur choisit une colonne, lance l’action de suppression et confirme.
Le système supprime la colonne ainsi que, selon la logique métier, les tâches associées ou leur réaffectation à une autre colonne.
L’interface se réorganise pour afficher seulement les colonnes restantes.
Un message confirme la suppression.

## Diagramme de séquence

<img width="1137" height="661" alt="image" src="https://github.com/user-attachments/assets/8e2b946b-e84b-4f18-abfb-3f51ca2e5cc3" />

# Diagramme d'État de "Tâche"

## **États possibles:**

**A faire faire :** État initial

**En attente  :** Si la tâche dépend d'une autre tâche non finie

**En Cours (In Progress) :** La tâche est en cours de réalisation

**Terminée (Done) :** La tâche est finie

**Archivée :** La tâche est sortie du flux de travail actif

## **Transitions :**

- *Création* -> **To Do**
- *Si dépendance ajoutée* -> Transition vers **Bloquée**
- *Si dépendance résolue (tâche mère finie)* -> Transition auto vers **To Do**
- *Drag & Drop vers colonne suivante* -> Transition **To Do** vers **In Progress**.
- *Fin de travail* -> Transition **In Progress** vers **Done**
- *Action Utilisateur* -> Transition **Done** vers **Archivée**

<img width="719" height="892" alt="image" src="https://github.com/user-attachments/assets/707576c0-3a51-48d7-a49b-e9da551cf53c" />

# Générer le diagramme de Gantt :

**Nom :** Générer un diagramme de Gantt

**Acteur principal :** Utilisateur

**But :** Obtenir une visualisation temporelle des tâches sous forme de diagramme de Gantt.

**Résumé :**

L’utilisateur demande la génération d’un diagramme de Gantt. Le système récupère les tâches, calcule leur position temporelle et génère un diagramme visuellement structuré reprenant les dates de début, de fin, la durée et le chevauchement des tâches. Le diagramme est ensuite affiché à l’utilisateur.

**Scénario principal :**

1. L’utilisateur sélectionne l’option « Gantt ».
2. Le système demande les paramètres d’affichage (période, filtres, tâches incluses).
3. L’utilisateur valide les paramètres.
4. Le système génère graphiquement le diagramme de Gantt.
5. Le système affiche le diagramme à l’utilisateur.

## Diagramme de séquence

<img width="1265" height="593" alt="image" src="https://github.com/user-attachments/assets/8d566e59-2a73-44f9-abe5-855a32926eec" />

Damme de classe

DIagramme d’actcivité
MAquette de l’appli
