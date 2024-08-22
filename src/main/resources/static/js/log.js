async function fetchLogList() {
    try {
        const response = await fetch('/api/logs/list');
        if (!response.ok) throw new Error('Failed to fetch log list');

        const logList = await response.json();
        const logListSelect = document.getElementById('logList');

        logList.forEach(log => {
            const option = document.createElement('option');
            option.value = log;
            option.text = log === "today" ? "Today" : log;
            logListSelect.appendChild(option);
        });

        // 'today.log'를 기본적으로 로드
        fetchLogs('today', 'matching');
        logListSelect.value = 'today';
    } catch (error) {
        console.error('Error fetching log list:', error);
    }
}

async function fetchLogs(date, filterType) {
    try {
        let endpoint = `/api/logs/${date}`;
        if (filterType === 'matching') {
            endpoint = `/api/logs/filtered/${date}`;
        } else if (filterType === 'searching') {
            endpoint = `/api/logs/searchingLog/${date}`;
        }
/*     채팅은 유저의 개인정보이다.   else if (filterType === 'chatting') {
                     endpoint = `/api/logs/chattingLog/${date}`;
                 } */

        const response = await fetch(endpoint);
        if (!response.ok) throw new Error('Failed to fetch logs');

        const logs = await response.json();
        document.getElementById('logContent').innerText = logs.join('\n');
    } catch (error) {
        console.error('Error fetching logs:', error);
    }
}

document.getElementById('logList').addEventListener('change', function() {
    const filterType = document.querySelector('input[name="logFilter"]:checked').value;
    fetchLogs(this.value, filterType);
});

document.querySelectorAll('input[name="logFilter"]').forEach(radio => {
    radio.addEventListener('change', function() {
        const selectedDate = document.getElementById('logList').value;
        fetchLogs(selectedDate, this.value);
    });
});

document.addEventListener('DOMContentLoaded', fetchLogList);