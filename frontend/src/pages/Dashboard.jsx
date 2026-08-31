import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { authService } from '../services/authService';

import CategoryManager from '../components/CategoryManager';
import BudgetManager from '../components/BudgetManager';
import TransactionManager from '../components/TransactionManager';

export default function Dashboard() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const [refreshTrigger, setRefreshTrigger] = useState(0);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await api.get('/categories'); 
                setCategories(response.data);
            } catch (err) {
                console.error("Failed to fetch categories:", err);
                setError('Could not load data. Please try logging in again.');
            } finally {
                setLoading(false);
            }
        };

        fetchCategories();
        
    }, [refreshTrigger]);

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    const handlePasswordReset = () => {
        navigate('/password');
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900">
                <p className="text-lg text-slate-600 dark:text-slate-400 animate-pulse">Loading your dashboard...</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900 py-8 px-4 sm:px-6 lg:px-8 transition-colors duration-200">
            <div className="max-w-7xl mx-auto space-y-8">
                
                {/* HEADER SECTION */}
                <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 transition-colors duration-200">
                    <div>
                        <h1 className="text-3xl font-bold text-slate-900 dark:text-white">My Financial Dashboard</h1>
                        <p className="text-slate-500 dark:text-slate-400 mt-1">Manage your budget and track your spending.</p>
                    </div>
                    <div className="flex items-center gap-3 w-full sm:w-auto">
                        <button
                            onClick={handlePasswordReset} 
                            className="flex-1 sm:flex-none text-center px-4 py-2.5 text-sm font-medium text-slate-700 dark:text-slate-200 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 rounded-lg transition duration-200"
                        >
                            Change Password
                        </button>
                        <button 
                            onClick={handleLogout} 
                            className="flex-1 sm:flex-none px-4 py-2.5 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg transition duration-200 shadow-sm"
                        >
                            Logout
                        </button>
                    </div>
                </header>

                {error && (
                    <div className="p-4 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-xl text-red-600 dark:text-red-400 text-sm">
                        {error}
                    </div>
                )}

                {/* MAIN CONTENT GRID */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    
                    {/* LEFT COLUMN: Categories & Budgets */}
                    <div className="lg:col-span-1 space-y-8">
                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 transition-colors duration-200">
                            <CategoryManager 
                                categories={categories} 
                                onCategoryChange={() => setRefreshTrigger(prev => prev + 1)} 
                            />
                        </section>

                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 transition-colors duration-200">
                            <BudgetManager categories={categories} />
                        </section>
                    </div>

                    {/* RIGHT COLUMN: Transactions */}
                    <div className="lg:col-span-2">
                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 h-full transition-colors duration-200">
                            <TransactionManager categories={categories} />
                        </section>
                    </div>

                </div>
            </div>
        </div>
    );
}
