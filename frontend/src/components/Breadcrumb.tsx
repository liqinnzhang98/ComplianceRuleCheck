function Breadcrumb({ children }: { children: string }) {
    return (
        <div className="h-8">
            <span className="border-b-maroon-600 border-b-2 h-full inline-block pt-1.5 px-3 text-primary-light text-sm">
                {children}
            </span>
        </div>
    );
}

export default Breadcrumb;
