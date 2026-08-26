import { useState } from 'react';
import api from '../services/api';

export default function CategoryForm({ onCategoryAdded }) {
    const [name, setName] = useState('');
    const [type, setType] = useState('EXPENSE');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        try {
            await api.post('/categories', { name, type });
            setMessage('✅ Category created successfully!');
            setName('');
            
            if (onCategoryAdded) onCategoryAdded();
        } catch (err) {
            setMessage('❌ Failed to create category.');
            console.error(err);
        }
    };

    return (
        <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
            <h3>Create a Category</h3>
            {message && <p>{message}</p>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label>Category Name: </label>
                    <input 
                        type="text" 
                        value={name}
                        onChange={(e) => setName(e.target.value)} 
                        required 
                        style={{ width: '100%', padding: '8px' }}
                        placeholder="e.g., Groceries"
                    />
                </div>

                <div>
                    <label>Type: </label>
                    <select 
                        value={type} 
                        onChange={(e) => setType(e.target.value)}
                        style={{ width: '100%', padding: '8px' }}
                    >
                        <option value="EXPENSE">Expense</option>
                        <option value="INCOME">Income</option>
                    </select>
                </div>

                <button type="submit" style={{ padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none' }}>
                    Save Category
                </button>
            </form>
        </div>
    );
}
