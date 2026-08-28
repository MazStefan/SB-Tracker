import { useState, useEffect } from 'react';
import { dataService } from '../services/dataService';
import api from '../services/api';

export default function BudgetManager({ categories }) {
    const [budgets, setBudgets] = useState([]);
    
    const [newCategoryId, setNewCategoryId] = useState('');
    const [newAmountLimit, setNewAmountLimit] = useState('');
    
    const [editingBudgetId, setEditingBudgetId] = useState(null);
    const [editAmountLimit, setEditAmountLimit] = useState('');

    const currentMonth = new Date().toISOString().slice(0, 7); 
    const [monthYear, setMonthYear] = useState(currentMonth);

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
    }, []);

    const handleCreate = async (e) => {
        e.preventDefault();
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
            alert('Failed to create budget');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this budget?")) return;
        try {
            await dataService.deleteBudget(id);
            setBudgets(budgets.filter(b => b.id !== id));
        } catch (err) {
            alert('Failed to delete budget');
        }
    };

    const handleSaveEdit = async (id) => {
        try {
            const updatedBudget = await dataService.updateBudget(id, { 
                monthlyLimit: parseFloat(editAmountLimit) 
            });
            
            setBudgets(budgets.map(b => b.id === id ? updatedBudget : b));
            
            setEditingBudgetId(null);
        } catch (err) {
            alert('Failed to update budget');
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto', fontFamily: 'sans-serif' }}>
            
            {/* 1. THE CREATE FORM */}
            <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', marginBottom: '30px' }}>
                <h3>Create New Budget</h3>
                <form onSubmit={handleCreate} style={{ display: 'flex', gap: '10px' }}>
                    <select 
                        value={newCategoryId} 
                        onChange={(e) => setNewCategoryId(e.target.value)} 
                        required
                        style={{ padding: '8px', flex: 1 }}
                    >
                        <option value="" disabled>Select Category</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                        ))}
                    </select>

                    <input 
                        type="number" 
                        step="0.01"
                        placeholder="Amount Limit"
                        value={newAmountLimit}
                        onChange={(e) => setNewAmountLimit(e.target.value)} 
                        required 
                        style={{ padding: '8px', flex: 1 }}
                    />

                    <input 
                        type="month" 
                        value={monthYear} 
                        onChange={(e) => setMonthYear(e.target.value)} 
                        required 
                        style={{ padding: '8px', width: '150px' }}
                    />
                    
                    <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#007bff', color: 'white', border: 'none' }}>
                        Add
                    </button>
                </form>
            </div>

            {/* 2. THE LIST (WITH EDIT & DELETE) */}
            <h3>My Active Budgets</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {budgets.map((budget) => (
                    <div key={budget.id} style={{ border: '1px solid #eee', padding: '15px', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        
                        <div>
                            <h4 style={{ margin: '0 0 5px 0' }}>{budget.categoryName} ({formatMonthYear(budget.monthYear)})</h4>
                            
                            {/* CONDITIONAL RENDERING FOR THE EDIT VIEW */}
                            {editingBudgetId === budget.id ? (
                                <div>
                                    <input 
                                        type="number" 
                                        value={editAmountLimit} 
                                        onChange={(e) => setEditAmountLimit(e.target.value)}
                                        style={{ padding: '4px', width: '100px' }}
                                    />
                                    <button onClick={() => handleSaveEdit(budget.id)} style={{ marginLeft: '5px', backgroundColor: '#28a745', color: 'white', border: 'none', padding: '5px' }}>Save</button>
                                    <button onClick={() => setEditingBudgetId(null)} style={{ marginLeft: '5px' }}>Cancel</button>
                                </div>
                            ) : (
                                <p style={{ margin: 0 }}>Limit: ${budget.monthlyLimit}</p>
                            )}
                        </div>

                        {/* ACTION BUTTONS */}
                        {editingBudgetId !== budget.id && (
                            <div>
                                <button 
                                    onClick={() => {
                                        setEditingBudgetId(budget.id);
                                        setEditAmountLimit(budget.monthlyLimit);
                                    }}
                                    style={{ marginRight: '10px', padding: '5px 10px', cursor: 'pointer' }}
                                >
                                    Edit
                                </button>
                                <button 
                                    onClick={() => handleDelete(budget.id)}
                                    style={{ padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', cursor: 'pointer' }}
                                >
                                    Delete
                                </button>
                            </div>
                        )}
                        
                    </div>
                ))}
            </div>
        </div>
    );
}
