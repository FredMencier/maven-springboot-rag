# RAG - Retrieval Augmented Generation

## JudoJourney RAG




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
