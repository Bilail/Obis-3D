# Projet-IHM-3D


![Release](https://img.shields.io/badge/Release-v1.0-blueviolet)
![Language](https://img.shields.io/badge/Language-Java-ff9214)
![size](https://img.shields.io/badge/Size-17Mo-f12222)
![Open Source](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)


## Description 
Le but du projet est de créer une application de visualisation de donnée des annimaux marins. 

## Application 

### Fonctionnalité
- recherche par nom d'espèce 
- auto completion pour les noms d'espèces
- recherche par zone géographique (faire clic + Alt sur le globe)
- recherche sur des intervalles de temps 
- vu des données par un histogramme
- précision géoHash 


### Donnée 
les données utilsé viennet de l'API : : https://api.obis.org/   
Vous avez la possibilité de faire des recherches en lignes avec des requête, ou de tester un jeu de donnée en local. 

### Intrerface

![interface](https://github.com/Bilail/Projet-IHM-3D/blob/master/image/interface.PNG)


## Pré-requis 
Dans le run configuration rajouté sa comme argument, pour pouvoir utiliser la bibliotéque ControlSFX
```
--add-modules javafx.controls,javafx.fxml
--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED
```

