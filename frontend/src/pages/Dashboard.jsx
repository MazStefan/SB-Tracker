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

    if (loading) return <p style={{ padding: '20px' }}>Loading your dashboard...</p>;

    return (
        <div style={{ maxWidth: '800px', margin: '40px auto', fontFamily: 'sans-serif' }}>
            
            {/* --- HEADER BAR --- */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' }}>
                <h2>My Financial Dashboard</h2>
                <button 
                    onClick={handleLogout}
                    style={{ padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    Logout
                </button>
                <button 
                    onClick={handlePasswordReset}
                    style={{ padding: '8px 16px', backgroundColor: '#a50690', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    Reset Password
                </button>
            </div>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {/* --- THE MANAGERS --- */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '50px' }}>
                
                <section>
                    <CategoryManager
                        categories={categories} 
                        onCategoryChange={() => setRefreshTrigger(prev => prev + 1)}
                    />
                </section>

                <section>
                    <BudgetManager categories={categories} />
                </section>

                <section>
                    <TransactionManager categories={categories} />
                </section>

            </div>
            
        </div>
    );
}
