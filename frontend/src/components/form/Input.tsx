import * as React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
}

const InputFieldRef = React.forwardRef(({ label, ...rest }: InputProps, ref: React.LegacyRef<HTMLInputElement>) => {
    return (
        <div className="w-full flex items-center">
            {label && (
                <label className="text-right m-3 w-1/4">
                    {rest.required && <span className="text-red-500 mr-1">*</span>}
                    {label}:
                </label>
            )}
            <input
                ref={ref}
                className="border border-solid border-grey-100 hover:border-oceanic-600 focus:border-oceanic-600 rounded-md p-1 flex-1 w-full"
                {...rest}
            />
        </div>
    );
});

InputFieldRef.displayName = 'InputFieldRef';

const Input = React.memo(InputFieldRef);

export default Input;
