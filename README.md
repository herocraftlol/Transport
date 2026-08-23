<div align="center">

# 🚀 CabinTransport

**Cabine volante animée en temps réel pour serveurs Paper 1.21**

Transporte vos joueurs d'un point A à un point B à bord d'une petite cabine qui
file le long d'une trajectoire en arc — comme un grand saut ou un voyage sur
un nuage. Chaque trajet est configurable : durée, hauteur de l'arc, particules,
sons et décor de cabine.

`Paper 1.21` · `Java 21` · `Maven`

</div>

---

## ✨ Présentation

CabinTransport ajoute un système de **transport de joueurs en temps réel** :
au lieu d'une simple téléportation instantanée, le joueur embarque à bord d'une
cabine volante qui parcourt, tick par tick, une **parabole** calculée entre le
départ et l'arrivée. Le résultat est fluide, immersif et entièrement
personnalisable.

Chaque trajet (route) définit :
- un **point de départ** et un **point d'arrivée** (même monde) ;
- une **durée** de trajet en secondes ;
- une **hauteur d'arc** (le « saut » au sommet de la parabole) ;
- des **particules** et **sons** de départ / boucle / arrivée ;
- un **décor de cabine** optionnel (plateforme + 4 poteaux en `BlockDisplay`) ;
- un **verrouillage de caméra** optionnel (le regard suit le sens du trajet,
  comme dans un vrai véhicule).

## 🎯 Ce que fait le plugin

- 🛫 Embarque un joueur dans une cabine et le déplace en temps réel vers sa
  destination, le long d'une courbe en arc.
- 🚀 Déplace le joueur **par téléportation directe à chaque tick** le long de
  la parabole : méthode fiable qui garantit un mouvement continu et fluide
  côté client, sans les désynchronisations de passager rencontrées avec un
  véhicule téléporté.
- 🎨 Affiche une petite cabine (plateforme + poteaux) via des `BlockDisplay`,
  repositionnés à chaque tick et orientés dans le sens du déplacement.
- 📷 **Verrouillage de caméra** (`lock-camera`) : la caméra du joueur peut
  suivre automatiquement la direction du trajet (effet « véhicule ») ou
  rester en vue libre pour qu'il regarde où il veut pendant le vol.
- ✨ Particules et sons à chaque étape du voyage (décollage, vol, atterrissage).
- 🛡️ Le joueur est **invulnérable** pendant le trajet (pas de dégâts de chute,
  de vide ou de suffocation dans le décor).
- 🪽 L'état de vol du joueur est **sauvegardé puis restauré** proprement à
  l'arrivée, pour ne pas perturber la gravité ni les anti-triches pendant le
  trajet.
- 🧹 Nettoyage propre à l'arrivée, en cas d'annulation manuelle
  (`/transport cancel`) ou de déconnexion.
- 🗺️ Trajets entièrement **créables et éditables en jeu**, avec
  **sauvegarde automatique** dans `config.yml` après chaque modification.
- 💾 **Sauvegarde à l'arrêt** : les trajets sont écrits dans `config.yml`
  quand le serveur s'éteint — rien ne se perd, même après un crash.
- 🌍 **Mondes chargés à la demande** : un trajet dont le monde n'est pas
  encore chargé (Multiverse, ordre de démarrage…) reste visible et redevient
  utilisable dès que son monde se charge — plus aucun trajet perdu au
  redémarrage.
- 🔄 Rechargement à chaud de la configuration sans redémarrer le serveur.

## 📦 Installation

1. Téléchargez le fichier `CabinTransport.jar` depuis la [page des releases](../../releases).
2. Placez-le dans le dossier `plugins/` de votre serveur **Paper 1.21**.
3. Démarrez (ou redémarrez) le serveur : un fichier `config.yml` est généré
   automatiquement avec un trajet d'exemple.
4. Personnalisez vos trajets et profitez !

> Prérequis serveur : **Paper 1.21** avec **Java 21**.

## ⚙️ Compilation (depuis les sources)

```bash
mvn clean package
```

Le fichier `target/CabinTransport.jar` est prêt à être placé dans `plugins/`.

Prérequis : **Java 21** et **Maven** installés.

## 🔧 Configuration (`config.yml`)

```yaml
routes:
  exemple:
    display-name: "Village -> Chateau"
    world: world
    start: {x: 0.5, y: 100.0, z: 0.5, yaw: 0.0, pitch: 0.0}
    end:   {x: 60.5, y: 110.0, z: 40.5, yaw: 0.0, pitch: 0.0}
    duration-seconds: 10      # durée totale du trajet
    arc-height: 25.0          # hauteur du "saut" au sommet de l'arc (0 = trajet plat)
    particle: CLOUD
    sound-start: ENTITY_ENDER_DRAGON_FLAP
    sound-loop: BLOCK_AMETHYST_BLOCK_CHIME
    sound-end: ENTITY_PLAYER_LEVELUP
    cabin-visual: true        # affiche une petite cabine (plateforme + poteaux)
    cabin-block: OAK_PLANKS
    lock-camera: true         # true = la caméra suit le sens du trajet ; false = vue libre
```

Ajoutez autant de trajets que vous le souhaitez sous `routes:`, chacun avec son
propre départ / arrivée / durée / hauteur / décor.

## 🎮 Commandes

| Commande | Permission | Description |
|---|---|---|
| `/transport list` | `transport.use` | Liste les trajets disponibles |
| `/transport go <trajet>` | `transport.use` | Embarque le joueur dans la cabine |
| `/transport cancel` | `transport.use` | Annule le voyage en cours |
| `/transport create <id> [nom...]` | `transport.admin` | Crée un nouveau trajet vide |
| `/transport setstart <id>` | `transport.admin` | Définit le point de départ à votre position |
| `/transport setend <id>` | `transport.admin` | Définit le point d'arrivée à votre position |
| `/transport setduration <id> <secondes>` | `transport.admin` | Change la durée du trajet |
| `/transport setheight <id> <hauteur>` | `transport.admin` | Change la hauteur de l'arc |
| `/transport setcabin <id> <true/false>` | `transport.admin` | Active/désactive le décor visuel |
| `/transport setcamera <id> <true/false>` | `transport.admin` | Verrouille (ou non) la caméra dans le sens du trajet |
| `/transport delete <id>` | `transport.admin` | Supprime un trajet |
| `/transport reload` | `transport.admin` | Recharge `config.yml` |
| `/transport save` | `transport.admin` | Sauvegarde les trajets créés en jeu dans `config.yml` |

Alias de commande : `/cabine`, `/ctransport`.

## 🚀 Créer un trajet en jeu (le plus simple)

```
/transport create paris
/transport setstart paris      # en vous tenant au point A
/transport setend paris        # en vous tenant au point B
/transport setduration paris 8
/transport setheight paris 30
/transport setcamera paris true   # la caméra suit le sens du trajet (optionnel)
```

Chaque commande admin (`create`, `setstart`, `setend`, `setduration`,
`setheight`, `setcabin`, `setcamera`, `delete`) **sauvegarde automatiquement**
`config.yml` : plus besoin de penser à `/transport save`, vos trajets survivent
à un redémarrage du serveur. `/transport save` reste disponible pour une
sauvegarde manuelle explicite si besoin.

Puis n'importe quel joueur peut faire `/transport go paris`. 🎉

## 🧠 Comment ça marche techniquement

- Le joueur est **téléporté directement** à chaque tick (20 fois par seconde)
  le long d'une **parabole** calculée entre le départ et l'arrivée
  (`y(t) = lerp(y₀,y₁,t) + arcHeight·4t(1-t)`), ce qui donne l'effet de grand
  saut / vol en cloche, avec un mouvement fluide et suivi en temps réel côté
  client.
  (Versions précédentes : le joueur était « monté » en passager sur un
  `ArmorStand`-véhicule téléporté à chaque tick. Cela ne fonctionne pas
  fiablement : le client Minecraft ne resynchronise pas toujours la position
  du passager quand le véhicule est téléporté à répétition, d'où le symptôme
  « je reste immobile puis je me téléporte à l'arrivée ». La téléportation
  directe du joueur règle ce problème — c'est le cœur de la v1.2.0.)
- Si `cabin-visual: true`, des entités `BlockDisplay` (plateforme + 4 poteaux)
  sont repositionnées à chaque tick autour du joueur pour former une petite
  cabine visible, **orientée dans le sens du déplacement**.
- `lock-camera: true` (par défaut) oriente la caméra du joueur dans le sens
  du trajet, comme dans un vrai véhicule ; mettez `false` pour qu'il garde le
  contrôle de sa vue pendant le trajet.
- Des particules et des sons accompagnent chaque phase du trajet.
- Les points de départ/arrivée sont stockés en **référence brute** (nom du
  monde + coordonnées, classe `PointRef`) et ne sont résolus vers un monde
  Bukkit qu'**au moment d'utiliser le trajet** — jamais au chargement du
  plugin. Ainsi, un trajet dont le monde se charge après le plugin (ordre de
  démarrage, Multiverse…) n'est plus jamais perdu : il s'affiche dans
  `/transport list` avec le statut `(monde non charge)` et redevient
  utilisable dès que son monde est là, sans `/transport reload`.
- Le joueur reçoit temporairement `allowFlight`/`flying` (pour éviter toute
  interférence avec la gravité pendant le trajet, état restauré à l'arrivée)
  et est rendu **invulnérable** (pas de dégâts de chute, de vide, etc.). Tout
  est nettoyé proprement à l'arrivée, en cas d'annulation ou de déconnexion.

## ⚠️ Limites connues

- Le départ et l'arrivée doivent être dans le **même monde**.
- Sur les serveurs très chargés, désactivez `cabin-visual` (`false`) pour
  limiter le nombre d'entités affichées pendant les trajets simultanés.

## 📜 Version

Version actuelle : **1.3.0** — voir les [releases](../../releases) pour le
journal des modifications et les téléchargements.

### 🆕 Nouveautés de la v1.3.0

- 🌍 **Correction des trajets qui disparaissaient au redémarrage** : le monde
  d'un trajet n'est plus résolu au chargement du plugin, mais **au moment
  d'utiliser le trajet**. Si le monde se charge après le plugin (ordre de
  démarrage des plugins, monde créé par Multiverse…), le trajet n'est plus
  perdu : il reste listé avec le statut `(monde non charge)` et redevient
  utilisable tout seul dès que son monde se charge, sans `/transport reload`.
- 💾 **Sauvegarde automatique** : chaque commande admin (`create`, `setstart`,
  `setend`, `setduration`, `setheight`, `setcabin`, `setcamera`, `delete`)
  écrit immédiatement `config.yml`. Vos trajets survivent aux redémarrages
  sans avoir à penser à `/transport save`.
- 🛑 **Sauvegarde à l'arrêt du serveur** : les trajets sont également écrits
  dans `config.yml` quand le plugin se désactive (`onDisable`), pour ne rien
  perdre même en cas d'arrêt inattendu.
- ✉️ **Nouveau message** `world-not-loaded` : si un joueur tente d'emprunter
  un trajet dont le monde n'est pas encore chargé, il reçoit un message clair
  au lieu d'une erreur générique.
- 🧭 **Voyage interrompu proprement** : si le monde d'un trajet est déchargé
  en plein vol, le voyage est annulé proprement (nettoyage complet du joueur
  et du décor) au lieu de rester bloqué.

### 🕰️ Rappel des nouveautés de la v1.2.0

- 🚀 **Refonte du déplacement** : le joueur est **téléporté directement** à
  chaque tick le long de la parabole, en remplacement du siège `ArmorStand`.
  Le mouvement est fluide, continu et correctement synchronisé côté client
  (plus de « freeze puis téléportation à l'arrivée »).
- 📷 **Verrouillage de caméra** : option `lock-camera` et commande
  `/transport setcamera <id> <true/false>`. En `true`, la caméra suit le sens
  du trajet (effet véhicule) ; en `false`, le joueur garde le contrôle de sa
  vue pendant le vol.
- 🪽 **Gestion de l'état de vol** : l'état `allowFlight`/`flying` du joueur
  est sauvegardé avant le trajet puis restauré à l'arrivée, sans interférer
  avec la gravité ni les anti-triches.
- 🛡️ **Invulnérabilité** pendant toute la durée du trajet (chute, vide,
  suffocation dans le décor), annulée proprement à la fin.
- 🧹 Nettoyage simplifié et plus robuste à l'arrivée, à l'annulation et à la
  déconnexion.
