export const formatAmount = (amount) => {
    if (amount == null) return "0.00";
    
    return new Intl.NumberFormat('ro-RO', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
};
