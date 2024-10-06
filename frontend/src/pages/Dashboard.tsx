import { useState } from 'react';
import Tab from '../components/Tab';
import ProcessReport from '../components/report/ProcessReport';

function Dashboard() {
    const [activeTab, setActiveTab] = useState<'Process' | 'Control'>('Process');

    return (
        <div className="text-center w-full p-5">
            <div className="flex mb-2 ">
                <Tab
                    text="Process"
                    onClick={() => setActiveTab('Process')}
                    className={
                        activeTab === 'Process'
                            ? 'bg-soft-blue-800 text-black px-8 py-2 text-lg mr-3'
                            : 'bg-grey-50 text-black px-8 py-2 text-lg mr-3'
                    }
                />
                <Tab
                    text="Control"
                    onClick={() => setActiveTab('Control')}
                    className={
                        activeTab === 'Control'
                            ? 'bg-soft-blue-800 text-black px-8 py-2 text-lg mr-3'
                            : 'bg-grey-50 text-black px-8 py-2 text-lg mr-3'
                    }
                />
            </div>
            {activeTab === 'Process' && <ProcessReport />}
            {activeTab === 'Control' && <p>Coming Soon...</p>}
        </div>
    );
}

export default Dashboard;
