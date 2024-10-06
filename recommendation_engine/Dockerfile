FROM python:3.8
WORKDIR /app

COPY recommendation_engine/. recommendation_engine/.
RUN pip3 install -r recommendation_engine/requirements.txt

CMD [ "uvicorn", "recommendation_engine.template_recommendation_api:app", "--host", "0.0.0.0", "--port", "8000", "--reload"];