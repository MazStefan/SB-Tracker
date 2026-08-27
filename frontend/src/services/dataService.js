import api from './api';

export const dataService = {
    // TRANSACTIONS
    createTransaction: async (transactionData) => {
        const response = await api.post('/transactions', transactionData);
        return response.data;
    },
    updateTransaction: async (id, transactionData) => {
        const response = await api.put(`/transactions/${id}`, transactionData);
        return response.data;
    },
    deleteTransaction: async (id) => {
        await api.delete(`/transactions/${id}`);
    },

    // BUDGETS
    createBudget: async (budgetData) => {
        const response = await api.post('/budgets', budgetData);
        return response.data;
    },
    updateBudget: async (id, budgetData) => {
        const response = await api.put(`/budgets/${id}`, budgetData);
        return response.data;
    },
    deleteBudget: async (id) => {
        await api.delete(`/budgets/${id}`);
    },

    // CATEGORIES
    createCategory: async (categoryData) => {
        const response = await api.post('/categories', categoryData);
        return response.data;
    },
    updateCategory: async (id, categoryData) => {
        const response = await api.put(`/categories/${id}`, categoryData);
        return response.data;
    },
    deleteCategory: async (id) => {
        await api.delete(`/categories/${id}`);
    }
};
