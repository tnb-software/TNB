import os, sys, asyncio

from telethon.sync import TelegramClient
from telethon.sessions import StringSession

async def send(api_id, api_hash, session_str, username, text):
    client = TelegramClient(StringSession(session_str), api_id, api_hash)
    async with client:
        msgObject = await client.send_message(username, text)
        print(f'Message Sent: "{msgObject.message}"')

if __name__ == '__main__':
    arg_api_id = int(os.environ["TELEGRAM_API_ID"])
    arg_api_hash = os.environ["TELEGRAM_API_HASH"]
    arg_session_str = os.environ["TELEGRAM_SESSION"]
    arg_username = os.environ["TELEGRAM_USERNAME"]

    if (len(sys.argv) == 2):
        arg_text = sys.argv[1]

    asyncio.run(send(arg_api_id, arg_api_hash, arg_session_str, arg_username, arg_text))
