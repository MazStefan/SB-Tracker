import { useState } from 'react';
import { dataService } from '../services/dataService';

export default function CategoryManager({ categories, onCategoryChange }) {
    const [newName, setNewName] = useState('');
    const [newType, setNewType] = useState('EXPENSE');
    
    const [editingId, setEditingId] = useState(null);
    const [editName, setEditName] = useState('');
    const [editType, setEditType] = useState('EXPENSE');

    const [error, setError] = useState('');
    const [deletingCategoryId, setDeletingCategoryId] = useState(null);

    const handleCreate = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await dataService.createCategory({ name: newName, type: newType });
            setNewName('');
            if (onCategoryChange) onCategoryChange();
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to create category';
            setError(errorMessage);
        }
    };

    const handleDelete = async (id) => {
        setError('');

        try {
            await dataService.deleteCategory(id);
            if (onCategoryChange) onCategoryChange();
            setDeletingCategoryId(null);
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to delete category';
            setError(errorMessage);
        }
    };

    const handleSaveEdit = async (id) => {
        setError('');

        try {
            await dataService.updateCategory(id, { name: editName, type: editType });
            setEditingId(null);
            if (onCategoryChange) onCategoryChange();
        } catch (err) {
            const errorMessage = err.response.data.error || 'Failed to update category';
            setError(errorMessage);
        }
    };

    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">Create New Category</h3>

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
                <input 
                    type="text" 
                    placeholder="Name (e.g. Groceries)" 
                    value={newName} 
                    onChange={(e) => setNewName(e.target.value)} 
                    required 
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                />
                <select 
                    value={newType} 
                    onChange={(e) => setNewType(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                </select>
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 rounded-lg transition">
                    Add Category
                </button>
            </form>

            <h4 className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-3 uppercase tracking-wider text-center border-b border-slate-200 dark:border-slate-700 pb-2">My Categories</h4>
            
            <div className="flex flex-col gap-2 overflow-y-auto max-h-64 pr-2">
                {categories.map((cat) => (
                    <div key={cat.id} className="flex justify-between items-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-100 dark:border-slate-600">
                        {editingId === cat.id ? (
                            <form 
                                onSubmit={(e) => {
                                    e.preventDefault();
                                    handleSaveEdit(cat.id);
                                }} 
                                className="flex flex-col w-full gap-2 mt-1"
                            >
                                <input 
                                    type="text" 
                                    required
                                    value={editName} 
                                    onChange={(e) => setEditName(e.target.value)} 
                                    className="px-2 py-1.5 w-full bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                />
                                <select 
                                    value={editType} 
                                    onChange={(e) => setEditType(e.target.value)}
                                    required
                                    className="px-2 py-1.5 w-full bg-white dark:bg-slate-600 border border-slate-300 dark:border-slate-500 rounded text-sm outline-none text-slate-900 dark:text-white"
                                >
                                    <option value="EXPENSE">Expense</option>
                                    <option value="INCOME">Income</option>
                                </select>
                                <div className="flex gap-2">
                                    <button type="submit" className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs px-3 py-2 rounded font-medium transition">Save</button>
                                    <button type="button" onClick={() => setEditingId(null)} className="flex-1 bg-slate-400 hover:bg-slate-500 text-white text-xs px-3 py-2 rounded font-medium transition">Cancel</button>
                                </div>
                            </form>
                        ) : (
                            <>
                                <div>
                                    <span className="text-slate-700 dark:text-slate-200 font-medium block">{cat.name}</span>
                                    <span className="text-xs text-slate-500 dark:text-slate-400">{cat.type}</span>
                                </div>
                                <div className="flex gap-2">
                                    {deletingCategoryId === cat.id ? (
                                        <div className="flex items-center gap-2">
                                            <span className="text-xs text-slate-500 dark:text-slate-400 italic">Sure?</span>
                                            <button onClick={() => handleDelete(cat.id)} className="text-red-600 dark:text-red-400 text-sm font-bold hover:underline">
                                                Yes
                                            </button>
                                            <span className="text-slate-300 dark:text-slate-600 text-sm">|</span>
                                            <button onClick={() => setDeletingCategoryId(null)} className="text-slate-600 dark:text-slate-400 text-sm hover:underline">
                                                No
                                            </button>
                                        </div>
                                    ) : (
                                        <>
                                            <button onClick={() => { setEditingId(cat.id); setEditName(cat.name); setEditType(cat.type); }} className="text-blue-600 dark:text-blue-400 text-sm hover:underline">Edit</button>
                                            <button onClick={() => setDeletingCategoryId(cat.id)} className="text-red-600 dark:text-red-400 text-sm hover:underline">Delete</button>
                                        </>
                                    )}
                                </div>
                            </>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
