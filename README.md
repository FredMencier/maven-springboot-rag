# RAG - Retrieval Augmented Generation

## JudoJourney RAG

Cette application est un système de Retrieval Augmented Generation (RAG) dédié aux techniques de Judo. Elle permet aux utilisateurs de poser des questions en langage naturel sur diverses techniques de Judo (comme les projections, les immobilisations, les étranglements, etc.) et obtient des réponses précises en s'appuyant sur une base de connaissances structurée. Le système utilise un modèle de langage à grande échelle (LLM) pour générer des réponses contextuelles, enrichi par la récupération d'informations pertinentes depuis une base de données vectorielle contenant les détails des techniques de Judo.

## Description technique de l'application

L'application JudoJourney RAG est construite avec les technologies suivantes :

- **Framework backend** : Spring Boot 4.1.x pour la création d'une API REST robuste
- **Modèle de langage** : Intégration avec des LLM via l'API Kilo.ai (support pour Claude Sonnet, Nemotron, etc.)
- **Modèle d'embedding** : text-embedding-3-small pour la vectorisation des techniques de Judo
- **Base de données vectorielle** : Stockage des embeddings pour la recherche sémantique efficace
- **Architecture RAG** :
  1. Réception de la question utilisateur en langage naturel
  2. Vectorisation de la question via le modèle d'embedding
  3. Recherche de similarité dans la base de données vectorielle pour trouver les techniques les plus pertinentes
  4. Augmentation du prompt avec le contexte récupéré
  5. Génération de la réponse par le LLM en utilisant le contexte enrichi
  6. Retour de la réponse structurée à l'utilisateur

Les données des techniques de Judo sont stockées dans le fichier `techniques.md` et sont traitées lors du démarrage de l'application pour créer la base de connaissances vectorielle.

L'API expose un endpoint POST `/JudoJourney/ask` qui accepte une question en français et retourne une réponse détaillée sur les techniques de Judo.


### 🚀 Quick Start

```bash
# 1. Setup environment variables
cp set_jj_env.sh.example set_jj_env.sh
# Edit set_jj_env.sh with your real API key

# 2. Run the setup script
source set_jj_env.sh

# 2. Check configuration
env | grep API_KEY

```

### 🌐 Access Points

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Endpoint**: POST http://localhost:8080/JudoJourney/ask

### 📝 Sample Prompt

```
Donne moi 2 techniques de Judo de niveau ceinture jaune ?
```

---

## 🔧 Configuration

### Environment Variables

All configuration is managed through environment variables:

| Variable  | Description | Default  |
|-----------|-------------|----------|
| `API_KEY` | LLM API Key | Required |

### Program Variables

Edit the folowing variables in `Assistant.java` :

| Variable               | Description         | Default  | Default Value                    |
|------------------------|---------------------|----------|----------------------------------|
| `CHAT_MODEL_NAME`      | Model name          | Required | claude-sonnet-4.6                |
| `FREE_CHAT_MODEL_NAME` | Free Model name     | Optional | nemotron-3-super-120b-a12b:free  |
| `RAG_MODEL_NAME`       | Embeding model name | Required | text-embedding-3-small           |
| `URL`                  | url                 | Required | https://api.kilo.ai/api/gateway/ |

## 🔒 Security

⚠️ **Important**: Never commit `set_jj_env.sh` with real credentials

- `set_jj_env.sh`is gitignored
