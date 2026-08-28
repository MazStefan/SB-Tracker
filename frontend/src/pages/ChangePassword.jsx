import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/authService';

export default function ChangePassword() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmNew, setConfirmNew] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');

        if (newPassword !== confirmNew) {
            setError("New passwords do not match!");
            return;
        }

        try {
            await authService.changePassword({ oldPassword, newPassword });
            setMessage('Password updated successfully! Please log in again.');
            
            setTimeout(() => {
                authService.logout();
                navigate('/login');
            }, 2000);
            
        } catch (err) {
            setError('Failed to change password. Please check your current password.');
            console.error('Password change error:', err);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', fontFamily: 'sans-serif' }}>
            <h2>Change Password</h2>
            
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                    <label>Current Password: </label>
                    <input type="password" value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} required style={{ width: '100%', padding: '8px' }} />
                </div>
                
                <div>
                    <label>New Password: </label>
                    <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required style={{ width: '100%', padding: '8px' }} />
                </div>

                <div>
                    <label>Confirm New Password: </label>
                    <input type="password" value={confirmNew} onChange={(e) => setConfirmNew(e.target.value)} required style={{ width: '100%', padding: '8px' }} />
                </div>
                
                <button type="submit" style={{ padding: '10px', backgroundColor: '#ffc107', color: 'black', border: 'none', cursor: 'pointer' }}>
                    Update Password
                </button>
            </form>

            <p style={{ marginTop: '20px' }}>
                <Link to="/dashboard">← Back to Dashboard</Link>
            </p>
        </div>
    );
}
