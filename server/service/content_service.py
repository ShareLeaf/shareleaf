import random

def get_random_character() -> str:
    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqprstuvwxyz0123456789"
    return random.choice(list(alphabet))

def generate_uid() -> str:
    length = 6
    uid = ""
    for i in range(length):
        uid += get_random_character()
    return uid