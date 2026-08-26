import { useState } from 'react';
import api from '../services/api';

export default function BudgetForm({ categories, onBudgetAdded }) {
    const [categoryId, setCategoryId] = useState('');
    const [amountLimit, setAmountLimit] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        try {
            await api.post('/budgets', { 
                categoryId: parseInt(categoryId),
                amountLimit: parseFloat(amountLimit)
            });
            
            setMessage('✅ Budget set successfully!');
            setCategoryId('');
            setAmountLimit('');
            
            if (onBudgetAdded) onBudgetAdded();
        } catch (err) {
            setMessage('❌ Failed to set budget.');
            console.error(err);
        }
    };

    return (
        <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
            <h3>Set a Budget Limit</h3>
            {message && <p>{message}</p>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label>Target Category: </label>
                    <select 
                        value={categoryId} 
                        onChange={(e) => setCategoryId(e.target.value)} 
                        required
                        style={{ width: '100%', padding: '8px' }}
                    >
                        <option value="" disabled>Select a category</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>
                                {cat.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label>Monthly Limit ($): </label>
                    <input 
                        type="number" 
                        step="0.01"
                        value={amountLimit}
                        onChange={(e) => setAmountLimit(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                        placeholder="e.g., 500.00"
                    />
                </div>

                <button type="submit" style={{ padding: '10px', backgroundColor: '#6f42c1', color: 'white', border: 'none' }}>
                    Save Budget
                </button>
            </form>
        </div>
    );
}
