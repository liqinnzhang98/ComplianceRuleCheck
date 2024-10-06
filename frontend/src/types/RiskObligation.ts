import { z } from 'zod';
import { Control } from './Control';

export const RiskObligation = z.object({
    id: z.number().min(1),
    type: z.enum(['risk', 'obligation']),
    name: z.string(),
    description: z.string(),
    category: z.string(),
    subCategory: z.string().optional(),
    controls: z.array(Control),
});

export type RiskObligation = z.infer<typeof RiskObligation>;

export const RiskObligationTableColumns = [
    { Header: 'ID', accessor: 'id', width: '1%' },
    { Header: 'Type', accessor: 'type', width: '7%' },
    { Header: 'Name', accessor: 'name', width: '15%' },
    { Header: 'Description', accessor: 'description', width: '25%' },
    { Header: 'Category', accessor: 'category', width: '11%' },
    { Header: 'Sub-Category', accessor: 'subCategory', width: '11%' },
    { Header: 'Control', accessor: 'controlId', width: '1%' },
    { Header: 'Control Name', accessor: 'controlName', width: '10%' },
    { Header: 'Control Description', accessor: 'controlDescription', width: '20%' },
];
