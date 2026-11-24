# JudoJourney RAG

Application Spring Boot qui répond aux questions sur le judo et le jujitsu en s'appuyant sur des documents locaux. Le contexte est récupéré avec un RAG (Retrieval-Augmented Generation), puis envoyé au modèle de chat. La réponse de l'interface comprend des citations dans le texte et la liste des documents récupérés.

## Technologies

- Java 21 et Spring Boot 4.2.0
- LangChain4j 1.24.0
- Chat : `openai/gpt-6-luna-pro` via le gateway Kilo (`https://api.kilo.ai/api/gateway/`)
- Embeddings : `qwen3-embedding` exécuté localement par Ollama
- Stockage vectoriel : index en mémoire, reconstruit au démarrage
- Extraction des documents : Apache Tika
- Conversion Markdown vers HTML : CommonMark

## Prérequis

- JDK 21
- Maven
- Ollama installé et démarré
- Une clé API Kilo dans la variable d'environnement `API_KEY`

## Démarrage

Télécharge le modèle d'embedding dans Ollama. Sur macOS, après avoir installé et ouvert l'application Ollama, exécute :

```bash
ollama pull qwen3-embedding
ollama list
```

Si le serveur Ollama n'est pas déjà démarré par l'application, lance-le dans un terminal séparé :

```bash
ollama serve
```

Configure ensuite la clé du gateway Kilo et, si nécessaire, l'adresse du serveur Ollama :

```bash
export API_KEY="votre-cle-kilo"
# Facultatif : la valeur par défaut est http://localhost:11434
export OLLAMA_BASE_URL="http://localhost:11434"
```

Depuis la racine du projet, démarre Spring Boot :

```bash
mvn spring-boot:run
```

L'application est disponible sur `http://localhost:8080/`. L'interface utilise l'endpoint synchrone. La documentation OpenAPI/Swagger est disponible sur `http://localhost:8080/swagger-ui/index.html`.

## Documents RAG

Place les documents à indexer dans `src/main/resources/data/`. Le chargeur Apache Tika traite les documents présents dans ce répertoire, notamment les fichiers Markdown et PDF. Le document actuel du programme de ceinture blanche à ceinture jaune se trouve dans `data/blanche-jaune.md`.

Au démarrage, l'application découpe les documents en segments de 500 caractères avec un chevauchement de 50 caractères. Les documents sont vectorisés sans préfixe. Pour chaque question, l'adaptateur Qwen ajoute une instruction de recherche adaptée au corpus français de judo/jujitsu. Il ne modifie pas le texte des documents enregistrés dans le stockage vectoriel.

Le moteur récupère au plus trois segments par question et utilise un score de pertinence minimal de `0.80` (score LangChain4j normalisé entre 0 et 1). Les noms de fichiers sont conservés afin d'identifier les sources présentées avec la réponse.

Les embeddings ne sont pas persistés : ils sont recalculés à chaque redémarrage. Ollama et `qwen3-embedding` doivent donc être disponibles avant de démarrer l'application.

## API

### Réponse complète avec sources

`POST /JudoJourney/ask/sync` génère la réponse complète, convertit le Markdown en HTML et renvoie les sources récupérées dans un objet JSON.

```bash
curl -X POST http://localhost:8080/JudoJourney/ask/sync \
  -H 'Content-Type: text/plain' \
  -H 'Accept: application/json' \
  --data 'Quelles techniques sont au programme de la ceinture jaune ?'
```

La réponse a cette forme :

```json
{
  "answerHtml": "<p>...</p>",
  "sources": ["blanche-jaune.md"]
}
```

L'interface web affiche le contenu HTML et les noms de sources. Les sources sont tirées des documents réellement récupérés, plutôt que de dépendre uniquement des citations générées par le modèle.

### Réponse en streaming

`POST /JudoJourney/ask` reste disponible pour recevoir la réponse sous forme de fragments `Flux<String>`.

```bash
curl -N -X POST http://localhost:8080/JudoJourney/ask \
  -H 'Content-Type: text/plain' \
  --data 'Comment réaliser O-goshi ?'
```

Les deux endpoints suivent le mode choisi dans `app.rag.enabled`. En mode RAG, ils utilisent le retriever vectoriel ; en mode non-RAG, ils incluent le contenu complet de `blanche-jaune.md`. Les deux modes utilisent le même modèle de chat et le fichier d'instructions `src/main/resources/system-prompt.md`.

## Configuration

| Variable | Rôle | Valeur par défaut |
|---|---|---|
| `API_KEY` | Clé d'accès au gateway Kilo pour le chat | Aucune ; à définir avant le démarrage |
| `OLLAMA_BASE_URL` | URL du serveur Ollama | `http://localhost:11434` |
| `app.rag.enabled` | Active l'index vectoriel RAG (`true`) ou le contexte intégral du fichier `blanche-jaune.md` (`false`) | `true` |

Le nom du modèle de chat, le nom du modèle d'embedding et l'URL du gateway sont définis dans `org.heg.ai.Assistant`.

Pour changer de mode, modifie `app.rag.enabled` dans `src/main/resources/application.properties`, puis redémarre l'application. En mode `false`, l'application ne construit pas l'index et n'a donc pas besoin d'Ollama ; le document complet `data/blanche-jaune.md` est inclus dans le message utilisateur.

Ne commite jamais de vraie clé API dans les fichiers du projet. Le script local `set_jj_env.sh` est ignoré par Git ; `set_jj_env.sh.example` peut servir de modèle.

## Vérification

```bash
mvn test
```
