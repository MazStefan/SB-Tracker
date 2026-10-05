import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { authService } from '../services/authService';
import { Client } from '@stomp/stompjs';

import CategoryManager from '../components/CategoryManager';
import BudgetManager from '../components/BudgetManager';
import TransactionManager from '../components/TransactionManager';
import ReportManager from '../components/ReportManager';
import GroupManager from '../components/GroupManager';

export default function Dashboard() {
    const [categories, setCategories] = useState([]);
    const [currentGroup, setCurrentGroup] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const [refreshTrigger, setRefreshTrigger] = useState(0);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await api.get('/categories'); 
                setCategories(response.data);
                const userRes = await api.get('/users/me'); 
                setCurrentGroup(userRes.data.group || null);
            } catch (err) {
                console.error("Failed to fetch dashboard data:", err);
                setError('Could not load data. Please try logging in again.');
            } finally {
                setLoading(false);
            }
        };

        fetchCategories();
        
    }, [refreshTrigger]);

    useEffect(() => {
        if (!currentGroup || !currentGroup.id) return;

        const token = localStorage.getItem('jwt_token'); 

        const stompClient = new Client({
            brokerURL: `ws://${window.location.hostname}:8080/ws`, 
            connectHeaders: { Authorization: `Bearer ${token}` },
            debug: (str) => console.log('STOMP: ' + str),
            onWebSocketClose: () => console.log('STOMP: Connection closed'),
            onWebSocketError: (err) => console.error('STOMP WS Error: ', err),
            
            onConnect: () => {
                console.log('✅ Connected to WebSockets for Group: ' + currentGroup.id);
                
                stompClient.subscribe(`/topic/group/${currentGroup.id}`, (message) => {
                    const payload = JSON.parse(message.body);
                    
                    if (payload.action === 'REFRESH_TRANSACTIONS') {
                        setRefreshTrigger(prev => prev + 1);
                    }
                });
            },
            
            onStompError: (frame) => {
                console.error('Broker error: ' + frame.headers['message']);
            }
        });

        stompClient.activate();

        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
        
    }, [currentGroup?.id]);

    const handleGroupUpdate = (updatedGroup) => {
        setCurrentGroup(updatedGroup);
        setRefreshTrigger(prev => prev + 1); 
    };

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
            <div className="w-full max-w-7xl mx-auto space-y-8">
                
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
                <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
                    
                    {/* LEFT COLUMN: Categories & Budgets */}
                    <div className="lg:col-span-1 space-y-8">
                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 transition-colors duration-200">
                            <CategoryManager 
                                categories={categories} 
                                onCategoryChange={() => setRefreshTrigger(prev => prev + 1)} 
                            />
                        </section>

                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 transition-colors duration-200">
                            <BudgetManager 
                                categories={categories}
                                refreshTrigger={refreshTrigger}
                                onDataChange={() => setRefreshTrigger(prev => prev + 1)}
                            />
                        </section>
                    </div>

                    {/* MIDDLE COLUMN: Transactions */}
                    <div className="lg:col-span-2">
                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 h-fit transition-colors duration-200">
                            <TransactionManager
                                categories={categories}
                                refreshTrigger={refreshTrigger}
                                onDataChange={() => setRefreshTrigger(prev => prev + 1)}
                            />
                        </section>
                    </div>

                    {/* RIGHT COLUMN: Reports & Groups*/}
                    <div className="lg:col-span-1 space-y-8">
                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 transition-colors duration-200">
                            <GroupManager 
                                currentGroup={currentGroup}
                                onGroupUpdate={handleGroupUpdate}
                            />
                        </section>

                        <section className="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6 h-fit transition-colors duration-200">
                            <ReportManager refreshTrigger={refreshTrigger} />
                        </section>
                    </div>

                </div>
            </div>
        </div>
    );
}
