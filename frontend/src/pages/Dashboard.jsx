import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { authService } from '../services/authService';
import TransactionForm from '../components/TransactionForm';

export default function Dashboard() {
    const [budgets, setBudgets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchBudgets = async () => {
            try {
                const response = await api.get('/budgets'); 
                setBudgets(response.data);
                setLoading(false);
            } catch (err) {
                console.error("Failed to fetch budgets:", err);
                setError('Could not load budget data. Please try logging in again.');
                setLoading(false);
            }
        };

        fetchBudgets();
    }, []);

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    if (loading) return <p style={{ padding: '20px' }}>Loading your dashboard...</p>;

    return (
        <div style={{ maxWidth: '800px', margin: '40px auto', fontFamily: 'sans-serif' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2>My Budgets</h2>
                <button 
                    onClick={handleLogout}
                    style={{ padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                    Logout
                </button>
            </div>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {budgets.length === 0 && !error ? (
                <p>No budgets found. Time to create one!</p>
            ) : (
                <div style={{ display: 'grid', gap: '15px', marginTop: '20px' }}>
                    {budgets.map((budget) => (
                        <div key={budget.id} style={{ border: '1px solid #ddd', padding: '15px', borderRadius: '8px' }}>
                            <h3 style={{ margin: '0 0 10px 0' }}>{budget.categoryName}</h3>
                            <p style={{ margin: '0' }}>
                                <strong>Limit:</strong> ${budget.amountLimit}
                            </p>
                        </div>
                    ))}
                </div>
            )}
            
            <TransactionForm 
                budgets={budgets} 
                onTransactionAdded={() => window.location.reload()}
            />
        </div>
    );
}
