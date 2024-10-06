from sentence_transformers import SentenceTransformer, util
import pickle
from fastapi import FastAPI

USE_POWER_SAVER = False

filename = 'recommendation_engine/template_embeddings_weak.pkl' if USE_POWER_SAVER else 'recommendation_engine/template_embeddings_strong.pkl'

with open(filename, "rb") as fIn:
    stored_data = pickle.load(fIn)
    stored_sentences = stored_data['templates']
    stored_embeddings = stored_data['embeddings']
    template_types = list(set([x[1] for x in stored_sentences]))
    
app = FastAPI()
model = SentenceTransformer('all-MiniLM-L6-v2' if USE_POWER_SAVER else 'all-mpnet-base-v2') 

@app.get("/")
async def get_recommended_templates(control_description: str, control_type: str=''):
    embedding = model.encode([control_description], convert_to_tensor=True)
    cosine_scores = util.cos_sim(embedding, stored_embeddings)
    data = sorted(list(zip(cosine_scores.tolist()[0], stored_sentences)), reverse=True)
    data = [x for x in data if x[1][1] == control_type] if control_type in template_types else data
    return {
        "controlDescription": control_description,
        "recommendations": {x[1][1] + ": " + x[1][0]: x[0] for x in data}
    }

