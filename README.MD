# AI Image Generation API

This project provides endpoints to generate images using AI providers like OpenAI (DALL·E) and Stability AI, with optional storage to Cloudinary and a local database.

---

## 🔧 Setup

1. **Create a `.properties` file**  
   Use the provided `application.sample` as a template for your configuration.

2. **Get API Keys**
    - **OpenAI**: Get your API key from [platform.openai.com/api-keys](https://platform.openai.com/api-keys)  
      Add it to your `.properties` file:
      ```
      spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
      ```
    - **Stability AI**: Get your API key from [stability.ai](https://stability.ai/)  
      Add it to your `.properties` file:
      ```
      spring.ai.stabilityai.api-key=YOUR_STABILITY_API_KEY
      ```

3. **Cloudinary Configuration**
    - Sign up and get your credentials from [cloudinary.com](https://cloudinary.com/)
    - Fill in the relevant fields in your `.properties` file.

---

## 🖼️ Image Generation Route

### Endpoint
GET /generate-image/{pathType}
### Path Types
- `justImage`: Returns only the generated image. Saves it to the database and Cloudinary.
- `fullDTO`: Returns a full DTO with the image and metadata, including Cloudinary and local DB URIs.

### Query Parameters
- `prompt` (required): The prompt to generate the image (e.g. `A great roast`)
- `provider` (optional): Choose the AI provider
    - `dalle` (OpenAI)
    - `stability` (Stability AI)

### Example
http://localhost:8080/generate-image/justImage?prompt=A%20Cute%20Dog&provider=dalle"# Spring_ImageGeneration_Exemple" 
