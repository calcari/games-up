from pydantic import BaseModel
from typing import List

class UserPurchase(BaseModel):
    game_id: int
    game_name: str
    author_id: int
    publisher_id: int
    genre_id: int
    category_id: int
    price: float
    mean_note: float
    user_note: float

class UserData(BaseModel):
    user_id: int
    purchases: List[UserPurchase]

class Recommendation(BaseModel):
    game_id: int
    game_name: str