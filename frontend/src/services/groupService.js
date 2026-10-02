import api from './api';

export const groupService = {
    createGroup: async (groupData) => {
        const response = await api.post('/groups', groupData);
        return response.data;
    },
    joinGroup: async (groupData) => {
        const response = await api.post('/groups/join', groupData);
        return response.data;
    },
    leaveGroup: async () => {
        const response = await api.post('/groups/leave');
        return response.data;
    }
};
