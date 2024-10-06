import { HTMLProps, useEffect, useRef } from 'react';
import { Column, FilterType, IdType, Row, useFilters, useRowSelect, useTable } from 'react-table';
import Input from './form/Input';
import { InboxIcon } from '@heroicons/react/24/outline';

type Props<T extends object> = {
    columns: Column<T>[];
    data: T[];
    onRowSelectStateChange?: (rows: T[]) => void;
    filtering?: boolean;
    searching?: boolean;
};

function IndeterminateCheckbox({
    indeterminate,
    className = '',
    ...rest
}: { indeterminate?: boolean } & HTMLProps<HTMLInputElement>) {
    const ref = useRef<HTMLInputElement>(null!);

    useEffect(() => {
        if (typeof indeterminate === 'boolean') {
            ref.current.indeterminate = !rest.checked && indeterminate;
        }
    }, [ref, indeterminate]);

    return <input type="checkbox" ref={ref} className={className + ' cursor-pointer'} {...rest} />;
}

function Table<T extends object>({ columns, data, onRowSelectStateChange, filtering }: Props<T>) {
    const ColumnFilter = ({
        column: { filterValue, setFilter, filter },
    }: {
        column: {
            filterValue: string;
            setFilter: (filter: string | undefined) => void;
            filter?: string | FilterType<T> | undefined;
        };
    }) => {
        return (
            <Input
                type="search"
                value={filterValue || ''}
                onChange={(e) => {
                    setFilter(e.target.value || undefined);
                }}
                className="text-sm pl-2 pr-1 py-0.5 font-normal bg-gray-50 border-2 rounded-sm border-gray-200 w-full min-w-[50px]"
            />
        );
    };

    const filterTypes = {
        text: (rows: Row<T>[], id: IdType<T>[], filterValue: string) => {
            return rows.filter((row) => {
                const rowValue = row.values[id[0]];
                return rowValue !== undefined
                    ? String(rowValue).toLowerCase().startsWith(String(filterValue).toLowerCase())
                    : true;
            });
        },
    };

    const defaultColumn: Partial<Column<T>> = {
        Filter: ColumnFilter,
    };

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
        selectedFlatRows,
        state: { selectedRowIds },
    } = useTable<T>({ columns, data, defaultColumn, filterTypes }, useFilters, useRowSelect, (hooks) => {
        hooks.visibleColumns.push((columns) => [
            ...(onRowSelectStateChange
                ? [
                      {
                          id: 'selection',
                          Header: ({ getToggleAllRowsSelectedProps }: { getToggleAllRowsSelectedProps: any }) => (
                              <IndeterminateCheckbox {...getToggleAllRowsSelectedProps()} />
                          ),
                          Cell: ({ row }: { row: any }) => (
                              <IndeterminateCheckbox {...row.getToggleRowSelectedProps()} />
                          ),
                          width: '0',
                      },
                  ]
                : []),
            ...columns,
        ]);
    });

    useEffect(() => onRowSelectStateChange?.(selectedFlatRows.map((d) => d.original)), [selectedRowIds]);

    return (
        <>
            <table {...getTableProps()} className="min-w-full">
                <thead>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()} key={'header' + headerGroup.id}>
                            {headerGroup.headers.map((column, i) => (
                                <th
                                    {...column.getHeaderProps()}
                                    key={column.id}
                                    style={column.width ? { width: column.width } : {}}
                                    className={
                                        'bg-soft-blue-800 px-2 py-2 border-white text-white text-left text-sm font-normal ' +
                                        (i != 0 ? 'border-l' : 'w-0')
                                    }
                                >
                                    {column.render('Header')}
                                </th>
                            ))}
                        </tr>
                    ))}
                </thead>
                <tbody {...getTableBodyProps()}>
                    {headerGroups.map((headerGroup) => (
                        <tr {...headerGroup.getHeaderGroupProps()} key={'header' + headerGroup.id}>
                            {headerGroup.headers.map((column, i) => (
                                <th key={column.id} className="p-2" style={column.width ? { width: column.width } : {}}>
                                    {column.canFilter && filtering ? column.render('Filter') : null}
                                </th>
                            ))}
                        </tr>
                    ))}
                    {rows.length > 0 &&
                        rows.map((row) => {
                            prepareRow(row);
                            return (
                                <tr {...row.getRowProps()} key={row.id}>
                                    {row.cells.map((cell, i) => {
                                        return (
                                            <td
                                                {...cell.getCellProps()}
                                                key={i + cell.row.id}
                                                className="text-left py-3 px-2 border-b text-sm"
                                            >
                                                {cell.render('Cell')}
                                            </td>
                                        );
                                    })}
                                </tr>
                            );
                        })}
                </tbody>
                {/* {JSON.stringify(
                {
                    selectedRowIds: selectedRowIds,
                    'selectedFlatRows[].original': selectedFlatRows.map((d: any) => d.original),
                },
                null,
                2,
            )} */}
            </table>
            {rows.length == 0 && (
                <div className="flex w-full h-1/2 flex-col">
                    <InboxIcon className="m-auto mb-0 w-1/5 stroke-gray-300" />
                    <h1 className="m-auto mt-0 font-bold text-xl text-gray-300">No Records Found</h1>
                </div>
            )}
        </>
    );
}
export default Table;
