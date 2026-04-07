# 📦 PDB — Projet Base de Données

> Cours de **Systèmes de Gestion de Bases de Données (SGBD)**  
> Bachelier en Développement d'Applications — ISFCE  
> Année académique 2025-2026

---

## 🛠️ Technologies

| Outil | Version | Rôle |
|-------|---------|------|
| Java | 25 | Langage principal |
| JavaFX | 25 | Interface graphique (IHM) |
| Firebird | — | Base de données |
| Lombok | — | Génération automatique du code (getters, equals...) |
| SLF4J | 2.0.17 | Logging |
| MyBatis | 3.5.19 | Exécution de scripts SQL (tests) |
| JUnit | 5 | Tests unitaires |
| Jaybird | 6.0.4 | Driver JDBC Firebird |

---

## 🏗️ Architecture du projet

Ce projet applique deux design patterns combinés :

### Pattern DAO (Data Access Object)
> Découpler l'application de la base de données.  
> La classe métier ne connaît jamais le SQL — elle passe par un DAO.

### Pattern Abstract Factory
> Permettre de changer de base de données (Firebird → H2 → PostgreSQL)  
> sans changer une seule ligne dans l'application.

```
org.isfce.pdb
├── dao/                        # Couche DAO
│   ├── IDAO.java               # Interface générique <T, K>
│   ├── DAOFactory.java         # Fabrique abstraite
│   ├── FBDAOFactory.java       # Fabrique concrète Firebird
│   ├── ITypePieceDao.java      # Interface DAO TypePiece
│   ├── SQLTypePieceDao.java    # Implémentation SQL TypePiece
│   ├── IPieceDao.java          # Interface DAO Piece
│   └── SQLPieceDao.java        # Implémentation SQL Piece
│
├── model/                      # Couche modèle (classes métier)
│   ├── TypePiece.java          # Entité TypePiece (code, nom, humide)
│   └── Piece.java              # Entité Piece (num, nom, description, etage, type, installation)
│
├── databases/
│   ├── connexion/              # Gestion de la connexion SQL
│   │   ├── IConnexionInfos.java      # @FunctionalInterface
│   │   ├── ConnexionFromFile.java    # Lit un fichier .properties
│   │   ├── ConnexionSingleton.java   # Pattern Singleton
│   │   └── PersistanceException.java
│   └── uri/                    # Construction des URLs JDBC
│       ├── IDbURL.java
│       ├── Databases.java      # Enum (FIREBIRD, H2)
│       ├── Firebird_URL.java   # Port 3050
│       └── H2_URL.java         # Port 8082
│
├── exceptions/
│   └── InstallationException.java
│
└── util/
    └── DatabaseUtil.java       # Exécution de scripts SQL via MyBatis

tests/org/isfce/pdb
└── dao/
    ├── TestDaoTypePiece.java   # Tests JUnit TypePiece (2/2 ✓)
    └── TestDaoPiece.java       # Tests JUnit Piece (2/2 ✓)
```

---

## 🔄 Flux de fonctionnement

```
@BeforeAll (tests)
    │
    ▼
ConnexionSingleton.setInfoConnexion(
    new ConnexionFromFile("connexion_test.properties", Databases.FIREBIRD))
    │
    ▼
ConnexionSingleton.getConnexion()  →  Connexion SQL unique (Singleton)
    │
    ▼
DAOFactory.getDAOFactory(FIREBIRD, connexion)
    │
    ▼
factory.getPieceDAO()  →  SQLPieceDao
    │
    ▼
dao.getFromID(1)  /  dao.getListe(null)
```

---

## 📅 Journal de progression

### 1) Initial commit
- Configuration JavaFX, Lombok..
- Pull le projet du prof de son compte GitLab
- Mise en place du projet sur Eclipse
- Configuration GitLab / GitHub 

### 2) Cours du 25 mars 2026 (prof)
**Théorie :** Design Pattern DAO + Abstract Factory  
**Code fourni par le prof :**
- `IDAO.java` — interface générique `<T, K>`
- `DAOFactory.java` — fabrique abstraite avec enum `TypePersistance`
- `FBDAOFactory.java` — fabrique concrète Firebird
- `ITypePieceDao.java` — interface DAO pour TypePiece
- `SQLTypePieceDao.java` — implémentation SQL (`getFromID`, `getListe`)
- `TypePiece.java` — modèle avec Lombok `@Data @AllArgsConstructor`

### 3) Travail personnel (après le cours)
**Construction de la couche Piece (en 5 étapes) :**
1. `Piece.java` — modèle avec 6 attributs dont composition vers `TypePiece`
2. `IPieceDao.java` — interface `extends IDAO<Piece, Integer>`
3. `SQLPieceDao.java` — implémentation avec `buildPiece(ResultSet rs)`
4. `getPieceDAO()` — ajouté dans `DAOFactory` et `FBDAOFactory`
5. `TestDaoPiece.java` — tests unitaires **2/2 ✓**

**Points techniques résolus :**
- Configuration JDK 25 + JavaFX 25 sur Modulepath (pas Classpath)
- `autoCommit = true` dans le fichier `.properties` de test
- `OVERRIDING SYSTEM VALUE` pour insérer un ID explicite sur une colonne `GENERATED ALWAYS AS IDENTITY` en Firebird
- Ordre des DELETE dans le script SQL : enfants avant parents (contrainte FK)

### 4) Cours du 2 avril 2026 (prof)
**Théorie :** Gestion des exceptions métier + méthode `insert()` + Lombok `@Builder`
 
**Code vu en cours :**
- `insert()` dans `SQLTypePieceDao` — implémentation du **C** de CRUD avec `executeUpdate()`, `commit()`, `rollback()`
- `dispatchException()` dans `FBDAOFactory` — traduction des codes d'erreur Firebird en exceptions Java
- `PKException.java` — exception pour clé primaire dupliquée (code Firebird `335544665`)
- `CheckException.java` — exception pour contrainte CHECK violée (code Firebird `335544347`)
- `Piece.java` refactorisé — `@Builder`, `BigDecimal` pour l'étage, annotations Lombok précises
- `testInsert()` dans `TestDaoTypePiece` — test des cas d'erreur avec `assertThrows`
- `scriptInitDBTest.sql` — ajout de `ALTER TABLE TPIECE ALTER NUM_PIE restart with 1`
 
**Points techniques importants :**
- `@Builder` au lieu de `@Data` : contrôle fin de `equals()` et construction fluente
- `BigDecimal` au lieu de `double` : précision numérique exacte
- `dispatchException()` : pattern anti-corruption — l'application ne voit jamais les codes SQL bruts
- `restart with 1` : remet le compteur auto-incrément à zéro après chaque DELETE pour des tests prévisibles
- `assertThrows(X.class, lambda)` : vérifie qu'une exception est bien levée
 
### 5) Travail personnel (après le cours du 2 avril)
**Mise à jour du projet pour suivre le cours :**
1. `Piece.java` — refactorisé avec `@Builder`, `@Getter`, `@EqualsAndHashCode`, `@ToString(exclude=...)`, `BigDecimal`
2. `PKException.java` — créée dans `org.isfce.pdb.exceptions`
3. `CheckException.java` — créée dans `org.isfce.pdb.exceptions`
4. `FBDAOFactory.java` — `dispatchException()` mis à jour avec switch sur codes Firebird
5. `SQLTypePieceDao.java` — ajout `insert()` + `SQL_INSERT` + attribut `factory`
6. `SQLPieceDao.java` — migration vers `Piece.builder()` dans `getFromID()`
7. `scriptInitDBTest.sql` — ajout `restart with 1`, ajout type `SAM`, INSERT sur une seule ligne
8. `TestDaoTypePiece.java` — ajout `testInsert()` **3/3 ✓**
9. `TestDaoPiece.java` — mis à jour avec builder **1/1 ✓**

---

## ⚙️ Configuration requise

### Prérequis
- JDK 25 ([jdk.java.net/25](https://jdk.java.net/25/))
- JavaFX SDK 25 ([gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/))
- Serveur Firebird démarré

### Fichier de connexion
Créer `ressources/connexion.properties` :
```properties
file : C:\\chemin\\vers\\votre\\base.FDB
autoCommit : true
user : SYSDBA
password : masterkey
encoding : UTF8
```

### Fichier de connexion de test
Créer `ressources/connexionPDB2526_test.properties` :
```properties
file : C:\\chemin\\vers\\votre\\base_TEST.FDB
autoCommit : true
user : SYSDBA
password : masterkey
encoding : UTF8
```

---

## 🧪 Lancer les tests

Dans Eclipse :
1. Clic droit sur `tests/org/isfce/pdb/dao/`
2. **Run As** → **JUnit Test**
3. Résultat attendu : **4/4 ✓** (2 TypePiece + 2 Piece)

---

## 📌 Points clés à retenir

| Concept | Explication courte |
|---------|-------------------|
| **DAO** | Sépare le SQL de la logique métier |
| **Abstract Factory** | Change de BD sans toucher l'application |
| **Singleton** | Une seule connexion SQL partagée |
| **IDAO\<T,K\>** | T = type de l'objet, K = type de la clé primaire |
| **Optional\<T\>** | Évite les NullPointerException sur getFromID |
| **OVERRIDING SYSTEM VALUE** | Impose un ID sur une colonne auto-incrémentée Firebird |
| **Modulepath vs Classpath** | JavaFX et JUnit 5 doivent être sur le Modulepath |
| **@Builder** | Construction fluente — plus lisible qu'un constructeur |
| **BigDecimal** | Précision exacte pour les valeurs numériques |
| **PKException** | Traduit l'erreur Firebird 335544665 (doublon de clé primaire) |
| **CheckException** | Traduit l'erreur Firebird 335544347 (contrainte CHECK violée) |
| **dispatchException()** | Pattern anti-corruption — traduit SQL brut en exceptions métier |
| **restart with 1** | Remet le compteur auto-incrément à zéro après DELETE |
| **assertThrows** | Vérifie qu'une exception est bien levée dans les tests |
| **OVERRIDING SYSTEM VALUE** | Impose un ID sur une colonne auto-incrémentée Firebird |
| **Modulepath vs Classpath** | JavaFX et JUnit 5 doivent être sur le Modulepath |

---

## 🔜 Prochaines étapes.. (sûrement)

- [ ] Cahier de charges du projet reçu par le prof..?
- [ ] IHM avec JavaFX + FXML
- [ ] Couche Vue (contrôleurs FXML)
- [ ] Connexion Vue ↔ DAO

---

*Readme réalisé dans le cadre du cours SGBD — ISFCE*
