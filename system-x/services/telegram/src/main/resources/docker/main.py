import os
import asyncio
import threading
from concurrent.futures import Future
from flask import Flask, request, jsonify

from telethon import TelegramClient
from telethon.sessions import StringSession

app = Flask(__name__)

# Environment variables
API_ID = int(os.environ["TELEGRAM_API_ID"])
API_HASH = os.environ["TELEGRAM_API_HASH"]
SESSION_STR = os.environ["TELEGRAM_SESSION"]
USERNAME = os.environ["TELEGRAM_USERNAME"]

# Global event loop and thread
loop = None
loop_thread = None


def start_event_loop():
    """Start the event loop in a background thread"""
    global loop
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_forever()


def run_async(coro):
    """Run a coroutine in the background event loop and wait for result"""
    future = asyncio.run_coroutine_threadsafe(coro, loop)
    return future.result()


async def send_message_async(text):
    """Send a message to the configured Telegram chat"""
    client = TelegramClient(StringSession(SESSION_STR), API_ID, API_HASH)
    async with client:
        msg_object = await client.send_message(USERNAME, text)
        return f'Message Sent: "{msg_object.message}"'


def transform_message(message):
    """Transform a Telegram message to JSON-serializable dict"""
    return {
        'sender_id': message.sender_id,
        'text': message.text,
        'chat_id': message.chat.id
    }


async def get_messages_async(limit):
    """Get last N messages from the configured Telegram chat"""
    client = TelegramClient(StringSession(SESSION_STR), API_ID, API_HASH)
    async with client:
        messages = await client.get_messages(USERNAME, limit)
        return list(map(transform_message, messages))


@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({'status': 'healthy'}), 200


@app.route('/messages', methods=['POST'])
def send_message():
    """Send a message to Telegram"""
    try:
        data = request.get_json()
        if not data or 'text' not in data:
            return jsonify({'error': 'Missing "text" in request body'}), 400

        text = data['text']
        result = run_async(send_message_async(text))
        return jsonify({'result': result}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/messages', methods=['GET'])
def get_messages():
    """Get last N messages from Telegram"""
    try:
        limit = request.args.get('limit', default=1, type=int)
        messages = run_async(get_messages_async(limit))
        return jsonify(messages), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    # Start the event loop in a background thread
    loop_thread = threading.Thread(target=start_event_loop, daemon=True)
    loop_thread.start()

    # Wait a bit for the loop to start
    import time
    time.sleep(0.1)

    app.run(host='0.0.0.0', port=8080)
