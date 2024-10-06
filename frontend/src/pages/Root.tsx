import { Outlet } from 'react-router-dom';
import Header from '../components/Header';
import Sidebar, { SidebarListItem } from '../components/Sidebar';
import HomeIcon from '../assets/icons/home.svg';
import RulesIcon from '../assets/icons/rules.svg';
import FolderIcon from '../assets/icons/folder.svg';
import { Process } from '../types/Process';
import { useProcesses } from '../hooks/processHooks';

function Root() {
    const { isError, isLoading, data } = useProcesses();

    // listItems array based on the fetched data:
    const listItems = data
        ? // map the array of Process objects to an array of SidebarListItem objects
          data.map((process: Process) => {
              const item = Process.parse(process); // parse the Process object to validate it against the Process schema
              return {
                  text: item.name,
                  url: '/processes/' + item.id,
                  icon: FolderIcon,
              } as SidebarListItem; // create a new SidebarListItem object with the above properties:
          })
        : []; // else return an empty array

    //------------------layout for application------------------
    return (
        // flex-col direction to stack its children vertically, and h-screen to occupy the full height of the screen.
        <div className="flex flex-col h-screen w-screen">
            <div className="flex-none">
                <Header />
            </div>

            <div className="flex flex-auto min-h-0">
                {/* flex container with fixed width (w-[400px]) and a shadowfor the sidebar */}
                <div className="flex-none w-[400px] pr-2 shadow flex flex-col z-10">
                    {/*  label "Navigation" styled with Tailwind CSS classes. */}
                    <div className="border-b-[1px] border-b-grey-200 p-1.5 pl-2.5 text-sm text-grey-950 opacity-60 flex-none">
                        Navigation
                    </div>

                    {/* An error message that is displayed when isError is truthy. */}
                    {isError && <p>{isError}</p>}

                    {/* a sidebar containing navigation items, receive listItems from useQuery() */}
                    <Sidebar
                        className="m-3 mr-1 flex-auto"
                        listItems={[
                            { text: 'Dashboard', url: '/', icon: HomeIcon },
                            { text: 'Risk & Obligation Register', url: '/register', icon: RulesIcon },
                            ...(isLoading ? [] : listItems),
                        ]}
                        isLoading={isLoading}
                    />
                </div>
                {/* the main content of the page, contains Outlet Component */}
                {/* used by react-router to render the appropriate child route components based on the current URL. */}
                {/* will be replaced by the child with matched url in main.tsx */}
                <div className="flex-1 min-w-0 overflow-x-auto overflow-y-auto">
                    {/* the flex-1 and overflow added here to prevent the table on right side not scrolling */}
                    <Outlet />
                </div>
            </div>
        </div>
    );
}

export default Root;
