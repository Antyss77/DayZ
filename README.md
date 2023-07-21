# DayZ

### Aperçu du Projet
DayZ est un projet dédié au développement d'un système d'inventaire d'objets. L'objectif de ce projet est de créer un système d'inventaire bien structuré et efficace, comprenant différents types d'objets tels que des équipements, des armures, des utilitaires, des armes à feu, des couteaux et des projectiles.

### Fonctionnalités
- Organisation des objets en plusieurs catégories : équipements, armures, utilitaires, armes à feu, couteaux et projectiles.
- Chaque objet est défini par des attributs.
- Possibilité d'ajouter des attributs propre à une catégorie d'item.
- Les classes sont bien structurées et héritent d'une classe de base pour une meilleure gestion des objets.
- La classe principale permet de tester les différents objets et d'afficher leurs caractéristiques.

### Exemple d'implémentation
```java
public class Parachute extends Equipment {
    public Parachute() {
        super("Parachute", ItemCategory.EQUIPMENT, ItemType.UTILITIES, 1, 300);
    }
}
```

### Tâches à réaliser
- Implémenter un système d'ID unique pour chaque objet, afin de faciliter leur gestion.
- Associer des munitions aux différentes catégories d'armes
- Associer des effets spéciaux à des armes ou des catégories d'armes

### Licence 
Ce projet est sous licence MIT. Veuillez consulter le fichier [LICENCE](https://github.com/Antyss77/DayZ/blob/main/LICENSE) pour plus d'informations.

