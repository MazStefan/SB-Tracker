import { useState } from 'react';
import { dataService } from '../services/dataService';

export default function CategoryManager({ categories, onCategoryChange }) {
    const [newName, setNewName] = useState('');
    const [newType, setNewType] = useState('EXPENSE');
    
    const [editingId, setEditingId] = useState(null);
    const [editName, setEditName] = useState('');

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            await dataService.createCategory({ name: newName, type: newType });
            setNewName('');
            if (onCategoryChange) onCategoryChange();
        } catch (err) {
            alert('Failed to create category');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this category?")) return;
        try {
            await dataService.deleteCategory(id);
            if (onCategoryChange) onCategoryChange();
        } catch (err) {
            alert('Failed to delete category');
        }
    };

    const handleSaveEdit = async (id, currentType) => {
        try {
            await dataService.updateCategory(id, { name: editName, type: currentType });
            setEditingId(null);
            if (onCategoryChange) onCategoryChange();
        } catch (err) {
            alert('Failed to update category');
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto', fontFamily: 'sans-serif' }}>

            {/* 1. THE CREATE FORM */}
            <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', marginBottom: '30px' }}>
                <h3>Create New Category</h3>
                <form onSubmit={handleCreate} style={{ display: 'flex', gap: '10px' }}>
                    <input type="text" placeholder="Name (e.g. Groceries)" value={newName} onChange={(e) => setNewName(e.target.value)} required style={{ padding: '8px', flex: 1 }} />
                    <select value={newType} onChange={(e) => setNewType(e.target.value)} style={{ padding: '8px' }}>
                        <option value="EXPENSE">Expense</option>
                        <option value="INCOME">Income</option>
                    </select>
                    <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#007bff', color: 'white', border: 'none' }}>Add</button>
                </form>
            </div>

            {/* 2. THE LIST (WITH EDIT & DELETE) */}
            <h3>My Categories</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {categories.map((cat) => (
                    <div key={cat.id} style={{ border: '1px solid #eee', padding: '15px', borderRadius: '8px', display: 'flex', justifyContent: 'space-between' }}>
                        <div>

                            {/* CONDITIONAL RENDERING FOR THE EDIT VIEW */}
                            {editingId === cat.id ? (
                                <div>
                                    <input type="text" value={editName} onChange={(e) => setEditName(e.target.value)} style={{ padding: '4px' }}/>
                                    <button onClick={() => handleSaveEdit(cat.id, cat.type)} style={{ marginLeft: '5px', backgroundColor: '#28a745', color: 'white', border: 'none' }}>Save</button>
                                    <button onClick={() => setEditingId(null)} style={{ marginLeft: '5px' }}>Cancel</button>
                                </div>
                            ) : (
                                <strong>{cat.name} ({cat.type})</strong>
                            )}
                        </div>

                        {/* ACTION BUTTONS */}
                        {editingId !== cat.id && (
                            <div>
                                <button onClick={() => { setEditingId(cat.id); setEditName(cat.name); }} style={{ marginRight: '10px' }}>Edit</button>
                                <button onClick={() => handleDelete(cat.id)} style={{ backgroundColor: '#dc3545', color: 'white', border: 'none' }}>Delete</button>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
