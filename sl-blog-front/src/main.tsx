import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import App from './App.tsx';
import Posts from './components/Posts';
import PostPage from './components/PostPage';
import './index.css'

//Do this instead of enabling auto-ssl on Javalin due to conflict with cron job.
if (location.hostname != 'localhost' && location.protocol !== 'https:') {
    location.replace(`https:${location.href.substring(location.protocol.length)}`);
}

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
	children:[
		{
			index: true,
			element: <Posts/>			
		},
		{
			path: 'post/:postId',
			element: <PostPage/>
		}
	]
  }
]);



createRoot(document.getElementById('root')!).render(
	<StrictMode>
		<RouterProvider router={router} />		
	</StrictMode>,
)
