import * as React from 'react';

enum TabSize {
    sm = 'sm',
    lg = 'lg',
}

type TabProps = {
    text: string;
    onClick?: () => void;
    className?: string;
    size?: keyof typeof TabSize;
};

function Tab({ text, onClick, className, size }: TabProps) {
    return (
        <button
            onClick={onClick}
            className={
                'rounded-md text-white flex items-center justify-center hover:bg-oceanic-600 min-h-[28px] ' +
                (size == TabSize.lg ? 'px-4 ' : 'px-3 ') +
                (className ? className : '')
            }
        >
            <span className={size === TabSize.lg ? 'text-md' : 'text-sm'}>{text}</span>
        </button>
    );
}

export default Tab;
