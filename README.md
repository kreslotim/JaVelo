# JaVelo

RAPPORT D’AMÉLIORATIONS APPORTÉES AU PROJET
En plus du fonctionnement standart de JaVelo, notre projet offrira les extensions suivantes: 
    • Le choix du fond de carte (parmis les 5 proposés)
    • Possibilité de supprimer tous les points de passages présents sur la carte,
      ainsi que inverser l’itinéraire.
    • Coloriage de la ligne représentant l'itinéraire, ainsi que le polygone représentant le profil d’élévations en fonction de l’élévation locale, en couleurs de l’arc-en-ciel.
    • L’ajout de l’éléphant rose qui accompagne le cycliste durant le trajet, en changeant sa direction (gauche-droite) en fonction du mouvement de la souris.

Point de vue de l'utilisateur :
Fonds de carte

L’utilisateur a la possibilité de changer le fond de la carte, au moyen des boutons prévus à cet effet, qui se trouvent dans le menu « Fonds de carte », dans la bar de menu en haut de l’application.
Les fonds proposés sont les suivants : 
    • OpenStreetMap (Fond standart)
    • Cycle-OSM (Fond avec les routes cyclables)
    • Dark Layer (Fond sombre, pour les cyclistes sensibles à la luminosité)
    • Light Layer (Fond clair, pour observer clairement la forme du trajet)
    • Swiss Style (Fond adapté au territoire de la Suisse)
                                                                                                                                          
Itinéraire

L’utilisateur a la possibilité de supprimer tous les points de passages (Waypoints)
présents sur la carte en un coup, ou d’inverser l’itinéraire en inversant l’ordre des points de passages constituants la route, au moyen des boutons prévus à cet effet, qui se trouvent dans le menu « Itinéraire » , dans la bar de menu en haut de l’application.



                                                                                                                                              
Coloriage

La ligne représentant l’itinéraire et le polygone représentant le profil d’élévations sont coloriés en couleurs de l’arc-en-ciel, au moyen d’un dégradé linéaire.
Les couleurs sont réparties en fonction de l’élévation : du bas vers le haut, plus l’élévation est grande, plus la longueur d’onde correspondant à sa couleur est élevée : du violet (400 nm) au rouge (700 nm).
                                                                                                                                

Éléphant Rose

Lors du survol de la souris près de l’itinéraire, l’utilisateur a la possibilité d’admirer un très joyeux éléphant rose accompagnant le cycliste solitaire, vers sa destination. En effet, si le cycliste décide de rebrousser chemin, l’éléphant rose dévoué changera de direction également, en suivant son compagnon.
                                                                                                                                  


# Mise en œuvre en Java 
Fonds de carte

Afin de pouvoir changer les fonds de carte, JaVelo est maintenant muni d’une propriété ObjectProperty contenant la carte annotée AnnotatedMapManager.
Chaque bouton de type MenuItem dans le menu « Fonds de carte » s’occupe de créer un nouveau TileManager, prenant en argument le chemin vers le cache contenant les tuiles OSM du serveur correspondant, et le nom du serveur utilisé.
Ce TileManager est ensuite utilisé pour la création d’une novelle carte annotée AnnotatedMapManager, qui est ensuite, à son tour, stockée dans la propriété prévue à cet effet.
Finalement, afin que le binding se refasse à chaque changement de la carte annotée, un listener est installé sur la propriété contenant la carte annotée, afin que celle-ci réagisse au changements.
L’instance de MapViewParameters est déplacée de AnnotatedMapManager vers JaVelo, et stockée dans une propriété, afin que le changement de fond ne provoque pas un recentrage de la carte sur Lausanne.


Itinéraire

Les boutons de type MenuItem dans le menu « Itinéraire » s’occupent chacun respectivement de supprimer les waypoints au moyen de la méthode clear() appelée sur la liste observable des waypoints obtenue grâce au getteur de RouteBean, et inverser l’itinéraire en inversant la liste observable des waypoints, obtenue grâce au getteur de RouteBean, au moyen de la méthode reverse() de Collections.

Coloriage

Le coloriage de la ligne d’itinéraire et du profil d’élévations se fait au moyen du dégradé linéaire en CSS (linear-gradient). Le style correspondant est ajouté à la polyligne au moyen de setStyle(-fx-stroke : linear-gradient(…)), dans la classe RouteManager.
La même procédure est appliquée sur le polygone représentant le profil d’élévations avec le dégradé linéaire dessiné du haut en bas, pour faire correspondre la couleur appropriée à l’élévation correspondante, dans la classe ElevationProfileManager.

Éléphant Rose

Le nœud JavaFX de type Circle qui représente la position mise en évidence est maintenant un disque muni d’une image PNG, stockée dans un répertoire à la racine du projet.
Trois attributs sont rajouté dans la classe RouteManager, de type :
Image, ImagePattern et FileInputStream.
L’ajout d’un attribut de type Image permet de stocker un flot d’entrée d’un fichier, en l’occurrence un PNG d’un éléphant rose volant, de type FileInputStream qui lance une exception de type FileNotFoundException si l’image PNG n’est pas trouvée. 
ImagePattern permet de remplir le cercle avec l’image de l’éléphant rose.
Le rayon du disque est augmenté jusqu’à 20 unités, pour que le cycliste puisse voir et voyager avec le joyeux éléphant rose. 
Afin que le disque réagisse au changement de direction du déplacement de la souris, un gestionnaire d’évènements est installé sur l’instance de Circle,
de type setOnMouseMoved() qui permet de stocker dans une propriété de type DoubleProperty la coordonnée horizontale X actuelle du curseur.
Finalement, un listener est installé sur cette propriété, détectant chaque changement de la position du curseur, lorsque ce dernier se trouve sur le disque, 
qui permet de remplir le contenu du disque avec l’image PNG correspondante, en fonction de si le curseur se déplace de gauche à droite, ou vice-versa.
La direction est déterminée en regardant le signe de la différence des précédentes et nouvelles positions du pointeur.

   
