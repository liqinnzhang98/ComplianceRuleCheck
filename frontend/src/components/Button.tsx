import * as React from 'react';

enum ButtonSize {
    sm = 'sm',
    lg = 'lg',
}

type ButtonProps = {
    text?: string;
    Icon?: React.ElementType;
    onClick?: () => void;
    className?: string;
    size?: keyof typeof ButtonSize;
    type?: 'button' | 'submit' | 'reset' | undefined;
};

function Button({ text, Icon, onClick, className, size, type }: ButtonProps) {
    function getIconClasses() {
        let classes = 'text-white-500 ml-0';
        if (size === ButtonSize.lg) {
            classes += ' h-6 w-6 m-2';
        } else {
            classes += ' h-4 w-4 m-1.5';
        }

        if (!text) {
            classes += ' m-0';
        }

        return classes;
    }
    return (
        <button
            type={type ? type : 'button'}
            onClick={onClick}
            className={
                'rounded-md bg-soft-blue-800 text-white flex items-center justify-center hover:bg-oceanic-600 min-h-[28px] min-w-[] ' +
                (size == ButtonSize.lg ? 'px-4 ' : 'px-3 ') +
                (className ? className : '')
            }
        >
            {Icon && <Icon className={getIconClasses()} />}
            {text && <span className={size === ButtonSize.lg ? 'text-md' : 'text-sm'}>{text}</span>}
        </button>
    );
}

export default Button;
