import { useState, useEffect } from 'react';
import { dataService } from '../services/dataService';
import api from '../services/api';
import { formatAmount } from '../services/utils';

export default function BudgetManager({ categories, refreshTrigger, onDataChange }) {
    const [budgets, setBudgets] = useState([]);
    const [newCategoryId, setNewCategoryId] = useState('');
    const [newAmountLimit, setNewAmountLimit] = useState('');
    
    // Updated to use separate month and year states like ReportManager
    const [month, setMonth] = useState(new Date().getMonth() + 1); 
    const [year, setYear] = useState(new Date().getFullYear());
    
    const [editingBudgetId, setEditingBudgetId] = useState(null);
    const [editCategoryId, setEditCategoryId] = useState('');
    const [editAmountLimit, setEditAmountLimit] = useState('');
    
    // Separate states for inline editing
    const [editMonth, setEditMonth] = useState(new Date().getMonth() + 1);
    const [editYear, setEditYear] = useState(new Date().getFullYear());

    const [error, setError] = useState('');
    const [deletingBudgetId, setDeletingBudgetId] = useState(null);

    const formatMonthYear = (dateString) => {
        if (!dateString) return '';
        const [yearStr, monthStr] = dateString.split('-'); 
        const dateObj = new Date(yearStr, Number(monthStr) - 1);
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
            // Format to YYYY-MM-01 for backend compatibility
            const formattedDate = `${year}-${String(month).padStart(2, '0')}-01`;
            
            const newBudget = await dataService.createBudget({
                categoryId: parseInt(newCategoryId),
                monthlyLimit: parseFloat(newAmountLimit),
                monthYear: formattedDate
            });
            setBudgets([...budgets, newBudget]);
            setNewCategoryId('');
            setNewAmountLimit('');
            if (onDataChange) onDataChange();
        } catch (err) {
           const errorMessage = err.response?.data?.error || 'Failed to create budget';
            setError(errorMessage);
        }
    };

    const handleDelete = async (id) => {
        try {
            await dataService.deleteBudget(id);
            setBudgets(budgets.filter(b => b.id !== id));
            setDeletingBudgetId(null);
            if (onDataChange) onDataChange();
        } catch (err) {
            const errorMessage = err.response?.data?.error || 'Failed to delete budget';
            setError(errorMessage);
        }
    };

    const handleSaveEdit = async (id) => {
        try {
            // Format to YYYY-MM-01 for backend compatibility
            const formattedDate = `${editYear}-${String(editMonth).padStart(2, '0')}-01`;
            
            const updatedBudget = await dataService.updateBudget(id, { 
                categoryId: parseInt(editCategoryId),
                monthlyLimit: parseFloat(editAmountLimit),
                monthYear: formattedDate
            });
            setBudgets(budgets.map(b => b.id === id ? updatedBudget : b));
            setEditingBudgetId(null);
            if (onDataChange) onDataChange();
        } catch (err) {
            const errorMessage = err.response?.data?.error || 'Failed to update budget';
            setError(errorMessage);
        }
    };

    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">Create New Budget</h3>
            
            {error && (
                <div className="flex items-center justify-between bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
                    <span className="block sm:inline text-sm">{error}</span>
                    <button 
                        type="button"
                        onClick={() => setError('')} 
                        className="text-red-500 hover:text-red-900 focus:outline-none text-xl font-bold ml-4 leading-none"
                        aria-label="Close"
                    >
                        &times;
                    </button>
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
                        <option key={cat.id} value={cat.id}>{cat.name} ({cat.type})</option>
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
                </div>
                
                {/* ⚠️ Updated: Month and Year selectors injected here */}
                <div className="flex gap-2 mb-1">
                    <select 
                        value={month} 
                        onChange={(e) => setMonth(Number(e.target.value))} 
                        className="flex-1 px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 text-sm appearance-none"
                    >
                        <option value={1}>January</option>
                        <option value={2}>February</option>
                        <option value={3}>March</option>
                        <option value={4}>April</option>
                        <option value={5}>May</option>
                        <option value={6}>June</option>
                        <option value={7}>July</option>
                        <option value={8}>August</option>
                        <option value={9}>September</option>
                        <option value={10}>October</option>
                        <option value={11}>November</option>
                        <option value={12}>December</option>
                    </select>
                    
                    <input 
                        type="number" 
                        value={year} 
                        onChange={(e) => setYear(Number(e.target.value))}
                        className="w-24 px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 text-sm text-center"
                    />
                </div>
                
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 rounded-lg transition">
                    Set Budget
                </button>
            </form>

            <h4 className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-3 uppercase tracking-wider text-center border-b border-slate-200 dark:border-slate-700 pb-2">My Active Budgets</h4>
            
            <div className="flex flex-col gap-2 overflow-y-auto max-h-64 pr-2">
                {budgets.map((budget) => {
                    const fallbackMonth = `${new Date().getFullYear()}-${String(new Date().getMonth() + 1).padStart(2, '0')}`;
                    const rawMonth = budget.monthYear ? budget.monthYear.slice(0, 7) : fallbackMonth;
                    
                    return (
                        <div key={budget.id} className="flex flex-col sm:flex-row sm:justify-between sm:items-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-100 dark:border-slate-600 gap-2">
                            <div className="w-full">
                                {budget.ownerEmail && (
                                    <h4 className="bg-purple-100 text-purple-700 text-[10px] px-2 py-0.5 rounded-full font-medium w-max mb-1">
                                        {budget.ownerEmail}
                                    </h4>
                                )}
                                <h4 className="font-medium text-slate-800 dark:text-slate-200 m-0">
                                    {budget.categoryName} ({budget.categoryType})
                                    <span className="text-sm font-normal text-slate-500 dark:text-slate-400 ml-1">
                                        ({formatMonthYear(budget.monthYear)})
                                    </span>
                                </h4>
                                
                                {editingBudgetId === budget.id ? (
                                    <form 
                                        onSubmit={(e) => {
                                            e.preventDefault();
                                            handleSaveEdit(budget.id);
                                        }} 
                                        className="flex flex-col w-full gap-2 mt-2"
                                    >
                                        <div className="flex gap-2">
                                            <input 
                                                type="number" 
                                                step="0.01"
                                                max="99999999.99"
                                                required
                                                value={editAmountLimit} 
                                                onChange={(e) => setEditAmountLimit(e.target.value)}
                                                className="px-2 py-1.5 w-1/3 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                            />
                                            {/* ⚠️ Updated Edit Inline Selectors */}
                                            <select 
                                                value={editMonth} 
                                                onChange={(e) => setEditMonth(Number(e.target.value))} 
                                                className="px-2 py-1.5 flex-1 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white appearance-none"
                                            >
                                                <option value={1}>Jan</option>
                                                <option value={2}>Feb</option>
                                                <option value={3}>Mar</option>
                                                <option value={4}>Apr</option>
                                                <option value={5}>May</option>
                                                <option value={6}>Jun</option>
                                                <option value={7}>Jul</option>
                                                <option value={8}>Aug</option>
                                                <option value={9}>Sep</option>
                                                <option value={10}>Oct</option>
                                                <option value={11}>Nov</option>
                                                <option value={12}>Dec</option>
                                            </select>
                                            
                                            <input 
                                                type="number" 
                                                value={editYear} 
                                                onChange={(e) => setEditYear(Number(e.target.value))}
                                                className="px-2 py-1.5 w-1/4 bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white text-center"
                                            />
                                        </div>
                                        <div className="flex gap-2">
                                            <button type="submit" className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs px-3 py-2 rounded font-medium transition">Save</button>
                                            <button type="button" onClick={() => setEditingBudgetId(null)} className="flex-1 bg-slate-400 hover:bg-slate-500 text-white text-xs px-3 py-2 rounded font-medium transition">Cancel</button>
                                        </div>
                                    </form>
                                ) : (
                                    <p className="text-sm text-slate-600 dark:text-slate-400 m-0">Limit: ${formatAmount(budget.monthlyLimit)}</p>
                                )}
                            </div>

                            {editingBudgetId !== budget.id && (
                                <div className="flex gap-2 self-start sm:self-center">
                                    {deletingBudgetId === budget.id ? (
                                        <div className="flex items-center gap-2">
                                            <span className="text-xs text-slate-500 dark:text-slate-400 italic">Sure?</span>
                                            <button onClick={() => handleDelete(budget.id)} className="text-red-600 dark:text-red-400 text-sm font-bold hover:underline">
                                                Yes
                                            </button>
                                            <span className="text-slate-300 dark:text-slate-600 text-sm">|</span>
                                            <button onClick={() => setDeletingBudgetId(null)} className="text-slate-600 dark:text-slate-400 text-sm hover:underline">
                                                No
                                            </button>
                                        </div>
                                    ) : (
                                        <>
                                            <button 
                                                onClick={() => { 
                                                    setEditingBudgetId(budget.id); 
                                                    setEditAmountLimit(budget.monthlyLimit); 
                                                    // Extract month and year from the raw string (e.g. "2026-10")
                                                    const [y, m] = rawMonth.split('-');
                                                    setEditMonth(Number(m));
                                                    setEditYear(Number(y)); 
                                                    setEditCategoryId(budget.categoryId || categories.find(c => c.name === budget.categoryName)?.id || '');
                                                }} 
                                                className="text-blue-600 dark:text-blue-400 text-sm hover:underline"
                                            >
                                                Edit
                                            </button>
                                            <button 
                                                onClick={() => setDeletingBudgetId(budget.id)} 
                                                className="text-red-600 dark:text-red-400 text-sm hover:underline"
                                            >
                                                Delete
                                            </button>
                                        </>
                                    )}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
