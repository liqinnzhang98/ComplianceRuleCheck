import * as React from 'react';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
    label?: string;
}

const SelectFieldRef = React.forwardRef(({ label, ...rest }: SelectProps, ref: React.LegacyRef<HTMLSelectElement>) => {
    return (
        <div className="w-full flex items-center">
            {label && (
                <label className="text-right m-3 w-1/4">
                    {rest.required && <span className="text-red-500 mr-1">*</span>}
                    {label}:
                </label>
            )}
            <select
                ref={ref}
                className="border border-solid border-grey-100 hover:border-oceanic-600 focus:border-oceanic-600 rounded-md p-1 flex-1 w-full"
                {...rest}
            />
        </div>
    );
});

SelectFieldRef.displayName = 'SelectFieldRef';

const Select = React.memo(SelectFieldRef);

export default Select;
