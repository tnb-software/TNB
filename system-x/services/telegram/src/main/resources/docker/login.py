import os

from telethon.sync import TelegramClient
from telethon.sessions import StringSession

def login(api_id, api_hash):
    client = TelegramClient(StringSession(), api_id, api_hash)
    with client:
        print("Your session string is:", client.session.save())

if __name__ == '__main__':
    arg_api_id = int(os.environ["TELEGRAM_API_ID"])
    arg_api_hash = os.environ["TELEGRAM_API_HASH"]

    login(arg_api_id, arg_api_hash)
