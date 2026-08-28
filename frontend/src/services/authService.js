import api from './api';

export const authService = {

    register: async (userData) => {
        const response = await api.post('/users/register', userData);
        return response.data;
    },

    login: async (credentials) => {
        const response = await api.post('/users/login', credentials);

        if (response.data && response.data.token) {
            localStorage.setItem('jwt_token', response.data.token);
        }
        
        return response.data;
    },

    changePassword: async (passwordData) => {
        const response = await api.post('/users/password', passwordData);
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('jwt_token');
    }
};
