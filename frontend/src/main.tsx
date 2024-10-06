//  importing React, ReactDOM, and other necessary packages such as react-router-dom and react-query.
import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import './index.css';

import Root from './pages/Root';
import Dashboard from './pages/Dashboard';
import ErrorPage from './pages/ErrorPage';
import RiskObligationRegisterPage from './pages/RiskObligationRegisterPage';
import ProcessPage from './pages/ProcessPage';

const queryClient = new QueryClient(); // creating a new instance of the QueryClient class.

// creating a new instance of the BrowserRouter class and passing it an array of routes.
const router = createBrowserRouter([
    {
        path: '/',
        element: <Root />,
        errorElement: <ErrorPage />,
        children: [
            // the child routes of the route.
            { path: '/', element: <Dashboard /> },
            { path: '/register', element: <RiskObligationRegisterPage /> },
            { path: '/processes/:processId', element: <ProcessPage /> },
        ],
    },
]);

// rendering the RouterProvider component, which provides the router to the rest of the application.
ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    // wrapping the RouterProvider component in the QueryClientProvider component, which provides the queryClient to the rest of the application.
    <React.StrictMode>
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>
    </React.StrictMode>,
);
