import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Routes, Route, BrowserRouter } from "react-router-dom";
import PostNew from './routes/PostNew';
import PostEdit from './routes/PostEdit';
import PostList from './routes/PostList';
import SettingEdit from './routes/SettingEdit';
import PageCompList from './routes/PageCompList';
import PageCompEdit from './routes/PageCompEdit';

import ManageApp from './ManageApp.tsx'
import LoginApp from './LoginApp.tsx'
import Login from './routes/Login'
import '../index.css'


createRoot(document.getElementById('root')!).render(
  <StrictMode>
  	<BrowserRouter>
  		<Routes>
			<Route path="m" element={<ManageApp />} >
				<Route path="posts" element={<PostList />} />
				<Route path="posts/edit" element={<PostEdit />} />
				<Route path="posts/new" element={<PostNew />} />
				<Route path="setting" element={<SettingEdit />} />
				<Route path="components" element={<PageCompList />} />
				<Route path="components/edit" element={<PageCompEdit />} />
			</Route>
			<Route path="login" element={<LoginApp />} >
				<Route path="login" element={<Login />} />
			</Route>
		</Routes>
    </BrowserRouter>
  </StrictMode>,
);
