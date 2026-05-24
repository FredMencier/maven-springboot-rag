# Streaming Chatbot Application
## Streaming Chatbot

Cette application de test est un chatbot conçu pour démontrer les capacités de streaming en temps réel des réponses générées par un modèle de langage à grande échelle (LLM). Elle permet aux utilisateurs d'interagir avec un système d'intelligence artificielle conversationnelle où les réponses sont transmises mot par mot, créant une expérience fluide et réactive similaire à celle des assistants virtuels modernes.

Contrairement aux chatbots traditionnels qui attendent la génération complète d'une réponse avant de l'afficher, cette application utilise le streaming HTTP pour afficher chaque token dès qu'il est produit par le modèle de langage. Cela réduit considérablement la latence perçue et améliore l'engagement utilisateur, particulièrement pour les réponses longues.

## Description technique de l'application

L'application Streaming Chatbot est construite avec les technologies suivantes :

- **Framework backend** : Spring Boot 4.1.x pour la création d'une API REST robuste
- **Modèle de langage** : Intégration avec des LLM via l'API Kilo.ai (support pour Claude Sonnet, Nemotron, etc.)
- **Streaming HTTP** : Utilisation de la réponse en flux continu (Server-Sent Events ou chunked encoding) pour transmettre les réponses mot par mot
- **Architecture de chat simple** :
  1. Réception de la question utilisateur via POST /chat/ask
  2. Transmission directe de la question au modèle de langage
  3. Capture du flux de réponse du LLM en temps réel
  4. Transmission immédiate du flux au client HTTP sans mise en tampon
  5. Fermeture propre de la connexion lorsque la réponse est complète

L'API expose un endpoint POST `/chat/ask` qui accepte une question en français et retourne une réponse en flux continu (Content-Type: text/event-stream) qui peut être consommée par des navigateurs ou des clients HTTP compatibles avec le streaming.

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
- **API Endpoint**: POST http://localhost:8080/chat/ask

### 📝 Sample Prompt

```
Raconte moi une blague de developpeur java ?
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
| `URL`                  | url                 | Required | https://api.kilo.ai/api/gateway/ |

## 🔒 Security

⚠️ **Important**: Never commit `set_jj_env.sh` with real credentials

- `set_jj_env.sh`is gitignored
