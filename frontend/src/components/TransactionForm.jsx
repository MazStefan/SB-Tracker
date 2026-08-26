import { useState } from 'react';
import api from '../services/api';

export default function TransactionForm({ budgets, onTransactionAdded }) {
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');
    const [categoryId, setCategoryId] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        try {
            const response = await api.post('/transactions', {
                amount: parseFloat(amount),
                description: description,
                categoryId: parseInt(categoryId) 
            });

            if (response.data.overSpend) {
                setMessage(`⚠️ Transaction added, but you have exceeded your budget!`);
            } else {
                setMessage('✅ Transaction added successfully!');
            }

            setAmount('');
            setDescription('');
            setCategoryId('');

            if (onTransactionAdded) onTransactionAdded();

        } catch (err) {
            setError('Failed to add transaction. Please check your inputs.');
            console.error(err);
        }
    };

    return (
        <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', marginTop: '30px' }}>
            <h3>Add New Transaction</h3>
            
            {message && <p style={{ color: message.includes('⚠️') ? '#d9534f' : 'green', fontWeight: 'bold' }}>{message}</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label>Category: </label>
                    <select 
                        value={categoryId} 
                        onChange={(e) => setCategoryId(e.target.value)} 
                        required
                        style={{ width: '100%', padding: '8px' }}
                    >
                        <option value="" disabled>Select a budget category</option>
                        {/* Map over the budgets to create dropdown options */}
                        {budgets.map(budget => (
                            <option key={budget.id} value={budget.categoryId || budget.id}>
                                {budget.categoryName}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Amount: </label>
                    <input 
                        type="number" 
                        step="0.01"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <div>
                    <label>Description: </label>
                    <input 
                        type="text" 
                        value={description}
                        onChange={(e) => setDescription(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>

                <button type="submit" style={{ padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Add Transaction
                </button>
            </form>
        </div>
    );
}
