import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import App from './App.tsx';
import Posts from './components/Posts';
import PostPage, {loader as postLoader} from './components/PostPage';
import './index.css'

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
			element: <PostPage/>,
			loader:postLoader
		}
	]
  }
]);

createRoot(document.getElementById('root')!).render(
	<StrictMode>
		<RouterProvider router={router} />		
	</StrictMode>,
)
