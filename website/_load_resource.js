const REFETCH_WAIT_MS = 10000; // 10 seconds

const $chat_contents = document.getElementById('chat_contents');
const $div_util = document.getElementById('div');
let uiState = {
    state: 'loading'
};

async function fetchChatResources() {
    try {
        // test only
        // http://[host]/chats
        // http://[host]/servers
        // replase this to your api server url
        const res = await fetch('http://localhost:5114/chats');
        const chats = await res.json();
        uiState = {
            state: 'ready',
            chats,
            updateTime: new Date(),
        };
    } catch (err) {
        console.error('Failed to fetch chat messages');
        console.error(err);
        uiState = { state: 'error' };
    }

    uiUpdateContent();
}

let nextRefetchTimeout = 0;
async function refetchLoopTrigger() {
    clearTimeout(nextRefetchTimeout);
    await fetchChatResources();
    nextRefetchTimeout = setTimeout(refetchLoopTrigger, REFETCH_WAIT_MS);
}

function uiUpdateContent() {
    $chat_contents.textContent = '';

    const chats = uiState.chats.server_chat;
    const names = uiState.chats.server_name;
    const servers = new Map();
    for (const server of names) {
        servers.set(server.first, server.second);
    }
    for (const chat of chats) {
        const $row = document.createElement('tr');
        const $server_cell = document.createElement('td');
        const $time_cell = document.createElement('td');
        const $sender_cell = document.createElement('td');
        const $content_cell = document.createElement('td');
        $server_cell.textContent = servers.get(chat.serverId);
        $time_cell.textContent = chat.time;
        $sender_cell.textContent = chat.sender;
        $content_cell.textContent = chat.chat;

        $row.appendChild($server_cell);
        $row.appendChild($content_cell);
        $row.appendChild($time_cell);
        $row.appendChild($sender_cell);
        $chat_contents.appendChild($row);
    }
}

refetchLoopTrigger();
