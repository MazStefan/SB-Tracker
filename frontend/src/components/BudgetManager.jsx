import { useState, useEffect } from 'react';
import { dataService } from '../services/dataService';
import api from '../services/api';

export default function BudgetManager({ categories, refreshTrigger }) {
    const [budgets, setBudgets] = useState([]);
    const [newCategoryId, setNewCategoryId] = useState('');
    const [newAmountLimit, setNewAmountLimit] = useState('');
    
    const [editingBudgetId, setEditingBudgetId] = useState(null);
    const [editCategoryId, setEditCategoryId] = useState('');
    const [editAmountLimit, setEditAmountLimit] = useState('');
    const [editMonthYear, setEditMonthYear] = useState('');

    const currentMonth = new Date().toISOString().slice(0, 7); 
    const [monthYear, setMonthYear] = useState(currentMonth);

    const [error, setError] = useState('');

    const formatMonthYear = (dateString) => {
        if (!dateString) return '';
        const [year, month] = dateString.split('-'); 
        const dateObj = new Date(year, month - 1);
        return dateObj.toLocaleDateString(undefined, { month: 'long', year: 'numeric' });
    };

    useEffect(() => {
        const fetchBudgets = async () => {
            try {
                const response = await api.get('/budgets');
                setBudgets(response.data);
            } catch (err) {
                console.error("Failed to load budgets", err);
            }
        };
        fetchBudgets();
    }, [refreshTrigger]);

    const handleCreate = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const newBudget = await dataService.createBudget({
                categoryId: parseInt(newCategoryId),
                monthlyLimit: parseFloat(newAmountLimit),
                monthYear: `${monthYear}-01`
            });
            setBudgets([...budgets, newBudget]);
            setNewCategoryId('');
            setNewAmountLimit('');
        } catch (err) {
           const errorMessage = err.response.data.error || 'Failed to create budget';
            setError(errorMessage);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this budget?")) return;
        try {
            await dataService.deleteBudget(id);
            setBudgets(budgets.filter(b => b.id !== id));
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to delete budget';
            setError(errorMessage);
        }
    };

    const handleSaveEdit = async (id) => {
        try {
            const updatedBudget = await dataService.updateBudget(id, { 
                categoryId: parseInt(editCategoryId),
                monthlyLimit: parseFloat(editAmountLimit),
                monthYear: `${editMonthYear}-01`
            });
            setBudgets(budgets.map(b => b.id === id ? updatedBudget : b));
            setEditingBudgetId(null);
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to update budget';
            setError(errorMessage);
        }
    };

    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">Create New Budget</h3>
            
            {error && (
                <div className="mb-4 p-3 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-sm font-medium rounded-lg border border-red-200 dark:border-red-800">
                    {error}
                </div>
            )}

            <form onSubmit={handleCreate} className="flex flex-col gap-3 mb-6">
                <select 
                    value={newCategoryId} 
                    onChange={(e) => setNewCategoryId(e.target.value)} 
                    required
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="" disabled>Select Category</option>
                    {categories.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.name}({cat.type})</option>
                    ))}
                </select>

                <div className="flex gap-2">
                    <input 
                        type="number" 
                        step="0.01"
                        max="99999999.99"
                        placeholder="Amount Limit"
                        value={newAmountLimit}
                        onChange={(e) => setNewAmountLimit(e.target.value)} 
                        required 
                        className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <input 
                        type="month" 
                        value={monthYear} 
                        onChange={(e) => setMonthYear(e.target.value)} 
                        required 
                        className="w-36 px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 rounded-lg transition">
                    Set Budget
                </button>
            </form>

            <h4 className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-3 uppercase tracking-wider text-center border-b border-slate-200 dark:border-slate-700 pb-2">My Active Budgets</h4>
            
            <div className="flex flex-col gap-2 overflow-y-auto max-h-64 pr-2">
                {budgets.map((budget) => {
                    const rawMonth = budget.monthYear ? budget.monthYear.slice(0, 7) : currentMonth;

                    return (
                        <div key={budget.id} className="flex flex-col sm:flex-row sm:justify-between sm:items-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-100 dark:border-slate-600 gap-2">
                            <div className="w-full">
                                <h4 className="font-medium text-slate-800 dark:text-slate-200 m-0">{budget.categoryName} <span className="text-sm font-normal text-slate-500 dark:text-slate-400">({formatMonthYear(budget.monthYear)})</span></h4>
                                
                                {editingBudgetId === budget.id ? (
                                    <div className="flex flex-col w-full gap-2 mt-2">
                                        <div className="flex gap-2">
                                            <input 
                                                type="number" 
                                                step="0.01"
                                                max="99999999.99"
                                                value={editAmountLimit} 
                                                onChange={(e) => setEditAmountLimit(e.target.value)}
                                                className="px-2 py-1.5 w-1/2 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                            />
                                            <input 
                                                type="month"
                                                value={editMonthYear}
                                                onChange={(e) => setEditMonthYear(e.target.value)}
                                                className="px-2 py-1.5 w-1/2 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                            />
                                        </div>
                                        <div className="flex gap-2">
                                            <button onClick={() => handleSaveEdit(budget.id)} className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs px-3 py-2 rounded font-medium transition">Save</button>
                                            <button onClick={() => setEditingBudgetId(null)} className="flex-1 bg-slate-400 hover:bg-slate-500 text-white text-xs px-3 py-2 rounded font-medium transition">Cancel</button>
                                        </div>
                                    </div>
                                ) : (
                                    <p className="text-sm text-slate-600 dark:text-slate-400 m-0">Limit: ${budget.monthlyLimit}</p>
                                )}
                            </div>

                            {editingBudgetId !== budget.id && (
                                <div className="flex gap-2 self-start sm:self-center">
                                    <button onClick={() => {
                                        setEditingBudgetId(budget.id); 
                                        setEditAmountLimit(budget.monthlyLimit); 
                                        setEditMonthYear(rawMonth); 
                                        setEditCategoryId(budget.categoryId || categories.find(c => c.name === budget.categoryName)?.id || '');
                                        }} className="text-blue-600 dark:text-blue-400 text-sm hover:underline">Edit</button>
                                    <button onClick={() => handleDelete(budget.id)} className="text-red-600 dark:text-red-400 text-sm hover:underline">Delete</button>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
