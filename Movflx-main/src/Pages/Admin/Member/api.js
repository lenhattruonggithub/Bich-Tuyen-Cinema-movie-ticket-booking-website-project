const BASE_API = 'http://localhost:8080';

export const fetchMembersApi = async () => {
    const response = await fetch(`${BASE_API}/api/member-manage/all`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
    });
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
};

export const toggleBanStatusApi = async (accountId, message) => {
    const response = await fetch(`${BASE_API}/api/member-manage/ban/${accountId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: message ? JSON.stringify({ message }) : null,
    });
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
};
