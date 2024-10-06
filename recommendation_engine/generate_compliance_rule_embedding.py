from sentence_transformers import SentenceTransformer, util
import pickle

USE_POWER_SAVER = False

model = SentenceTransformer('all-MiniLM-L6-v2' if USE_POWER_SAVER else 'all-mpnet-base-v2') 
filename = 'template_embeddings_weak.pkl' if USE_POWER_SAVER else 'template_embeddings_strong.pkl'

compliance_template_descriptions = [
    ("Task P must precede task Q", "Control Flow"),
    ("Task Q must immediately follow task P", "Control Flow"),
    ("Task Q must occur after task P", "Control Flow"),
    ("Task P must occur at least once", "Control Flow"),
    ("Task t must be performed by resource R", "Resource"),
    ("Task t1 and t2 must be performed by different roles and users", "Resource"),
    ("Task t1 and t2 must be performed by different users", "Resource"),
    ("Task t1 and t2 must be performed by the same user", "Resource"),
    ("Task t1 and t2 must be performed by the same role but different users", "Resource"),
    ("Task Q must occur within k time units after task P", "Time"),
    ("Task P must happen within k time units", "Time"),
    ("Task Q must occur at least k time units after task P", "Time"),
    ("Task Q must follow task P at exactly time k", "Time"),
    ("Task Q must occur exactly k time units after task P", "Time"),
    ("Value X must equal Value Y", "Data"),
    ("Value X must be greater than Value Y", "Data"),
    ("Value X must be greater than or equal to Value Y", "Data"),
    ("Value X must be less than Value Y", "Data"),
    ("Value X must be less than or equal to Value Y", "Data")
]

embeddings = model.encode([x[0] for x in compliance_template_descriptions], convert_to_tensor=True)

with open(filename, "wb") as fOut:
    pickle.dump({'templates': compliance_template_descriptions, 'embeddings': embeddings}, fOut, protocol=pickle.HIGHEST_PROTOCOL)
