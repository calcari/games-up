# recommendation.py
from data_loader import load_training_data
from models import UserData
import numpy as np
import pandas as pd
import sklearn
from sklearn.preprocessing import OneHotEncoder
from sklearn.preprocessing import MinMaxScaler
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
from sklearn.neighbors import NearestNeighbors
from fastapi.encoders import jsonable_encoder


RECOMMENDATION_COUNT = 10

# Features
features_categorielles = ["author_id","publisher_id","genre_id","category_id"]
features_numeriques = ["price", "mean_note"]
all_features = features_categorielles + features_numeriques

df_training_data: pd.DataFrame
top_games: pd.DataFrame

model: NearestNeighbors

transformer = ColumnTransformer([
    ("ohe", OneHotEncoder(handle_unknown="ignore"), features_categorielles), # Si une catégorie, un author ... est inconnu (car le csv n'est plus à jour) on l'ignore pour éviter l'erreur "Found unknown categories".
    ("minmax", MinMaxScaler(), features_numeriques)
])

# Extraction et préparation des données
df_training_data = load_training_data("data.csv")
df_features_raw = df_training_data[all_features]


# Préparation des données
transformer.fit(df_features_raw)
df_features_transformed = transformer.transform(df_features_raw)

# Entraînement du modèle
model = NearestNeighbors()
model.fit(df_features_transformed)

top_games = df_training_data.sort_values(by="mean_note", ascending=False).head(RECOMMENDATION_COUNT)


def generate_recommendations(user_data: UserData):
    purchases = user_data.purchases

    if len( [purchase for purchase in purchases if purchase.user_note >= 3]) == 0:
        return top_games[["game_id", "game_name"]].to_dict(orient="records")

    # Calcul d'un jeu "moyen" pour représenter le profil utilisateur
    df_purchases = pd.DataFrame(jsonable_encoder(purchases)) # https://stackoverflow.com/questions/61814887/how-to-convert-a-list-of-pydantic-basemodels-to-pandas-dataframe
    df_purchases["bareme"] = df_purchases["user_note"].apply(bareme)

    np_bareme = np.array(df_purchases["bareme"])

    df_purchases_transformed = transformer.transform(df_purchases[all_features])
    X_mean_game = np.average(df_purchases_transformed.toarray(), weights=np_bareme, axis=0)
    df_purchases["mean_note"] = 5
    # Recherche des jeux similaires
    _, indices = model.kneighbors([X_mean_game], n_neighbors=RECOMMENDATION_COUNT + len(df_purchases))

    recommanded_indexes = indices[0]
    recommanded_games: pd.DataFrame = df_training_data.iloc[recommanded_indexes]

    # On ne recommande pas les jeux déjà achetés
    recommanded_games_filtered = recommanded_games[recommanded_games["game_id"].isin(df_purchases["game_id"]) == False]

    return recommanded_games_filtered[["game_id", "game_name"]].head(RECOMMENDATION_COUNT).to_dict(orient="records")

def bareme(note:float):
    if note < 3:
        return 0
    elif note <= 4:
        return 1
    elif note <= 5:
        return 2
    else:
        return 0