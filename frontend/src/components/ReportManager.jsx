import { useState, useEffect } from 'react';
import { formatAmount } from '../services/utils';
import api from '../services/api';

export default function ReportManager() {
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
    }, [month, year]);

    return (
        <div className="flex flex-col gap-4 p-4 bg-white dark:bg-slate-800 rounded-lg shadow">
            <h3 className="text-lg font-bold text-slate-800 dark:text-white">Monthly Spending</h3>
            
            <div className="flex gap-4">
                <select value={month} onChange={(e) => setMonth(Number(e.target.value))} className="p-2 border rounded">
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
                    className="p-2 border rounded w-24"
                />
            </div>

            <div className="flex flex-col gap-2 mt-4">
                {reportData.length === 0 ? (
                    <p className="text-slate-500">No spending data for this month.</p>
                ) : (
                    reportData.map((item, index) => (
                        <div key={index} className="flex justify-between items-center p-3 bg-slate-50 dark:bg-slate-700 rounded border">
                            <span className="font-medium text-slate-700 dark:text-slate-200">
                                {item.categoryName}
                            </span>
                            <span className="font-bold text-red-600 dark:text-red-400">
                                ${formatAmount(item.totalSpent)}
                            </span>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
