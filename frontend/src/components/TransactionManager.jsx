import { useState, useEffect } from 'react';
import { dataService } from '../services/dataService';
import api from '../services/api';

export default function TransactionManager({ categories, refreshTrigger }) {
    const [transactions, setTransactions] = useState([]);
    
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');
    const [categoryId, setCategoryId] = useState('');
    const [warning, setWarning] = useState('');

    const [editTransactionId, setEditTranasactionId] = useState(null);
    const [editAmount, setEditAmount] = useState('');
    const [editDescription, setEditDescription] = useState('');
    const [editCategoryId, setEditCategoryId] = useState('');
    const [editDate, setEditDate] = useState('');

    const [error, setError] = useState('');

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
    }, [refreshTrigger]);

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
                setWarning('⚠️ Transaction edited, but you have exceeded your budget!');
            }

            setTransactions([response, ...transactions]);
            setAmount('');
            setDescription('');
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to create transaction';
            setError(errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this transaction?")) return;
        try {
            await dataService.deleteTransaction(id);
            setTransactions(transactions.filter(t => t.id !== id));
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to delete transaction';
            setError(errorMessage);
        }
    };

    const handleSaveEdit = async(id) => {
        try{
            const updatedTransaction = await dataService.updateTransaction(id, {
                amount: parseFloat(editAmount),
                description: editDescription,
                categoryId: parseInt(editCategoryId),
                date: `${editDate}:00`
            });

            if (updatedTransaction.overSpend) {
                setWarning('⚠️ Transaction edited, but you have exceeded your budget!');
            }

            setTransactions(transactions.map(t => t.id === id ? updatedTransaction : t));
            setEditTranasactionId(null);
        } catch(err) {
            const errorMessage = err.response.data.error || 'Failed to update transaction';
            setError(errorMessage);
        }
    };

    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">Log Transaction</h3>
            
            {error && (
                <div className="mb-4 p-3 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-sm font-medium rounded-lg border border-red-200 dark:border-red-800">
                    {error}
                </div>
            )}

            {warning && (
                <div className="mb-4 p-3 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-sm font-medium rounded-lg border border-red-200 dark:border-red-800">
                    {warning}
                </div>
            )}
            
            <form onSubmit={handleCreate} className="flex flex-col gap-4 mb-8">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <select 
                        value={categoryId} 
                        onChange={(e) => setCategoryId(e.target.value)} 
                        required
                        className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="" disabled>Select Category</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}({cat.type})</option>
                        ))}
                    </select>
                    
                    <input 
                        type="number" 
                        step="0.01" 
                        max="99999999.99"
                        placeholder="Amount" 
                        value={amount} 
                        onChange={(e) => setAmount(e.target.value)} 
                        required 
                        className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <input 
                        type="text" 
                        placeholder="Description" 
                        value={description} 
                        onChange={(e) => setDescription(e.target.value)} 
                        required 
                        className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <input 
                        type="datetime-local" 
                        value={date} 
                        onChange={(e) => setDate(e.target.value)} 
                        required 
                        className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5 rounded-lg transition mt-2">
                    Add Transaction
                </button>
            </form>

            <h4 className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-3 uppercase tracking-wider text-center border-b border-slate-200 dark:border-slate-700 pb-2">Recent Transactions</h4>
            
            <div className="flex flex-col gap-3 overflow-y-auto max-h-96 pr-2">
                {transactions.map((t) => (
                    <div key={t.id} className="p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl border border-slate-100 dark:border-slate-600">
                        {editTransactionId === t.id ? (
                            <form 
                                onSubmit={(e) => {
                                    e.preventDefault();
                                    handleSaveEdit(t.id);
                                }} 
                                className="flex flex-col gap-3"
                            >
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                                    <select 
                                        value={editCategoryId} 
                                        onChange={(e) => setEditCategoryId(e.target.value)} 
                                        required
                                        className="px-2 py-1.5 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                    >
                                        <option value="" disabled>Category</option>
                                        {categories.map(cat => (
                                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                                        ))}
                                    </select>
                                    <input 
                                        type="number" 
                                        step="0.01" 
                                        max="99999999.99"
                                        required
                                        value={editAmount} 
                                        onChange={(e) => setEditAmount(e.target.value)} 
                                        className="px-2 py-1.5 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white" 
                                    />
                                </div>
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                                    <input 
                                        type="text" 
                                        required
                                        value={editDescription} 
                                        onChange={(e) => setEditDescription(e.target.value)} 
                                        className="px-2 py-1.5 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white w-full" 
                                    />
                                    <input 
                                        type="datetime-local"
                                        required
                                        value={editDate}
                                        onChange={(e) => setEditDate(e.target.value)}
                                        className="px-2 py-1.5 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white w-full"
                                    />
                                </div>
                                <div className="flex gap-2 mt-1">
                                    <button type="submit" className="bg-green-600 hover:bg-green-700 text-white text-xs px-4 py-2 rounded">Save</button>
                                    <button type="button" onClick={() => setEditTranasactionId(null)} className="bg-slate-400 hover:bg-slate-500 text-white text-xs px-4 py-2 rounded">Cancel</button>
                                </div>
                            </form>
                        ) : (
                            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
                                <div>
                                    <div className="text-xs font-medium text-slate-500 dark:text-slate-400 mb-1">
                                        {new Date(t.date).toLocaleString()} <span className="mx-1">•</span> <span className="text-blue-600 dark:text-blue-400">{t.categoryName} ({t.categoryType})</span>
                                    </div>
                                    <div className="text-slate-800 dark:text-slate-200">
                                        <strong className="text-lg mr-2">${t.amount}</strong> 
                                        <span className="text-sm">{t.description}</span>
                                    </div>
                                </div>
                                <div className="flex gap-3 sm:justify-end">
                                    <button 
                                        onClick={() => {
                                            setEditTranasactionId(t.id);
                                            setEditAmount(t.amount);
                                            setEditDescription(t.description);
                                            setEditCategoryId(t.categoryId || categories.find(c => c.name === t.categoryName)?.id || '');
                                            setEditDate(t.date ? getLocalIsoString(t.date) : getLocalIsoString());
                                        }} 
                                        className="text-blue-600 dark:text-blue-400 text-sm hover:underline font-medium"
                                    >
                                        Edit
                                    </button>
                                    <button onClick={() => handleDelete(t.id)} className="text-red-600 dark:text-red-400 text-sm hover:underline font-medium">
                                        Delete
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
