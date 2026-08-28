import { useState, useEffect } from 'react';
import { dataService } from '../services/dataService';
import api from '../services/api';

export default function TransactionManager({ categories }) {
    const [transactions, setTransactions] = useState([]);
    
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');
    const [categoryId, setCategoryId] = useState('');
    const [warning, setWarning] = useState('');

    const [editTransactionId, setEditTranasactionId] = useState(null);

    const getLocalIsoString = () => {
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        return now.toISOString().slice(0, 16);
    };

    const [date, setDate] = useState(getLocalIsoString());

    useEffect(() => {
        const fetchTransactions = async () => {
            try {
                const response = await api.get('/transactions');
                setTransactions(response.data);
            } catch (err) {
                console.error("Failed to load transactions", err);
            }
        };
        fetchTransactions();
    }, []);

    const handleCreate = async (e) => {
        e.preventDefault();
        setWarning('');
        try {
            const response = await dataService.createTransaction({
                amount: parseFloat(amount),
                description,
                categoryId: parseInt(categoryId),
                date: `${date}:00`
            });

            if (response.overSpend) {
                setWarning('⚠️ Transaction added, but you have exceeded your budget!');
            }

            setTransactions([response, ...transactions]);
            setAmount('');
            setDescription('');
        } catch (err) {
            alert('Failed to create transaction');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this transaction?")) return;
        try {
            await dataService.deleteTransaction(id);
            setTransactions(transactions.filter(t => t.id !== id));
        } catch (err) {
            alert('Failed to delete transaction');
        }
    };

    const handleSaveEdit = async(id) => {
        try{
            const updatedTransaction = await dataService.updateTransaction(id, {
                amount: parseFloat(amount),
                description,
                categoryId: parseInt(categoryId)});

                setTransactions(transactions.map(t => t.id === id ? updatedTransaction : t));

                setEditTranasactionId(null);
        } catch(err) {
            alert('Failed to update transaction');
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto', fontFamily: 'sans-serif' }}>
            
            {/* 1. LOG TRANSACTION FORM */}
            <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', marginBottom: '30px' }}>
                <h3>Log Transaction</h3>
                {warning && <p style={{ color: '#d9534f', fontWeight: 'bold' }}>{warning}</p>}
                
                <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required style={{ padding: '8px' }}>
                        <option value="" disabled>Select Category</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                        ))}
                    </select>
                    <input type="number" step="0.01" placeholder="Amount" value={amount} onChange={(e) => setAmount(e.target.value)} required style={{ padding: '8px' }} />
                    <input type="text" placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} required style={{ padding: '8px' }} />
                    <input 
                        type="datetime-local" 
                        value={date} 
                        onChange={(e) => setDate(e.target.value)} 
                        required 
                        style={{ padding: '8px' }} 
                    />
                    
                    <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>Add</button>
                </form>
            </div>

            {/* 2. RECENT TRANSACTIONS LIST */}
            <h3>Recent Transactions</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {transactions.map((t) => (
                    <div key={t.id} style={{ border: '1px solid #eee', padding: '15px', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        
                        {/* CONDITIONAL RENDERING: EDIT MODE VS VIEW MODE */}
                        {editTransactionId === t.id ? (
                            
                            <div style={{ display: 'flex', gap: '10px', flex: 1, marginRight: '10px' }}>
                                <select 
                                    value={categoryId} 
                                    onChange={(e) => setCategoryId(e.target.value)} 
                                    style={{ padding: '4px', flex: 1 }}
                                >
                                    <option value="" disabled>Category</option>
                                    {categories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                                
                                <input 
                                    type="number" 
                                    step="0.01" 
                                    value={amount} 
                                    onChange={(e) => setAmount(e.target.value)} 
                                    style={{ padding: '4px', width: '80px' }} 
                                />
                                
                                <input 
                                    type="text" 
                                    value={description} 
                                    onChange={(e) => setDescription(e.target.value)} 
                                    style={{ padding: '4px', flex: 2 }} 
                                />
                                
                                <button onClick={() => handleSaveEdit(t.id)} style={{ backgroundColor: '#28a745', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer' }}>Save</button>
                                
                                <button onClick={() => {
                                    setEditTranasactionId(null);
                                    setAmount('');
                                    setDescription('');
                                    setCategoryId('');
                                }} style={{ padding: '5px 10px', cursor: 'pointer' }}>Cancel</button>
                            </div>
                            
                        ) : (
                            
                            <>
                                <div>
                                    <small style={{ color: '#666', display: 'block' }}>
                                        {new Date(t.date).toLocaleString()} - {t.categoryName}
                                    </small>
                                    <strong>${t.amount}</strong> - {t.description}
                                </div>
                                
                                <div>
                                    <button 
                                        onClick={() => {
                                            setEditTranasactionId(t.id);
                                            setAmount(t.amount);
                                            setDescription(t.description);
                                            setCategoryId(t.categoryId || '');
                                        }} 
                                        style={{ marginRight: '10px', padding: '5px 10px', cursor: 'pointer' }}
                                    >
                                        Edit
                                    </button>
                                    
                                    <button onClick={() => handleDelete(t.id)} style={{ backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer' }}>
                                        Delete
                                    </button>
                                </div>
                            </>
                            
                        )}
                        
                    </div>
                ))}
            </div>
        </div>
    );
}
