type IconSVGProps = React.PropsWithoutRef<React.SVGProps<SVGSVGElement>> & React.RefAttributes<SVGSVGElement>;
type IconProps = IconSVGProps & {
    title?: string;
    titleId?: string;
};

// NonSeperatorItem is an object with an icon, tooltip and handler
type NonSeperatorItem = {
    icon: string | React.FC<IconProps>;
    tooltip?: string;
    handler: () => void;
};

type ActionBarItem = NonSeperatorItem | (() => JSX.Element);

const actionBarItemFilter = 'invert(27%) sepia(0%) saturate(0%) hue-rotate(56deg) brightness(95%) contrast(81%)';

export function Seperator() {
    return <span className="mx-2 w-0.5 h-5 align-middle border-x-[1px] border-l-white border-r-grey-200"></span>;
}

function ActionBar({ items }: { items: ActionBarItem[] }) {
    return (
        <div className="bg-grey-50 shadow-md h-8 pl-3 pt-1 flex items-center">
            {items.map((item, index) => {
                if (typeof item == 'object') {
                    if (typeof item.icon == 'string') {
                        return (
                            <button
                                className="h-5 w-5 hover:!filter-none"
                                key={index}
                                title={item.tooltip}
                                style={{
                                    backgroundImage: 'url(' + item.icon + ')',
                                    filter: actionBarItemFilter,
                                    backgroundRepeat: 'no-repeat',
                                }}
                                onClick={item.handler}
                            />
                        );
                    } else {
                        return (
                            <item.icon
                                className="h-5 w-5 hover:!filter-none hover:cursor-pointer"
                                key={index}
                                title={item.tooltip}
                                style={{ filter: actionBarItemFilter }}
                                onClick={item.handler}
                            />
                        );
                    }
                } else {
                    return <div key={index}>{item()}</div>;
                }
            })}
        </div>
    );
}

export default ActionBar;
