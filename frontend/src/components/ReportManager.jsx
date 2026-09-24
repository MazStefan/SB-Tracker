import { useState, useEffect } from 'react';
import { formatAmount } from '../services/utils';
import api from '../services/api';

export default function ReportManager({ refreshTrigger }) {
    const [reportData, setReportData] = useState([]);
    const [month, setMonth] = useState(new Date().getMonth() + 1); 
    const [year, setYear] = useState(new Date().getFullYear());

    useEffect(() => {
        const fetchReport = async () => {
            try {
                const response = await api.get(`/transactions/report?month=${month}&year=${year}`);
                setReportData(response.data);
            } catch (err) {
                console.error("Failed to fetch report", err);
            }
        };
        fetchReport();
    }, [month, year, refreshTrigger]);

    return (
        <div className="flex flex-col h-full">
            <h3 className="text-lg font-semibold text-slate-800 dark:text-slate-200 mb-4 text-center">
                Monthly Spending
            </h3>

            <div className="flex gap-3 mb-2">
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

            <div className="flex flex-col gap-2 mt-4 overflow-y-auto pr-2 max-h-64">
                {reportData.length === 0 ? (
                    <p className="text-sm text-slate-500 dark:text-slate-400 text-center mt-4">
                        No spending data for this month.
                    </p>
                ) : (
                    reportData.map((item, index) => {
                        const budgetLimit = Number(item.budgetLimit);
                        const totalSpent = Number(item.totalSpent);
                        const isOverBudget = budgetLimit > 0 && totalSpent > budgetLimit;

                        return (
                            <div 
                                key={index} 
                                className={`flex justify-between items-center p-3 rounded-lg border ${
                                    isOverBudget 
                                        ? 'bg-red-50 border-red-200 dark:bg-red-900/20 dark:border-red-800/50' 
                                        : 'bg-slate-50 dark:bg-slate-700/50 border-slate-100 dark:border-slate-600'
                                }`}
                            >
                                <div className="flex flex-col">
                                    <span className="font-medium text-slate-700 dark:text-slate-200 text-sm">
                                        {item.categoryName}
                                    </span>
                                    {item.budgetLimit > 0 && (
                                        <span className="text-[10px] text-slate-500 dark:text-slate-400">
                                            Limit: {formatAmount(item.budgetLimit)}
                                        </span>
                                    )}
                                </div>
                                
                                <div className="flex items-center gap-2">
                                    <span className={`font-bold text-sm ${
                                        isOverBudget 
                                            ? 'text-red-600 dark:text-red-400' 
                                            : 'text-slate-900 dark:text-white'
                                    }`}>
                                        ${formatAmount(item.totalSpent)}
                                    </span>
                                    
                                    {isOverBudget && (
                                        <span className="text-red-500 font-bold text-lg leading-none" title="Over Budget">
                                            !
                                        </span>
                                    )}
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
}
