import '../App.css'
import { Link, Outlet, useNavigate } from "react-router-dom";
import { useEffect,useState } from "react";
import UserName from "./components/UserName";


function ManageApp() {
	const navigate = useNavigate();
	const [userName, setUserName] = useState('');
	useEffect(() => {
		fetch('/mng/check-login', {
			method: "POST"
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setUserName(data.userName);
				} else {
					//Show the login screen.
					navigate('/login/login');
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to Check Login.');
			});
		
	}, []);

	return (
		<>
			<div className="container">
				<div className="flex p-4">
					<div className="grow">
						<h1 className="text-5xl">Manage</h1>
					</div>
					<div> 
						<UserName userName={userName}/>
					</div>
				</div>
				<div>
					<div className="drawer lg:drawer-open">
						<input id="my-drawer-2" type="checkbox" className="drawer-toggle" />
						<div className="drawer-content flex flex-col ">
							{/* Page content here */}
							<label htmlFor="my-drawer-2" className="btn btn-primary drawer-button lg:hidden mb-3">
								Open Menu
							</label>
							<div className="ml-4">
								<Outlet />
							</div>
						</div>
						<div className="drawer-side">
							<label htmlFor="my-drawer-2" aria-label="close sidebar" className="drawer-overlay"></label>
							<ul className="menu bg-base-200 text-base-content min-h-full w-60 p-4">
								<li><Link to="/m/posts">Posts</Link></li>
								<li><Link to="/m/components">Page Components</Link></li>
								<li><Link to="/m/tags">Tags</Link></li>
								<li><Link to="/m/setting">Setting</Link></li>
								<li className='pl-4'>------------</li>
								<li><Link to="/m/analytics/daily-visits">Daily Visits</Link></li>
								<li><Link to="/m/analytics/page-engagement">Page Engagement</Link></li>
								<li className='pl-4'>------------</li>								
								<li><a href="/">Show Blog</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</>
	)
}

export default ManageApp
