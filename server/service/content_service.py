import sys
from os.path import dirname
import random
import requests
from bs4 import BeautifulSoup

sys.path.append(dirname(__file__).split("/python")[0])

from parsers import Reddit

headers = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'}


# def get_random_character() -> str:
#     alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqprstuvwxyz0123456789"
#     return random.choice(list(alphabet))
#
#
# def generate_uid() -> str:
#     length = 6
#     uid = ""
#     for i in range(length):
#         uid += get_random_character()
#     return uid


def download_media(src, uid):
    response = requests.get(src, headers=headers)
    handler = None
    if response.status_code == 200:
        soup = BeautifulSoup(response.text)
        if soup:
            if "reddit.com" in src:
                handler = Reddit(soup, src, uid)
        else:
            print("Unable to parse ", src)
    if handler:
        handler.process_soup()
