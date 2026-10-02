import { useState } from 'react';
import { groupService } from '../services/groupService'; // Adjust path as needed

export default function GroupManager({ currentGroup, onGroupUpdate }) {
    const [groupName, setGroupName] = useState('');
    const [inviteCode, setInviteCode] = useState('');
    
    // State for UI feedback
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleCreateGroup = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            const groupData = { name: groupName }; 
            const newGroup = await groupService.createGroup(groupData);
            onGroupUpdate(newGroup); 
            setGroupName('');
        } catch (err) {
            setError(err.response?.data?.message || 'An error occurred while creating the group.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleJoinGroup = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            const groupData = { inviteCode: inviteCode };
            const joinedGroup = await groupService.joinGroup(groupData);
            onGroupUpdate(joinedGroup);
            setInviteCode('');
        } catch (err) {
            setError(err.response?.data?.message || 'Invalid invite code.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleLeaveGroup = async (e) => {
        e.preventDefault();
        if (!window.confirm("Are you sure? Your shared categories will be cloned to your personal account.")) return;

        setIsLoading(true);
        setError(null);

        try {
            const leftGroup = await groupService.leaveGroup();
            onGroupUpdate(leftGroup || null); 
            setGroupName('');
            setInviteCode('');
        } catch (err) {
            setError(err.response?.data?.message || 'An error occurred while leaving the group.');
        } finally {
            setIsLoading(false);
        }
    };

    // VIEW 1: User is already in a group
    if (currentGroup) {
        return (
            <div className="flex flex-col h-full">
                <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">
                    {currentGroup.name}
                </h3>
                
                {/* Replaced ErrorBanner component with inline JSX */}
                {error && (
                    <div className="flex items-center justify-between bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
                        <span className="block sm:inline text-sm">{error}</span>
                        <button 
                            type="button"
                            onClick={() => setError(null)} 
                            className="text-red-500 hover:text-red-900 focus:outline-none text-xl font-bold ml-4 leading-none"
                            aria-label="Close"
                        >
                            &times;
                        </button>
                    </div>
                )}
                
                <div className="p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl border border-slate-100 dark:border-slate-600 mb-4 flex-grow">
                    <div className="flex justify-between items-center mb-6">
                        <div>
                            <div className="text-xs font-medium text-slate-500 dark:text-slate-400 mb-1 uppercase tracking-wider">
                                Invite Code
                            </div>
                            <strong className="text-xl text-slate-800 dark:text-slate-200 tracking-widest">
                                {currentGroup.inviteCode}
                            </strong>
                        </div>
                        <button 
                            type="button"
                            onClick={() => navigator.clipboard.writeText(currentGroup.inviteCode)}
                            className="text-blue-600 dark:text-blue-400 text-sm hover:underline font-medium px-3 py-1.5 bg-blue-50 dark:bg-blue-900/30 rounded-lg transition"
                        >
                            Copy
                        </button>
                    </div>
                    
                    {currentGroup.memberNames && currentGroup.memberNames.length > 0 && (
                        <div>
                            <div className="text-xs font-medium text-slate-500 dark:text-slate-400 mb-2 uppercase tracking-wider border-b border-slate-200 dark:border-slate-600 pb-1">
                                Members
                            </div>
                            <div className="flex flex-wrap gap-2 mt-3">
                                {currentGroup.memberNames.map(name => (
                                    <span key={name} className="bg-blue-100 text-blue-800 dark:bg-blue-900/50 dark:text-blue-300 text-xs px-2.5 py-1 rounded-full font-medium">
                                        {name}
                                    </span>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                <form onSubmit={handleLeaveGroup} className="mt-auto">
                    <button 
                        type="submit" 
                        disabled={isLoading} 
                        className="w-full bg-red-600 hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-2.5 rounded-lg transition"
                    >
                        {isLoading ? 'Leaving...' : 'Leave Group'}
                    </button>
                </form>
            </div>
        );
    }

    // VIEW 2: User is flying solo
    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">Group Management</h3>
            
            {/* Replaced ErrorBanner component with inline JSX */}
            {error && (
                <div className="flex items-center justify-between bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
                    <span className="block sm:inline text-sm">{error}</span>
                    <button 
                        type="button"
                        onClick={() => setError(null)} 
                        className="text-red-500 hover:text-red-900 focus:outline-none text-xl font-bold ml-4 leading-none"
                        aria-label="Close"
                    >
                        &times;
                    </button>
                </div>
            )}

            <form onSubmit={handleCreateGroup} className="flex flex-col gap-4 mb-6">
                <input 
                    type="text" 
                    value={groupName} 
                    onChange={(e) => setGroupName(e.target.value)} 
                    placeholder="New Group Name"
                    disabled={isLoading}
                    required
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                />
                <button 
                    type="submit" 
                    disabled={isLoading}
                    className="w-full bg-blue-600 hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-2.5 rounded-lg transition mt-1"
                >
                    {isLoading ? 'Creating...' : 'Create Group'}
                </button>
            </form>

            <h4 className="text-sm font-semibold text-slate-500 dark:text-slate-400 mb-4 uppercase tracking-wider text-center border-b border-slate-200 dark:border-slate-700 pb-2">
                Join Existing Group
            </h4>

            <form onSubmit={handleJoinGroup} className="flex flex-col gap-4">
                <input 
                    type="text" 
                    value={inviteCode} 
                    onChange={(e) => setInviteCode(e.target.value)} 
                    placeholder="8-Character Invite Code"
                    disabled={isLoading}
                    required
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-300 dark:border-slate-600 rounded-lg text-slate-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 uppercase placeholder:normal-case"
                />
                <button 
                    type="submit" 
                    disabled={isLoading}
                    className="w-full bg-slate-800 hover:bg-slate-900 dark:bg-slate-600 dark:hover:bg-slate-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium py-2.5 rounded-lg transition mt-1"
                >
                    {isLoading ? 'Joining...' : 'Join Group'}
                </button>
            </form>
        </div>
    );
}
